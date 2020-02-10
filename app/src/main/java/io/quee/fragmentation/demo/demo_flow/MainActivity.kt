package io.quee.fragmentation.demo.demo_flow

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import io.quee.fragmentation.core.ISupportFragment
import io.quee.fragmentation.core.anim.FragmentAnimator
import io.quee.fragmentation.demo.R
import io.quee.fragmentation.demo.demo_flow.base.BaseMainFragment
import io.quee.fragmentation.demo.demo_flow.base.BaseMainFragment.OnFragmentOpenDrawerListener
import io.quee.fragmentation.demo.demo_flow.base.MySupportActivity
import io.quee.fragmentation.demo.demo_flow.base.MySupportFragment
import io.quee.fragmentation.demo.demo_flow.ui.fragment.account.LoginFragment
import io.quee.fragmentation.demo.demo_flow.ui.fragment.account.LoginFragment.OnLoginSuccessListener
import io.quee.fragmentation.demo.demo_flow.ui.fragment.discover.DiscoverFragment
import io.quee.fragmentation.demo.demo_flow.ui.fragment.home.HomeFragment
import io.quee.fragmentation.demo.demo_flow.ui.fragment.shop.ShopFragment

/**
 * 流程式demo  tip: 多使用右上角的"查看栈视图"
 * Created by YoKeyword on 16/1/29.
 */
class MainActivity : MySupportActivity(),
    NavigationView.OnNavigationItemSelectedListener, OnFragmentOpenDrawerListener,
    OnLoginSuccessListener {
    private var TOUCH_TIME: Long = 0
    private var mDrawer: DrawerLayout? = null
    private var mNavigationView: NavigationView? = null
    private var mTvName // NavigationView上的名字
            : TextView? = null
    private var mImgNav // NavigationView上的头像
            : ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragment: MySupportFragment? = findFragment(HomeFragment::class.java)
        if (fragment == null) {
            loadRootFragment(R.id.fl_container, HomeFragment.Companion.newInstance())
        }
        initView()
    }

    /**
     * 设置动画，也可以使用setFragmentAnimator()设置
     */
    override fun onCreateFragmentAnimator(): FragmentAnimator? { // 设置默认Fragment动画  默认竖向(和安卓5.0以上的动画相同)
        return super.onCreateFragmentAnimator()
        // 设置横向(和安卓4.x动画相同)
//        return new DefaultHorizontalAnimator();
// 设置自定义动画
//        return new FragmentAnimator(enter,exit,popEnter,popExit);
    }

    private fun initView() {
        mDrawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
            this, mDrawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        //        mDrawer.setDrawerListener(toggle);
        toggle.syncState()
        mNavigationView = findViewById<View>(R.id.nav_view) as NavigationView
        mNavigationView!!.setNavigationItemSelectedListener(this)
        mNavigationView?.setCheckedItem(R.id.nav_home)
        val llNavHeader = mNavigationView!!.getHeaderView(0) as LinearLayout
        mTvName = llNavHeader.findViewById<View>(R.id.tv_name) as TextView
        mImgNav =
            llNavHeader.findViewById<View>(R.id.img_nav) as ImageView
        llNavHeader.setOnClickListener {
            mDrawer!!.closeDrawer(GravityCompat.START)
            mDrawer!!.postDelayed({ goLogin() }, 250)
        }
    }

    override fun onBackPressedSupport() {
        if (mDrawer!!.isDrawerOpen(GravityCompat.START)) {
            mDrawer!!.closeDrawer(GravityCompat.START)
        } else {
            val topFragment = topFragment
            // 主页的Fragment
            if (topFragment is BaseMainFragment) {
                mNavigationView?.setCheckedItem(R.id.nav_home)
            }
            if (supportFragmentManager.backStackEntryCount > 1) {
                pop()
            } else {
                if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
                    finish()
                } else {
                    TOUCH_TIME = System.currentTimeMillis()
                    Toast.makeText(this, R.string.press_again_exit, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * 打开抽屉
     */
    override fun onOpenDrawer() {
        if (!mDrawer!!.isDrawerOpen(GravityCompat.START)) {
            mDrawer!!.openDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        mDrawer!!.closeDrawer(GravityCompat.START)
        mDrawer!!.postDelayed({
            val id = item.itemId
            val topFragment = topFragment
            val myHome = topFragment as MySupportFragment
            if (id == R.id.nav_home) {
                val fragment =
                    findFragment(HomeFragment::class.java)
                val newBundle = Bundle()
                newBundle.putString("from", "From:" + topFragment.javaClass.simpleName)
                fragment!!.putNewBundle(newBundle)
                myHome.start(fragment, ISupportFragment.SINGLETASK)
            } else if (id == R.id.nav_discover) {
                val fragment = findFragment(
                    DiscoverFragment::class.java
                )
                if (fragment == null) {
                    myHome.startWithPopTo(
                        DiscoverFragment.Companion.newInstance(),
                        HomeFragment::class.java,
                        false
                    )
                } else { // 如果已经在栈内,则以SingleTask模式start
                    myHome.start(fragment, ISupportFragment.SINGLETASK)
                }
            } else if (id == R.id.nav_shop) {
                val fragment =
                    findFragment(
                        ShopFragment::class.java
                    )
                if (fragment == null) {
                    myHome.startWithPopTo(
                        ShopFragment.Companion.newInstance(),
                        HomeFragment::class.java,
                        false
                    )
                } else { // 如果已经在栈内,则以SingleTask模式start,也可以用popTo
//                        start(fragment, SupportFragment.SINGLETASK);
                    myHome.popTo(
                        ShopFragment::class.java,
                        false
                    )
                }
            } else if (id == R.id.nav_login) {
                goLogin()
            } else if (id == R.id.nav_swipe_back) {
                startActivity(Intent(this@MainActivity, SwipeBackSampleActivity::class.java))
            }
        }, 300)
        return true
    }

    private fun goLogin() {
        start(LoginFragment.Companion.newInstance())
    }

    override fun onLoginSuccess(account: String?) {
        mTvName!!.text = account
        mImgNav!!.setImageResource(R.drawable.ic_account_circle_white_48dp)
        Toast.makeText(this, R.string.sign_in_success, Toast.LENGTH_SHORT).show()
    }

    companion object {
        val TAG =
            MainActivity::class.java.simpleName
        // 再点一次退出程序时间设置
        private const val WAIT_TIME = 2000L
    }
}