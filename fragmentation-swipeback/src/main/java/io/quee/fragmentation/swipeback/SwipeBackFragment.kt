package io.quee.fragmentation.swipeback

import android.os.Bundle
import android.view.View
import androidx.annotation.FloatRange
import io.quee.fragmentation.CoreFragment
import io.quee.fragmentation.SwipeBackLayout
import io.quee.fragmentation.SwipeBackLayout.EdgeLevel
import io.quee.fragmentation.swipeback.core.ISwipeBackFragment
import io.quee.fragmentation.swipeback.core.SwipeBackFragmentDelegate

/**
 * You can also refer to [SwipeBackFragment] to implement YourSwipeBackFragment
 * (extends Fragment and impl [io.quee.fragmentation.core.ISupportFragment])
 *
 *
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */
open class SwipeBackFragment : CoreFragment(), ISwipeBackFragment {
    val mDelegate = SwipeBackFragmentDelegate(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDelegate.onCreate(savedInstanceState)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        mDelegate.onViewCreated(view, savedInstanceState)
    }

    override fun attachToSwipeBack(view: View?): View? {
        return mDelegate.attachToSwipeBack(view)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        mDelegate.onHiddenChanged(hidden)
    }

    override val swipeBackLayout: SwipeBackLayout?
        get() = mDelegate.swipeBackLayout

    /**
     * 是否可滑动
     *
     * @param enable
     */
    override fun setSwipeBackEnable(enable: Boolean) {
        mDelegate.setSwipeBackEnable(enable)
    }

    override fun setEdgeLevel(edgeLevel: EdgeLevel?) {
        mDelegate.setEdgeLevel(edgeLevel)
    }

    override fun setEdgeLevel(widthPixel: Int) {
        mDelegate.setEdgeLevel(widthPixel)
    }

    /**
     * Set the offset of the parallax slip.
     */
    override fun setParallaxOffset(
        @FloatRange(
            from = 0.0,
            to = 1.0
        ) offset: Float
    ) {
        mDelegate.setParallaxOffset(offset)
    }

    override fun onDestroyView() {
        mDelegate.onDestroyView()
        super.onDestroyView()
    }
}