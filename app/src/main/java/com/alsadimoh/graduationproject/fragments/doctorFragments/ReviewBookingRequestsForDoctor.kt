package com.alsadimoh.graduationproject.fragments.doctorFragments

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
import com.alsadimoh.graduationproject.adapters.doctorsAdapters.ReviewBookingRequestsAdapter
import com.alsadimoh.graduationproject.databinding.FragmentReviewBookingRequestsForDoctorBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.doctorBooking.BookingModelForDoctor
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory

class ReviewBookingRequestsForDoctor : Fragment(R.layout.fragment_review_booking_requests_for_doctor) {

    private val text = "ReviewBookingRequestsForDoctor"
    private lateinit var viewModel: MyViewModel
    lateinit var binding:FragmentReviewBookingRequestsForDoctorBinding
    private lateinit var reviewBookingAdapter : ReviewBookingRequestsAdapter
    private lateinit var objModel :BookingModelForDoctor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentReviewBookingRequestsForDoctorBinding.inflate(inflater, container, false)

        initViewModel()
        getPendingBookingsForDoctorResponse()
        getApproveBookingForDoctorResponse()
        viewModel.getPendingBookingForDoctor()


        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService))
        ).get(MyViewModel::class.java)
    }

    private fun getPendingBookingsForDoctorResponse() {
        viewModel.getPendingBookingsForDoctorResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val response = it.data!!.items
                    setAdapterItems(response as MutableList<BookingModelForDoctor>)
                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    CommonConstants.showCustomToast(requireActivity(),  it.message.toString(),"error")
                    CommonConstants.hideProgressDialog()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getApproveBookingForDoctorResponse() {
        viewModel.getApproveBookingForDoctorResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val response = it.data!!.items
                   // reviewBookingAdapter.data.remove(objModel)
                   // reviewBookingAdapter.notifyDataSetChanged()

                 /*   if (reviewBookingAdapter.data.isEmpty()){
                        binding.layoutEmpty.visibility= View.VISIBLE
                    }else{
                        binding.layoutEmpty.visibility= View.GONE
                    }*/

                    setAdapterItems(response as MutableList<BookingModelForDoctor>)

                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    CommonConstants.showCustomToast(requireActivity(),  it.message.toString(),"error")
                    CommonConstants.hideProgressDialog()
                }
            }
        }
    }

    private fun setAdapterItems(data: MutableList<BookingModelForDoctor>){
        reviewBookingAdapter = ReviewBookingRequestsAdapter(requireActivity(),data)
        binding.rvReviewBookings.adapter = reviewBookingAdapter
        binding.rvReviewBookings.layoutManager  = LinearLayoutManager(requireActivity())

        if (reviewBookingAdapter.data.isNotEmpty()){
            binding.layoutEmpty.visibility = View.GONE
        }else{
            binding.layoutEmpty.visibility = View.VISIBLE
        }

        reviewBookingAdapter.onClickItem= { model ->
            val action = IncomingDoctorBookingFragmentDirections.actionGlobalBookingDetailsFragment(model.id,"pending")
            findNavController().navigate(action)
        }

        reviewBookingAdapter.onClickAcceptItem = {model: BookingModelForDoctor ->
            objModel = model
            val status  = when {
                model.status.startsWith("pending") -> {
                    "pending"
                }
                model.status.startsWith("edit") -> {
                    "edit"
                }
                model.status.startsWith("cancel") -> {
                    "cancel"
                }
                else -> {
                    ""
                }
            }

            viewModel.approveBookingForDoctor(model.id,status)
        }

        reviewBookingAdapter.onClickCancelItem = {model: BookingModelForDoctor ->

            val status  = when {
                model.status.startsWith("pending") -> {
                    "pending"
                }
                model.status.startsWith("edit") -> {
                    "edit"
                }
                model.status.startsWith("cancel") -> {
                    "cancel"
                }
                else -> {
                    ""
                }
            }

            val action = ReviewBookingRequestsForDoctorDirections.actionReviewBookingRequestsForDoctorToBookingCancelFragment(model.id,
                isPopToDoctorHome = false,
                isIncoming = false,
                status = status
            )
            findNavController().navigate(action)
        }
    }
}