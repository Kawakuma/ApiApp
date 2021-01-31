package jp.techacademy.kentaro.kawamura.apiapp


import com.google.gson.annotations.SerializedName
import java.io.Serializable
//このクラスにAPI通信結果kotlin変換して格納する。

data class ApiResponse(
    @SerializedName("results")
    val results: Results
)

data class Results(

    @SerializedName("shop")
    val shop: List<Shop>
)

data class Shop(  //レッスン7 6-5ApiFragmentでlistにShopのデータを入れている。list = apiResponse.results.shop。　◆id nameたちをまとめて１個分扱い
 //下のデータをまとめて1件分　これをリスト化している。　List(まとまり,まとまり,まとまり,～）
    @SerializedName("coupon_urls")
    val couponUrls: CouponUrls,  //45行目がString型なのでApiFragmentのonClickItem(it)で取ることができる。onClickItemはStringを引数に取る

    @SerializedName("id")
    val id: String,

    @SerializedName("logo_image")
    val logoImage: String,

    @SerializedName("name")
    val name: String,




    @SerializedName("pc")
    val pc: String,
    @SerializedName("sp")
    val sp: String,




    @SerializedName("address")
    val address: String,

    @SerializedName("wifi")  //◆ここ消すと赤くなる
    val wifi: String



):Serializable //このインターフェイスによってデータをまるごとファイルに保存したり、別のActivityに渡すことができるようにする

//（）内はコンストラクタ？　レッスン６　５．２のvarたちはプロパティ


data class CouponUrls(
    @SerializedName("pc")
    val pc: String,
    @SerializedName("sp")
    val sp: String
):Serializable
