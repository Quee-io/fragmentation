package io.quee.fragmentation.demo.demo_flow.ui.fragment.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.quee.fragmentation.demo.R
import io.quee.fragmentation.demo.demo_flow.adapter.PagerAdapter
import io.quee.fragmentation.demo.demo_flow.base.MySupportFragment
import io.quee.fragmentation.demo.demo_flow.listener.OnItemClickListener
import io.quee.fragmentation.demo.demo_flow.ui.fragment.CycleFragment
import java.util.*

class PagerChildFragment : MySupportFragment() {
    private var mFrom = 0
    private var mRecy: RecyclerView? = null
    private var mAdapter: PagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        if (args != null) {
            mFrom = args.getInt(ARG_FROM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_pager, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        mRecy = view.findViewById<View>(R.id.recy) as RecyclerView
        mAdapter = PagerAdapter(_mActivity)
        val manager = LinearLayoutManager(_mActivity)
        mRecy!!.layoutManager = manager
        mRecy!!.adapter = mAdapter
        mAdapter!!.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int, view: View) {
                if (parentFragment is DiscoverFragment) {
                    (parentFragment as DiscoverFragment).start(
                        CycleFragment.newInstance(
                            1
                        )
                    )
                }
            }
        })
        mRecy!!.post {
            // Init Datas
            val items: MutableList<String> =
                ArrayList()
            for (i in 0..19) {
                var item: String
                item = if (mFrom == 0) {
                    getString(R.string.recommend) + " " + i
                } else if (mFrom == 1) {
                    getString(R.string.hot) + " " + i
                } else {
                    getString(R.string.favorite) + " " + i
                }
                items.add(item)
            }
            mAdapter!!.setDatas(items)
        }
    }

    companion object {
        private const val ARG_FROM = "arg_from"
        fun newInstance(from: Int): PagerChildFragment {
            val args = Bundle()
            args.putInt(ARG_FROM, from)
            val fragment = PagerChildFragment()
            fragment.arguments = args
            return fragment
        }
    }
}