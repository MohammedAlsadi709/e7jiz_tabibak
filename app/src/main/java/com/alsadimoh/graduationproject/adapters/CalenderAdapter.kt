package com.alsadimoh.graduationproject.adapters

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alsadimoh.graduationproject.databinding.ItemDayOfCalenderBinding
import com.alsadimoh.graduationproject.retrofit.models.userBooking.CalenderDaysAdapterModel
import java.text.SimpleDateFormat
import java.util.*

class CalenderAdapter(var activity: Activity, var data:MutableList<CalenderDaysAdapterModel>): RecyclerView.Adapter<CalenderAdapter.MyViewHolder>() {

    var onClickItem :((model: CalenderDaysAdapterModel)->Unit)?=null

    inner class MyViewHolder(var root: ItemDayOfCalenderBinding) : RecyclerView.ViewHolder(root.root) {

        init {
            root.item.setOnClickListener {
                onClickItem?.invoke(data[adapterPosition])
            }
        }

        fun bind(model: CalenderDaysAdapterModel){
            root.dayDate.text = model.dayNumber.toString()
          /*  val s = SimpleDateFormat("yyyy-M-d", Locale.US)
            root.dayName.text = SimpleDateFormat("EEEE", Locale.US).format(s.parse(model.date)!!).substring(0,3)*/

            val s = SimpleDateFormat("yyyy-M-d", Locale("ar"))
            root.dayName.text = SimpleDateFormat("EEEE", Locale("ar")).format(s.parse(model.date)!!)//.substring(0,3)

            if (model.isSelected){
                root.parentLayoutItem.background.setTint(Color.parseColor("#00A4CE"))
                root.dayDate.setTextColor(Color.parseColor("#FFFFFF"))
                root.dayName.setTextColor(Color.parseColor("#FFFFFF"))
            }else{
                root.parentLayoutItem.background.setTint(Color.parseColor("#F8F8F8"))
                root.dayDate.setTextColor(Color.parseColor("#2E2E2E"))
                root.dayName.setTextColor(Color.parseColor("#2E2E2E"))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = ItemDayOfCalenderBinding.inflate(
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