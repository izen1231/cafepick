package com.cafe.dghackathon.shimhg02.sharecafe

import android.annotation.SuppressLint
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.View
import android.widget.Toast
import com.cafe.dghackathon.shimhg02.dghack.*

class MainActivity : BaseActivity() {

    override var viewId: Int = R.layout.activity_main
    override var toolbarId: Int? = R.id.toolbar
    private lateinit var toast: Toast
    private var backKeyPressedTime: Long = 200
    private var mViewPager: ViewPager? = null
    @SuppressLint("ShowToast")
    override fun onCreate() {
        showActionBar()
        mViewPager = findViewById(R.id.viewPager)
        mViewPager!!.adapter = PagerAdapter(supportFragmentManager)
        mViewPager!!.currentItem = 1
        val tabLayout = findViewById<View>(R.id.tabs) as TabLayout
        tabLayout.setupWithViewPager(mViewPager)
        // set icons
        tabLayout.getTabAt(0)!!.setIcon(R.drawable.cafe)
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.credit)
        tabLayout.getTabAt(2)!!.setIcon(R.drawable.set)
        mViewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) = tab.select()
            override fun onTabUnselected(tab: TabLayout.Tab) { }
            override fun onTabReselected(tab: TabLayout.Tab) { }
        })
        /* viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
             override fun onPageScrollStateChanged(state: Int) {
             }
             override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
             }
             override fun onPageSelected(position: Int) {
             }
         })*/
    }
    override fun onBackPressed() {
        val delay = 500
        when {
            System.currentTimeMillis() > backKeyPressedTime + delay -> {
                backKeyPressedTime = System.currentTimeMillis()
                toast.show()
                return
            }
            System.currentTimeMillis() <= backKeyPressedTime + delay -> {
                toast.cancel()
                this.finish()
            }
        }
    }
    inner class PagerAdapter(supportFragmentManager: FragmentManager) : FragmentStatePagerAdapter(supportFragmentManager) {
        override fun getItem(position: Int): Fragment? {
            return when (position) {
                0 ->
                    CafeSearchFragment()
                1 ->

                    CrdFragment()
                2 ->
                    SettingFragment()
                else ->
                    null
            }
        }
        override fun getCount(): Int = 3
    }
}