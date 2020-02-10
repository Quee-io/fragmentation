package androidx.fragment.app

/**
 * Created by Ibrahim AlTamimi on 2020-02-09..
 * Licensed for Quee.io
 */
object FragmentationMagician {
    fun isStateSaved(fragmentManager: FragmentManager): Boolean {
        if (fragmentManager !is FragmentManagerImpl) return false
        try {
            return fragmentManager.isStateSaved
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * Like [FragmentManager.popBackStack]} but allows the commit to be executed after an
     * activity's state is saved.  This is dangerous because the action can
     * be lost if the activity needs to later be restored from its state, so
     * this should only be used for cases where it is okay for the UI state
     * to change unexpectedly on the user.
     */
    fun popBackStackAllowingStateLoss(fragmentManager: FragmentManager) {
        hookStateSaved(fragmentManager,
            Runnable { fragmentManager.popBackStack() })
    }

    /**
     * Like [FragmentManager.popBackStackImmediate]} but allows the commit to be executed after an
     * activity's state is saved.
     */
    fun popBackStackImmediateAllowingStateLoss(fragmentManager: FragmentManager) {
        hookStateSaved(fragmentManager,
            Runnable { fragmentManager.popBackStackImmediate() })
    }

    /**
     * Like [FragmentManager.popBackStackImmediate]} but allows the commit to be executed after an
     * activity's state is saved.
     */
    fun popBackStackAllowingStateLoss(
        fragmentManager: FragmentManager,
        name: String?,
        flags: Int
    ) {
        hookStateSaved(fragmentManager,
            Runnable { fragmentManager.popBackStack(name, flags) })
    }

    /**
     * Like [FragmentManager.executePendingTransactions] but allows the commit to be executed after an
     * activity's state is saved.
     */
    fun executePendingTransactionsAllowingStateLoss(fragmentManager: FragmentManager) {
        hookStateSaved(fragmentManager,
            Runnable { fragmentManager.executePendingTransactions() })
    }

    fun getActiveFragments(fragmentManager: FragmentManager): List<Fragment?> {
        return fragmentManager.fragments
    }

    private fun hookStateSaved(
        fragmentManager: FragmentManager,
        runnable: Runnable
    ) {
        if (fragmentManager !is FragmentManagerImpl) return
        if (isStateSaved(fragmentManager)) {
            val tempStateSaved = fragmentManager.mStateSaved
            val tempStopped = fragmentManager.mStopped
            fragmentManager.mStateSaved = false
            fragmentManager.mStopped = false
            runnable.run()
            fragmentManager.mStopped = tempStopped
            fragmentManager.mStateSaved = tempStateSaved
        } else {
            runnable.run()
        }
    }
}