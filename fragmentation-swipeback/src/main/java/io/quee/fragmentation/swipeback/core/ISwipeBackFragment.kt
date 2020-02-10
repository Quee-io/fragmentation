package io.quee.fragmentation.swipeback.core

import android.view.View
import androidx.annotation.FloatRange
import io.quee.fragmentation.SwipeBackLayout
import io.quee.fragmentation.SwipeBackLayout.EdgeLevel

/**
 *
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */
interface ISwipeBackFragment {
    fun attachToSwipeBack(view: View?): View?
    val swipeBackLayout: SwipeBackLayout?
    fun setSwipeBackEnable(enable: Boolean)
    fun setEdgeLevel(edgeLevel: EdgeLevel?)
    fun setEdgeLevel(widthPixel: Int)
    /**
     * Set the offset of the parallax slip.
     */
    fun setParallaxOffset(
        @FloatRange(
            from = 0.0,
            to = 1.0
        ) offset: Float
    )
}