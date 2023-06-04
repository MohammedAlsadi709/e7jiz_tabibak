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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.adapters.RecommendedMedicineAdapter
import com.alsadimoh.graduationproject.databinding.FragmentFinishedAppointmentDetailsBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.userBooking.BookingModel
import com.alsadimoh.graduationproject.retrofit.models.userBooking.ProfileToBooking
import com.alsadimoh.graduationproject.retrofit.models.userBooking.TreatmentsModel
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory


class FinishedAppointmentDetailsFragment :
    Fragment(R.layout.fragment_finished_appointment_details) {

    private val text = "FinishedAppointmentDetailsFragment"
    private lateinit var viewModel: MyViewModel
    val  args:FinishedAppointmentDetailsFragmentArgs by navArgs()
    lateinit var binding: FragmentFinishedAppointmentDetailsBinding
    var doctor :ProfileToBooking?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFinishedAppointmentDetailsBinding.inflate(inflater, container, false)

        initViewModel()
        getFinishedBookingDetailsForUserResponse()
        viewModel.getFinishedBookingDetailsForUser(args.bookingId)

        binding.btnTakeAppointmentAgain.setOnClickListener {
           if (doctor!= null){
               val action = FinishedAppointmentDetailsFragmentDirections.actionFinishedAppointmentDetailsFragmentToBookingAppointmentFragment(doctor!!)
               findNavController().navigate(action)
           }else{
               CommonConstants.showCustomToast(requireActivity(),  "حدث خلل ما","error")
           }
        }

        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService))
        ).get(MyViewModel::class.java)
    }

    private fun getFinishedBookingDetailsForUserResponse() {
        viewModel.getFinishedBookingDetailsForUserResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.layoutContent.visibility = View.VISIBLE
                    binding.layoutEmptyContent.visibility = View.GONE
                    CommonConstants.hideProgressDialog()
                    val response = it.data!!.items
                    doctor = ProfileToBooking(response.doctor_id,response.doctor.name,response.doctor.specialization.title,response.doctor.image,response.doctor.patient_examination_price.toFloat())
                    loadDataToThePage(response)
                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    binding.layoutContent.visibility = View.GONE
                    binding.layoutEmptyContent.visibility = View.VISIBLE
                    CommonConstants.showCustomToast(requireActivity(),  it.message.toString(),"error")
                    CommonConstants.hideProgressDialog()
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun loadDataToThePage(model: BookingModel){
        CommonConstants.loadImageWithPicasso(model.doctor.image,binding.imgDoctor)
        binding.txtDoctorName.text = model.doctor.name
        binding.txtDoctorTitle.text = model.doctor.specialization.title + " - " + model.doctor.clinic.name
        binding.txtDate.text = model.date
        binding.txtTime.text = model.time
        binding.txtPrice.text = model.price.toString()
        binding.txtDoctorNotice.text = model.doctor_notes

       if (model.treatments.isNotEmpty()){
           binding.txtM.visibility = View.VISIBLE
           binding.layoutRecommendedHeader.visibility = View.VISIBLE
           binding.rvRecommendedMedicines.visibility = View.VISIBLE
           binding.layoutEmpty.visibility = View.GONE
           setMedicineItems(model.treatments as MutableList<TreatmentsModel>)
       }else{
           binding.txtM.visibility = View.GONE
           binding.layoutRecommendedHeader.visibility = View.GONE
           binding.rvRecommendedMedicines.visibility = View.GONE
           binding.layoutEmpty.visibility = View.VISIBLE
       }
    }

    private fun setMedicineItems(data:MutableList<TreatmentsModel>){
        val recommendedMedicineAdapter = RecommendedMedicineAdapter(requireActivity(),data)
        binding.rvRecommendedMedicines.adapter = recommendedMedicineAdapter
        binding.rvRecommendedMedicines.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvRecommendedMedicines.isNestedScrollingEnabled = false
    }

}