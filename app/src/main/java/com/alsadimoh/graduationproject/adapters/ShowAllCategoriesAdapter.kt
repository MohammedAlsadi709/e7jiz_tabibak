package com.alsadimoh.graduationproject.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.databinding.ItemShowAllCategoriesBinding
import com.alsadimoh.graduationproject.classes.CommonConstants.onOpenFragmentChangeEmptyMenuItemIconBitmapDrawable
import com.alsadimoh.graduationproject.retrofit.models.userHome.SpecializationModel

class ShowAllCategoriesAdapter(var activity: Activity,val data :MutableList<SpecializationModel>) :
    RecyclerView.Adapter<ShowAllCategoriesAdapter.MyViewHolder>() {

    var onCategoryClick: ((model: SpecializationModel) -> Unit)? = null

    inner class MyViewHolder(var root: ItemShowAllCategoriesBinding) :
        RecyclerView.ViewHolder(root.root) {

        init {
            root.categoryItem.setOnClickListener {
                onCategoryClick?.invoke( data[adapterPosition])
                onOpenFragmentChangeEmptyMenuItemIconBitmapDrawable?.invoke(data[adapterPosition].image)
            }
        }
        fun bind(model: SpecializationModel){
            CommonConstants.loadCategoryImageWithPicasso(model.image,root.imgCategory)
            root.txtCategoryName.text = model.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = ItemShowAllCategoriesBinding.inflate(
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