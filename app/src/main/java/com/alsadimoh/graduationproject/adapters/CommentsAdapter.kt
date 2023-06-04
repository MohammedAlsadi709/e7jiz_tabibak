package com.alsadimoh.graduationproject.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.databinding.ItemCommentBinding
import com.alsadimoh.graduationproject.retrofit.models.userDoctors.CommentForUserModel
import java.text.SimpleDateFormat
import java.util.*

class CommentsAdapter(var activity: Activity,val data:MutableList<CommentForUserModel>): RecyclerView.Adapter<CommentsAdapter.MyViewHolder>() {

   lateinit var s: SimpleDateFormat
    var isShowMore = false
    inner class MyViewHolder(var root: ItemCommentBinding) : RecyclerView.ViewHolder(root.root) {
        init {
            s = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            root.txtShowMore.setOnClickListener {
                if (isShowMore){
                    root.txtCommentBody.text = data[adapterPosition].comment.substring(0,75)
                    isShowMore = false
                }else{
                    root.txtCommentBody.text = data[adapterPosition].comment
                    isShowMore = true
                }
            }
        }
        fun bind(model: CommentForUserModel){
            root.txtCommentName.text = model.user.name

            CommonConstants.loadImageWithPicasso(model.user.image,root.imgComment)

            // UTC -> Locale
            val date = s.parse(CommonConstants.convertStrUTCTimeToStrLocal(model.created_at))!!.time
           // val date = s.parse(model.created_at)!!.time
            root.txtCreateAt.text = CommonConstants.getStringDifferenceBetweenTwoDates(date)

           if (model.comment.length > 75){
               root.txtCommentBody.text = model.comment.substring(0,75)
               root.txtShowMore.visibility = View.VISIBLE
           }else{
               root.txtCommentBody.text = model.comment
               root.txtShowMore.visibility = View.GONE
           }

            root.ratingBar.rating = model.rate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = ItemCommentBinding.inflate(
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