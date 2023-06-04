package com.alsadimoh.graduationproject.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.databinding.FragmentChangePasswordBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.auth.ChangePasswordModel
import com.alsadimoh.graduationproject.retrofit.models.auth.ForgetPasswordModel
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth


class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {

    private val text = "ChangePasswordFragment"
    private lateinit var viewModel: MyViewModel
    lateinit var binding: FragmentChangePasswordBinding
    val args: ChangePasswordFragmentArgs by navArgs()
    private var isForgetAndNotChange = false
    private lateinit var mAuth: FirebaseAuth
    private var newPassword = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)

        initViewModel()
        getChangePasswordForForgetPasswordResponse()
        getChangePasswordResponse()

        mAuth = FirebaseAuth.getInstance()

        if (args.phoneNumber.isNullOrEmpty()) {
            binding.txtN.visibility = View.VISIBLE
            binding.txtN2.visibility = View.VISIBLE
            isForgetAndNotChange = false
        } else {
            binding.txtN.visibility = View.GONE
            binding.txtN2.visibility = View.GONE
            isForgetAndNotChange = true
        }


        binding.btnSave.setOnClickListener {
            if (binding.txtNewPassword.text.toString().isNotEmpty()) {
                if (binding.txtConfirmNewPassword.text.toString().isNotEmpty()) {
                    if (binding.txtNewPassword.text.toString().length >= 8) {
                        if (binding.txtConfirmNewPassword.text.toString()==binding.txtNewPassword.text.toString()) {
                            if (isForgetAndNotChange){
                                newPassword = binding.txtNewPassword.text.toString()
                                viewModel.changePasswordForForgetPassword(ForgetPasswordModel(args.phoneNumber!!,newPassword))

                            }else{
                                if (binding.txtCurrentPassword.text.toString().isNotEmpty()) {
                                    if (binding.txtCurrentPassword.text.toString().length >= 8) {
                                        newPassword = binding.txtNewPassword.text.toString()
                                        viewModel.changePassword(ChangePasswordModel(binding.txtCurrentPassword.text.toString(),newPassword))

                                    } else {
                                        CommonConstants.showCustomToast(requireActivity(), "يرجى ادخال كلمة المرور الحالية","warning")
                                    }
                                } else {
                                    CommonConstants.showCustomToast(requireActivity(), "يرجى ادخال كلمة المرور الحالية لا تقل عن 8 حروف","warning")
                                }
                            }

                        } else {
                            CommonConstants.showCustomToast(requireActivity(), "كلمة المرور وتأكيد كلمة المرور مختلفتان","warning")
                        }
                    } else {
                        CommonConstants.showCustomToast(requireActivity(), "يرجى ادخال كلمة مرور لا تقل عن 8 حروف","warning")
                    }

                } else {
                    CommonConstants.showCustomToast(requireActivity(), "يرجى تأكيد كلمة المرور الجديدة","warning")
                }
            } else {
                CommonConstants.showCustomToast(requireActivity(), "يرجى ادخال كلمة مرور جديدة","warning")
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

    private fun getChangePasswordForForgetPasswordResponse() {
        viewModel.changePasswordForForgetPasswordResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                  //  val response = it.data
                    val password  = CommonConstants.myShared.getString(CommonConstants.userPassword,"password")
                    mAuth.signInWithEmailAndPassword(CommonConstants.emailPrefix+args.phoneNumber+ CommonConstants + CommonConstants.emailSuffix,password!!).addOnSuccessListener {
                        val user = mAuth.currentUser
                        user!!.updatePassword(newPassword)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    CommonConstants.hideProgressDialog()
                                    findNavController().popBackStack(R.id.loginFragment,false)
                                }
                            }.addOnFailureListener {
                                CommonConstants.hideProgressDialog()
                                findNavController().popBackStack(R.id.loginFragment,false)
                            }
                    }.addOnFailureListener {
                        CommonConstants.hideProgressDialog()
                        findNavController().popBackStack(R.id.loginFragment,false)
                    }

                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    CommonConstants.hideProgressDialog()
                }
            }
        }
    }

    private fun getChangePasswordResponse() {
        viewModel.changePasswordResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                   // val response = it.data
                    val user = mAuth.currentUser
                    user!!.updatePassword(newPassword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                CommonConstants.hideProgressDialog()
                            }
                        }.addOnFailureListener {
                            CommonConstants.hideProgressDialog()
                        }
                    CommonConstants.showCustomToast(requireActivity(), "تم تغيير كلمة المرور بنجاح","success")

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
}