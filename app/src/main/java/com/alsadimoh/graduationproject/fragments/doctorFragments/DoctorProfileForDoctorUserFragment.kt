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
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.databinding.FragmentDoctorProfileForDoctorUserBinding
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.doctorProfile.UpdateDoctorProfile
import com.alsadimoh.graduationproject.retrofit.models.userDoctors.DoctorProfileForUserResponse
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory

class DoctorProfileForDoctorUserFragment : Fragment(R.layout.fragment_doctor_profile_for_doctor_user) {

    private val text = "DoctorProfileForDoctorUserFragment"
    private lateinit var viewModel: MyViewModel
    lateinit var binding: FragmentDoctorProfileForDoctorUserBinding
     var image :String? = null
    var model :UpdateDoctorProfile? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentDoctorProfileForDoctorUserBinding.inflate(inflater, container, false)

        initViewModel()
        getDoctorProfile()

        viewModel.getDoctorProfileForDoctor()

        binding.btnShowReviews.setOnClickListener {
            val action = DoctorProfileForDoctorUserFragmentDirections.actionDoctorProfileForDoctorUserFragmentToReviewsFragment(2,false)
            findNavController().navigate(action)
          //  CommonConstants.onOpenFragmentsUpdateMenuItemsIcons?.invoke(R.mipmap.rated_doctor_item)
            if (image != null){
                CommonConstants.onOpenFragmentChangeEmptyMenuItemIconBitmapDrawable?.invoke(image!!)
            }
        }

        binding.btnEditProfile.setOnClickListener {
            if (model != null){
                val action = DoctorProfileForDoctorUserFragmentDirections.actionDoctorProfileForDoctorUserFragmentToEditProfileForDoctor(model!!)
                findNavController().navigate(action)
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

    private fun getDoctorProfile() {
        viewModel.getDoctorProfileForDoctorResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    binding.layoutContent.visibility = View.VISIBLE
                    binding.layoutEmpty.visibility = View.GONE
                    val response = it.data!!.items
                    model = UpdateDoctorProfile(response.bio,response.average_answer_time,response.waiting_time,response.clinic.address,response.patient_examination_price,response.lat,response.long,response.image)
                    handleDataInThisPage(response)
                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    binding.layoutContent.visibility = View.GONE
                    binding.layoutEmpty.visibility = View.VISIBLE
                    CommonConstants.showCustomToast(requireActivity(), it.message.toString(),"error")
                    CommonConstants.hideProgressDialog()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleDataInThisPage(doctorData: DoctorProfileForUserResponse){
        image = doctorData.image
        CommonConstants.loadImageWithPicasso(doctorData.image,binding.imgDoctor)
        binding.txtDoctorName.text = doctorData.name
        binding.txtAbstract.text = doctorData.bio
        binding.txtRatingStartsNumber.text = doctorData.rate.toString()
        binding.ratingBar.rating = doctorData.rate
        binding.txtReviewNumber.text = doctorData.num_ratings.toString()
        binding.txtPhoneNumber.text = doctorData.phone_number
        binding.txtGender.text = if(doctorData.gender == "male"){
            "ذكر"
        }else{
            "أنثى"
        }
        binding.txtDomain.text = doctorData.specialization.title
        binding.txtClinicName.text = doctorData.clinic.name
        binding.txtPrice.text = doctorData.patient_examination_price.toString()
        binding.txtWaitingDur.text = CommonConstants.getStringTimeFormat((doctorData.average_answer_time*60).toFloat())
        binding.txtWorkTimes.text = "يوم ${doctorData.worktime[0].days} من ${doctorData.worktime[0].open_time} إلى ${doctorData.worktime[0].close_time}"
        binding.txtLocation.text = doctorData.clinic.address
    }


}