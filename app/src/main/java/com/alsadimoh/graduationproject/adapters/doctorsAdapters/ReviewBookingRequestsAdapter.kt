package com.alsadimoh.graduationproject.adapters.doctorsAdapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.databinding.ItemReviewBookingBinding
import com.alsadimoh.graduationproject.retrofit.models.doctorBooking.BookingModelForDoctor
import java.text.SimpleDateFormat
import java.util.*

class ReviewBookingRequestsAdapter(var activity: Activity, var data: MutableList<BookingModelForDoctor>) : RecyclerView.Adapter<ReviewBookingRequestsAdapter.MyViewHolder>() {

    var onClickItem: ((model: BookingModelForDoctor) -> Unit)? = null
    var onClickAcceptItem: ((model: BookingModelForDoctor) -> Unit)? = null
    var onClickCancelItem: ((model: BookingModelForDoctor) -> Unit)? = null
    val s = SimpleDateFormat("EEEE dd MMMM yyyy", Locale.US)
    val s2 = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    inner class MyViewHolder(var root: ItemReviewBookingBinding) :
        RecyclerView.ViewHolder(root.root) {
        init {
            root.item.setOnClickListener {
                onClickItem?.invoke(data[adapterPosition])
                CommonConstants.onOpenFragmentChangeEmptyMenuItemIconBitmapDrawable?.invoke(data[adapterPosition].user.image)
            }
            root.btnCancel.setOnClickListener {
                onClickCancelItem?.invoke(data[adapterPosition])
            }
            root.btnAccept.setOnClickListener {
                onClickAcceptItem?.invoke(data[adapterPosition])
            }
        }


        @SuppressLint("SetTextI18n")
        fun bind(model: BookingModelForDoctor) {
            val status  = when {
                model.status.startsWith("pending") -> {
                    "(حجز)"
                }
                model.status.startsWith("edit") -> {
                    "(تعديل)"
                }
                model.status.startsWith("cancel") -> {
                    "(الغاء)"
                }
                else -> {
                    ""
                }
            }
            CommonConstants.loadImageWithPicasso(model.user.image, root.imgItem)
            root.txtName.text = model.user.name+" $status"
            root.txtCreateAt.text = s.format(s2.parse(model.date)!!)
            root.txtCreateTime.text = model.time


            if ( (model.status.startsWith("pending")||( model.status.startsWith("edit")) )&& model.is_valid ){
                root.btnAccept.isEnabled = true
                root.layoutAccept.background.setTint(activity.getColor(R.color.primaryColor))
                if ( model.status.startsWith("edit")){
                    root.txtCreateTime.text = model.time +" إلى " + model.doctor_notes.substringAfterLast(",")
                }
            }else if (model.status.startsWith("cancel")){
                root.btnAccept.isEnabled = true
                root.layoutAccept.background.setTint(activity.getColor(R.color.primaryColor))
            }else{
                root.btnAccept.isEnabled = false
                root.layoutAccept.background.setTint(activity.getColor(R.color.grayIcons))
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = ItemReviewBookingBinding.inflate(
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