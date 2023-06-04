package com.alsadimoh.graduationproject.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.databinding.ItemBtnsForPaginationBinding
import com.alsadimoh.graduationproject.retrofit.models.userDoctors.PaginationButtonModel

class PaginationAdapter(var activity: Activity, val data:MutableList<PaginationButtonModel>): RecyclerView.Adapter<PaginationAdapter.MyViewHolder>() {

    var onClickItem:((position:Int,model: PaginationButtonModel)->Unit)? = null
    inner class MyViewHolder(var root: ItemBtnsForPaginationBinding) : RecyclerView.ViewHolder(root.root) {
        init {
            root.item.setOnClickListener {
                onClickItem?.invoke(adapterPosition,data[adapterPosition])
            }
        }
        fun bind(model: PaginationButtonModel){
            root.itemButton.text = model.number.toString()
            if (model.isSelected){
                root.item.background.setTint(activity.getColor(R.color.primaryColor))
                root.itemButton.setTextColor(activity.getColor(R.color.white))
            }else{
                root.item.background.setTint(activity.getColor(R.color.backgroundColor))
                root.itemButton.setTextColor(activity.getColor(R.color.grayText))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = ItemBtnsForPaginationBinding.inflate(
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