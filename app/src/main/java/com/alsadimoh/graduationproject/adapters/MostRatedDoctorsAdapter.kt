package com.alsadimoh.graduationproject.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.databinding.ItemRatedDoctorBinding
import com.alsadimoh.graduationproject.retrofit.models.userHome.DoctorModelForUserHome

class MostRatedDoctorsAdapter(var activity: Activity, var data:MutableList<DoctorModelForUserHome>,val isBookingMarks:Boolean = false): RecyclerView.Adapter<MostRatedDoctorsAdapter.MyViewHolder>() {

     var onClickItem : ((model:DoctorModelForUserHome)->Unit)? = null

    inner class MyViewHolder(var root: ItemRatedDoctorBinding) : RecyclerView.ViewHolder(root.root) {
      init {
          root.itemRatedDoctor.setOnClickListener {
              onClickItem?.invoke(data[adapterPosition])
          }
      }


      fun bind(model:DoctorModelForUserHome){
          if(model.image!= null){
              CommonConstants.loadImageWithPicasso(model.image,root.imgRatedDoctor)
          }
          root.txtRatedDoctorName.text = model.name
          if (isBookingMarks){
              root.txtDoctorClinicTitle.text = model.specialization.title+" - "+model.clinic.name
          }else{
              root.txtDoctorClinicTitle.text = "${model.spec} - ${model.clin}"
          }
          root.txtDoctorClinicAddress.text = model.address
          root.txtDoctorRate.text = model.rate.toString()
      }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = ItemRatedDoctorBinding.inflate(
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