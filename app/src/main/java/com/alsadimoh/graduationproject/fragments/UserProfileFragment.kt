package com.alsadimoh.graduationproject.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.databinding.FragmentUserProfileBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.userProfile.UpdateProfileModel
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory

class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {

    private lateinit var viewModel: MyViewModel
    lateinit var binding: FragmentUserProfileBinding
    val text = "UserProfileFragment"
     var model : UpdateProfileModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =  FragmentUserProfileBinding.inflate(inflater, container, false)

        initViewModel()
        getUserProfileResponse()
        viewModel.getUserProfile()

        binding.btnAboutUs.setOnClickListener {
            val action = UserProfileFragmentDirections.actionUserProfileFragmentToAboutAppFragment()
            findNavController().navigate(action)
        }

        binding.btnEditProfile.setOnClickListener {
           if (model!= null){
               val action = UserProfileFragmentDirections.actionUserProfileFragmentToEditProfileFragment(model!!)
               findNavController().navigate(action)
           }else{
               CommonConstants.showCustomToast(requireActivity(), "حدث خطأ ما!!","error")
           }
        }

        binding.btnFavorite.setOnClickListener {
            val action = UserProfileFragmentDirections.actionUserProfileFragmentToFavoriteFragment()
            findNavController().navigate(action)
        }

        binding.btnTakeTimeAgain.setOnClickListener {
            val action = UserProfileFragmentDirections.actionUserProfileFragmentToAllRatedDoctorsFragment()
            findNavController().navigate(action)
        }

        binding.btnHelp.setOnClickListener {
            val alert = AlertDialog.Builder(requireActivity())
            alert.setMessage("للحصول على المساعدة يرجى التواصل مع الادارة من خلال الاتصال على أحد الأرقام التالية \n  0597653709 \n  0598805868")
            alert.setTitle("المساعدة")
            alert.setCancelable(false)
            alert.setIcon(R.mipmap.help_gray)

            alert.setPositiveButton("موافق") { _, _ -> }

            alert.create().show()
        }

        return binding.root
    }


    private fun initViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService))
        ).get(MyViewModel::class.java)
    }


    private fun getUserProfileResponse() {
        viewModel.getUserProfileResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val response = it.data!!.items
                    binding.layoutContent.visibility = View.VISIBLE
                    binding.layoutEmpty.visibility = View.GONE
                    model = UpdateProfileModel(response.name,response.gender,response.dateOfBirth,response.phone_number,response.image)
                    CommonConstants.loadImageWithPicasso(response.image,binding.imgDoctor)
                    binding.txtDoctorName.text = response.name
                    binding.txtPhoneNumber.text = response.phone_number

                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    binding.layoutContent.visibility = View.INVISIBLE
                    binding.layoutEmpty.visibility = View.VISIBLE
                    CommonConstants.hideProgressDialog()
                    CommonConstants.showCustomToast(requireActivity(),  it.message.toString(),"error")
                }
            }
        }
    }
}