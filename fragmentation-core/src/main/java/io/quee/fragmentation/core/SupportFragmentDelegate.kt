package io.quee.fragmentation.core

import android.R
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import io.quee.fragmentation.core.ExtraTransaction.ExtraTransactionImpl
import io.quee.fragmentation.core.ISupportFragment.LaunchMode
import io.quee.fragmentation.core.anim.FragmentAnimator
import io.quee.fragmentation.core.helper.internal.AnimatorHelper
import io.quee.fragmentation.core.helper.internal.ResultRecord
import io.quee.fragmentation.core.helper.internal.TransactionRecord
import io.quee.fragmentation.core.helper.internal.VisibleDelegate

/**
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */

class SupportFragmentDelegate(support: ISupportFragment?) {
    protected lateinit var _mActivity: FragmentActivity
    var mFragmentAnimator: FragmentAnimator? = null
    var mAnimHelper: AnimatorHelper? = null
    var mLockAnim = false
    var mContainerId = 0
    var mTransactionRecord: TransactionRecord? = null
    var mNewBundle: Bundle? = null
    var mAnimByActivity = true
    var mEnterAnimListener: EnterAnimListener? = null
    private var mRootStatus = STATUS_UN_ROOT
    private var mIsSharedElement = false
    private var mCustomEnterAnim = Int.MIN_VALUE
    private var mCustomExitAnim = Int.MIN_VALUE
    private var mCustomPopExitAnim = Int.MIN_VALUE
    private var mHandler: Handler? = null
    private var mFirstCreateView = true
    private var mReplaceMode = false
    private var mIsHidden = true
    private var mTransactionDelegate: TransactionDelegate? = null
    // SupportVisible
    private var mVisibleDelegate: VisibleDelegate? = null
    private var mSaveInstanceState: Bundle? = null
    private lateinit var mSupportF: ISupportFragment
    private lateinit var mFragment: Fragment
    private var mSupport: ISupportActivity? = null
    private var mRootViewClickable = false

    private val mNotifyEnterAnimEndRunnable = Runnable {
        mSupportF.onEnterAnimationEnd(mSaveInstanceState)
        if (mRootViewClickable) return@Runnable
        val view = mFragment.view ?: return@Runnable
        val preFragment: ISupportFragment =
            SupportHelper.getPreFragment(mFragment) ?: return@Runnable
        val prePopExitDuration =
            preFragment.getSupportDelegate().getPopExitAnimDuration()
        val enterDuration = getEnterAnimDuration()
        mHandler!!.postDelayed({ view.isClickable = false }, prePopExitDuration - enterDuration)
    }

    /**
     * Perform some extra transactions.
     * 额外的事务：自定义Tag，添加SharedElement动画，操作非回退栈Fragment
     */
    fun extraTransaction(): ExtraTransaction {
        if (mTransactionDelegate == null) throw RuntimeException(mFragment.javaClass.getSimpleName() + " not attach!")
        return ExtraTransactionImpl(
            mSupport as FragmentActivity,
            mSupportF,
            mTransactionDelegate!!,
            false
        )
    }

    fun onAttach(activity: Activity) {
        if (activity is ISupportActivity) {
            mSupport = activity
            _mActivity = activity as FragmentActivity
            mTransactionDelegate = mSupport!!.getSupportDelegate().getTransactionDelegate()
        } else {
            throw RuntimeException(activity.javaClass.simpleName + " must impl ISupportActivity!")
        }
    }

