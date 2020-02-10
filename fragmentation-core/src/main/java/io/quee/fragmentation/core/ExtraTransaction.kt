package io.quee.fragmentation.core

import android.os.Build
import android.view.View
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import io.quee.fragmentation.core.helper.internal.TransactionRecord
import io.quee.fragmentation.core.helper.internal.TransactionRecord.SharedElement

/**
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */
abstract class ExtraTransaction {
    /**
     * @param tag Optional tag name for the fragment, to later retrieve the
     * fragment with [SupportHelper.findFragment]
     * , pop(String)
     * or FragmentManager.findFragmentByTag(String).
     */
    abstract fun setTag(tag: String?): ExtraTransaction?

    /**
     * Set specific animation resources to run for the fragments that are
     * entering and exiting in this transaction. These animations will not be
     * played when popping the back stack.
     */
    abstract fun setCustomAnimations(
        @AnimatorRes @AnimRes targetFragmentEnter: Int,
        @AnimatorRes @AnimRes currentFragmentPopExit: Int
    ): ExtraTransaction?

    /**
     * Set specific animation resources to run for the fragments that are
     * entering and exiting in this transaction. The `currentFragmentPopEnter`
     * and `targetFragmentExit` animations will be played for targetFragmentEnter/currentFragmentPopExit
     * operations specifically when popping the back stack.
     */
    abstract fun setCustomAnimations(
        @AnimatorRes @AnimRes targetFragmentEnter: Int,
        @AnimatorRes @AnimRes currentFragmentPopExit: Int,
        @AnimatorRes @AnimRes currentFragmentPopEnter: Int,
        @AnimatorRes @AnimRes targetFragmentExit: Int
    ): ExtraTransaction?

    /**
     * Used with custom Transitions to map a View from a removed or hidden
     * Fragment to a View from a shown or added Fragment.
     * <var>sharedElement</var> must have a unique transitionName in the View hierarchy.
     *
     * @param sharedElement A View in a disappearing Fragment to match with a View in an
     * appearing Fragment.
     * @param sharedName    The transitionName for a View in an appearing Fragment to match to the shared
     * element.
     * @see Fragment.setSharedElementReturnTransition
     * @see Fragment.setSharedElementEnterTransition
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    abstract fun addSharedElement(
        sharedElement: View?,
        sharedName: String?
    ): ExtraTransaction?

    abstract fun loadRootFragment(containerId: Int, toFragment: ISupportFragment)
    abstract fun loadRootFragment(
        containerId: Int,
        toFragment: ISupportFragment,
        addToBackStack: Boolean,
        allowAnim: Boolean
    )

    abstract fun start(toFragment: ISupportFragment)
    abstract fun startDontHideSelf(toFragment: ISupportFragment)
    abstract fun startDontHideSelf(toFragment: ISupportFragment, @ISupportFragment.LaunchMode launchMode: Int)
    abstract fun start(toFragment: ISupportFragment, @ISupportFragment.LaunchMode launchMode: Int)
    abstract fun startForResult(toFragment: ISupportFragment, requestCode: Int)
    abstract fun startForResultDontHideSelf(
        toFragment: ISupportFragment,
        requestCode: Int
    )

    abstract fun startWithPop(toFragment: ISupportFragment)
    abstract fun startWithPopTo(
        toFragment: ISupportFragment,
        targetFragmentTag: String?,
        includeTargetFragment: Boolean
    )

    abstract fun replace(toFragment: ISupportFragment)
    /**
     * 使用setTag()自定义Tag时，使用下面popTo()／popToChild()出栈
     *
     * @param targetFragmentTag     通过setTag()设置的tag
     * @param includeTargetFragment 是否包含目标(Tag为targetFragmentTag)Fragment
     */
    abstract fun popTo(
        targetFragmentTag: String?,
        includeTargetFragment: Boolean
    )

    abstract fun popTo(
        targetFragmentTag: String?,
        includeTargetFragment: Boolean,
        afterPopTransactionRunnable: Runnable?,
        popAnim: Int
    )

    abstract fun popToChild(
        targetFragmentTag: String?,
        includeTargetFragment: Boolean
    )

    abstract fun popToChild(
        targetFragmentTag: String?,
        includeTargetFragment: Boolean,
        afterPopTransactionRunnable: Runnable?,
        popAnim: Int
    )

