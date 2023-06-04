package com.alsadimoh.graduationproject.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.databinding.CustomChooseGenderDialogBinding
import com.alsadimoh.graduationproject.databinding.FragmentSignUpFirstBinding
import com.alsadimoh.graduationproject.retrofit.models.auth.RegisterModel
import java.util.*

class SignUpFirstFragment : Fragment(R.layout.fragment_sign_up_first) {

    lateinit var binding: FragmentSignUpFirstBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignUpFirstBinding.inflate(inflater, container, false)

        binding.txtGender.setOnClickListener {
            val dialog = Dialog(requireActivity())
            val dialogBinding =
                CustomChooseGenderDialogBinding.inflate(LayoutInflater.from(activity))
            dialog.setContentView(dialogBinding.root)
            dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialogBinding.btnDone.setOnClickListener {
                binding.txtGender.text =
                    if (dialogBinding.rbGroup.checkedRadioButtonId == R.id.rbMale) {
                        "ذكر"
                    } else {
                        "أنثى"
                    }
                dialog.dismiss()
            }
            dialog.show()
        }

        binding.txtDateOfBirth.setOnClickListener {
            val mCurrentDate = Calendar.getInstance()
            val day = mCurrentDate.get(Calendar.DAY_OF_MONTH)
            val month = mCurrentDate.get(Calendar.MONTH)
            val year1 = mCurrentDate.get(Calendar.YEAR)
            val picker = DatePickerDialog(
                requireActivity(),
                { _, year, monthOfYear, dayOfMonth ->
                    binding.txtDateOfBirth.text = "$year-$monthOfYear-$dayOfMonth"
                }, year1, month, day
            )
            picker.show()

        }


        binding.btnNext.setOnClickListener {

            if (binding.txtFullName.text.isNotEmpty()) {
                if (binding.txtFullName.text.toString().length >=5){
                    if (binding.txtDateOfBirth.text != "--/--/----") {
                        if (binding.txtPhoneNumber.text.isNotEmpty()) {
                            if (binding.txtPhoneNumber.text.length == 10 && (binding.txtPhoneNumber.text.startsWith(
                                    "059"
                                ) || binding.txtPhoneNumber.text.startsWith("056"))
                            ) {
                                val action =
                                    SignUpFirstFragmentDirections.actionSignUpFirstFragmentToPhoneVerificationFragment(
                                        RegisterModel(
                                            binding.txtFullName.text.toString(),
                                            binding.txtGender.text.toString(),
                                            binding.txtDateOfBirth.text.toString(),
                                            binding.txtPhoneNumber.text.toString(),
                                            null
                                        )
                                    )
                                findNavController().navigate(action)
                            } else {
                                CommonConstants.showCustomToast(requireActivity(),"يرجى ادخال رقم هاتف صالح","warning")

                            }
                        } else {
                            CommonConstants.showCustomToast(requireActivity(),"يرجى ادخال رقم الهاتف","warning")

                        }
                    } else {
                        CommonConstants.showCustomToast(requireActivity(),"يرجى ادخال تاريخ الميلاد","warning")

                    }
                }else{
                    CommonConstants.showCustomToast(requireActivity(),"يجب ان يكون الاسم على الاقل مكون من 5 حروف","warning")

                }

            } else {
                CommonConstants.showCustomToast(requireActivity(),"يرجى ادخال الإسم كاملاً","warning")
            }
        }



        return binding.root
    }
}