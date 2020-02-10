package io.quee.fragmentation.swipeback

import android.os.Bundle
import io.quee.fragmentation.CoreActivity
import io.quee.fragmentation.SwipeBackLayout
import io.quee.fragmentation.SwipeBackLayout.EdgeLevel
import io.quee.fragmentation.swipeback.core.ISwipeBackActivity
import io.quee.fragmentation.swipeback.core.SwipeBackActivityDelegate

/**
 * You can also refer to [SwipeBackActivity] to implement YourSwipeBackActivity
 * (extends Activity and impl [io.quee.fragmentation.core.ISupportActivity])
 *
 *
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */
open class SwipeBackActivity : CoreActivity(), ISwipeBackActivity {
    private val swipeBackActivityDelegate = SwipeBackActivityDelegate(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        swipeBackActivityDelegate.onCreate(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        swipeBackActivityDelegate.onPostCreate(savedInstanceState)
    }

    override val swipeBackLayout: SwipeBackLayout?
        get() = swipeBackActivityDelegate.swipeBackLayout

    /**
     * 是否可滑动
     *
     * @param enable
     */
    override fun setSwipeBackEnable(enable: Boolean) {
        swipeBackActivityDelegate.setSwipeBackEnable(enable)
    }

    override fun setEdgeLevel(edgeLevel: EdgeLevel?) {
        swipeBackActivityDelegate.setEdgeLevel(edgeLevel)
    }

    override fun setEdgeLevel(widthPixel: Int) {
        swipeBackActivityDelegate.setEdgeLevel(widthPixel)
    }

    /**
     * 限制SwipeBack的条件,默认栈内Fragment数 <= 1时 , 优先滑动退出Activity , 而不是Fragment
     *
     * @return true: Activity优先滑动退出;  false: Fragment优先滑动退出
     */
    override fun swipeBackPriority(): Boolean {
        return swipeBackActivityDelegate.swipeBackPriority()
    }
}