package jp.techacademy.kentaro.kawamura.apiapp
//Fragmentはコンテンツとライフサイクルを持ったビュー。FragmentもActivityに非常に近いライフサイクルを持っている。 //これはAPI通信結果を表示するktファイルである。
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_api.*
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor

import java.io.IOException
import android.content.Context

import androidx.recyclerview.widget.RecyclerView

class ApiFragment: Fragment() {

    private val apiAdapter by lazy { ApiAdapter(requireContext()) }
    private val handler = Handler(Looper.getMainLooper())   //メモ参照
    //UI部品の変更をメインスレッド以外で行うために使う◆


    private var page = 0

    // Apiでデータを読み込み中ですフラグ。追加ページの読み込みの時にこれがないと、
    // 連続して読み込んでしまうので、それの制御のため
    private var isLoading = false



    private var fragmentCallback : FragmentCallback? = null // Fragment -> Activity にFavoriteの変更を通知する
//コンテキストはアプリの基本情報。　18行目からコンテキストをとれるようにしている。　もしかしてcontextはMainActivity!?//つまりcontextはContextとfragmentCallbackの２つを持っている。
    override fun onAttach(context: Context) { //ここではフラグメントをMainActivityに渡す作業をしている。
        super.onAttach(context)
        if (context is FragmentCallback) {//ここではMainActivityがFragmentCallbackを持ってるか確認してる。isはインターフェイスやクラスを判別する。context(MainActivity)がFragmentCallbackを持ってるなら...
            fragmentCallback = context    //contextを変数fragmentCallback に渡す。
            //これによりfragmentCallbackがFragmentCallbackを持てるようになる。つまり３７行目が実行可能となる。fragmentCallbackはただの変数。
        }
    }//というのもAppCompatActivity()はコンテキストクラスを持っていて、それをMainActivityは継承している。つまりMainActivityはContextとfragmentCallbackの２つを持っている。３８行目と同じことを言ってる。

    override fun onResume() {//フラグメントが再開されたとき更新する
        super.onResume()
        updateView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? { // LayoutInflaterのｲﾝｽﾀﾝｽinflaterがすでに用意されている。
        return inflater.inflate(R.layout.fragment_api, container, false) //◆ fragment_api.xmlが反映されたViewを作成して、viewPager2へreturnする



    }//ホルダーをセットするfragment、つまりR.layout.fragment_apiを取り出し、第２引数のviewPagerに当てはめる。第３引数はApiAdapter35行目と同じ。

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {  //onCreateViewで作ったレイアウトをここで表示させる
        super.onViewCreated(view, savedInstanceState)
        // ここから初期化処理を行う

        apiAdapter.apply {
            onClickAddFavorite = { //
                // ApiAdapterでonClickAddFavoriteはShop型のdataを持っている。そのデータをonAddFavoriteにitとして渡す。
                fragmentCallback?.onAddFavorite(it)//通知されたときに働かせる処理を書く。これをMainに通知する。
            }//↑ここでMainに通知
            onClickDeleteFavorite = { // Adapterの処理をそのままActivityに通知する
                fragmentCallback?.onDeleteFavorite(it.id)
            }
            // Itemをクリックしたとき
            onClickItem = {
                fragmentCallback?.onClickItem(it)//ApiAdpetr１１４で取ったデータの型はCouponUrls、.onClickItemの引数は(url: String)の型。入れていいの？
            }



        }



        // RecyclerViewの初期化
        recyclerView.apply {
            adapter = apiAdapter  //Main20と同じようにadapterにapiAdapterを渡すことで、apiAdapter内のデータをここで扱えるようにしている
            layoutManager = LinearLayoutManager(requireContext()) // 一列ずつ表示

         // addOnScrollListenerはスクロールを検知するリスナー  //↓このobjectは無名関数。オブジェクトに集約されてる感じ。使うメソッドを｛｝内に書く。
            addOnScrollListener(object: RecyclerView.OnScrollListener() { // Scrollを検知するListenerを実装する。これによって、RecyclerViewの下端に近づいた時に次のページを読み込んで、下に付け足す
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) { // dx はx軸方向の変化量(横) dy はy軸方向の変化量(縦) ここではRecyclerViewは縦方向なので、dyだけ考慮する
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy == 0) { // 縦方向の変化量(スクロール量)が0の時は動いていないので何も処理はしない
                        return
                    }
                    val totalCount = apiAdapter.itemCount // RecyclerViewの現在の表示アイテム数.呼び出すときはgetitemCountではなくitemCountで呼ぶ
                    val lastVisibleItem = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition() // RecyclerViewの現在見えている最後のViewHolderのposition
                    //８０行目のlayoutManagerをLinearLayoutManagerとして使い、LinearLayoutManagerが持つメソッドfindLastVisibleItemPosition()をlastVisibleItemに渡す
                    //findLastVisibleItemPosition()は見えているホルダーのポジションを返す

                    // 例:totalCountが20、lastVisibleItemが15の時は、現在のスクロール位置から下に5件見えていないアイテムがある)
                    // ここでは、一番下から5番目を表示した時に追加読み込みする様に実装する
                    if (!isLoading && lastVisibleItem >= totalCount - 6) { //ホルダーのポジションは１から始まる。。isLoadingはisLoading==trueの省略。これの否定つまりfalseだったら(ロードしてないなら)updateData.
                        updateData(true)
                    }
                }
            })
        }



