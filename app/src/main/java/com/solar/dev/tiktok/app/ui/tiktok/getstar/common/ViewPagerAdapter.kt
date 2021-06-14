package com.solar.dev.tiktok.app.ui.tiktok.getstar.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.dev.anhnd.mybaselibrary.utils.app.getAppColor
import com.solar.dev.tiktok.app.R

class ViewPagerAdapter(
    private val ctx: Context,
    fm: FragmentManager,
    i: Int
) : FragmentStatePagerAdapter(fm, i) {

    private val mFragmentList = arrayListOf<Fragment>()

    private val mFragmentTitle = arrayListOf<String>()

    override fun getItem(position: Int): Fragment = mFragmentList[position]

    override fun getCount(): Int = mFragmentList.size

//    override fun getPageTitle(position: Int): CharSequence? = mFragmentTitle[position]

    fun addFragment(fragment: Fragment, title: String, position: Int) {
        mFragmentList.add(position, fragment)
        mFragmentTitle.add(position, title)
    }

    fun getTabView(position: Int): View? {
        val view = LayoutInflater.from(ctx).inflate(R.layout.layout_tab_view, null)
        view.findViewById<TextView>(R.id.tvTitle).apply {
            text = mFragmentTitle[position]
            if (position == 0) {
                setTextColor(getAppColor(R.color.colorTextYellow))
                setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_point_selected_yellow)
            } else {
                setTextColor(getAppColor(R.color.colorHint))
                setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_point_selected_black)
            }
        }
        return view
    }
}