package com.alsadimoh.graduationproject.adapters.doctorsAdapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alsadimoh.graduationproject.databinding.ItemBottomSheetBinding
import com.alsadimoh.graduationproject.retrofit.models.doctorBooking.BottomSheetItems

class BottomSheetAdapter(var activity: Activity, var data: MutableList<BottomSheetItems>) :
    RecyclerView.Adapter<BottomSheetAdapter.MyViewHolder>() {

    var onClickItem: ((model: BottomSheetItems,position:Int) -> Unit)? = null

    inner class MyViewHolder(var root: ItemBottomSheetBinding) :
        RecyclerView.ViewHolder(root.root) {

        init {
            root.item.setOnClickListener {
                onClickItem?.invoke(data[adapterPosition],adapterPosition)
            }
            root.cbItem.setOnClickListener {
               onClickItem?.invoke(data[adapterPosition],adapterPosition)
            }
        }

        fun bind(model: BottomSheetItems) {
            root.itemName.text = model.itemName
            root.cbItem.isChecked = model.isChecked
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = ItemBottomSheetBinding.inflate(
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