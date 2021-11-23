package com.example.chain

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import android.util.Log
import com.example.chain.fragment.*
import kotlinx.android.synthetic.main.activity_main.*



class ViewPager2AdapterLog(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    val fragmentList: MutableList<FragmentName> = mutableListOf()

    companion object{
        //bottomNav管理用の番号　0->ホーム　1->記録　2->通知
        var navFlag: Int = 0

    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {

        return when (fragmentList[position]) {
            FragmentName.WHITE -> TimeFragment()
            FragmentName.RED -> VideoFragment()
            FragmentName.GREEN -> SituationFragment()
        }

        /*

        return when (fragmentList[position]) {
            FragmentName.WHITE -> {
                Log.d("TAG", "navFlag:$navFlag")
                when (MainActivity.navFlag) {
                    0 -> StartFragment()
                    1 -> TimeFragment()
                    2 -> FromSchoolFragment()
                    else -> StartFragment()
                }
            }
            FragmentName.RED -> {
                when (MainActivity.navFlag) {
                    0 -> GoalFragment()
                    1 -> VideoFragment()
                    2 -> FromChainFragment()
                    else -> GoalFragment()
                }
            }
            FragmentName.GREEN -> {
                when (MainActivity.navFlag) {
                    0 -> ReserveFragment()
                    1 -> SituationFragment()
                    2 -> UnknownFragment()
                    else -> ReserveFragment()
                }
            }

        }

         */

    }

    override fun getItemId(position: Int): Long {
        return fragmentList[position].ordinal.toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        val fragment = FragmentName.values()[itemId.toInt()]
        return fragmentList.contains(fragment)
    }

    fun add(fragment: FragmentName) {
        fragmentList.add(fragment)
        notifyDataSetChanged()
    }

    /*
    fun add(index: Int, fragment: FragmentName) {
        fragmentList.add(index, fragment)
        notifyDataSetChanged()
    }

    fun remove(index: Int) {
        fragmentList.removeAt(index)
        notifyDataSetChanged()
    }

    fun remove(name: FragmentName) {
        fragmentList.remove(name)
        notifyDataSetChanged()
    }
    */

    enum class FragmentName {
        WHITE,
        RED,
        GREEN,

    }

}