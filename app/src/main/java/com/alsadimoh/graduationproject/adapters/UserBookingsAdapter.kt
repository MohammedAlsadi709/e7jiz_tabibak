package com.alsadimoh.graduationproject.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.databinding.ItemIncomingTimeBinding
import com.alsadimoh.graduationproject.retrofit.models.userBooking.BookingModel

class UserBookingsAdapter(var activity: Activity, val data:MutableList<BookingModel>, private val bookingType: String) :
    RecyclerView.Adapter<UserBookingsAdapter.MyViewHolder>() {

    var onItemClick: ((model:BookingModel) -> Unit)? = null
    var onCancelClick: ((model:BookingModel) -> Unit)? = null
    var onEditClick: ((model:BookingModel) -> Unit)? = null

    inner class MyViewHolder(var root: ItemIncomingTimeBinding) : RecyclerView.ViewHolder(root.root) {
        init {
            root.item.setOnClickListener {
                onItemClick?.invoke(data[adapterPosition])
            }

            root.btnCancelTime.setOnClickListener {
                onCancelClick?.invoke(data[adapterPosition])
            }

            root.btnChangeTime.setOnClickListener {
                onEditClick?.invoke(data[adapterPosition])
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(model:BookingModel){

            CommonConstants.loadImageWithPicasso(model.doctor.image,root.imgDoctor)
            root.txtDoctorTitle.text = model.doctor.specialization.title + " - " + model.doctor.clinic.name
            root.txtPrice.text = "${model.price} شيكل"
            root.txtDate.text = model.date
            root.txtTime.text = model.time

            if (bookingType == "finished") {
                root.layout1.visibility = View.GONE
                root.layout2.visibility = View.GONE
                root.txtDoctorName.text = model.doctor.name
            } else {
                root.layout1.visibility = View.VISIBLE
                root.layout2.visibility = View.VISIBLE
                if (bookingType=="pending"){
                    val status  = when {
                        model.status.startsWith("pending") -> {
                            root.layout1.visibility = View.VISIBLE
                            root.layout2.visibility = View.VISIBLE
                            "حجز"
                        }
                        model.status.startsWith("edit") -> {
                            root.layout1.visibility = View.GONE
                            root.layout2.visibility = View.GONE
                            "تعديل"
                        }
                        model.status.startsWith("cancel") -> {
                            root.layout1.visibility = View.GONE
                            root.layout2.visibility = View.GONE
                            "الغاء"
                        }
                        else -> {
                            ""
                        }
                    }
                    root.txtDoctorName.text = model.doctor.name + " ($status)"
                }else if (bookingType == "incoming"){
                    root.layout1.visibility = View.VISIBLE
                    root.layout2.visibility = View.VISIBLE
                    root.txtDoctorName.text = model.doctor.name
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = ItemIncomingTimeBinding.inflate(
            LayoutInflater.from(activity)//parent.context
            , parent, false
        )
        return MyViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}