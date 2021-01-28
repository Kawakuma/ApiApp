package jp.techacademy.kentaro.kawamura.apiapp    //MainActivityへ通知するためのFragmentCallbackというinterface。ここで定義されたメソッドをコールバックメソッドともおいう。

interface FragmentCallback {  //課題はここをいじるよ～

    // Itemを押したときの処理
    fun onClickItem(shop:Shop)
    // お気に入り追加時の処理
    fun onAddFavorite(shop: Shop)
    // お気に入り削除時の処理
    fun onDeleteFavorite(id: String)

    fun onClickFavoItem(FavoShop:FavoriteShop)





}