    fun onCreate(savedInstanceState: Bundle?) {
        getVisibleDelegate().onCreate(savedInstanceState)
        val bundle = mFragment.arguments
        if (bundle != null) {
            mRootStatus = bundle.getInt(
                TransactionDelegate.FRAGMENTATION_ARG_ROOT_STATUS,
                STATUS_UN_ROOT
            )
            mIsSharedElement =
                bundle.getBoolean(TransactionDelegate.FRAGMENTATION_ARG_IS_SHARED_ELEMENT, false)
            mContainerId = bundle.getInt(TransactionDelegate.FRAGMENTATION_ARG_CONTAINER)
            mReplaceMode = bundle.getBoolean(TransactionDelegate.FRAGMENTATION_ARG_REPLACE, false)
            mCustomEnterAnim = bundle.getInt(
                TransactionDelegate.FRAGMENTATION_ARG_CUSTOM_ENTER_ANIM,
                Int.MIN_VALUE
            )
            mCustomExitAnim = bundle.getInt(
                TransactionDelegate.FRAGMENTATION_ARG_CUSTOM_EXIT_ANIM,
                Int.MIN_VALUE
            )
            mCustomPopExitAnim = bundle.getInt(
                TransactionDelegate.FRAGMENTATION_ARG_CUSTOM_POP_EXIT_ANIM,
                Int.MIN_VALUE
            )
        }
        if (savedInstanceState == null) {
            getFragmentAnimator()
        } else {
            savedInstanceState.classLoader = javaClass.classLoader
            mSaveInstanceState = savedInstanceState
            mFragmentAnimator =
                savedInstanceState.getParcelable(TransactionDelegate.FRAGMENTATION_STATE_SAVE_ANIMATOR)!!
            mIsHidden =
                savedInstanceState.getBoolean(TransactionDelegate.FRAGMENTATION_STATE_SAVE_IS_HIDDEN)
            mContainerId =
                savedInstanceState.getInt(TransactionDelegate.FRAGMENTATION_ARG_CONTAINER)
        }
        mAnimHelper = AnimatorHelper(_mActivity.applicationContext, mFragmentAnimator)
        val enter = getEnterAnim() ?: return
        getEnterAnim()!!.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                mSupport!!.getSupportDelegate().mFragmentClickable = false // 开启防抖动
                mHandler!!.postDelayed({
                    mSupport!!.getSupportDelegate().mFragmentClickable = true
                }, enter.duration)
            }

            override fun onAnimationEnd(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        if (mSupport!!.getSupportDelegate().mPopMultipleNoAnim || mLockAnim) {
            return if (transit == FragmentTransaction.TRANSIT_FRAGMENT_CLOSE && enter) {
                mAnimHelper?.noneAnimFixed
            } else mAnimHelper?.noneAnim
        }
        return if (transit == FragmentTransaction.TRANSIT_FRAGMENT_OPEN) {
            if (enter) {
                val enterAnim: Animation
                if (mRootStatus == STATUS_ROOT_ANIM_DISABLE) {
                    enterAnim = mAnimHelper?.noneAnim!!
                } else {
                    enterAnim = mAnimHelper?.enterAnim!!
                    fixAnimationListener(enterAnim)
                }
                enterAnim
            } else {
                mAnimHelper?.popExitAnim
            }
        } else if (transit == FragmentTransaction.TRANSIT_FRAGMENT_CLOSE) {
            if (enter) mAnimHelper?.popEnterAnim else mAnimHelper?.exitAnim
        } else {
            if (mIsSharedElement && enter) {
                compatSharedElements()
            }
            if (!enter) {
                mAnimHelper?.compatChildFragmentExitAnim(mFragment)
            } else null
        }
    }

    fun onSaveInstanceState(outState: Bundle) {
        getVisibleDelegate().onSaveInstanceState(outState)
        outState.putParcelable(
            TransactionDelegate.FRAGMENTATION_STATE_SAVE_ANIMATOR,
            mFragmentAnimator
        )
        outState.putBoolean(
            TransactionDelegate.FRAGMENTATION_STATE_SAVE_IS_HIDDEN,
            mFragment.isHidden
        )
        outState.putInt(TransactionDelegate.FRAGMENTATION_ARG_CONTAINER, mContainerId)
    }

