package io.quee.fragmentation.demo.demo_flow.base

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import io.quee.fragmentation.core.ExtraTransaction
import io.quee.fragmentation.core.ISupportActivity
import io.quee.fragmentation.core.ISupportFragment
import io.quee.fragmentation.core.ISupportFragment.LaunchMode
import io.quee.fragmentation.core.SupportActivityDelegate
import io.quee.fragmentation.core.SupportHelper.findFragment
import io.quee.fragmentation.core.SupportHelper.getTopFragment
import io.quee.fragmentation.core.anim.FragmentAnimator

/**
 * 展示自定制的MySupportActivity，不继承SupportActivity
 * Created by YoKey on 17/6/24.
 */
abstract class MySupportActivity : AppCompatActivity(), ISupportActivity {
    val mDelegate = SupportActivityDelegate(this)
    override fun getSupportDelegate(): SupportActivityDelegate {
        return mDelegate
    }

    /**
     * Perform some extra transactions.
     * 额外的事务：自定义Tag，添加SharedElement动画，操作非回退栈Fragment
     */
    override fun extraTransaction(): ExtraTransaction? {
        return mDelegate.extraTransaction()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDelegate.onCreate()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDelegate.onPostCreate()
    }

    override fun onDestroy() {
        mDelegate.onDestroy()
        super.onDestroy()
    }

    /**
     * Note： return mDelegate.dispatchTouchEvent(ev) || super.dispatchTouchEvent(ev);
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return mDelegate.dispatchTouchEvent() || super.dispatchTouchEvent(ev)
    }

    /**
     * 不建议复写该方法,请使用 [.onBackPressedSupport] 代替
     */
    override fun onBackPressed() {
        mDelegate.onBackPressed()
    }

    /**
     * 该方法回调时机为,Activity回退栈内Fragment的数量 小于等于1 时,默认finish Activity
     * 请尽量复写该方法,避免复写onBackPress(),以保证SupportFragment内的onBackPressedSupport()回退事件正常执行
     */
    override fun onBackPressedSupport() {
        mDelegate.onBackPressedSupport()
    }

    /**
     * 获取设置的全局动画 copy
     *
     * @return FragmentAnimator
     */
    override fun getFragmentAnimator(): FragmentAnimator? {
        return mDelegate.getFragmentAnimator()
    }

    /**
     * Set all fragments animation.
     * 设置Fragment内的全局动画
     */
    override fun setFragmentAnimator(fragmentAnimator: FragmentAnimator?) {
        mDelegate.setFragmentAnimator(fragmentAnimator!!)
    }

    /**
     * Set all fragments animation.
     * 构建Fragment转场动画
     *
     *
     * 如果是在Activity内实现,则构建的是Activity内所有Fragment的转场动画,
     * 如果是在Fragment内实现,则构建的是该Fragment的转场动画,此时优先级 > Activity的onCreateFragmentAnimator()
     *
     * @return FragmentAnimator对象
     */
    override fun onCreateFragmentAnimator(): FragmentAnimator? {
        return mDelegate.onCreateFragmentAnimator()
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
    override fun post(runnable: Runnable?) {
        mDelegate.post(runnable!!)
    }

    /****************************************以下为可选方法(Optional methods) */ // 选择性拓展其他方法
    fun loadRootFragment(containerId: Int, toFragment: ISupportFragment) {
        mDelegate.loadRootFragment(containerId, toFragment)
    }

    fun start(toFragment: ISupportFragment?) {
        mDelegate.start(toFragment)
    }

    /**
     * @param launchMode Same as Activity's LaunchMode.
     */
    fun start(toFragment: ISupportFragment?, @LaunchMode launchMode: Int) {
        mDelegate.start(toFragment, launchMode)
    }

    /**
     * It is recommended to use [io.quee.fragmentation.CoreFragment.startWithPopTo].
     *
     * @see .popTo
     * @see .start
     */
    fun startWithPopTo(
        toFragment: ISupportFragment?,
        targetFragmentClass: Class<*>?,
        includeTargetFragment: Boolean
    ) {
        mDelegate.startWithPopTo(toFragment, targetFragmentClass!!, includeTargetFragment)
    }

    /**
     * Pop the fragment.
     */
    fun pop() {
        mDelegate.pop()
    }

    /**
     * Pop the last fragment transition from the manager's fragment
     * back stack.
     */
    fun popTo(
        targetFragmentClass: Class<*>?,
        includeTargetFragment: Boolean
    ) {
        mDelegate.popTo(targetFragmentClass!!, includeTargetFragment)
    }

    /**
     * If you want to begin another FragmentTransaction immediately after popTo(), use this method.
     * 如果你想在出栈后, 立刻进行FragmentTransaction操作，请使用该方法
     */
    fun popTo(
        targetFragmentClass: Class<*>?,
        includeTargetFragment: Boolean,
        afterPopTransactionRunnable: Runnable?
    ) {
        mDelegate.popTo(targetFragmentClass!!, includeTargetFragment, afterPopTransactionRunnable)
    }

    fun popTo(
        targetFragmentClass: Class<*>?,
        includeTargetFragment: Boolean,
        afterPopTransactionRunnable: Runnable?,
        popAnim: Int
    ) {
        mDelegate.popTo(
            targetFragmentClass!!,
            includeTargetFragment,
            afterPopTransactionRunnable,
            popAnim
        )
    }

    /**
     * 得到位于栈顶Fragment
     */
    val topFragment: ISupportFragment?
        get() = getTopFragment(supportFragmentManager)

    /**
     * 获取栈内的fragment对象
     */
    fun <T : ISupportFragment> findFragment(fragmentClass: Class<T>?): T? {
        return findFragment(supportFragmentManager, fragmentClass)
    }
}