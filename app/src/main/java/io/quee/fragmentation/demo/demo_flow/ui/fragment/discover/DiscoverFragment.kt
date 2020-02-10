package io.quee.fragmentation.demo.demo_flow.ui.fragment.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import io.quee.fragmentation.demo.R
import io.quee.fragmentation.demo.demo_flow.adapter.DiscoverFragmentAdapter
import io.quee.fragmentation.demo.demo_flow.base.BaseMainFragment

/**
 * Created by YoKeyword on 16/2/3.
 */
class DiscoverFragment :
    BaseMainFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_discover, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        val mToolbar =
            view.findViewById<View>(R.id.toolbar) as Toolbar
        val mTabLayout =
            view.findViewById<View>(R.id.tab_layout) as TabLayout
        val mViewPager =
            view.findViewById<View>(R.id.viewPager) as ViewPager
        mToolbar.setTitle(R.string.discover)
        initToolbarNav(mToolbar)
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.recommend))
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.hot))
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.favorite))
        mViewPager.adapter = DiscoverFragmentAdapter(
            childFragmentManager,
            arrayOf(
                getString(R.string.recommend),
                getString(R.string.hot),
                getString(R.string.favorite)
            )
        )
        mTabLayout.setupWithViewPager(mViewPager)
    }

    companion object {
        fun newInstance(): DiscoverFragment {
            return DiscoverFragment()
        }
    }
}