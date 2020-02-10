package io.quee.fragmentation.swipeback.core

import io.quee.fragmentation.SwipeBackLayout
import io.quee.fragmentation.SwipeBackLayout.EdgeLevel

/**
 *
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */
interface ISwipeBackActivity {
    val swipeBackLayout: SwipeBackLayout?
    fun setSwipeBackEnable(enable: Boolean)
    fun setEdgeLevel(edgeLevel: EdgeLevel?)
    fun setEdgeLevel(widthPixel: Int)
    /**
     * 限制SwipeBack的条件,默认栈内Fragment数 <= 1时 , 优先滑动退出Activity , 而不是Fragment
     *
     * @return true: Activity可以滑动退出, 并且总是优先;  false: Fragment优先滑动退出
     */
    fun swipeBackPriority(): Boolean
}