    /**
     * Don't add this extraTransaction to the back stack.
     */
    abstract fun dontAddToBackStack(): DontAddToBackStackTransaction?

    /**
     * 使用dontAddToBackStack() 加载Fragment时， 使用remove()移除Fragment
     */
    abstract fun remove(fragment: ISupportFragment, showPreFragment: Boolean)

    interface DontAddToBackStackTransaction {
        /**
         * add() +  hide(preFragment)
         */
        fun start(toFragment: ISupportFragment)

        /**
         * Only add()
         */
        fun add(toFragment: ISupportFragment)

        /**
         * replace()
         */
        fun replace(toFragment: ISupportFragment)
    }

    /**
     * Impl
     */
    internal class ExtraTransactionImpl<T : ISupportFragment>(
        private val mActivity: FragmentActivity,
        private val mSupportF: T,
        transactionDelegate: TransactionDelegate,
        fromActivity: Boolean
    ) :
        ExtraTransaction(), DontAddToBackStackTransaction {
        private val mFragment: Fragment?
        private val mTransactionDelegate: TransactionDelegate
        private val mFromActivity: Boolean
        private val mRecord: TransactionRecord
        override fun setTag(tag: String?): ExtraTransaction {
            mRecord.tag = tag
            return this
        }

        override fun setCustomAnimations(
            @AnimRes targetFragmentEnter: Int
            , @AnimRes currentFragmentPopExit: Int
        ): ExtraTransaction {
            mRecord.targetFragmentEnter = targetFragmentEnter
            mRecord.currentFragmentPopExit = currentFragmentPopExit
            mRecord.currentFragmentPopEnter = 0
            mRecord.targetFragmentExit = 0
            return this
        }

        override fun setCustomAnimations(
            @AnimRes targetFragmentEnter: Int,
            @AnimRes currentFragmentPopExit: Int,
            @AnimRes currentFragmentPopEnter: Int,
            @AnimRes targetFragmentExit: Int
        ): ExtraTransaction {
            mRecord.targetFragmentEnter = targetFragmentEnter
            mRecord.currentFragmentPopExit = currentFragmentPopExit
            mRecord.currentFragmentPopEnter = currentFragmentPopEnter
            mRecord.targetFragmentExit = targetFragmentExit
            return this
        }

        override fun addSharedElement(
            sharedElement: View?,
            sharedName: String?
        ): ExtraTransaction {
            if (mRecord.sharedElementList == null) {
                mRecord.sharedElementList = ArrayList()
            }
            mRecord.sharedElementList?.add(SharedElement(sharedElement!!, sharedName!!))
            return this
        }

        override fun loadRootFragment(containerId: Int, toFragment: ISupportFragment) {
            loadRootFragment(containerId, toFragment, addToBackStack = true, allowAnim = false)
        }

        override fun loadRootFragment(
            containerId: Int,
            toFragment: ISupportFragment,
            addToBackStack: Boolean,
            allowAnim: Boolean
        ) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord
            mTransactionDelegate.loadRootTransaction(
                fragmentManager,
                containerId,
                toFragment,
                addToBackStack,
                allowAnim
            )
        }

        override fun dontAddToBackStack(): DontAddToBackStackTransaction {
            mRecord.dontAddToBackStack = true
            return this
        }

        override fun remove(
            fragment: ISupportFragment,
            showPreFragment: Boolean
        ) {
            mTransactionDelegate.remove(
                fragmentManager,
                fragment as Fragment?,
                showPreFragment
            )
        }

        override fun popTo(
            targetFragmentTag: String?,
            includeTargetFragment: Boolean
        ) {
            popTo(
                targetFragmentTag,
                includeTargetFragment,
                null,
                TransactionDelegate.DEFAULT_POPTO_ANIM
            )
        }

        override fun popTo(
            targetFragmentTag: String?,
            includeTargetFragment: Boolean,
            afterPopTransactionRunnable: Runnable?,
            popAnim: Int
        ) {
            mTransactionDelegate.popTo(
                targetFragmentTag!!,
                includeTargetFragment,
                afterPopTransactionRunnable,
                fragmentManager,
                popAnim
            )
        }

