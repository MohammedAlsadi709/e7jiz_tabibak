package com.alsadimoh.graduationproject.adapters.doctorsAdapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.databinding.ItemDoctorBookingBinding
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.retrofit.models.doctorBooking.BookingModelForDoctor

class DoctorHomeBookingAdapter(var activity: Activity, val data:MutableList<BookingModelForDoctor>): RecyclerView.Adapter<DoctorHomeBookingAdapter.MyViewHolder>() {

    var onClickBookingItem:((model: BookingModelForDoctor)->Unit)? = null

    inner class MyViewHolder(var root: ItemDoctorBookingBinding) : RecyclerView.ViewHolder(root.root) {

        init {
            root.itemBooking.setOnClickListener {
                onClickBookingItem?.invoke(data[adapterPosition])
                CommonConstants.onOpenFragmentChangeEmptyMenuItemIconBitmapDrawable?.invoke(data[adapterPosition].user.image)
            }
        }

        fun bind(model: BookingModelForDoctor){
            CommonConstants.loadImageWithPicasso(model.user.image,root.imgItem)
            root.txtItemName.text = model.user.name
            root.txtItemTime.text = model.time


            when (model.status) {
                "done" -> {
                    root.cardState.visibility = View.VISIBLE
                    root.imgItemState.setImageResource(R.mipmap.true_state)
                }
                "canceled" -> {
                    root.cardState.visibility = View.VISIBLE
                    root.imgItemState.setImageResource(R.mipmap.false_state)
                }
                else -> {
                    root.cardState.visibility = View.GONE
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = ItemDoctorBookingBinding.inflate(
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