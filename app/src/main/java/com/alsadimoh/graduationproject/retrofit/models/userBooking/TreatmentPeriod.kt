package com.alsadimoh.graduationproject.retrofit.models.userBooking

import android.os.Parcel
import android.os.Parcelable

data class TreatmentPeriod(val id:Int,val medication_timings:String,val description:String,val deleted_at:String?):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(medication_timings)
        parcel.writeString(description)
        parcel.writeString(deleted_at)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TreatmentPeriod> {
        override fun createFromParcel(parcel: Parcel): TreatmentPeriod {
            return TreatmentPeriod(parcel)
        }

        override fun newArray(size: Int): Array<TreatmentPeriod?> {
            return arrayOfNulls(size)
        }
    }
}
