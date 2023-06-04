package com.alsadimoh.graduationproject.retrofit.models.userBooking

import android.os.Parcel
import android.os.Parcelable

data class MedicineModel(val id:Int,val name:String,val description:String,val deleted_at:String?):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(deleted_at)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MedicineModel> {
        override fun createFromParcel(parcel: Parcel): MedicineModel {
            return MedicineModel(parcel)
        }

        override fun newArray(size: Int): Array<MedicineModel?> {
            return arrayOfNulls(size)
        }
    }
}
