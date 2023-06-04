package com.alsadimoh.graduationproject.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.adapters.NotificationsAdapter
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.databinding.FragmentNotificationsBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.NotificationModel
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private val text = "NotificationsFragment"
    private lateinit var viewModel: MyViewModel
    lateinit var binding: FragmentNotificationsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        initViewModel()
        getPendingBookingsResponse()
        viewModel.getAllNotifications()

        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService))
        ).get(MyViewModel::class.java)
    }


    private fun getPendingBookingsResponse() {
        viewModel.getAllNotificationsResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val response = it.data
                    if (response!!.items.isNotEmpty()){
                        setNotificationsItems(response.items as MutableList<NotificationModel>)
                        binding.layoutEmpty.visibility = View.GONE
                    }else{
                        binding.layoutEmpty.visibility = View.VISIBLE
                    }
                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    CommonConstants.hideProgressDialog()
                    CommonConstants.showCustomToast(requireActivity(),  it.message.toString(),"error")
                }
            }
        }
    }

    private fun setNotificationsItems(mutableList: MutableList<NotificationModel>) {
        val notificationsAdapter = NotificationsAdapter(requireActivity(),mutableList)
        binding.rvNotifications.adapter  = notificationsAdapter
        binding.rvNotifications.layoutManager = LinearLayoutManager(requireActivity())
    }

}