package io.quee.fragmentation.core

import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentationMagician.getActiveFragments
import io.quee.fragmentation.core.ExtraTransaction.ExtraTransactionImpl
import io.quee.fragmentation.core.anim.DefaultVerticalAnimator
import io.quee.fragmentation.core.anim.FragmentAnimator
import io.quee.fragmentation.core.debug.DebugStackDelegate
import io.quee.fragmentation.core.queue.Action

/**
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */


class SupportActivityDelegate(support: ISupportActivity) {
    var mPopMultipleNoAnim = false
    var mFragmentClickable = true
    private val mSupport: ISupportActivity
    private val mActivity: FragmentActivity
    private var mTransactionDelegate: TransactionDelegate? = null
    private var mFragmentAnimator: FragmentAnimator? = null
    private var mDefaultFragmentBackground = 0
    private val mDebugStackDelegate: DebugStackDelegate
    /**
     * Perform some extra transactions.
     * 额外的事务：自定义Tag，添加SharedElement动画，操作非回退栈Fragment
     */
    fun extraTransaction(): ExtraTransaction {
        return ExtraTransactionImpl(
            mSupport as FragmentActivity,
            getTopFragment(),
            getTransactionDelegate(),
            true
        )
    }

    fun onCreate() {
        mTransactionDelegate = getTransactionDelegate()
        mFragmentAnimator = mSupport.onCreateFragmentAnimator()
        mDebugStackDelegate.onCreate(Fragmentation.default.mode)
    }

    fun getTransactionDelegate(): TransactionDelegate {
        if (mTransactionDelegate == null)
            mTransactionDelegate = TransactionDelegate(mSupport)
        return mTransactionDelegate!!
    }

    fun onPostCreate() {
        mDebugStackDelegate.onPostCreate(Fragmentation.default.mode)
    }

    /**
     * Get set global animation copy
     *
     * @return FragmentAnimator
     */
    fun getFragmentAnimator(): FragmentAnimator {
        return mFragmentAnimator!!.copy()
    }

    /**
     * Set all fragments animation.
     * Set global animation in Fragment
     */
    fun setFragmentAnimator(fragmentAnimator: FragmentAnimator) {
        mFragmentAnimator = fragmentAnimator
        for (fragment in getActiveFragments(
            getSupportFragmentManager()
        )) {
            if (fragment is ISupportFragment) {
                val iF = fragment as ISupportFragment
                val delegate = iF.getSupportDelegate()
                if (delegate.mAnimByActivity) {
                    delegate.mFragmentAnimator = fragmentAnimator.copy()
                    if (delegate.mAnimHelper != null) {
                        delegate.mAnimHelper?.notifyChanged(delegate.mFragmentAnimator!!)
                    }
                }
            }
        }
    }

    /**
     * Set all fragments animation.
     * Construct Fragment transition animation
     *
     *
     * If implemented in Activity, the transition animation of all Fragments in Activity is constructed,
     * If implemented in Fragment, the transition animation of the Fragment is constructed, at this time the priority> Activity's onCreateFragmentAnimator ()
     *
     * @return FragmentAnimator object
     */
    fun onCreateFragmentAnimator(): FragmentAnimator {
        return DefaultVerticalAnimator()
    }

    fun getDefaultFragmentBackground(): Int {
        return mDefaultFragmentBackground
    }

    /**
     * When the Fragment root layout does not set the background property,
     * Fragmentation uses Theme's android: windowbackground as the Fragment's background by default.
     * This method can change the Fragment background.
     */
    fun setDefaultFragmentBackground(@DrawableRes backgroundRes: Int) {
        mDefaultFragmentBackground = backgroundRes
    }

    /**
     * Show stack view dialog, used when debugging
     */
    fun showFragmentStackHierarchyView() {
        mDebugStackDelegate.showFragmentStackHierarchyView()
    }

    /**
     * Display stack view log, used when debugging
     */
    fun logFragmentStackHierarchy(TAG: String?) {
        mDebugStackDelegate.logFragmentRecords(TAG)
    }

    /**
     * Causes the Runnable r to be added to the action queue.
     *
     *
     * The runnable will be run after all the previous action has been run.
     *
     *
     * Execute the action after all the previous transactions have been executed
     */
    fun post(runnable: Runnable) {
        mTransactionDelegate?.post(runnable)
    }

    /**
     * It is not recommended to override this method, please use [.onBackPressedSupport] instead
     */
    fun onBackPressed() {
        mTransactionDelegate?.mActionQueue?.enqueue(object : Action(ACTION_BACK) {
            override fun run() {
                if (!mFragmentClickable) {
                    mFragmentClickable = true
                }
                val activeFragment: ISupportFragment? =
                    SupportHelper.getActiveFragment(getSupportFragmentManager())
                if (mTransactionDelegate?.dispatchBackPressedEvent(activeFragment)!!) return
                mSupport.onBackPressedSupport()
            }
        })
    }

    /**
     * 该方法回调时机为,Activity回退栈内Fragment的数量 小于等于1 时,默认finish Activity
     * 请尽量复写该方法,避免复写onBackPress(),以保证SupportFragment内的onBackPressedSupport()回退事件正常执行
     */
    fun onBackPressedSupport() {
        if (getSupportFragmentManager().backStackEntryCount > 1) {
            pop()
        } else {
            ActivityCompat.finishAfterTransition(mActivity)
        }
    }

    fun onDestroy() {
        mDebugStackDelegate.onDestroy()
    }

