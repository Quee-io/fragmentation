package io.quee.fragmentation.core.debug

/**
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */

class DebugFragmentRecord(
    var fragmentName: CharSequence,
    var childFragmentRecord: List<DebugFragmentRecord>
)
