package com.alsadimoh.graduationproject.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.databinding.ItemCategoryBinding
import com.alsadimoh.graduationproject.retrofit.models.userHome.SpecializationModel

class CategoriesAdapter(var activity: Activity,val data:MutableList<SpecializationModel>): RecyclerView.Adapter<CategoriesAdapter.MyViewHolder>() {

    var onClickCategoryItem:((model:SpecializationModel)->Unit)? = null
    inner class MyViewHolder(var root: ItemCategoryBinding) : RecyclerView.ViewHolder(root.root) {
        init {
            root.categoryItem.setOnClickListener {
                onClickCategoryItem?.invoke(data[adapterPosition])
               /* root.imgCategory.isDrawingCacheEnabled = true
                root.imgCategory.drawingCache*/
                CommonConstants.onOpenFragmentChangeEmptyMenuItemIconBitmapDrawable?.invoke(data[adapterPosition].image)
            }

        }
        fun bind(model: SpecializationModel){
            //CommonConstants.loadImageWithPicasso(model.image,root.imgCategory)
            CommonConstants.loadCategoryImageWithPicasso(model.image,root.imgCategory)
            root.txtCategoryName.text= model.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = ItemCategoryBinding.inflate(
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