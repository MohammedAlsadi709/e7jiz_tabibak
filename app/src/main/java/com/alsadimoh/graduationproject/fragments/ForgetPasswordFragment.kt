package com.alsadimoh.graduationproject.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.databinding.FragmentForgetPasswordBinding

class ForgetPasswordFragment : Fragment(R.layout.fragment_forget_password) {

    lateinit var binding: FragmentForgetPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)

        binding.btnSend.setOnClickListener {
            if (binding.txtPhoneNumber.text.isNotEmpty()){
                val phone = binding.txtPhoneNumber.text.toString()
                if (phone.length==10 && (phone.startsWith("059")||phone.startsWith("056"))) {
                    val action = ForgetPasswordFragmentDirections.actionForgetPasswordFragmentToPhoneVerificationForForgetPasswordFragment(phone)
                    findNavController().navigate(action)
                }else{
                    CommonConstants.showCustomToast(requireActivity(), "يرجى ادخال رقم الهاتف صالح","warning")
                }
                }else{
                CommonConstants.showCustomToast(requireActivity(), "يرجى ادخال رقم الهاتف","warning")
                }
            }

        return binding.root
    }
}