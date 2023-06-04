package com.alsadimoh.graduationproject.chatUtils.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.databinding.ItemImageMessageBinding

class ImageMessagesAdapter(var activity: Activity, val data: MutableList<String>) :
    RecyclerView.Adapter<ImageMessagesAdapter.MyViewHolder>() {

    var onClickDeleteItem: ((image: String) -> Unit)? = null

    inner class MyViewHolder(var root: ItemImageMessageBinding) :
        RecyclerView.ViewHolder(root.root) {

        init {
            root.btnDeleteImage.setOnClickListener {
                onClickDeleteItem?.invoke(data[adapterPosition])
            }
        }

        fun bind(image: String) {
            CommonConstants.stringToBitmap(image, root.imgMessage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = ItemImageMessageBinding.inflate(
            LayoutInflater.from(activity)//parent.context
            , parent, false
        )
        return MyViewHolder(inflate)
    }


    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position])
    }


}