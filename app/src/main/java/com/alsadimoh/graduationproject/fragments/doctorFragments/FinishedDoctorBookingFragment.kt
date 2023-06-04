package com.alsadimoh.graduationproject.fragments.doctorFragments

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
import com.alsadimoh.graduationproject.adapters.doctorsAdapters.DoctorHomeBookingAdapter
import com.alsadimoh.graduationproject.databinding.FragmentFinishedDoctorBookingBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.doctorBooking.BookingModelForDoctor
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory


class FinishedDoctorBookingFragment : Fragment(R.layout.fragment_finished_doctor_booking) {

     val text: String = "FinishedDoctorBookingFragment"
    private lateinit var viewModel: MyViewModel
    lateinit var binding: FragmentFinishedDoctorBookingBinding
    private var canSendRequest = true

    override fun onResume() {
        super.onResume()
        if (canSendRequest){
            viewModel.getFinishedBookingForDoctor()
            canSendRequest = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentFinishedDoctorBookingBinding.inflate(inflater, container, false)
        canSendRequest = true // عشان لما يرجع من شاشة اخرى يرفرش


        initViewModel()
        getFinishedBookingsForDoctorResponse()

        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService))
        ).get(MyViewModel::class.java)
    }

    private fun getFinishedBookingsForDoctorResponse() {
        viewModel.getFinishedBookingsForDoctorResponse().observe(viewLifecycleOwner) {
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
                    CommonConstants.hideProgressDialog()
                    CommonConstants.showCustomToast(requireActivity(),  it.message.toString(),"error")
                }
            }
        }
    }

    private fun setAdapterItems(data: MutableList<BookingModelForDoctor>){
        val bookingAdapter = DoctorHomeBookingAdapter(requireActivity(),data)
        binding.rvFinishedBookings.adapter = bookingAdapter
        binding.rvFinishedBookings.layoutManager  = LinearLayoutManager(requireActivity())
        if (bookingAdapter.data.isNotEmpty()){
            binding.layoutEmpty.visibility = View.GONE
        }else{
            binding.layoutEmpty.visibility = View.VISIBLE
        }

        bookingAdapter.onClickBookingItem= { model ->
            val action = FinishedDoctorBookingFragmentDirections.actionGlobalBookingDetailsFragment(model.id,"finished")
            findNavController().navigate(action)
        }
    }

}