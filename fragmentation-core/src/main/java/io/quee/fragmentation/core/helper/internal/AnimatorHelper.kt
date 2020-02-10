package io.quee.fragmentation.core.helper.internal

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import io.quee.fragmentation.core.R
import io.quee.fragmentation.core.anim.FragmentAnimator

/**
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */
class AnimatorHelper(
    private val context: Context,
    var fragmentAnimator: FragmentAnimator?
) {
    var enterAnim: Animation? = null
    var exitAnim: Animation? = null
    var popEnterAnim: Animation? = null
    var popExitAnim: Animation? = null
    var noneAnim: Animation? = null
        get() {
            if (field == null) {
                field = AnimationUtils.loadAnimation(context, R.anim.no_anim)
            }
            return field
        }
        private set
    var noneAnimFixed: Animation? = null
        get() {
            if (field == null) {
                field = object : Animation() {}
            }
            return field
        }
        private set

    fun notifyChanged(fragmentAnimator: FragmentAnimator) {
        this.fragmentAnimator = fragmentAnimator
        enterAnim = initEnterAnim()
        exitAnim = initExitAnim()
        popEnterAnim = initPopEnterAnim()
        popExitAnim = initPopExitAnim()
    }

    fun compatChildFragmentExitAnim(fragment: Fragment): Animation? {
        if (fragment.tag != null && fragment.tag!!.startsWith("android:switcher:") && fragment.userVisibleHint ||
            fragment.parentFragment != null && fragment.parentFragment!!.isRemoving && !fragment.isHidden
        ) {
            val animation: Animation = object : Animation() {}
            animation.duration = exitAnim!!.duration
            return animation
        }
        return null
    }

    private fun initEnterAnim(): Animation {
        return if (fragmentAnimator?.enter == 0) {
            AnimationUtils.loadAnimation(context, R.anim.no_anim)
        } else {
            AnimationUtils.loadAnimation(context, fragmentAnimator?.enter!!)
        }
    }

    private fun initExitAnim(): Animation {
        return if (fragmentAnimator?.exit == 0) {
            AnimationUtils.loadAnimation(context, R.anim.no_anim)
        } else {
            AnimationUtils.loadAnimation(context, fragmentAnimator?.exit!!)
        }
    }

    private fun initPopEnterAnim(): Animation {
        return if (fragmentAnimator?.popEnter == 0) {
            AnimationUtils.loadAnimation(context, R.anim.no_anim)
        } else {
            AnimationUtils.loadAnimation(context, fragmentAnimator?.popEnter!!)
        }
    }

    private fun initPopExitAnim(): Animation {
        return if (fragmentAnimator?.popExit == 0) {
            AnimationUtils.loadAnimation(context, R.anim.no_anim)
        } else {
            AnimationUtils.loadAnimation(context, fragmentAnimator?.popExit!!)
        }
    }

    init {
        notifyChanged(fragmentAnimator!!)
    }
}