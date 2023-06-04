package com.alsadimoh.graduationproject.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.databinding.ItemCategoryBinding
import com.alsadimoh.graduationproject.databinding.ItemNotificationBinding
import com.alsadimoh.graduationproject.retrofit.models.NotificationModel
import java.text.SimpleDateFormat
import java.util.*

class NotificationsAdapter(var activity: Activity,val data:MutableList<NotificationModel>) :
    RecyclerView.Adapter<NotificationsAdapter.MyViewHolder>() {

    lateinit var s: SimpleDateFormat

    inner class MyViewHolder(var root: ItemNotificationBinding) : RecyclerView.ViewHolder(root.root) {

        init {
            s = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        }

        fun bind(model:NotificationModel){
            root.txtNotificationTitle.text = model.title
            root.txtNotificationDetails.text = model.body

            // UTC -> Locale
            val date = s.parse(CommonConstants.convertStrUTCTimeToStrLocal(model.created_at))!!.time
            root.txtNotificationTime.text = CommonConstants.getStringDifferenceBetweenTwoDates(date)
            root.imgNotificationIcon.setImageResource(when(model.notification_type){
                "remind"-> R.mipmap.alert_blue
                "pending approved"->R.mipmap.true_state
                "approve"->R.mipmap.true_state
                "pending cancel"->R.mipmap.warning_blue
                "done"->R.mipmap.ic_done
                "edit"->R.mipmap.compose_blue
                "cancel"->R.mipmap.warning_blue
                "rating"->R.mipmap.favorite_blue
                else->R.mipmap.calendar_blue
            })

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = ItemNotificationBinding.inflate(
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