        override fun popToChild(
            targetFragmentTag: String?,
            includeTargetFragment: Boolean
        ) {
            popToChild(
                targetFragmentTag,
                includeTargetFragment,
                null,
                TransactionDelegate.DEFAULT_POPTO_ANIM
            )
        }

        override fun popToChild(
            targetFragmentTag: String?,
            includeTargetFragment: Boolean,
            afterPopTransactionRunnable: Runnable?,
            popAnim: Int
        ) {
            if (mFromActivity) {
                popTo(
                    targetFragmentTag,
                    includeTargetFragment,
                    afterPopTransactionRunnable,
                    popAnim
                )
            } else {
                mTransactionDelegate.popTo(
                    targetFragmentTag!!,
                    includeTargetFragment,
                    afterPopTransactionRunnable,
                    mFragment!!.childFragmentManager,
                    popAnim
                )
            }
        }

        override fun add(toFragment: ISupportFragment) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord
            mTransactionDelegate.dispatchStartTransaction(
                fragmentManager,
                mSupportF,
                toFragment,
                0,
                ISupportFragment.STANDARD,
                TransactionDelegate.TYPE_ADD_WITHOUT_HIDE
            )
        }

        override fun start(toFragment: ISupportFragment) {
            start(toFragment, ISupportFragment.STANDARD)
        }

        override fun startDontHideSelf(toFragment: ISupportFragment) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord
            mTransactionDelegate.dispatchStartTransaction(
                fragmentManager,
                mSupportF,
                toFragment,
                0,
                ISupportFragment.STANDARD,
                TransactionDelegate.TYPE_ADD_WITHOUT_HIDE
            )
        }

        override fun startDontHideSelf(toFragment: ISupportFragment, @ISupportFragment.LaunchMode launchMode: Int) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord
            mTransactionDelegate.dispatchStartTransaction(
                fragmentManager,
                mSupportF,
                toFragment,
                0,
                launchMode,
                TransactionDelegate.TYPE_ADD_WITHOUT_HIDE
            )
        }

        override fun start(toFragment: ISupportFragment, @ISupportFragment.LaunchMode launchMode: Int) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord
            mTransactionDelegate.dispatchStartTransaction(
                fragmentManager,
                mSupportF,
                toFragment,
                0,
                launchMode,
                TransactionDelegate.TYPE_ADD
            )
        }

        override fun startForResult(
            toFragment: ISupportFragment,
            requestCode: Int
        ) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord
            mTransactionDelegate.dispatchStartTransaction(
                fragmentManager,
                mSupportF,
                toFragment,
                requestCode,
                ISupportFragment.STANDARD,
                TransactionDelegate.TYPE_ADD_RESULT
            )
        }

        override fun startForResultDontHideSelf(
            toFragment: ISupportFragment,
            requestCode: Int
        ) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord
            mTransactionDelegate.dispatchStartTransaction(
                fragmentManager,
                mSupportF,
                toFragment,
                requestCode,
                ISupportFragment.STANDARD,
                TransactionDelegate.TYPE_ADD_RESULT_WITHOUT_HIDE
            )
        }

        override fun startWithPop(toFragment: ISupportFragment) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord
            mTransactionDelegate.startWithPop(fragmentManager, mSupportF, toFragment)
        }

        override fun startWithPopTo(
            toFragment: ISupportFragment,
            targetFragmentTag: String?,
            includeTargetFragment: Boolean
        ) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord
            mTransactionDelegate.startWithPopTo(
                fragmentManager,
                mSupportF,
                toFragment,
                targetFragmentTag,
                includeTargetFragment
            )
        }

        override fun replace(toFragment: ISupportFragment) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord
            mTransactionDelegate.dispatchStartTransaction(
                fragmentManager,
                mSupportF,
                toFragment,
                0,
                ISupportFragment.STANDARD,
                TransactionDelegate.TYPE_REPLACE
            )
        }

        private val fragmentManager: FragmentManager
            get() = if (mFragment == null) {
                mActivity.supportFragmentManager
            } else mFragment.fragmentManager!!

        init {
            mFragment = mSupportF as Fragment
            mTransactionDelegate = transactionDelegate
            mFromActivity = fromActivity
            mRecord = TransactionRecord()
        }
    }
}
