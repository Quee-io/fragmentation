package io.quee.fragmentation.core.helper.internal

import android.view.View
import java.util.*

/**
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */
class TransactionRecord {
    var tag: String? = null
    var targetFragmentEnter = Int.MIN_VALUE
    var currentFragmentPopExit = Int.MIN_VALUE
    var currentFragmentPopEnter = Int.MIN_VALUE
    var targetFragmentExit = Int.MIN_VALUE
    var dontAddToBackStack = false
    var sharedElementList: ArrayList<SharedElement>? = null

    class SharedElement(var sharedElement: View, var sharedName: String)
}