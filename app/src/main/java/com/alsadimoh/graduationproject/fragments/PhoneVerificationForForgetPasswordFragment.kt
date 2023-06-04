package com.alsadimoh.graduationproject.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.databinding.FragmentPhoneVerificationForForgetPasswordBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class PhoneVerificationForForgetPasswordFragment : Fragment(R.layout.fragment_phone_verification_for_forget_password) {

    lateinit var binding: FragmentPhoneVerificationForForgetPasswordBinding
    private val args: PhoneVerificationForForgetPasswordFragmentArgs by navArgs()
    private var isClickableNextButton = false
    private var verificationCode: String? = "123456"
    lateinit var handler:Handler


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPhoneVerificationForForgetPasswordBinding.inflate(inflater, container, false)

        val phone = args.phoneNumber
        handler = Handler(Looper.getMainLooper())

        binding.txtPhoneToVerify.text = phone
        binding.btnNext.isEnabled = isClickableNextButton
        binding.txtVerificationCodeFirstDigit.addTextChangedListener {
            enableNextButton()
            if (binding.txtVerificationCodeFirstDigit.text.toString().length == 1) {
                binding.txtVerificationCodeSecondDigit.requestFocus()
            }
        }
        binding.txtVerificationCodeSecondDigit.addTextChangedListener {
            enableNextButton()
            if (binding.txtVerificationCodeSecondDigit.text.toString().length == 1) {
                binding.txtVerificationCodeThirdDigit.requestFocus()
            }
        }
        binding.txtVerificationCodeThirdDigit.addTextChangedListener {
            enableNextButton()
            if (binding.txtVerificationCodeThirdDigit.text.toString().length == 1) {
                binding.txtVerificationCodeFourthDigit.requestFocus()
            }
        }
        binding.txtVerificationCodeFourthDigit.addTextChangedListener {
            enableNextButton()
            if (binding.txtVerificationCodeFourthDigit.text.toString().length == 1) {
                binding.txtVerificationCodeFifthDigit.requestFocus()
            }
        }
        binding.txtVerificationCodeFifthDigit.addTextChangedListener {
            enableNextButton()
            if (binding.txtVerificationCodeFifthDigit.text.toString().length == 1) {
                binding.txtVerificationCodeSixthDigit.requestFocus()
            }
        }
        binding.txtVerificationCodeSixthDigit.addTextChangedListener {
            enableNextButton()
        }

        binding.btnNext.setOnClickListener {
            val enteredCode =
                binding.txtVerificationCodeFirstDigit.text.toString() + binding.txtVerificationCodeSecondDigit.text.toString() + binding.txtVerificationCodeThirdDigit.text.toString() + binding.txtVerificationCodeFourthDigit.text.toString() + binding.txtVerificationCodeFifthDigit.text.toString() + binding.txtVerificationCodeSixthDigit.text.toString()
            if (verificationCode == enteredCode) {
                val action = PhoneVerificationForForgetPasswordFragmentDirections.actionPhoneVerificationForForgetPasswordFragmentToChangePasswordFragment(phone)
                findNavController().navigate(action)
            } else {
                CommonConstants.showCustomToast(requireActivity(), "الرمز غير صحيح، يرجى ادخال رمز صالح!!!","warning")
            }
        }

        binding.btnChangePhoneNumber.setOnClickListener {
            findNavController().popBackStack()
        }

        phoneVerify(phone.removePrefix("0"))

        binding.btnResendAgain.setOnClickListener {
            if (binding.btnResendAgain.text == "أعد الارسال"){
                phoneVerify(phone.removePrefix("0"))
            }else{
                CommonConstants.showCustomToast(requireActivity(), "يمكنك اعادة ارسال الرمز فقط بعد انتهاء المدة","warning")
            }
        }

        return binding.root
    }

    private fun enableNextButton() {
        isClickableNextButton =
            if (binding.txtVerificationCodeFirstDigit.text.isNotEmpty() && binding.txtVerificationCodeSecondDigit.text.isNotEmpty() && binding.txtVerificationCodeThirdDigit.text.isNotEmpty() && binding.txtVerificationCodeFourthDigit.text.isNotEmpty()&& binding.txtVerificationCodeFifthDigit.text.isNotEmpty()&& binding.txtVerificationCodeSixthDigit.text.isNotEmpty()) {
                binding.layoutNext.background.setTint(Color.parseColor("#00A4CE"))
                true
            } else {
                binding.layoutNext.background.setTint(Color.parseColor("#6A6A6A"))
                false
            }
        binding.btnNext.isEnabled = isClickableNextButton
    }

    private fun phoneVerify(phone:String) {
        var counter = 60
        handler.postDelayed(object : Runnable {
            @SuppressLint("SetTextI18n")
            override fun run() {
                if (counter>=1){
                    counter--
                    binding.btnResendAgain.text = "أعد الارسال بعد ($counter)"
                    handler.postDelayed(this, 1000)
                }else{
                    binding.btnResendAgain.text = "أعد الارسال"
                }
            }
        }, 10)

        val firebaseAuth = FirebaseAuth.getInstance()

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber("+972$phone")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    verificationCode = credential.smsCode
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }
}