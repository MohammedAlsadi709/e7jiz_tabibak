package com.alsadimoh.graduationproject.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.databinding.ItemRecommendedMedicineBinding
import com.alsadimoh.graduationproject.retrofit.models.userBooking.TreatmentsModel

class RecommendedMedicineAdapter(var activity: Activity, var data:MutableList<TreatmentsModel>): RecyclerView.Adapter<RecommendedMedicineAdapter.MyViewHolder>() {


    inner class MyViewHolder(var root: ItemRecommendedMedicineBinding) : RecyclerView.ViewHolder(root.root) {

        @SuppressLint("SetTextI18n")
        fun bind(model: TreatmentsModel){
            root.txtMedicineName.text = model.medicine.name
            root.txtMedicineDuration.text = model.duration.toString() + " يوم"
            root.txtMedicineTimes.text = model.treatment_period.medication_timings

            if (adapterPosition == data.lastIndex){
                root.itemRecommendedMedicine.setBackgroundResource(R.drawable.round_only_bottom_layout)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = ItemRecommendedMedicineBinding.inflate(
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