package com.alsadimoh.graduationproject.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.databinding.ItemTimeHourBinding
import com.alsadimoh.graduationproject.retrofit.models.userBooking.AvailableTimesForAddNewBooking

class TimeHoursAdapter(var activity: Activity,val data:MutableList<AvailableTimesForAddNewBooking>): RecyclerView.Adapter<TimeHoursAdapter.MyViewHolder>() {

    var onClickItem:((position:Int,model: AvailableTimesForAddNewBooking)->Unit)? = null
    inner class MyViewHolder(var root: ItemTimeHourBinding) : RecyclerView.ViewHolder(root.root) {
        init {
            root.item.setOnClickListener {
                onClickItem?.invoke(adapterPosition,data[adapterPosition])
            }
        }
        fun bind(model:AvailableTimesForAddNewBooking){
            root.txtHour.text = model.time

            when {
                model.isSelected -> {
                    root.item.background.setTint(activity.getColor(R.color.primaryColor))
                    root.txtHour.setTextColor(activity.getColor(R.color.white))
                }
                model.for_user -> {
                    root.item.background.setTint(activity.getColor(R.color.primaryColorWithAlpha))
                    root.txtHour.setTextColor(activity.getColor(R.color.white))
                }
                else -> {
                    root.item.background.setTint(activity.getColor(R.color.backgroundColor))
                    root.txtHour.setTextColor(activity.getColor(R.color.grayText))
                }
            }

            when {
                model.for_user -> {
                    root.item.isEnabled = false
                }
                model.status -> {
                    root.item.isEnabled = true
                }
                else -> {
                    root.item.isEnabled = false
                    root.item.background.setTint(activity.getColor(R.color.backgroundColorForNotEnableItems))
                    root.txtHour.setTextColor(activity.getColor(R.color.black))
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = ItemTimeHourBinding.inflate(
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