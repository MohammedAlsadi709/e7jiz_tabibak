package com.alsadimoh.graduationproject.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.databinding.FragmentLoginBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.auth.LoginModel
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory


class LoginFragment : Fragment(R.layout.fragment_login) {

    lateinit var binding: FragmentLoginBinding
    private val text = "LoginFragment"
    lateinit var viewModel: MyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        initViewModel()
        getLoginInfo()


        binding.btnLogin.setOnClickListener {

            if (binding.txtUsername.text.isNotEmpty()) {
                if (binding.txtPassword.text.isNotEmpty()) {
                    if (binding.txtPassword.text.toString().length >= 8) {
                        viewModel.login(LoginModel(binding.txtUsername.text.toString(),binding.txtPassword.text.toString()))
                    } else {
                        CommonConstants.showCustomToast(requireActivity(), "يرجى ادخال كلمة مرور لا تقل عن 8 حروف","warning")
                    }
                } else {
                    CommonConstants.showCustomToast(requireActivity(), "يرجى ادخال كلمة المرور","warning")
                }
            } else {
                CommonConstants.showCustomToast(requireActivity(), "يرجى ادخال رقم الهاتف","warning")
            }
        }

        binding.btnSignUp.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToSignUpFirstFragment()
            findNavController().navigate(action)
        }

        binding.forgetPassword.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToForgetPasswordFragment()
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

    private fun getLoginInfo() {
        viewModel.getLoginResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val response = it.data
                    Log.e(text, "response: ${response.toString()}")

                    CommonConstants.setSharedPrefForSigning(
                        response!!.items.token,
                        response.items.image,
                        response.items.name,
                        response.items.phone_number,
                        binding.txtPassword.text.toString(),
                        response.items.user_type
                    )


                    if (response.items.user_type == "user"){
                        val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                        findNavController().navigate(action)
                    }else if(response.items.user_type == "doctor"){
                        val action = LoginFragmentDirections.actionLoginFragmentToDoctorHomeFragment()
                        findNavController().navigate(action)
                    }


                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    CommonConstants.hideProgressDialog()
                    binding.layoutValidationAlert.visibility = View.VISIBLE
                    binding.txtError.text = it.message
                }
            }
        }
    }
}