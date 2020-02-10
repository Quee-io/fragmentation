package io.quee.fragmentation.core.queue

import androidx.fragment.app.FragmentManager


/**
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */

abstract class Action {
    var fragmentManager: FragmentManager? = null
    var action = ACTION_NORMAL
    var duration: Long = 0

    constructor() {}
    constructor(action: Int) {
        this.action = action
    }

    constructor(
        action: Int,
        fragmentManager: FragmentManager?
    ) : this(action) {
        this.fragmentManager = fragmentManager
    }

    abstract fun run()

    companion object {
        const val DEFAULT_POP_TIME = 300L
        const val ACTION_NORMAL = 0
        const val ACTION_POP = 1
        const val ACTION_POP_MOCK = 2
        const val ACTION_BACK = 3
        const val ACTION_LOAD = 4
    }
}