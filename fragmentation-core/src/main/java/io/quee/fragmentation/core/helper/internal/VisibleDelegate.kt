package io.quee.fragmentation.core.helper.internal

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentationMagician.getActiveFragments
import io.quee.fragmentation.core.ISupportFragment

/**
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */
class VisibleDelegate(fragment: ISupportFragment) {
    // SupportVisible相关
    var isSupportVisible = false
        private set
    private var mNeedDispatch = true
    private var mInvisibleWhenLeave = false
    private var mIsFirstVisible = true
    private var mFirstCreateViewCompatReplace = true
    private var mAbortInitVisible = false
    private var taskDispatchSupportVisible: Runnable? = null
    private var mHandler: Handler? = null
    private var mSaveInstanceState: Bundle? = null

    private val mSupportF: ISupportFragment
    private val mFragment: Fragment

    fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            mSaveInstanceState = savedInstanceState
            // setUserVisibleHint() may be called before onCreate()
            mInvisibleWhenLeave =
                savedInstanceState.getBoolean(FRAGMENTATION_STATE_SAVE_IS_INVISIBLE_WHEN_LEAVE)
            mFirstCreateViewCompatReplace =
                savedInstanceState.getBoolean(FRAGMENTATION_STATE_SAVE_COMPAT_REPLACE)
        }
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(
            FRAGMENTATION_STATE_SAVE_IS_INVISIBLE_WHEN_LEAVE,
            mInvisibleWhenLeave
        )
        outState.putBoolean(
            FRAGMENTATION_STATE_SAVE_COMPAT_REPLACE,
            mFirstCreateViewCompatReplace
        )
    }

    fun onActivityCreated(savedInstanceState: Bundle?) {
        if (!mFirstCreateViewCompatReplace && mFragment.tag != null && mFragment.tag!!.startsWith(
                "android:switcher:"
            )
        ) {
            return
        }
        if (mFirstCreateViewCompatReplace) {
            mFirstCreateViewCompatReplace = false
        }
        initVisible()
    }

    private fun initVisible() {
        if (!mInvisibleWhenLeave && !mFragment.isHidden && mFragment.userVisibleHint) {
            if (mFragment.parentFragment != null && isFragmentVisible(mFragment.parentFragment)
                || mFragment.parentFragment == null
            ) {
                mNeedDispatch = false
                safeDispatchUserVisibleHint(true)
            }
        }
    }

    fun onResume() {
        if (!mIsFirstVisible) {
            if (!isSupportVisible && !mInvisibleWhenLeave && isFragmentVisible(mFragment)) {
                mNeedDispatch = false
                dispatchSupportVisible(true)
            }
        } else {
            if (mAbortInitVisible) {
                mAbortInitVisible = false
                initVisible()
            }
        }
    }

    fun onPause() {
        if (taskDispatchSupportVisible != null) {
            handler.removeCallbacks(taskDispatchSupportVisible!!)
            mAbortInitVisible = true
            return
        }
        if (isSupportVisible && isFragmentVisible(mFragment)) {
            mNeedDispatch = false
            mInvisibleWhenLeave = false
            dispatchSupportVisible(false)
        } else {
            mInvisibleWhenLeave = true
        }
    }

    fun onHiddenChanged(hidden: Boolean) {
        if (!hidden && !mFragment.isResumed) { //if fragment is shown but not resumed, ignore...
            onFragmentShownWhenNotResumed()
            return
        }
        if (hidden) {
            safeDispatchUserVisibleHint(false)
        } else {
            enqueueDispatchVisible()
        }
    }

    private fun onFragmentShownWhenNotResumed() {
        mInvisibleWhenLeave = false
        dispatchChildOnFragmentShownWhenNotResumed()
    }

    private fun dispatchChildOnFragmentShownWhenNotResumed() {
        val fragmentManager =
            mFragment.childFragmentManager
        val childFragments =
            getActiveFragments(fragmentManager)
        if (childFragments != null) {
            for (child in childFragments) {
                if (child is ISupportFragment && !child.isHidden && child.userVisibleHint) {
                    (child as ISupportFragment).getSupportDelegate().getVisibleDelegate()
                        .onFragmentShownWhenNotResumed()
                }
            }
        }
    }

    fun onDestroyView() {
        mIsFirstVisible = true
    }

    fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (mFragment.isResumed || !mFragment.isAdded && isVisibleToUser) {
            if (!isSupportVisible && isVisibleToUser) {
                safeDispatchUserVisibleHint(true)
            } else if (isSupportVisible && !isVisibleToUser) {
                dispatchSupportVisible(false)
            }
        }
    }

    private fun safeDispatchUserVisibleHint(visible: Boolean) {
        if (mIsFirstVisible) {
            if (!visible) return
            enqueueDispatchVisible()
        } else {
            dispatchSupportVisible(visible)
        }
    }

    private fun enqueueDispatchVisible() {
        taskDispatchSupportVisible = Runnable {
            taskDispatchSupportVisible = null
            dispatchSupportVisible(true)
        }
        handler.post(taskDispatchSupportVisible!!)
    }

    private fun dispatchSupportVisible(visible: Boolean) {
        if (visible && isParentInvisible) return
        if (isSupportVisible == visible) {
            mNeedDispatch = true
            return
        }
        isSupportVisible = visible
        if (visible) {
            if (checkAddState()) return
            mSupportF.onSupportVisible()
            if (mIsFirstVisible) {
                mIsFirstVisible = false
                mSupportF.onLazyInitView(mSaveInstanceState)
            }
            dispatchChild(true)
        } else {
            dispatchChild(false)
            mSupportF.onSupportInvisible()
        }
    }

    private fun dispatchChild(visible: Boolean) {
        if (!mNeedDispatch) {
            mNeedDispatch = true
        } else {
            if (checkAddState()) return
            val fragmentManager =
                mFragment.childFragmentManager
            val childFragments =
                getActiveFragments(fragmentManager)
            if (childFragments != null) {
                for (child in childFragments) {
                    if (child is ISupportFragment && !child.isHidden && child.userVisibleHint) {
                        (child as ISupportFragment).getSupportDelegate().getVisibleDelegate()
                            .dispatchSupportVisible(visible)
                    }
                }
            }
        }
    }

    private val isParentInvisible: Boolean
        private get() {
            val parentFragment = mFragment.parentFragment
            return if (parentFragment is ISupportFragment) {
                !(parentFragment as ISupportFragment).isSupportVisible()
            } else parentFragment != null && !parentFragment.isVisible
        }

    private fun checkAddState(): Boolean {
        if (!mFragment.isAdded) {
            isSupportVisible = !isSupportVisible
            return true
        }
        return false
    }

    private fun isFragmentVisible(fragment: Fragment?): Boolean {
        return !fragment!!.isHidden && fragment.userVisibleHint
    }

    private val handler: Handler
        private get() {
            if (mHandler == null) {
                mHandler = Handler(Looper.getMainLooper())
            }
            return mHandler!!
        }

    companion object {
        private const val FRAGMENTATION_STATE_SAVE_IS_INVISIBLE_WHEN_LEAVE =
            "fragmentation_invisible_when_leave"
        private const val FRAGMENTATION_STATE_SAVE_COMPAT_REPLACE =
            "fragmentation_compat_replace"
    }

    init {
        mSupportF = fragment
        mFragment = fragment as Fragment
    }
}