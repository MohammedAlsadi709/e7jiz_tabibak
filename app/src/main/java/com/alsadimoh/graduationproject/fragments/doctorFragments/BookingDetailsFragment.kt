package com.alsadimoh.graduationproject.fragments.doctorFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.classes.CommonConstants.getDurationFromDateUntilNow
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.adapters.doctorsAdapters.IncomingShowMedicinesAdapter
import com.alsadimoh.graduationproject.databinding.FragmentBookingDetailsBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.doctorBooking.IncomingBookingMedicineModel
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*


class BookingDetailsFragment : Fragment(R.layout.fragment_booking_details) {
    private val text = "BookingDetailsFragment"
    private lateinit var viewModel: MyViewModel
    lateinit var binding: FragmentBookingDetailsBinding
    val args : BookingDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentBookingDetailsBinding.inflate(inflater, container, false)

        settingScreen()

        initViewModel()
        getBookingsInfoForDoctorResponse()
        viewModel.getBookingsInfoForDoctor(args.bookingId)

        binding.btnCancel.setOnClickListener {
           if (args.bookingType == "incoming"){
               val action = BookingDetailsFragmentDirections.actionBookingDetailsFragmentToBookingCancelFragment(args.bookingId,
                   isPopToDoctorHome = true,
                   isIncoming = true,
                   status = ""
               )
               findNavController().navigate(action)
           }
        }


        binding.btnEnterResult.setOnClickListener {
            val action = BookingDetailsFragmentDirections.actionBookingDetailsFragmentToTestingResultFragment(args.bookingId)
            findNavController().navigate(action)
        }
        binding.btnEdit.setOnClickListener {
            val action = BookingDetailsFragmentDirections.actionBookingDetailsFragmentToTestingResultFragment(args.bookingId,true)
            findNavController().navigate(action)
        }


        return binding.root
    }
    private fun initViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService))
        ).get(MyViewModel::class.java)
    }

    private fun getBookingsInfoForDoctorResponse() {
        viewModel.getBookingsInfoForDoctorResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.layoutContent.visibility = View.VISIBLE
                    binding.layoutEmpty.visibility = View.GONE
                    CommonConstants.hideProgressDialog()
                    val response = it.data!!.items

                    val s = SimpleDateFormat("EEEE dd MMMM yyyy", Locale("ar"))
                    val s2 = SimpleDateFormat("yyyy-MM-dd", Locale.US)

                    if (response.editable != null && response.editable == true){
                        binding.layoutEdit.visibility = View.VISIBLE

                    }else{
                        binding.layoutEdit.visibility = View.GONE
                    }

                    binding.txtBookingDate.text = s.format(s2.parse(response.date)!!)
                    binding.txtBookingTime.text = response.time
                    binding.txtPatientName.text = response.user.name
                    binding.txtPatientGender.text = response.user.gender
                    binding.txtPatientAge.text = getDurationFromDateUntilNow(Date(),s2.parse(response.user.dateOfBirth)!!)
                    binding.txtPatientPhoneNumber.text =response.user.phone_number
                    binding.txtCancelWithDoctorAcceptance.text = response.cancel_with_doctor_accept.toString()
                    binding.txtTotalBookingCount.text = response.total_booking_count.toString()
                    binding.txtComingCount.text = response.attending_count.toString()
                    binding.txtUnComingCount.text = response.inattending_count.toString()
                    if (args.bookingType == "incoming"){
                        setMedicineItems(response.medicines as MutableList<IncomingBookingMedicineModel>)
                    }
                }

                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }

                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    binding.layoutContent.visibility = View.INVISIBLE
                    binding.layoutEmpty.visibility = View.VISIBLE
                    CommonConstants.showCustomToast(requireActivity(), it.message.toString(),"error")
                    CommonConstants.hideProgressDialog()
                }
            }
        }
    }

    private fun setMedicineItems(data:MutableList<IncomingBookingMedicineModel>){
        val incomingShowMedicinesAdapter = IncomingShowMedicinesAdapter(requireActivity(),data)
        binding.rvMedicines.adapter = incomingShowMedicinesAdapter
        binding.rvMedicines.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvMedicines.isNestedScrollingEnabled = false

        if (incomingShowMedicinesAdapter.data.isNotEmpty()){
            binding.txtM.visibility = View.VISIBLE
            binding.layoutMedicinesHeader.visibility = View.VISIBLE
            binding.rvMedicines.visibility = View.VISIBLE
        }else{
            binding.txtM.visibility = View.GONE
            binding.layoutMedicinesHeader.visibility = View.GONE
            binding.rvMedicines.visibility = View.GONE
        }
    }

    private fun settingScreen(){
        if (args.bookingType == "incoming"){
            binding.layoutEnterResult.visibility = View.VISIBLE
            binding.layoutCancel.visibility = View.VISIBLE
            binding.txtM.visibility = View.VISIBLE
            binding.layoutMedicinesHeader.visibility = View.VISIBLE
            binding.rvMedicines.visibility = View.VISIBLE

        }else{
            binding.layoutEnterResult.visibility = View.GONE
            binding.layoutCancel.visibility = View.GONE
            binding.txtM.visibility = View.GONE
            binding.layoutMedicinesHeader.visibility = View.GONE
            binding.rvMedicines.visibility = View.GONE
        }
    }

}