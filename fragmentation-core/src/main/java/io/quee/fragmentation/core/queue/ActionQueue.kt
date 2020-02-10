package io.quee.fragmentation.core.queue

import android.os.Handler
import android.os.Looper
import io.quee.fragmentation.core.ISupportFragment
import io.quee.fragmentation.core.SupportHelper
import java.util.*


/**
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */

class ActionQueue(private val mMainHandler: Handler) {
    private val mQueue: Queue<Action> =
        LinkedList()

    fun enqueue(action: Action) {
        if (isThrottleBACK(action)) return
        if (action.action == Action.ACTION_LOAD && mQueue.isEmpty()
            && Thread.currentThread() === Looper.getMainLooper().thread
        ) {
            action.run()
            return
        }
        mMainHandler.post { enqueueAction(action) }
    }

    private fun enqueueAction(action: Action) {
        mQueue.add(action)
        if (mQueue.size == 1) {
            handleAction()
        }
    }

    private fun handleAction() {
        if (mQueue.isEmpty()) return
        val action = mQueue.peek()
        action.run()
        executeNextAction(action)
    }

    private fun executeNextAction(action: Action) {
        if (action.action == Action.ACTION_POP) {
            val top: ISupportFragment =
                SupportHelper.getBackStackTopFragment(action.fragmentManager)!!
            action.duration = top.getSupportDelegate().getExitAnimDuration()
        }
        mMainHandler.postDelayed({
            mQueue.poll()
            handleAction()
        }, action.duration)
    }

    private fun isThrottleBACK(action: Action): Boolean {
        if (action.action == Action.ACTION_BACK) {
            val head = mQueue.peek()
            if (head != null && head.action == Action.ACTION_POP) {
                return true
            }
        }
        return false
    }

}