package com.alsadimoh.graduationproject.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alsadimoh.graduationproject.databinding.ItemCaregoryRadioBtnBinding
import com.alsadimoh.graduationproject.retrofit.models.userHome.SpecializationModel

class CategoriesDialogAdapter(var activity: Activity, val data: MutableList<SpecializationModel>) :
    RecyclerView.Adapter<CategoriesDialogAdapter.MyViewHolder>() {

    var onClickCategoryItem: ((position:Int,model: SpecializationModel) -> Unit)? = null

    inner class MyViewHolder(var root: ItemCaregoryRadioBtnBinding) :
        RecyclerView.ViewHolder(root.root) {
        init {
            root.item.setOnClickListener {
                onClickCategoryItem?.invoke(adapterPosition,data[adapterPosition])
            }
            root.cbItem.setOnClickListener {
                onClickCategoryItem?.invoke(adapterPosition,data[adapterPosition])
            }

        }

        fun bind(model: SpecializationModel) {
            root.txtItemName.text = model.title
            root.cbItem.isChecked = model.isSelected
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = ItemCaregoryRadioBtnBinding.inflate(
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