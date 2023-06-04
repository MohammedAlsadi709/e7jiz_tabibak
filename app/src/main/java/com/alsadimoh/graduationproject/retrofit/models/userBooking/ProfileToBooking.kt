package com.alsadimoh.graduationproject.retrofit.models.userBooking

import android.os.Parcel
import android.os.Parcelable

data class ProfileToBooking(val doctor_id:Int,val name:String,val spec:String,val image:String,val price:Float):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readFloat()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(doctor_id)
        parcel.writeString(name)
        parcel.writeString(spec)
        parcel.writeString(image)
        parcel.writeFloat(price)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProfileToBooking> {
        override fun createFromParcel(parcel: Parcel): ProfileToBooking {
            return ProfileToBooking(parcel)
        }

        override fun newArray(size: Int): Array<ProfileToBooking?> {
            return arrayOfNulls(size)
        }
    }
}