    fun onActivityCreated(savedInstanceState: Bundle?) {
        getVisibleDelegate().onActivityCreated(savedInstanceState)
        val view = mFragment.view
        if (view != null) {
            mRootViewClickable = view.isClickable
            view.isClickable = true
            setBackground(view)
        }
        if (savedInstanceState != null || mRootStatus == STATUS_ROOT_ANIM_DISABLE || mFragment.tag != null && mFragment.tag!!.startsWith(
                "android:switcher:"
            )
            || mReplaceMode && !mFirstCreateView
        ) {
            notifyEnterAnimEnd()
        } else if (mCustomEnterAnim != Int.MIN_VALUE) {
            fixAnimationListener(
                if (mCustomEnterAnim == 0) mAnimHelper?.noneAnim!! else AnimationUtils.loadAnimation(
                    _mActivity,
                    mCustomEnterAnim
                )
            )
        }
        if (mFirstCreateView) {
            mFirstCreateView = false
        }
    }

    fun onResume() {
        getVisibleDelegate().onResume()
    }

    fun onPause() {
        getVisibleDelegate().onPause()
    }

    fun onDestroyView() {
        mSupport!!.getSupportDelegate().mFragmentClickable = true
        getVisibleDelegate().onDestroyView()
        getHandler().removeCallbacks(mNotifyEnterAnimEndRunnable)
    }

    fun onDestroy() {
        mTransactionDelegate?.handleResultRecord(mFragment)
    }

    fun onHiddenChanged(hidden: Boolean) {
        getVisibleDelegate().onHiddenChanged(hidden)
    }

    fun setUserVisibleHint(isVisibleToUser: Boolean) {
        getVisibleDelegate().setUserVisibleHint(isVisibleToUser)
    }

    /**
     * Causes the Runnable r to be added to the action queue.
     *
     *
     * The runnable will be run after all the previous action has been run.
     *
     *
     * 前面的事务全部执行后 执行该Action
     *
     */
    @Deprecated("Use {@link #post(Runnable)} instead.", ReplaceWith("post(runnable)"))
    fun enqueueAction(runnable: Runnable) {
        post(runnable)
    }

    /**
     * Causes the Runnable r to be added to the action queue.
     *
     *
     * The runnable will be run after all the previous action has been run.
     *
     *
     * 前面的事务全部执行后 执行该Action
     */
    fun post(runnable: Runnable) {
        mTransactionDelegate?.post(runnable)
    }

    /**
     * Called when the enter-animation end.
     * 入栈动画 结束时,回调
     */
    fun onEnterAnimationEnd(savedInstanceState: Bundle?) {}

    /**
     * Lazy initial，Called when fragment is first visible.
     *
     *
     * 同级下的 懒加载 ＋ ViewPager下的懒加载  的结合回调方法
     */
    fun onLazyInitView(savedInstanceState: Bundle?) {}

    /**
     * Called when the fragment is visible.
     *
     *
     * 当Fragment对用户可见时回调
     *
     *
     * Is the combination of  [onHiddenChanged() + onResume()/onPause() + setUserVisibleHint()]
     */
    fun onSupportVisible() {}

    /**
     * Called when the fragment is invivible.
     *
     *
     * Is the combination of  [onHiddenChanged() + onResume()/onPause() + setUserVisibleHint()]
     */
    fun onSupportInvisible() {}

    /**
     * Return true if the fragment has been supportVisible.
     */
    fun isSupportVisible(): Boolean {
        return getVisibleDelegate().isSupportVisible
    }

    /**
     * Set fragment animation with a higher priority than the ISupportActivity
     * 设定当前Fragmemt动画,优先级比在ISupportActivity里高
     */
    fun onCreateFragmentAnimator(): FragmentAnimator? {
        return mSupport!!.getFragmentAnimator()
    }

    /**
     * 获取设置的全局动画
     *
     * @return FragmentAnimator
     */
    fun getFragmentAnimator(): FragmentAnimator? {
        if (mSupport == null) throw RuntimeException("Fragment has not been attached to Activity!")
        if (mFragmentAnimator == null) {
            mFragmentAnimator = mSupportF.onCreateFragmentAnimator()!!
            if (mFragmentAnimator == null) {
                mFragmentAnimator = mSupport!!.getFragmentAnimator()!!
            }
        }
        return mFragmentAnimator
    }