        swipeRefreshLayout.setOnRefreshListener {
            updateData() }

        updateData()  //ホルダーが詰め込まれたら起動
    }


    fun updateView() { // お気に入りが削除されたときの処理（Activityからコールされる）
        recyclerView.adapter?.notifyDataSetChanged() // RecyclerViewのAdapterの内容を再描画のリクエストをする。Activityにお願いする。
    }

    private fun updateData(isAdd: Boolean = false) {     //引数が空の時はfalseになる。isLoading pageはクラス直下で定義してる
        if (isLoading) { //if(isLoading)でtrue。 ★isLoading＝＝trueをisLoadingに省略できる。
            return  //trueならなにもしない
        } else {
            isLoading = true
        }
        if (isAdd) {                    //
            page ++
        } else {
            page = 0
        }
        val start = page * COUNT + 1//最初を０ページ目とすると１件目から２０件目までとれる



        val url = StringBuilder()  //StringBuilder()直後にappendすることでstringが付け足されていく。　appendを続けるとつながる。
            .append(getString(R.string.base_url)) // https://webservice.recruit.co.jp/hotpepper/gourmet/v1/
            .append("?key=").append(getString(R.string.api_key)) // Apiを使うためのApiKey
            .append("&start=").append(start) // 何件目からのデータを取得するか
            .append("&count=").append(COUNT) // 1回で20件取得する　　　　　ここにcount=20もしくは直接20を渡してはダメなのか
            .append("&keyword=").append(getString(R.string.api_keyword)) // お店の検索ワード。ここでは例として「ランチ」を検索
            .append("&format=json") // ここで利用しているAPIは戻りの形をxmlかjsonが選択することができる。Androidで扱う場合はxmlよりもjsonの方が扱いやすいので、jsonを選択
            .toString()//最後にString()で変換

        val client = OkHttpClient.Builder()
                //↓この部分を入れることで、ログに通信の詳細を出すことができる。　　HttpLoggingInterceptor()がログを出すメソッド
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY    //levelはどの程度情報を取得するかのレベル？　リファレンス参照。
            })
            .build()
        val request = Request.Builder()                      //RequestをBuilderでつくる
            .url(url)
            .build()


        client.newCall(request).enqueue(object: Callback { //Client(Okhttp)のnewCallメソッドでURlの準備とCallメソッド生成。
            //enqueueでリクエストが実行されるようにスケジュールして、実行の結果Callbackが呼ばれるようにする。
            //CallbackクラスのonFailureもしくはonResponseをよぶ。
            override fun onFailure(call: Call, e: IOException) { //kotlinで非同期処理を行うときはCallを用いる。IOExceptionのeにはエラー情報が入る。

                e.printStackTrace()//IOExceptionを用いてエラーの情報をlogに表示させる
                handler.post {
                    updateRecyclerView(listOf(), isAdd)  //空のリストで更新
                }
                isLoading = false // 読み込みできるようfalseにする
            }
            override fun onResponse(call: Call, response: Response) { // 成功時の処理
                var list = listOf<Shop>()
                response.body?.string()?.also {
                    //↑返ってきたresponseの、body部分のstringを見ている？
                    val apiResponse = Gson().fromJson(it, ApiResponse::class.java)//itは取ってきたデータ          //Gsonでjsonを変換
                    list = apiResponse.results.shop
                }
                handler.post {
                    updateRecyclerView(list, isAdd)
                }
                isLoading = false


            }
        })


    }






    private fun updateRecyclerView(list: List<Shop>, isAdd: Boolean) {
        if (isAdd) {
            apiAdapter.add(list)
        } else {
            apiAdapter.refresh(list)  //◆ここのrefｒeshによってApiAdapterのitemsにlistデータが入る
        }
        swipeRefreshLayout.isRefreshing = false // SwipeRefreshLayoutのくるくるを消す
    }

    companion object {
        private const val COUNT = 20 // 1回のAPIで取得する件数
        // private const val　定数を扱う。固定値として扱うとき使う。
    }
}