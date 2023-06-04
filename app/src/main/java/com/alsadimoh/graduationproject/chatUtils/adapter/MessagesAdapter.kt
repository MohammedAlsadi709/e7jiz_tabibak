package com.alsadimoh.graduationproject.chatUtils.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.chatUtils.models.MessageModel
import com.alsadimoh.graduationproject.databinding.ItemMessageBinding
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*


class MessagesAdapter(var activity: Activity, val data:MutableList<MessageModel>): RecyclerView.Adapter<MessagesAdapter.MyViewHolder>() {

    var onClickChatItem:((model: MessageModel)->Unit)? = null
    lateinit var mAuth: FirebaseAuth
    lateinit var s:SimpleDateFormat
    inner class MyViewHolder(var root: ItemMessageBinding) : RecyclerView.ViewHolder(root.root) {
        init {
           s = SimpleDateFormat("HH:mm", Locale.US)
           mAuth = FirebaseAuth.getInstance()
        }

        @SuppressLint("RtlHardcoded")
        fun bind(model: MessageModel){

            if (model.senderId==mAuth.currentUser?.uid){
                root.rootLayout.gravity = Gravity.RIGHT
                root.cardReceiverProfileImage.visibility = View.GONE
                root.cardSenderProfileImage.visibility = View.VISIBLE
                CommonConstants.stringToBitmap(model.profilePicture!!,root.imgSenderProfileImage)

                root.item.background.setTint(Color.parseColor("#00A4CE"))
                if (model.isImage){
                    root.imgMessage.visibility = View.VISIBLE
                    root.txtMessage.visibility = View.GONE
                    CommonConstants.stringToBitmap(model.message!!,root.imgMessage)
                }else{
                    root.imgMessage.visibility = View.GONE
                    root.txtMessage.visibility = View.VISIBLE
                    root.txtMessage.text = model.message
                    root.txtTime.text = s.format(model.time!!.toLong())
                    root.txtMessage.setTextColor(Color.parseColor("#FFFFFF"))
                }

                if (model.isSentSuccessfully && adapterPosition == data.lastIndex){
                    root.imgIsSent.visibility = View.VISIBLE
                }else{
                    root.imgIsSent.visibility = View.GONE
                }

            }else{
                root.rootLayout.gravity = Gravity.LEFT
                root.cardSenderProfileImage.visibility = View.GONE
                root.cardReceiverProfileImage.visibility = View.VISIBLE
                CommonConstants.stringToBitmap(model.profilePicture!!,root.imgReceiverProfileImage)

                root.item.background.setTint(Color.parseColor("#EBEBEB"))
                if (model.isImage){
                    root.imgMessage.visibility = View.VISIBLE
                    root.txtMessage.visibility = View.GONE
                    CommonConstants.stringToBitmap(model.message!!,root.imgMessage)
                }else{
                    root.imgMessage.visibility = View.GONE
                    root.txtMessage.visibility = View.VISIBLE
                    root.txtMessage.text = model.message
                    root.txtTime.text = s.format(model.time!!.toLong())
                    root.txtMessage.setTextColor(Color.parseColor("#000000"))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = ItemMessageBinding.inflate(
            LayoutInflater.from(activity)//parent.context
            , parent, false)
        return MyViewHolder(inflate)
    }


    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position])
    }



}