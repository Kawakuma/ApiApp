package jp.techacademy.kentaro.kawamura.apiapp

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


class FavoriteAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // お気に入りが登録されているときのリスト
    class FavoriteItemViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {  //第２引数にView、つまりonCreateViewHolderでinflateしたレイアウト
        val rootView: ConstraintLayout = view.findViewById(R.id.rootView)
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val addressTextView: TextView = view.findViewById(R.id.addressTextView)
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val favoriteImageView: ImageView = view.findViewById(R.id.favoriteImageView)
    }

    // お気に入り登録がまだ行われていないとき
    class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)


    // onCreateViewHolderの第二引数(viewType: Int)はここで決める。ﾃﾞﾌｫﾙﾄは０を返す。
    override fun getItemViewType(position: Int): Int {
        return if (items.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_ITEM
    }

    companion object {
        // Viewの種類を表現する定数、こちらはお気に入りのお店
        private const val VIEW_TYPE_ITEM = 0

        // Viewの種類を表現する定数、こちらはお気に入りが１件もないとき
        private const val VIEW_TYPE_EMPTY = 1
    }


    // お気に入り画面用のViewHolderオブジェクトの生成
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            // ViewTypeがVIEW_TYPE_EMPTY（つまり、お気に入り登録が0件）の場合
            VIEW_TYPE_EMPTY -> EmptyViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.recycler_favorite_empty, parent, false)
            )
            // 上記以外（つまり、1件以上のお気に入りが登録されている場合
            else -> FavoriteItemViewHolder(
                LayoutInflater.from(context).inflate(R.layout.recycler_favorite, parent, false)
            )
        }
    }


    // お気に入り登録したShopを格納
    private val items = mutableListOf<FavoriteShop>()

    // 更新用のメソッド
    fun refresh(list: List<FavoriteShop>) {
        items.apply {
            clear() // items を 空にする
            addAll(list) // itemsにlistを全て追加する
        }
        notifyDataSetChanged() // recyclerViewを再描画させる
    }


    var onClickFavoItem: ((FavoriteShop) -> Unit)? = null

    // お気に入りが1件もない時に、「お気に入りはありません」を出すため
    override fun getItemCount(): Int {
        return if (items.isEmpty()) 1 else items.size
    }


    // ViewHolderのバインド
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FavoriteItemViewHolder) {
            updateFavoriteItemViewHolder(holder, position)
        }
    }

    // お気に入り画面から削除するときのコールバック（ApiFragmentへ通知するメソッド)
    var onClickDeleteFavorite: ((FavoriteShop) -> Unit)? = null

    // ViewHolder内のUI部品に値などをセット
    private fun updateFavoriteItemViewHolder(holder: FavoriteItemViewHolder, position: Int) {
        val data = items[position]
        holder.apply {
            rootView.apply {
                setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        if (position % 2 == 0) android.R.color.white else android.R.color.darker_gray
                    )
                ) // 偶数番目と機数番目で背景色を変更させる

                setOnClickListener {
                    onClickFavoItem?.invoke(data)

                }
                nameTextView.text = data.name
                addressTextView.text=data.address
                Picasso.get().load(data.imageUrl)
                    .into(imageView) // Picassoというライブラリを使ってImageVIewに画像をはめ込む
                favoriteImageView.setOnClickListener {
                    onClickDeleteFavorite?.invoke(data)   //invoke(date)?
                    notifyItemChanged(position)    //そのポジションのセルの設定を確定。　setChangeはアダプター全体の変更を確定させる。
                }
            }
        }
    }

}