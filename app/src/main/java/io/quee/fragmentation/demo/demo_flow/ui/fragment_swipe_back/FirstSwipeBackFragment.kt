package io.quee.fragmentation.demo.demo_flow.ui.fragment_swipe_back

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import io.quee.fragmentation.demo.R

/**
 * Created by YoKeyword on 16/4/19.
 */
class FirstSwipeBackFragment : BaseSwipeBackFragment() {
    private var mToolbar: Toolbar? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View =
            inflater.inflate(R.layout.fragment_swipe_back_first, container, false)
        mToolbar =
            view.findViewById<View>(R.id.toolbar) as Toolbar
        mToolbar!!.title = "SwipeBackActivity's Fragment"
        _initToolbar(mToolbar!!)
        view.findViewById<View>(R.id.btn)
            .setOnClickListener { start(RecyclerSwipeBackFragment.Companion.newInstance()) }
        return attachToSwipeBack(view)
    }

    companion object {
        fun newInstance(): FirstSwipeBackFragment {
            val args = Bundle()
            val fragment = FirstSwipeBackFragment()
            fragment.arguments = args
            return fragment
        }
    }
}