    /**
     * Set the fragment animation.
     */
    fun setFragmentAnimator(fragmentAnimator: FragmentAnimator) {
        mFragmentAnimator = fragmentAnimator
        if (mAnimHelper != null) {
            mAnimHelper?.notifyChanged(fragmentAnimator)
        }
        mAnimByActivity = false
    }

    /**
     * 类似 [Activity.setResult]
     *
     *
     * Similar to [Activity.setResult]
     *
     * @see .startForResult
     */
    fun setFragmentResult(resultCode: Int, bundle: Bundle?) {
        val args = mFragment.arguments
        if (args == null || !args.containsKey(TransactionDelegate.FRAGMENTATION_ARG_RESULT_RECORD)) {
            return
        }
        val resultRecord: ResultRecord =
            args.getParcelable(TransactionDelegate.FRAGMENTATION_ARG_RESULT_RECORD)!!
        resultRecord.resultCode = resultCode
        resultRecord.resultBundle = bundle!!
    }

    /**
     * 类似  [Activity.onActivityResult]
     *
     *
     * Similar to [Activity.onActivityResult]
     *
     * @see .startForResult
     */
    fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {}

    /**
     * 在start(TargetFragment,LaunchMode)时,启动模式为SingleTask/SingleTop, 回调TargetFragment的该方法
     * 类似 [Activity.onNewIntent]
     *
     *
     * Similar to [Activity.onNewIntent]
     *
     * @param args putNewBundle(Bundle newBundle)
     * @see .start
     */
    fun onNewBundle(args: Bundle?) {}

    /**
     * 添加NewBundle,用于启动模式为SingleTask/SingleTop时
     *
     * @see .start
     */
    fun putNewBundle(newBundle: Bundle?) {
        mNewBundle = newBundle
    }
    /** */
    /**
     * Back Event
     *
     * @return false则继续向上传递, true则消费掉该事件
     */
    fun onBackPressedSupport(): Boolean {
        return false
    }

    /**
     * 隐藏软键盘
     */
    fun hideSoftInput() {
        val activity = mFragment.activity ?: return
        val view = activity.window.decorView
        SupportHelper.hideSoftInput(view)
    }

    /**
     * 显示软键盘,调用该方法后,会在onPause时自动隐藏软键盘
     */
    fun showSoftInput(view: View?) {
        SupportHelper.showSoftInput(view)
    }

    /**
     * 加载根Fragment, 即Activity内的第一个Fragment 或 Fragment内的第一个子Fragment
     */
    fun loadRootFragment(containerId: Int, toFragment: ISupportFragment?) {
        loadRootFragment(containerId, toFragment, addToBackStack = true, allowAnim = false)
    }

