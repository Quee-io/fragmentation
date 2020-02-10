package io.quee.fragmentation.core.anim

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.AnimRes

/**
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */
open class FragmentAnimator(
    @param:AnimRes var enter: Int? = 0,
    @param:AnimRes var exit: Int? = 0,
    @param:AnimRes var popEnter: Int? = 0,
    @param:AnimRes var popExit: Int? = 0
) :
    Parcelable {
    companion object CREATOR : Parcelable.Creator<FragmentAnimator> {
        override fun createFromParcel(parcel: Parcel): FragmentAnimator {
            return FragmentAnimator(parcel)
        }

        override fun newArray(size: Int): Array<FragmentAnimator?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    fun fragmentAnimator() {}

    fun fragmentAnimator(enter: Int, exit: Int) {
        this.enter = enter
        this.exit = exit
    }

    fun fragmentAnimator(enter: Int, exit: Int, popEnter: Int, popExit: Int) {
        this.enter = enter
        this.exit = exit
        this.popEnter = popEnter
        this.popExit = popExit
    }

    protected fun fragmentAnimator(parcel: Parcel) {
        enter = parcel.readInt()
        exit = parcel.readInt()
        popEnter = parcel.readInt()
        popExit = parcel.readInt()
    }

    fun copy(): FragmentAnimator {
        return FragmentAnimator(enter, exit, popEnter, popExit)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(enter!!)
        dest.writeInt(exit!!)
        dest.writeInt(popEnter!!)
        dest.writeInt(popExit!!)
    }
}