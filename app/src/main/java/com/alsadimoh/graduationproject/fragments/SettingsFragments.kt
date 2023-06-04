package com.alsadimoh.graduationproject.fragments

import android.app.ActionBar
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.classes.CommonConstants.logOut
import com.alsadimoh.graduationproject.MainActivity
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.databinding.CustomChangeFontsSizeDialogBinding
import com.alsadimoh.graduationproject.databinding.FragmentSettingsFragmentsBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory

class SettingsFragments : Fragment(R.layout.fragment_settings_fragments) {

    lateinit var binding: FragmentSettingsFragmentsBinding
    lateinit var viewModel: MyViewModel
    private var loggedOut = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingsFragmentsBinding.inflate(inflater, container, false)
        initViewModel()
        getLogoutResponse()

        binding.btnChangePassword.setOnClickListener {
            val action =
                SettingsFragmentsDirections.actionSettingsFragmentsToChangePasswordFragment()
            findNavController().navigate(action)
        }

        binding.btnChangeFontSize.setOnClickListener {

            val dialog = Dialog(requireActivity())
            val dialogBinding =
                CustomChangeFontsSizeDialogBinding.inflate(LayoutInflater.from(activity))
            dialog.setContentView(dialogBinding.root)

            var changeValue = -1.0F
            val mainTitleSize = 18 //61F//18 sp // 12 sp -> 22 sp
            // 48 -> 14 sp
            val subTitleSize = 15//58F//15 sp
            val paragraphSize = 12//41F //12 sp
            val savedSize = MainActivity.appFontSize//-0.3F

            dialogBinding.sliderRange.value = (savedSize) * mainTitleSize
            dialogBinding.txtMainTitles.textSize = mainTitleSize * (savedSize)
            dialogBinding.txtSubTitles.textSize = subTitleSize * (savedSize)
            dialogBinding.txtParagraph.textSize = paragraphSize * (savedSize)

            dialogBinding.sliderRange.setLabelFormatter { value: Float ->
                "${(value).toInt()}"
            }

            dialogBinding.sliderRange.addOnChangeListener { _, value, _ ->
                dialogBinding.txtMainTitles.textSize = value // main
                dialogBinding.txtSubTitles.textSize = value - 3F // less 3 sp -> 15sp
                dialogBinding.txtParagraph.textSize = value - 6F // less 6 sp -> 12sp
                changeValue = value / mainTitleSize
            }

            dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialogBinding.btnReset.setOnClickListener {
                dialogBinding.sliderRange.value = 18.0F
            }

            dialogBinding.btnDone.setOnClickListener {
                if (changeValue != -1.0F) {
                    MainActivity.appFontSize = changeValue
                    dialog.dismiss()
                    CommonConstants.changeAppFontsSize(
                        requireActivity(),
                        requireActivity().resources.configuration,
                        changeValue
                    )

                    val action = SettingsFragmentsDirections.actionSettingsFragmentsSelf()
                    findNavController().navigate(action)

                } else {
                    CommonConstants.showCustomToast(requireActivity(), "لم يتم التغيير ليتم حفظه","info")
                }
            }

            dialog.show()
            val window: Window = dialog.window!!
            window.setLayout(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT
            )
        }


        binding.switchNightMode.isChecked = MainActivity.isNightMode
        binding.switchNightMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                MainActivity.isNightMode = true
            } else {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                MainActivity.isNightMode = false
            }
        }


        binding.btnLogOut.setOnClickListener {
            val alert = AlertDialog.Builder(requireActivity())
            alert.setMessage("هل أنت متأكد من عملية تسجيل الخروج")
            alert.setTitle("تسجيل الخروج")
            alert.setIcon(R.mipmap.export_gray)

            alert.setPositiveButton("تسجيل الخروج") { dialogInterface, _ ->
                loggedOut = true
                viewModel.logout()
                dialogInterface.cancel()
            }

            alert.setNegativeButton("ابقاء الجلسة") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
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


    private fun getLogoutResponse() {
        viewModel.getLogoutResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                  if (loggedOut){
                      loggedOut = false
                      CommonConstants.hideProgressDialog()
                      logOut()
                      val action = SettingsFragmentsDirections.actionGlobalLoggingTypeFragment2()
                      findNavController().navigate(action)
                  }
                }
                Status.LOADING -> {
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    CommonConstants.hideProgressDialog()
                    CommonConstants.showCustomToast(requireActivity(),  it.message.toString(),"error")
                }
            }
        }
    }
}