    fun loadRootFragment(
        containerId: Int,
        toFragment: ISupportFragment?,
        addToBackStack: Boolean,
        allowAnim: Boolean
    ) {
        mTransactionDelegate?.loadRootTransaction(
            getChildFragmentManager(),
            containerId,
            toFragment!!,
            addToBackStack,
            allowAnim
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
            getChildFragmentManager(),
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
     */
    fun showHideFragment(showFragment: ISupportFragment?) {
        showHideFragment(showFragment, null)
    }

    /**
     * show一个Fragment,hide一个Fragment ; 主要用于类似微信主页那种 切换tab的情况
     */
    fun showHideFragment(
        showFragment: ISupportFragment?,
        hideFragment: ISupportFragment?
    ) {
        mTransactionDelegate?.showHideFragment(
            getChildFragmentManager(),
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
    fun start(toFragment: ISupportFragment?, @LaunchMode launchMode: Int) {
        mTransactionDelegate?.dispatchStartTransaction(
            mFragment.fragmentManager!!,
            mSupportF,
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
            mFragment.fragmentManager!!,
            mSupportF,
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
        mTransactionDelegate?.startWithPop(mFragment.fragmentManager!!, mSupportF, toFragment!!)
    }

    fun startWithPopTo(
        toFragment: ISupportFragment?,
        targetFragmentClass: Class<*>,
        includeTargetFragment: Boolean
    ) {
        mTransactionDelegate?.startWithPopTo(
            mFragment.fragmentManager!!,
            mSupportF,
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
            mFragment.fragmentManager!!,
            mSupportF,
            toFragment!!,
            0,
            ISupportFragment.STANDARD,
            if (addToBackStack) TransactionDelegate.TYPE_REPLACE else TransactionDelegate.TYPE_REPLACE_DONT_BACK
        )
    }

    fun startChild(toFragment: ISupportFragment?) {
        startChild(toFragment, ISupportFragment.STANDARD)
    }

    fun startChild(toFragment: ISupportFragment?, @LaunchMode launchMode: Int) {
        mTransactionDelegate?.dispatchStartTransaction(
            getChildFragmentManager(),
            getTopFragment(),
            toFragment!!,
            0,
            launchMode,
            TransactionDelegate.TYPE_ADD
        )
    }

    fun startChildForResult(toFragment: ISupportFragment?, requestCode: Int) {
        mTransactionDelegate?.dispatchStartTransaction(
            getChildFragmentManager(),
            getTopFragment(),
            toFragment!!,
            requestCode,
            ISupportFragment.STANDARD,
            TransactionDelegate.TYPE_ADD_RESULT
        )
    }

    fun startChildWithPop(toFragment: ISupportFragment?) {
        mTransactionDelegate?.startWithPop(
            getChildFragmentManager(),
            getTopFragment(),
            toFragment!!
        )
    }

    fun replaceChildFragment(
        toFragment: ISupportFragment?,
        addToBackStack: Boolean
    ) {
        mTransactionDelegate?.dispatchStartTransaction(
            getChildFragmentManager(),
            getTopFragment(),
            toFragment!!,
            0,
            ISupportFragment.STANDARD,
            if (addToBackStack) TransactionDelegate.TYPE_REPLACE else TransactionDelegate.TYPE_REPLACE_DONT_BACK
        )
    }

    fun pop() {
        mTransactionDelegate?.pop(mFragment.fragmentManager!!)
    }

    /**
     * Pop the child fragment.
     */
    fun popChild() {
        mTransactionDelegate?.pop(getChildFragmentManager())
    }
    /**
     * If you want to begin another FragmentTransaction immediately after popTo(), use this method.
     * 如果你想在出栈后, 立刻进行FragmentTransaction操作，请使用该方法
     */
    /**
     * Pop the last fragment transition from the manager's fragment
     * back stack.
     *
     *
     * 出栈到目标fragment
     *
     * @param targetFragmentClass   目标fragment
     * @param includeTargetFragment 是否包含该fragment
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
            mFragment.fragmentManager!!,
            popAnim
        )
    }

    @JvmOverloads
    fun popToChild(
        targetFragmentClass: Class<*>,
        includeTargetFragment: Boolean,
        afterPopTransactionRunnable: Runnable? = null,
        popAnim: Int = TransactionDelegate.DEFAULT_POPTO_ANIM
    ) {
        mTransactionDelegate?.popTo(
            targetFragmentClass.name,
            includeTargetFragment,
            afterPopTransactionRunnable,
            getChildFragmentManager(),
            popAnim
        )
    }

    fun popQuiet() {
        mTransactionDelegate?.popQuiet(mFragment.fragmentManager!!, mFragment)
    }

    private fun getChildFragmentManager(): FragmentManager {
        return mFragment.childFragmentManager
    }

    private fun getTopFragment(): ISupportFragment {
        return SupportHelper.getTopFragment(getChildFragmentManager())!!
    }

    private fun fixAnimationListener(enterAnim: Animation) { // AnimationListener is not reliable.
        getHandler().postDelayed(mNotifyEnterAnimEndRunnable, enterAnim.duration)
        mSupport!!.getSupportDelegate().mFragmentClickable = true
        if (mEnterAnimListener != null) {
            getHandler().post {
                mEnterAnimListener!!.onEnterAnimStart()
                mEnterAnimListener = null
            }
        }
    }

    private fun compatSharedElements() {
        notifyEnterAnimEnd()
    }

    fun setBackground(view: View) {
        if (mFragment.tag != null && mFragment.tag!!.startsWith("android:switcher:") || mRootStatus != STATUS_UN_ROOT || view.background != null
        ) {
            return
        }
        val defaultBg: Int = mSupport!!.getSupportDelegate().getDefaultFragmentBackground()
        if (defaultBg == 0) {
            val background = getWindowBackground()
            view.setBackgroundResource(background)
        } else {
            view.setBackgroundResource(defaultBg)
        }
    }

    private fun getWindowBackground(): Int {
        val a = _mActivity.theme.obtainStyledAttributes(
            intArrayOf(
                R.attr.windowBackground
            )
        )
        val background = a.getResourceId(0, 0)
        a.recycle()
        return background
    }

    private fun notifyEnterAnimEnd() {
        getHandler().post(mNotifyEnterAnimEndRunnable)
        mSupport!!.getSupportDelegate().mFragmentClickable = true
    }

    private fun getHandler(): Handler {
        if (mHandler == null) {
            mHandler = Handler(Looper.getMainLooper())
        }
        return mHandler!!
    }

    fun getVisibleDelegate(): VisibleDelegate {
        if (mVisibleDelegate == null) {
            mVisibleDelegate = VisibleDelegate(mSupportF)
        }
        return mVisibleDelegate!!
    }

    fun getActivity(): FragmentActivity? {
        return _mActivity
    }

    private fun getEnterAnim(): Animation? {
        if (mCustomEnterAnim == Int.MIN_VALUE) {
            if (mAnimHelper != null && mAnimHelper?.enterAnim != null) {
                return mAnimHelper?.enterAnim
            }
        } else {
            try {
                return AnimationUtils.loadAnimation(_mActivity, mCustomEnterAnim)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    private fun getEnterAnimDuration(): Long {
        val enter = getEnterAnim()
        return enter?.duration ?: NOT_FOUND_ANIM_TIME
    }

    fun getExitAnimDuration(): Long {
        if (mCustomExitAnim == Int.MIN_VALUE) {
            if (mAnimHelper != null && mAnimHelper?.exitAnim != null) {
                return mAnimHelper?.exitAnim!!.duration
            }
        } else {
            try {
                return AnimationUtils.loadAnimation(_mActivity, mCustomExitAnim).duration
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return NOT_FOUND_ANIM_TIME
    }

    private fun getPopExitAnimDuration(): Long {
        if (mCustomPopExitAnim == Int.MIN_VALUE) {
            if (mAnimHelper != null && mAnimHelper?.popExitAnim != null) {
                return mAnimHelper?.popExitAnim!!.duration
            }
        } else {
            try {
                return AnimationUtils.loadAnimation(_mActivity, mCustomPopExitAnim).duration
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return NOT_FOUND_ANIM_TIME
    }

    fun getExitAnim(): Animation? {
        if (mCustomExitAnim == Int.MIN_VALUE) {
            if (mAnimHelper != null && mAnimHelper?.exitAnim != null) {
                return mAnimHelper?.exitAnim
            }
        } else {
            try {
                return AnimationUtils.loadAnimation(_mActivity, mCustomExitAnim)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    interface EnterAnimListener {
        fun onEnterAnimStart()
    }

    companion object {
        const val STATUS_UN_ROOT = 0
        const val STATUS_ROOT_ANIM_DISABLE = 1
        const val STATUS_ROOT_ANIM_ENABLE = 2
        private const val NOT_FOUND_ANIM_TIME = 300L
    }

    init {
        if (support !is Fragment) throw RuntimeException("Must extends Fragment")
        mSupportF = support
        mFragment = support
    }
}