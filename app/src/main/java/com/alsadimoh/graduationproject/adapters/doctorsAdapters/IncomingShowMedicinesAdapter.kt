package com.alsadimoh.graduationproject.adapters.doctorsAdapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.databinding.ItemIncomingBookingShowMedicineBinding
import com.alsadimoh.graduationproject.databinding.ItemRecommendedMedicineBinding
import com.alsadimoh.graduationproject.retrofit.models.doctorBooking.IncomingBookingMedicineModel
import com.alsadimoh.graduationproject.retrofit.models.userBooking.TreatmentsModel

class IncomingShowMedicinesAdapter(var activity: Activity, var data:MutableList<IncomingBookingMedicineModel>): RecyclerView.Adapter<IncomingShowMedicinesAdapter.MyViewHolder>() {


    inner class MyViewHolder(var root: ItemIncomingBookingShowMedicineBinding) : RecyclerView.ViewHolder(root.root) {

        fun bind(model: IncomingBookingMedicineModel){
            root.txtMedicineName.text = model.name
            root.txtMedicineStatus.text = model.active

            if (adapterPosition == data.lastIndex){
                root.itemRecommendedMedicine.setBackgroundResource(R.drawable.round_only_bottom_layout)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = ItemIncomingBookingShowMedicineBinding.inflate(
            LayoutInflater.from(activity)//parent.context
            , parent, false)
        return MyViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
      holder.bind(data[position])
    }

    override fun getItemCount(): Int {
       return data.size
    }
}