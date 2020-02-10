package io.quee.fragmentation.demo.demo_flow.ui.fragment.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.quee.fragmentation.core.anim.DefaultNoAnimator
import io.quee.fragmentation.core.anim.FragmentAnimator
import io.quee.fragmentation.demo.R
import io.quee.fragmentation.demo.demo_flow.adapter.MenuAdapter
import io.quee.fragmentation.demo.demo_flow.base.MySupportFragment
import io.quee.fragmentation.demo.demo_flow.listener.OnItemClickListener
import java.util.*

/**
 * Created by YoKeyword on 16/2/9.
 */
class MenuListFragment : MySupportFragment() {
    private var mRecy: RecyclerView? = null
    private var mAdapter: MenuAdapter? = null
    private var mMenus: List<String>? = null
    private var mCurrentPosition = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        if (args != null) {
            mMenus =
                args.getStringArrayList(ARG_MENUS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View =
            inflater.inflate(R.layout.fragment_list_menu, container, false)
        initView(view)
        return view
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator? {
        return DefaultNoAnimator()
    }

    private fun initView(view: View) {
        mRecy = view.findViewById<View>(R.id.recy) as RecyclerView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val manager = LinearLayoutManager(_mActivity)
        mRecy!!.layoutManager = manager
        mAdapter = MenuAdapter(_mActivity!!)
        mRecy!!.adapter = mAdapter
        mAdapter!!.setDatas(mMenus!!)
        mAdapter!!.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int, view: View) {
                showContent(position)
            }
        })
        if (savedInstanceState != null) {
            mCurrentPosition =
                savedInstanceState.getInt(SAVE_STATE_POSITION)
            mAdapter!!.setItemChecked(mCurrentPosition)
        } else {
            mCurrentPosition = 0
            mAdapter!!.setItemChecked(0)
        }
    }

    private fun showContent(position: Int) {
        if (position == mCurrentPosition) {
            return
        }
        mCurrentPosition = position
        mAdapter!!.setItemChecked(position)
        val fragment: ContentFragment =
            ContentFragment.Companion.newInstance(
                mMenus!![position]
            )
        (parentFragment as ShopFragment?)!!.switchContentFragment(
            fragment
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(
            SAVE_STATE_POSITION,
            mCurrentPosition
        )
    }

    companion object {
        private const val ARG_MENUS = "arg_menus"
        private const val SAVE_STATE_POSITION = "save_state_position"
        fun newInstance(menus: ArrayList<String>?): MenuListFragment {
            val args = Bundle()
            args.putStringArrayList(
                ARG_MENUS,
                menus
            )
            val fragment =
                MenuListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}