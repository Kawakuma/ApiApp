package jp.techacademy.kentaro.kawamura.apiapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_favorite.*

class FavoriteFragment: Fragment() {

    private val favoriteAdapter by lazy { FavoriteAdapter(requireContext()) }

    // FavoriteFragment -> MainActivity に削除を通知する
    private var fragmentCallback : FragmentCallback? = null

    override fun onAttach(context: Context) {   //FragmentがActivityへアタッチされたときに呼び出される
        super.onAttach(context)
        if (context is FragmentCallback) {
            fragmentCallback = context          //なんのためのコード?
        }
    }

    override fun onResume() {//フラグメントが再開されたとき更新する
        super.onResume()
        updateData()
    }

    //↓このFragmentのメインとなるViewを生成し、返す必要があるときに呼び出される
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // fragment_favorite.xmlが反映されたViewを作成して、returnします
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }


    //↓このFragmentのViewが生成された後に呼び出され、Viewの初期化・Fragmentの状態の復元を行う
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ここから初期化処理を行う
        // FavoriteAdapterのお気に入り削除用のメソッドの追加を行う
        favoriteAdapter.apply {
            // Adapterの処理をそのままActivityに通知
            onClickDeleteFavorite = {
                fragmentCallback?.onDeleteFavorite(it.id)
            }

            // Itemをクリックしたとき
            onClickFavoItem = {
                fragmentCallback?.onClickFavoItem(it)} //◆onClickItemにはfragmentCallback?.onClickItemが実装されていて
            // FavoriteAdapterからタップを検知したとき、onClickItemが動く？このときのitはadapterのinvokeの引数からとってきたもの。



        }



        // RecyclerViewの初期化
        recyclerView.apply {
            adapter = favoriteAdapter
            layoutManager = LinearLayoutManager(requireContext()) // 一列ずつ表示
        }
        swipeRefreshLayout.setOnRefreshListener {
            updateData()
        }
        updateData()
    }

    fun updateData() {
        favoriteAdapter.refresh(FavoriteShop.findAll())
        swipeRefreshLayout.isRefreshing = false
    }
}