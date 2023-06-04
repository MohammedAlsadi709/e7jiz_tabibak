package com.alsadimoh.graduationproject.retrofit.models.userBooking

import android.os.Parcel
import android.os.Parcelable

data class TreatmentsModel(val id:Int,val user_id:Int,val doctor_id:Int,val medicine_id:Int,val duration:Double,val treatment_period_id:Int,val status:String,val booking_id:Int,val deleted_at:String?,val medicine:MedicineModel,val treatment_period:TreatmentPeriod):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString(),
        parcel.readParcelable(MedicineModel::class.java.classLoader)!!,
        parcel.readParcelable(TreatmentPeriod::class.java.classLoader)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(user_id)
        parcel.writeInt(doctor_id)
        parcel.writeInt(medicine_id)
        parcel.writeDouble(duration)
        parcel.writeInt(treatment_period_id)
        parcel.writeString(status)
        parcel.writeInt(booking_id)
        parcel.writeString(deleted_at)
        parcel.writeParcelable(medicine, flags)
        parcel.writeParcelable(treatment_period, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TreatmentsModel> {
        override fun createFromParcel(parcel: Parcel): TreatmentsModel {
            return TreatmentsModel(parcel)
        }

        override fun newArray(size: Int): Array<TreatmentsModel?> {
            return arrayOfNulls(size)
        }
    }
}
