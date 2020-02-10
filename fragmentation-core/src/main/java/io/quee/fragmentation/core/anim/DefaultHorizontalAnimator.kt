package io.quee.fragmentation.core.anim

import android.os.Parcel
import android.os.Parcelable
import io.quee.fragmentation.core.R

/**
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */
open class DefaultHorizontalAnimator : FragmentAnimator, Parcelable {

    constructor() : super(
        R.anim.h_fragment_enter,
        R.anim.h_fragment_exit,
        R.anim.h_fragment_pop_enter,
        R.anim.h_fragment_pop_exit
    )

    protected constructor(parcel: Parcel) : super(parcel)

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DefaultHorizontalAnimator> {
        override fun createFromParcel(parcel: Parcel): DefaultHorizontalAnimator {
            return DefaultHorizontalAnimator(parcel)
        }

        override fun newArray(size: Int): Array<DefaultHorizontalAnimator?> {
            return arrayOfNulls(size)
        }
    }
}
