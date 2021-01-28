package jp.techacademy.kentaro.kawamura.apiapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_web_view.*
import java.net.URL

import android.util.Log

class WebViewActivity: AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)//onCreateでactivity_web_view)を起動

        var shop:Shop?=intent.getSerializableExtra(KEY_SHOP) as Shop? //もしデータが入っていなかった時のことを考えてヌル許容型にしておく。?を使ってnull許容型とする。
        var favoshop:FavoriteShop?=intent.getSerializableExtra(KEY_FAVOSHOP) as FavoriteShop?//getSerializableExtraでデータをShop型として取る

        if (favoshop!=null){
           webView.loadUrl(favoshop?.url.toString())
        }//２２行目で送ったurlをgetしてactivity_web_viewに表示させる。
        else { webView.loadUrl(if(shop!!.couponUrls.sp.isNotEmpty()){shop!!.couponUrls.sp} else {shop!!.couponUrls.pc})}


        var checkfavo:FavoriteShop? = FavoriteShop.findBy(shop?.id.toString())

        favoriteImageView.apply {
            setImageResource(if(checkfavo?.id==shop?.id) R.drawable.ic_star else R.drawable.ic_star_border)


            setOnClickListener {
                if (checkfavo?.id==shop?.id) {FavoriteShop.delete(favoshop?.id.toString()) //true(お気に入り)ならDeleteする
              
                    setImageResource(R.drawable.ic_star_border)//画像も☆に切り替える


                } else {
                    FavoriteShop.insert(FavoriteShop().apply {
                    id = shop!!.id
                    name = shop.name
                    address=shop.address
                    imageUrl = shop.logoImage
                        url = if (shop.couponUrls.sp.isNotEmpty()) shop.couponUrls.sp else shop.couponUrls.pc

                        setImageResource(R.drawable.ic_star)
                   } )
                }
            }
        }
    }


    companion object {
        private const val KEY_SHOP = "key_shop"
        fun start(activity: Activity, shop: Shop) {
            activity.startActivity(
                Intent(activity, WebViewActivity::class.java).putExtra(KEY_SHOP, shop)
            )
        }//画面遷移を行うには、ActivityクラスにあるstartActivity()メソッドを呼び出す。その際の引数にURL情報を付加したIntentオブジェクトを入れる。
        //Intentの第１引数はstartの第１引数、第２引数には遷移先(クラス)。putExtraではキーとurlを送る。このurlは１４行目でgetされる。


        private const val KEY_FAVOSHOP = "key_favoshop"
        fun start2(activity: Activity, FavoShop: FavoriteShop) {
            activity.startActivity(
                Intent(activity, WebViewActivity::class.java).putExtra(KEY_FAVOSHOP, FavoShop)
            )
        }
    }


}

