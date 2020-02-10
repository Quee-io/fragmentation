package io.quee.fragmentation.core.anim

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */
open class DefaultNoAnimator : FragmentAnimator, Parcelable {

    constructor() : super()

    protected constructor(parcel: Parcel) : super(parcel)

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DefaultNoAnimator> {
        override fun createFromParcel(parcel: Parcel): DefaultNoAnimator {
            return DefaultNoAnimator(parcel)
        }

        override fun newArray(size: Int): Array<DefaultNoAnimator?> {
            return arrayOfNulls(size)
        }
    }
}
