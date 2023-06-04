package com.alsadimoh.graduationproject.adapters.doctorsAdapters

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alsadimoh.graduationproject.databinding.ItemDayHeaderDoctorHomeBinding
import com.alsadimoh.graduationproject.retrofit.models.userBooking.CalenderDaysAdapterModel
import java.text.SimpleDateFormat
import java.util.*

class HeaderDaysForDoctorHomeAdapter(var activity: Activity, var data:MutableList<CalenderDaysAdapterModel>, val currentDate:String, val tomorrowDate:String): RecyclerView.Adapter<HeaderDaysForDoctorHomeAdapter.MyViewHolder>() {

    var onClickItem :((model: CalenderDaysAdapterModel)->Unit)?=null

    inner class MyViewHolder(var root: ItemDayHeaderDoctorHomeBinding) : RecyclerView.ViewHolder(root.root) {

        init {
            root.item.setOnClickListener {
                onClickItem?.invoke(data[adapterPosition])
            }
        }

        fun bind(model: CalenderDaysAdapterModel){

            val s = SimpleDateFormat("yyyy-M-d", Locale("ar"))

            root.txtDayName.text = when (model.date) {
                currentDate -> {
                    "اليوم"
                }
                tomorrowDate -> {
                    "غداً"
                }
                else -> {
                    SimpleDateFormat("EEEE", Locale("ar")).format(s.parse(model.date)!!)
                }
            }

            if (model.isSelected){
                root.item.background.setTint(Color.parseColor("#00A4CE"))
                root.txtDayName.setTextColor(Color.parseColor("#FFFFFF"))
            }else{
                root.item.background.setTint(Color.parseColor("#F8F8F8"))
                root.txtDayName.setTextColor(Color.parseColor("#2E2E2E"))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = ItemDayHeaderDoctorHomeBinding.inflate(
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