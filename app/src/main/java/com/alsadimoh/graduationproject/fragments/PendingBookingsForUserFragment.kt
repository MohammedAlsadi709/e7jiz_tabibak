package com.alsadimoh.graduationproject.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.adapters.UserBookingsAdapter
import com.alsadimoh.graduationproject.databinding.FragmentPendingBookingsForUserBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.userBooking.BookingModel
import com.alsadimoh.graduationproject.retrofit.models.userBooking.ProfileToBooking
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory

class PendingBookingsForUserFragment : Fragment(R.layout.fragment_pending_bookings_for_user) {

    lateinit var binding: FragmentPendingBookingsForUserBinding

    private val text = "PendingBookingsForUserFragment"
    private lateinit var viewModel: MyViewModel
    private var canSendRequest = true
    private lateinit var timesAdapter  : UserBookingsAdapter
    private lateinit var modelPressed : BookingModel

    override fun onResume() {
        super.onResume()

        if (canSendRequest) {
            viewModel.getPendingBookings()
            canSendRequest = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentPendingBookingsForUserBinding.inflate(inflater, container, false)
        canSendRequest = true // عشان لما يرجع من شاشة اخرى يرفرش

        initViewModel()
        getPendingBookingsResponse()
        getCancelBookingForUserResponse()

        return binding.root
    }


    private fun initViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService))
        ).get(MyViewModel::class.java)
    }


    private fun getPendingBookingsResponse() {
        viewModel.getPendingBookingsResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val response = it.data
                    if (response!!.items.isNotEmpty()){
                        setPendingBookingsItems(response.items as MutableList<BookingModel>)
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


    @SuppressLint("NotifyDataSetChanged")
    private fun getCancelBookingForUserResponse() {
        viewModel.getCancelBookingForUser().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                 //   val response = it.data
                    timesAdapter.data.remove(modelPressed)
                    timesAdapter.notifyDataSetChanged()
                    if (timesAdapter.data.isNotEmpty()){
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

    private fun setPendingBookingsItems(data:MutableList<BookingModel>){
        timesAdapter  = UserBookingsAdapter(requireActivity(),data,"pending")
        binding.rvPendingTimes.adapter = timesAdapter
        binding.rvPendingTimes.layoutManager = LinearLayoutManager(requireActivity())

        timesAdapter.onCancelClick = {model ->
            modelPressed = model
           viewModel.cancelBookingForUser(model.id)
        }

        timesAdapter.onEditClick = {model ->
           val action = PendingBookingsForUserFragmentDirections.actionGlobalBookingAppointmentFragment(
               ProfileToBooking(model.doctor.id,model.doctor.name,model.doctor.specialization.title,model.doctor.image,model.doctor.patient_examination_price.toFloat()),true,model.id
           )
           findNavController().navigate(action)
        }


        timesAdapter.onItemClick = { model->
            val action =PendingBookingsForUserFragmentDirections.actionGlobalIncomingAppointmentDetailsFragment(model.id,false)
            findNavController().navigate(action)
        }
    }
}