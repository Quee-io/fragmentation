package io.quee.fragmentation.demo.demo_flow.ui.fragment_swipe_back

import androidx.appcompat.widget.Toolbar
import io.quee.fragmentation.demo.R
import io.quee.fragmentation.swipeback.SwipeBackFragment

/**
 * Created by YoKeyword on 16/4/21.
 */
open class BaseSwipeBackFragment : SwipeBackFragment() {
    fun _initToolbar(toolbar: Toolbar) {
        toolbar.title = "SwipeBackActivity's Fragment"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { coreActivity!!.onBackPressed() }
    }
}