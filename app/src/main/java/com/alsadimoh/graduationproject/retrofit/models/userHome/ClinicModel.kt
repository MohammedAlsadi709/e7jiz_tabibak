package com.alsadimoh.graduationproject.retrofit.models.userHome

import android.os.Parcel
import android.os.Parcelable

data class ClinicModel(val id:Int,val name:String,val address:String,val deleted_at:String):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeString(deleted_at)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClinicModel> {
        override fun createFromParcel(parcel: Parcel): ClinicModel {
            return ClinicModel(parcel)
        }

        override fun newArray(size: Int): Array<ClinicModel?> {
            return arrayOfNulls(size)
        }
    }
}
