package jp.techacademy.kentaro.kawamura.apiapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import androidx.appcompat.app.AlertDialog



class MainActivity : AppCompatActivity(),FragmentCallback{  //AppCompatActivity()はコンテキストクラスを持っている。

    private val viewPagerAdapter by lazy { ViewPagerAdapter(this) } //ViewPagerAdapterを
       //遅らせて初期化しviewPagerAdapterにした。これはviewPager2の２０行目の右辺がが実行されたときに初期化される。

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ViewPager2の初期化               //どのページを表示させるかを決めることをページングという。
        viewPager2.apply {
            adapter = viewPagerAdapter  //　使うアダプター設定　 //adapterにviewPagerAdapterを渡すと同時にviewPagerAdapter内のデータがここで扱えるようになる
            orientation = ViewPager2.ORIENTATION_HORIZONTAL // スワイプの向き横（ORIENTATION_VERTICAL を指定すれば縦スワイプで実装可能です）
            offscreenPageLimit = viewPagerAdapter.itemCount // ViewPager2で保持する画面数　◆viewPagerAdapter.getItemCount()ではエラーになる...
        }

        // TabLayoutの初期化
        // TabLayoutとViewPager2を紐づける
        // TabLayoutのTextを指定する
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.setText(viewPagerAdapter.titleIds[position]) //アプリを起動してtabの０番目が作られるとき->０番目のtitleIdのテキストがtabにセットされる
        }.attach() //attachで確定
    }


    override fun onClickItem(shop:Shop) {
        WebViewActivity.start(this, shop)
    }

    override  fun onClickFavoItem(FavoShop:FavoriteShop){
        WebViewActivity.start2(this, FavoShop)
    }

    override fun onAddFavorite(shop: Shop) { //

            //◆insertの引数にクラスも入れられる？↓データが渡された変数じゃなくていいのか。⇒companion objectだからおｋ
            //FavoriteShopのプロパティに｛｝内の内容を反映(apply)させる。
        FavoriteShop.insert(FavoriteShop().apply {
            id = shop.id
            name = shop.name
            address=shop.address
            imageUrl = shop.logoImage
            url = if (shop.couponUrls.sp.isNotEmpty()) shop.couponUrls.sp else shop.couponUrls.pc  //◆SPがなければPCで
        })
        (viewPagerAdapter.fragments[VIEW_PAGER_POSITION_FAVORITE] as FavoriteFragment).updateData()  //お気に入りの更新
    }

    companion object {
        private const val VIEW_PAGER_POSITION_API = 0
        private const val VIEW_PAGER_POSITION_FAVORITE = 1
    }


    override fun onDeleteFavorite(id: String) { // Favoriteから削除するときのメソッド(Fragment -> Activity へ通知する)
        showConfirmDeleteFavoriteDialog(id)
    }

    private fun showConfirmDeleteFavoriteDialog(id: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_favorite_dialog_title)
            .setMessage(R.string.delete_favorite_dialog_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                deleteFavorite(id)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->}
            .create()
            .show()
    }

    private fun deleteFavorite(id: String) {  //63行目
        FavoriteShop.delete(id)
        (viewPagerAdapter.fragments[VIEW_PAGER_POSITION_API] as ApiFragment).updateView()
        (viewPagerAdapter.fragments[VIEW_PAGER_POSITION_FAVORITE] as FavoriteFragment).updateData()
    }




}