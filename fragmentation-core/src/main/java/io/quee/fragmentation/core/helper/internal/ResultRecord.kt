package io.quee.fragmentation.core.helper.internal

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */
open class ResultRecord(
    var requestCode: Int? = 0,
    var resultCode: Int? = 0
) : Parcelable {
    var resultBundle: Bundle? = null

    protected constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt()
    ) {
        resultBundle = parcel.readBundle(javaClass.classLoader)!!
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(requestCode!!)
        dest.writeInt(resultCode!!)
        dest.writeBundle(resultBundle!!)
    }

    companion object CREATOR : Parcelable.Creator<ResultRecord> {
        override fun createFromParcel(parcel: Parcel): ResultRecord {
            return ResultRecord(parcel)
        }

        override fun newArray(size: Int): Array<ResultRecord?> {
            return arrayOfNulls(size)
        }
    }
}