package io.quee.fragmentation.demo.demo_flow

import android.os.Bundle
import io.quee.fragmentation.SwipeBackLayout
import io.quee.fragmentation.core.anim.DefaultHorizontalAnimator
import io.quee.fragmentation.core.anim.FragmentAnimator
import io.quee.fragmentation.demo.R
import io.quee.fragmentation.swipeback.SwipeBackActivity

/**
 * Created by YoKeyword on 16/4/19.
 */
class SwipeBackSampleActivity : SwipeBackActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swipe_back)
        swipeBackLayout!!.setEdgeOrientation(SwipeBackLayout.EDGE_ALL)
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator? {
        return DefaultHorizontalAnimator()
    }
}