package jp.techacademy.kentaro.kawamura.apiapp  //どのフラグメントを設定するか決めるファイル

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

//◆Main11行目のように、ViewPagerAdapterの引数にactivity_mainのフラグメント（viewPager2）が渡されることで、指定された数だけページが作られる？
class ViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    val titleIds = listOf(R.string.tab_title_api, R.string.tab_title_favorite) //タブの名前を格納したリスト。

    val fragments = listOf(ApiFragment(), FavoriteFragment()) //0ページ目がApiFragment、1ページ目がFavoriteFragment　//viewPager2の画面がfragmentである。★この時点でフラグメントは作られている

    override fun getItemCount(): Int { //ページ数の数を返す
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment { //受け取ったfragmentsのポジションに対応してページをviewPager2へ返す。

        //これらのgetItemCountやcreateFragmentはviewPagerの内部から呼び出しをされているメソッドである。
        //このメソッドのポジションはViewPagerAdapterが内部から呼び出された際に、渡される値である。◆呼び出された時にposition0が渡されたなら、１枚目のfragmentを返す。viewPager2に返してる
        return fragments[position]
    }
}
