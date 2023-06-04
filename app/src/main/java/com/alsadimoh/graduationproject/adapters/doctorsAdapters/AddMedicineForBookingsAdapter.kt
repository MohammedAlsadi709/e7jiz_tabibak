package com.alsadimoh.graduationproject.adapters.doctorsAdapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.alsadimoh.graduationproject.databinding.ItemAddRecommendedMedicineBinding
import com.alsadimoh.graduationproject.retrofit.models.doctorBooking.MedicineCardModel

class AddMedicineForBookingsAdapter(var activity: Activity, var data: MutableList<MedicineCardModel>) :
    RecyclerView.Adapter<AddMedicineForBookingsAdapter.MyViewHolder>() {

    var onClickItem: ((textView:TextView,textViewId:TextView,type:String) -> Unit)? = null
   // var onClickDelete : ((position:Int,model:MedicineCardModel)->Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    inner class MyViewHolder(var root: ItemAddRecommendedMedicineBinding) :
        RecyclerView.ViewHolder(root.root) {

        init {
            root.btnClose.setOnClickListener {
                //data.removeAt(adapterPosition)
                data.remove(data[adapterPosition])
                notifyDataSetChanged()
            }

            root.txtMedicineName.setOnClickListener {
                onClickItem?.invoke(root.txtMedicineName,root.txtMedicineId,"medicine")
            }
            root.txtMedicineUsesPerTime.setOnClickListener {
                onClickItem?.invoke(root.txtMedicineUsesPerTime,root.txtPeriodId,"period")
            }

            root.txtMedicineUsesPerTime.addTextChangedListener{
                data[adapterPosition].periods = root.txtMedicineUsesPerTime.text.toString()
            }
            root.txtMedicineId.addTextChangedListener{
                data[adapterPosition].medicine_id = root.txtMedicineId.text.toString()
            }
            root.txtPeriodId.addTextChangedListener{
                data[adapterPosition].period_id = root.txtPeriodId.text.toString()
            }

            root.txtMedicineName.addTextChangedListener{
                data[adapterPosition].name = root.txtMedicineName.text.toString()
            }
            root.txtMedicineDuration.addTextChangedListener {
                data[adapterPosition].duration = root.txtMedicineDuration.text.toString()
            }
        }

        fun bind(model: MedicineCardModel) {

            if (data.size>=2){
                root.btnClose.visibility = View.VISIBLE
            }else{
                root.btnClose.visibility = View.GONE
            }

            if (!(model.name.isNullOrEmpty())){
                root.txtMedicineName.text = model.name
            }else{
                root.txtMedicineName.text = "لم يتم التحديد"
            }

            if (!(model.medicine_id.isNullOrEmpty())){
                root.txtMedicineId.text = model.medicine_id
            }else{
                root.txtMedicineId.text = "لم يتم التحديد"
            }


            if (!(model.period_id.isNullOrEmpty())){
                root.txtPeriodId.text = model.period_id
            }else{
                root.txtPeriodId.text = "لم يتم التحديد"
            }


            if (!(model.periods.isNullOrEmpty())){
                root.txtMedicineUsesPerTime.text = model.periods
            }else{
                root.txtMedicineUsesPerTime.text = "لم يتم التحديد"
            }


            if (!(model.duration.isNullOrEmpty())){
                root.txtMedicineDuration.setText(model.duration)
            }else{
                root.txtMedicineDuration.text.clear()
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = ItemAddRecommendedMedicineBinding.inflate(
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