package jp.techacademy.kentaro.kawamura.apiapp    //お気に入り保存を行うクラス

import io.realm.Realm
import io.realm.RealmObject   //RealmObjectでデータ用のクラスを定義する
import io.realm.annotations.PrimaryKey


import java.io.Serializable

open class FavoriteShop: RealmObject(),Serializable {//Serializableでデータを送れるようにする
    @PrimaryKey   //主キーともいう。これに指定されたプロパティはユニーク(一意)なものとなる。
    var id: String = ""
    var imageUrl: String = ""
    var name: String = ""
    var url: String = ""


    var address:String=""

    companion object {    //呼び出しメソッド（クラス名.メソッド名）で呼び出したいときcompanion objectを用いる。favoriteAdapter.refresh(FavoriteShop.findAll())の様に()内でクラス名.メソッド名にする。
        fun findAll():List<FavoriteShop> = // お気に入りのShopを全件取得
            Realm.getDefaultInstance().use { realm ->    //useで使う
                realm.where(FavoriteShop::class.java)
                    .findAll().let {
                        realm.copyFromRealm(it)     //◆let⇒データがあれば処理を行う。　　//itはfindしてきた内容
                        // データを返すときはいったんcopyする?35行目でcopyはしないのか？
                        //取ってくるときはrealm型のままだからいったんコピーしてkotlin型にする。追加の場合は必要ない。
                    }
            }



        fun findBy(id: String): FavoriteShop? = // お気に入りされているShopをidで検索して返す。お気に入りに登録されていなければnullで返す
            Realm.getDefaultInstance().use { realm ->
                realm.where(FavoriteShop::class.java)
                    .equalTo(FavoriteShop::id.name, id) //idのnameがid（引数で取った値）のデータをFavoriteShopのrealmから取ってください？
                    //↑id.nameで文字列型のidをとっている
                    .findFirst()?.let {
                        realm.copyFromRealm(it)//◆let⇒データがあれば処理を行う。　　　it(取ってきたデータ)をコピーする。
                    }
            }




        fun insert(favoriteShop: FavoriteShop) = // お気に入り追加。引数にこのクラス自身のプロパティを入れる。
            Realm.getDefaultInstance().executeTransaction {
                it.insertOrUpdate(favoriteShop)   //realmのメソッドを呼び出して、そのit(引数)にデータがあれば更新処理、なければ追加。主キーを見て判断
            }




        fun delete(id: String) = // idでお気に入りから削除する
            Realm.getDefaultInstance().use { realm ->
                realm.where(FavoriteShop::class.java)
                    .equalTo(FavoriteShop::id.name, id)
                    .findFirst()?.also { deleteShop ->              //とってきたデータの名前がdeleteShop
                        realm.executeTransaction {
                            deleteShop.deleteFromRealm()
                        }
                    }
            }
    }
}