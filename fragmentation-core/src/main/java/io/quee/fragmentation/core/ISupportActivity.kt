package io.quee.fragmentation.core

import android.view.MotionEvent
import io.quee.fragmentation.core.anim.FragmentAnimator

/**
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */
interface ISupportActivity {
    fun getSupportDelegate(): SupportActivityDelegate
    fun extraTransaction(): ExtraTransaction?
    fun getFragmentAnimator(): FragmentAnimator?
    fun setFragmentAnimator(fragmentAnimator: FragmentAnimator?)
    fun onCreateFragmentAnimator(): FragmentAnimator?
    fun post(runnable: Runnable?)
    fun onBackPressed()
    fun onBackPressedSupport()
    fun dispatchTouchEvent(ev: MotionEvent): Boolean
}
