package com.alsadimoh.graduationproject.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.classes.CommonConstants.hideProgressDialog
import com.alsadimoh.graduationproject.classes.CommonConstants.setSharedPrefForSigning
import com.alsadimoh.graduationproject.classes.CommonConstants.showProgressDialog
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.databinding.FragmentCreatePasswordBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory

class CreatePasswordFragment : Fragment(R.layout.fragment_create_password) {

    lateinit var binding: FragmentCreatePasswordBinding
    private val args: CreatePasswordFragmentArgs by navArgs()
    private val text = "CreatePasswordFragment"
    private lateinit var viewModel: MyViewModel
    private lateinit var email :String
    private lateinit var password:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCreatePasswordBinding.inflate(inflater, container, false)

        initViewModel()
        getRegistrationInfo()


        binding.btnCreateAnAccount.setOnClickListener {
            if (binding.txtPassword.text.isNotEmpty() && binding.txtConfirmPassword.text.isNotEmpty()) {
                if (binding.txtPassword.text.length >= 8) {
                    if (binding.txtPassword.text.toString() == binding.txtConfirmPassword.text.toString()) {
                        val newObjWithPassword = args.registerObj

                        password =    binding.txtPassword.text.toString()
                        email = CommonConstants.emailPrefix+args.registerObj.phone_number+ CommonConstants.emailSuffix

                        newObjWithPassword.password = password

                        viewModel.register(newObjWithPassword)

                    } else {
                        CommonConstants.showCustomToast(requireActivity(), "كلمة المرور وتأكيد كلمة المرور غير متطابقتان، يرجى تأكيد كلمة المرور","warning")
                    }
                } else {
                    CommonConstants.showCustomToast(requireActivity(), "يرجى ادخال كلمة مرور تتألف من 8 حروف أو أكثر","warning")
                }

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

    private fun getRegistrationInfo() {
        viewModel.getRegisterResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    hideProgressDialog()
                    val response = it.data
                    setSharedPrefForSigning(response!!.items.token, null, response.items.name, response.items.phone_number, binding.txtPassword.text.toString(), response.items.user_type)
                    CommonConstants.showCustomToast(requireActivity(), "تم التسجيل بنجاح","success")
                    val action = CreatePasswordFragmentDirections.actionCreatePasswordFragmentToHomeFragment()
                    findNavController().navigate(action)
                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    hideProgressDialog()
                    binding.layoutValidationAlert.visibility = View.VISIBLE
                    binding.txtError.text = it.message
                }
            }
        }
    }

}