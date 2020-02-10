package io.quee.fragmentation.core

import android.os.Bundle
import androidx.annotation.IntDef
import io.quee.fragmentation.core.anim.FragmentAnimator
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */

interface ISupportFragment {
    fun getSupportDelegate(): SupportFragmentDelegate
    fun extraTransaction(): ExtraTransaction?
    fun enqueueAction(runnable: Runnable?)
    fun post(runnable: Runnable?)
    fun onEnterAnimationEnd(savedInstanceState: Bundle?)
    fun onLazyInitView(savedInstanceState: Bundle?)
    fun onSupportVisible()
    fun onSupportInvisible()
    fun isSupportVisible(): Boolean
    fun onCreateFragmentAnimator(): FragmentAnimator?
    fun getFragmentAnimator(): FragmentAnimator?
    fun setFragmentAnimator(fragmentAnimator: FragmentAnimator?)
    fun setFragmentResult(resultCode: Int, bundle: Bundle?)
    fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?)
    fun onNewBundle(args: Bundle?)
    fun putNewBundle(newBundle: Bundle?)
    fun onBackPressedSupport(): Boolean
    @IntDef(
        STANDARD,
        SINGLETOP,
        SINGLETASK
    )
    @Retention(RetentionPolicy.SOURCE)
    annotation class LaunchMode

    companion object {
        // LaunchMode
        const val STANDARD = 0
        const val SINGLETOP = 1
        const val SINGLETASK = 2
        // ResultCode
        const val RESULT_CANCELED = 0
        const val RESULT_OK = -1
    }
}
