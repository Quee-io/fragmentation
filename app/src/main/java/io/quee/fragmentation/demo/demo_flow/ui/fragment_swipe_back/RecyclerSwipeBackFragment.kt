package io.quee.fragmentation.demo.demo_flow.ui.fragment_swipe_back

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.quee.fragmentation.demo.R
import io.quee.fragmentation.demo.demo_flow.adapter.PagerAdapter
import io.quee.fragmentation.demo.demo_flow.listener.OnItemClickListener
import java.util.*

class RecyclerSwipeBackFragment : BaseSwipeBackFragment() {
    private var mToolbar: Toolbar? = null
    private var mRecy: RecyclerView? = null
    private var mAdapter: PagerAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View =
            inflater.inflate(R.layout.fragment_swipe_back_recy, container, false)
        initView(view)
        return attachToSwipeBack(view)
    }

    private fun initView(view: View) {
        mRecy = view.findViewById<View>(R.id.recy) as RecyclerView
        mToolbar =
            view.findViewById<View>(R.id.toolbar) as Toolbar
        _initToolbar(mToolbar!!)
        mAdapter = PagerAdapter(coreActivity)
        val manager = LinearLayoutManager(coreActivity)
        mRecy!!.layoutManager = manager
        mRecy!!.adapter = mAdapter
        mAdapter!!.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int, view: View) {
                start(FirstSwipeBackFragment.newInstance())
            }
        })
        // Init Datas
        val items: MutableList<String> =
            ArrayList()
        for (i in 0..19) {
            var item: String
            item = getString(R.string.favorite) + " " + i
            items.add(item)
        }
        mAdapter!!.setDatas(items)
    }

    companion object {
        private const val ARG_FROM = "arg_from"
        fun newInstance(): RecyclerSwipeBackFragment {
            return RecyclerSwipeBackFragment()
        }
    }
}