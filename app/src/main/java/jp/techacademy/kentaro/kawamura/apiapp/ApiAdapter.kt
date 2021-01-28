package jp.techacademy.kentaro.kawamura.apiapp //

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ApiAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //RecyclerView.ViewHolder型のRecyclerView.Adapterクラスを継承している

    // 取得したJsonデータを解析し、Shop型オブジェクトとして生成したものを格納するリスト
    private val items = mutableListOf<Shop>()   //


    fun refresh(list: List<Shop>) {
        update(list, false)
    }

    fun add(list: List<Shop>) {
        update(list, true)
    }

    // 表示リスト更新時に呼び出すメソッド
    fun update(list: List<Shop>, isAdd: Boolean) {
        items.apply {
            if(!isAdd){ //　isAddじゃないとき、つまり追加じゃないとき
                clear() // items を 空にする。
            }
            addAll(list) // itemsにlistを全て追加する。　listへの追加はaddAll(追加するもの)。androidに備わっているメソッドaddAll
        }
        notifyDataSetChanged() // recyclerViewを再描画させる
    }



    //RecyclerViewで表示させる1件分の枠組みを作成するメソッド。必要なホルダーの数がそろえばもう呼ばれない。
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //　レイアウトを切り替えるためにviewTypeを7 7.5で使っている。デフォルトでは０が渡される。ここでは使う必要はない。
        //parentはインフレートされるレイアウト。viewTypeには何が入る？
        // ViewHolderを継承したApiItemViewHolderオブジェクトを生成し戻す
        return ApiItemViewHolder(
            LayoutInflater.from(context).inflate(R.layout.recycler_favorite, parent, false)
        )
        //LayoutInflater.from(context)に入っているinflateを使う
    }

    //第3引数はｲﾝﾌﾚｰﾄされたﾚｲｱｳﾄをviewGroupへｱﾀｯﾁするか決める。ここではすでにｱﾀｯﾁされているのでfalseでおｋ
    // ViewHolderを継承したApiItemViewHolderクラスの定義
    class ApiItemViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {     //30行目にonCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
        //となっているということはRecyclerView.ViewHolderを、もしくはそれを継承したものをreturnしなければならないので、ここで作る。
        // レイアウトファイルからidがrootViewのConstraintLayoutオブジェクトを取得し、代入
        val rootView: ConstraintLayout = view.findViewById(R.id.rootView)

        // レイアウトファイルからidがnameTextViewのCTextViewオブジェクトを取得し、代入
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)

        val addressTextView: TextView = view.findViewById(R.id.addressTextView)

        // レイアウトファイルからidがimageViewのImageViewオブジェクトを取得し、代入
        val imageView: ImageView = view.findViewById(R.id.imageView)

        // レイアウトファイルからidがfavoriteImageViewのImageViewオブジェクトを取得し、代入
        val favoriteImageView: ImageView = view.findViewById(R.id.favoriteImageView)


    }


    override fun getItemCount(): Int {
        // itemsプロパティに格納されている要素数を返す
        return items.size
    }


    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {  //作られたホルダーのポジションとそのホルダーが自動で呼ばれ、以下に記述する処理を行う
        if (holder is ApiItemViewHolder) {
            // 生成されたViewHolderがApiItemViewHolderだったら。。。
            updateApiItemViewHolder(holder, position)
        }//
        // 別のViewHolderをバインドさせる(当てはめる)ことが可能となる
        // }
    }

    private fun updateApiItemViewHolder(
        holder: ApiItemViewHolder,
        position: Int
    ) {  //onCreateViewHolderで作られたホルダーたちにポジションが勝手につけられその番号に対応してこのﾒｿｯﾄﾞが呼ばれる？
        // 生成されたViewHolderの位置を指定し、オブジェクトを代入　。　このﾒｿｯﾄﾞはonBindViewHolderにつられて自動で呼ばれている。
        val data = items[position]

        val isFavorite = FavoriteShop.findBy(data.id) != null         //お気に入り登録されているShopのデータが渡される

        holder.apply {
            rootView.apply {
                // 偶数番目と奇数番目で背景色を変更させる
                setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        if (position % 2 == 0) android.R.color.white else android.R.color.darker_gray
                    )
                )   //２で割って０なら白色

                setOnClickListener {
                    onClickItem?.invoke(data)

                }
            }


            // nameTextViewのtextプロパティに代入されたオブジェクトのnameプロパティを代入
            nameTextView.text = data.name

            addressTextView.text=data.address

            // Picassoライブラリを使い、imageViewにdata.logoImageのurlの画像を読み込ませる
            Picasso.get().load(data.logoImage).into(imageView)

            // 白抜きの星マークの画像を指定
            favoriteImageView.apply {
                setImageResource(if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_border) // お気に入りに入っているデータのみ塗りつぶしの★にする。
                setOnClickListener {
                    if (isFavorite) {
                        onClickDeleteFavorite?.invoke(data)     //invokeでonClickを実行。(data)にはShop型のデータが入る？これを(Shop) -> Unitで表している？
                    } else {
                        onClickAddFavorite?.invoke(data)
                    }
                    notifyItemChanged(position)
                }
            }
        }
    }
    // 一覧画面から登録するときのコールバック（FavoriteFragmentへ通知するメソッド)
    var onClickAddFavorite: ((Shop) -> Unit)? = null  //shopを受け取ってUnitを返す。　nullは初期化している。　
    //オブジェクトクラスの１番上(始祖)がUnit。　ビューもユニット。　人間　動物　ハサミ　ノリ　すべてのものをまとめると「モノ」といえる。Unitはすべてのクラスの頂点。

    // 一覧画面から削除するときのコールバック（ApiFragmentへ通知するメソッド)
    var onClickDeleteFavorite: ((Shop) -> Unit)? = null

    // Itemを押したときのメソッド
    var onClickItem: ((Shop)-> Unit)? = null






}