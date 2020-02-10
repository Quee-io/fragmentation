package io.quee.fragmentation.demo.demo_flow.ui.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.quee.fragmentation.core.ISupportActivity
import io.quee.fragmentation.core.anim.DefaultHorizontalAnimator
import io.quee.fragmentation.core.anim.DefaultNoAnimator
import io.quee.fragmentation.core.anim.DefaultVerticalAnimator
import io.quee.fragmentation.demo.R
import io.quee.fragmentation.demo.demo_flow.adapter.HomeAdapter
import io.quee.fragmentation.demo.demo_flow.base.BaseMainFragment
import io.quee.fragmentation.demo.demo_flow.entity.Article
import io.quee.fragmentation.demo.demo_flow.listener.OnItemClickListener
import java.util.*

class HomeFragment : BaseMainFragment(),
    Toolbar.OnMenuItemClickListener {
    private lateinit var mTitles: Array<String>
    private lateinit var mContents: Array<String>
    private var mToolbar: Toolbar? = null
    private var mRecy: RecyclerView? = null
    private var mAdapter: HomeAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        initView(view)
        //        动态改动 当前Fragment的动画
//        setFragmentAnimator(fragmentAnimator);
        return view
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_anim -> {
                val popupMenu =
                    PopupMenu(_mActivity!!, mToolbar!!, GravityCompat.END)
                popupMenu.inflate(R.menu.home_pop)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_anim_veritical -> {
                            (_mActivity as ISupportActivity).setFragmentAnimator(
                                DefaultVerticalAnimator()
                            )
                            Toast.makeText(_mActivity, R.string.anim_v, Toast.LENGTH_SHORT)
                                .show()
                        }
                        R.id.action_anim_horizontal -> {
                            (_mActivity as ISupportActivity).setFragmentAnimator(
                                DefaultHorizontalAnimator()
                            )
                            Toast.makeText(_mActivity, R.string.anim_h, Toast.LENGTH_SHORT)
                                .show()
                        }
                        R.id.action_anim_none -> {
                            (_mActivity as ISupportActivity).setFragmentAnimator(
                                DefaultNoAnimator()
                            )
                            Toast.makeText(_mActivity, R.string.anim_none, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    popupMenu.dismiss()
                    true
                }
                popupMenu.show()
            }
        }
        return true
    }

    private fun initView(view: View) {
        mToolbar =
            view.findViewById<View>(R.id.toolbar) as Toolbar
        mRecy = view.findViewById<View>(R.id.recy) as RecyclerView
        mTitles = resources.getStringArray(R.array.array_title)
        mContents = resources.getStringArray(R.array.array_content)
        mToolbar?.setTitle(R.string.home)
        initToolbarNav(mToolbar!!, true)
        mToolbar!!.inflateMenu(R.menu.home)
        mToolbar!!.setOnMenuItemClickListener(this)
        mAdapter = HomeAdapter(_mActivity)
        val manager = LinearLayoutManager(_mActivity)
        mRecy!!.layoutManager = manager
        mRecy!!.adapter = mAdapter
        mAdapter!!.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int, view: View) {
                start(
                    DetailFragment.newInstance(
                        mAdapter!!.getItem(position).title
                    )
                )
            }
        })
        // Init Datas
        val articleList: MutableList<Article> =
            ArrayList()
        for (i in 0..14) {
            val index = (Math.random() * 3).toInt()
            val article =
                Article(
                    mTitles[index],
                    mContents[index]
                )
            articleList.add(article)
        }
        mAdapter!!.setDatas(articleList)
    }

    /**
     * 类似于 Activity的 onNewIntent()
     */
    override fun onNewBundle(args: Bundle?) {
        super.onNewBundle(args)
        Toast.makeText(_mActivity, args!!.getString("from"), Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "Fragmentation"
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}