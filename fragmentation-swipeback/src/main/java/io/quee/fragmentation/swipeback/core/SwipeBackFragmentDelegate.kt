package io.quee.fragmentation.swipeback.core

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.FloatRange
import androidx.fragment.app.Fragment
import io.quee.fragmentation.SwipeBackLayout
import io.quee.fragmentation.SwipeBackLayout.EdgeLevel
import io.quee.fragmentation.core.ISupportFragment

/**
 *
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */
class SwipeBackFragmentDelegate(swipeBackFragment: ISwipeBackFragment?) {
    private val mFragment: Fragment
    private val mSupport: ISupportFragment
    var swipeBackLayout: SwipeBackLayout? = null
        private set

    fun onCreate(savedInstanceState: Bundle?) {
        onFragmentCreate()
    }

    fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        if (view is SwipeBackLayout) {
            val childView = view.getChildAt(0)
            mSupport.getSupportDelegate().setBackground(childView)
        } else {
            mSupport.getSupportDelegate().setBackground(view!!)
        }
    }

    fun attachToSwipeBack(view: View?): View? {
        swipeBackLayout!!.attachToFragment(mSupport, view)
        return swipeBackLayout
    }

    fun setEdgeLevel(edgeLevel: EdgeLevel?) {
        swipeBackLayout!!.setEdgeLevel(edgeLevel)
    }

    fun setEdgeLevel(widthPixel: Int) {
        swipeBackLayout!!.setEdgeLevel(widthPixel)
    }

    fun onHiddenChanged(hidden: Boolean) {
        if (hidden && swipeBackLayout != null) {
            swipeBackLayout!!.hiddenFragment()
        }
    }

    fun setSwipeBackEnable(enable: Boolean) {
        swipeBackLayout!!.setEnableGesture(enable)
    }

    /**
     * Set the offset of the parallax slip.
     */
    fun setParallaxOffset(@FloatRange(from = 0.0, to = 1.0) offset: Float) {
        swipeBackLayout!!.setParallaxOffset(offset)
    }

    fun onDestroyView() {
        swipeBackLayout!!.internalCallOnDestroyView()
    }

    private fun onFragmentCreate() {
        if (mFragment.context == null) return
        swipeBackLayout = SwipeBackLayout(mFragment.context)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        swipeBackLayout!!.layoutParams = params
        swipeBackLayout!!.setBackgroundColor(Color.TRANSPARENT)
    }

    init {
        if (swipeBackFragment !is Fragment || swipeBackFragment !is ISupportFragment) throw RuntimeException(
            "Must extends Fragment and implements ISupportFragment!"
        )
        mFragment = swipeBackFragment
        mSupport = swipeBackFragment
    }
}