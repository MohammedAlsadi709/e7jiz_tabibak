package com.alsadimoh.graduationproject.chatUtils.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.classes.CommonConstants.getStringDifferenceBetweenTwoDates
import com.alsadimoh.graduationproject.chatUtils.models.UserModelForRegister
import com.alsadimoh.graduationproject.databinding.ItemChatUserBinding

class ChatUsersAdapter(var activity: Activity, val data:MutableList<UserModelForRegister>): RecyclerView.Adapter<ChatUsersAdapter.MyViewHolder>() {

    var onClickChatItem:((modelForRegister: UserModelForRegister)->Unit)? = null
    inner class MyViewHolder(private var root: ItemChatUserBinding) : RecyclerView.ViewHolder(root.root) {
        init {
            root.itemChat.setOnClickListener {
                onClickChatItem?.invoke(data[adapterPosition])
            }
        }
        fun bind(modelForRegister: UserModelForRegister){
            root.txtUsername.text = modelForRegister.username
            CommonConstants.stringToBitmap(modelForRegister.image!!,root.imgUserProfile)
            root.txtMessage.text = modelForRegister.lastMessageBody
            if ( modelForRegister.lastMessageTime != null){
                root.txtMessageTime.text = getStringDifferenceBetweenTwoDates(modelForRegister.lastMessageTime!!.toLong())

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = ItemChatUserBinding.inflate(
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