    fun dispatchTouchEvent(): Boolean { // 防抖动(防止点击速度过快)
        return !mFragmentClickable
    }
    /** */
    /**
     * 加载根Fragment, 即Activity内的第一个Fragment 或 Fragment内的第一个子Fragment
     */
    fun loadRootFragment(containerId: Int, toFragment: ISupportFragment?) {
        loadRootFragment(containerId, toFragment, true, false)
    }

    fun loadRootFragment(
        containerId: Int,
        toFragment: ISupportFragment?,
        addToBackStack: Boolean,
        allowAnimation: Boolean
    ) {
        mTransactionDelegate?.loadRootTransaction(
            getSupportFragmentManager(),
            containerId,
            toFragment!!,
            addToBackStack,
            allowAnimation
        )
    }

    /**
     * 加载多个同级根Fragment,类似Wechat, QQ主页的场景
     */
    fun loadMultipleRootFragment(
        containerId: Int,
        showPosition: Int,
        toFragments: Array<ISupportFragment>
    ) {
        mTransactionDelegate?.loadMultipleRootTransaction(
            getSupportFragmentManager(),
            containerId,
            showPosition,
            toFragments
        )
    }

    /**
     * show一个Fragment,hide其他同栈所有Fragment
     * 使用该方法时，要确保同级栈内无多余的Fragment,(只有通过loadMultipleRootFragment()载入的Fragment)
     *
     *
     * 建议使用更明确的[.showHideFragment]
     *
     * @param showFragment 需要show的Fragment
     */
    fun showHideFragment(showFragment: ISupportFragment?) {
        showHideFragment(showFragment, null)
    }

    /**
     * show一个Fragment,hide一个Fragment ; 主要用于类似微信主页那种 切换tab的情况
     *
     * @param showFragment 需要show的Fragment
     * @param hideFragment 需要hide的Fragment
     */
    fun showHideFragment(
        showFragment: ISupportFragment?,
        hideFragment: ISupportFragment?
    ) {
        mTransactionDelegate?.showHideFragment(
            getSupportFragmentManager(),
            showFragment!!,
            hideFragment
        )
    }

    fun start(toFragment: ISupportFragment?) {
        start(toFragment, ISupportFragment.STANDARD)
    }

    /**
     * @param launchMode Similar to Activity's LaunchMode.
     */
    fun start(toFragment: ISupportFragment?, @ISupportFragment.LaunchMode launchMode: Int) {
        mTransactionDelegate?.dispatchStartTransaction(
            getSupportFragmentManager(),
            getTopFragment(),
            toFragment!!,
            0,
            launchMode,
            TransactionDelegate.TYPE_ADD
        )
    }

    /**
     * Launch an fragment for which you would like a result when it poped.
     */
    fun startForResult(toFragment: ISupportFragment?, requestCode: Int) {
        mTransactionDelegate?.dispatchStartTransaction(
            getSupportFragmentManager(),
            getTopFragment(),
            toFragment!!,
            requestCode,
            ISupportFragment.STANDARD,
            TransactionDelegate.TYPE_ADD_RESULT
        )
    }

    /**
     * Start the target Fragment and pop itself
     */
    fun startWithPop(toFragment: ISupportFragment?) {
        mTransactionDelegate?.startWithPop(
            getSupportFragmentManager(),
            getTopFragment(),
            toFragment!!
        )
    }

    fun startWithPopTo(
        toFragment: ISupportFragment?,
        targetFragmentClass: Class<*>,
        includeTargetFragment: Boolean
    ) {
        mTransactionDelegate?.startWithPopTo(
            getSupportFragmentManager(),
            getTopFragment(),
            toFragment!!,
            targetFragmentClass.name,
            includeTargetFragment
        )
    }

    fun replaceFragment(
        toFragment: ISupportFragment?,
        addToBackStack: Boolean
    ) {
        mTransactionDelegate?.dispatchStartTransaction(
            getSupportFragmentManager(),
            getTopFragment(),
            toFragment!!,
            0,
            ISupportFragment.STANDARD,
            if (addToBackStack) TransactionDelegate.TYPE_REPLACE else TransactionDelegate.TYPE_REPLACE_DONT_BACK
        )
    }

    /**
     * Pop the child fragment.
     */
    fun pop() {
        mTransactionDelegate?.pop(getSupportFragmentManager())
    }
    /**
     * If you want to begin another FragmentTransaction immediately after popTo(), use this method.
     * If you want to perform FragmentTransaction operation immediately after popping, please use this method
     */
    /**
     * Pop the last fragment transition from the manager's fragment
     * back stack.
     *
     *
     * Unstacking to target fragment
     *
     * @param targetFragmentClass   Target fragment
     * @param includeTargetFragment Whether to include the fragment
     */
    @JvmOverloads
    fun popTo(
        targetFragmentClass: Class<*>,
        includeTargetFragment: Boolean,
        afterPopTransactionRunnable: Runnable? = null,
        popAnim: Int = TransactionDelegate.DEFAULT_POPTO_ANIM
    ) {
        mTransactionDelegate?.popTo(
            targetFragmentClass.name,
            includeTargetFragment,
            afterPopTransactionRunnable,
            getSupportFragmentManager(),
            popAnim
        )
    }

    private fun getSupportFragmentManager(): FragmentManager {
        return mActivity.supportFragmentManager
    }

    private fun getTopFragment(): ISupportFragment {
        return SupportHelper.getTopFragment(getSupportFragmentManager())!!
    }

    init {
        if (support !is FragmentActivity) throw RuntimeException("Must extends FragmentActivity/AppCompatActivity")
        mSupport = support
        mActivity = support
        mDebugStackDelegate = DebugStackDelegate(mActivity)
    }
}
