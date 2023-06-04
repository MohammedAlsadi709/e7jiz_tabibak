package com.alsadimoh.graduationproject.fragments.doctorFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.databinding.FragmentBookingCancelBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory


class BookingCancelFragment : Fragment(R.layout.fragment_booking_cancel) {

    private val text = "BookingCancelFragment"
    private lateinit var viewModel: MyViewModel
    lateinit var binding: FragmentBookingCancelBinding
    val args: BookingCancelFragmentArgs by navArgs()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentBookingCancelBinding.inflate(inflater, container, false)
        initViewModel()
        getCancelBookingForDoctorResponse()

        if (args.isIncoming){
            binding.cbIsAttended.visibility = View.VISIBLE
        }else{
            binding.cbIsAttended.visibility = View.GONE
        }

        binding.txtCauseToCancel.addTextChangedListener {
            binding.txtInputTextCount.text = "${binding.txtCauseToCancel.text.toString().length}/100"
        }

        binding.btnSend.setOnClickListener {
            if (binding.txtCauseToCancel.text.isNotEmpty()){

                val isAttended =   if (args.isIncoming){
                    (!(binding.cbIsAttended.isChecked)).toString()
                }else{
                    ""
                }


                val alert = AlertDialog.Builder(requireActivity())
                alert.setMessage("هل أنت متأكد من أنك تود إلغاء هذا الحجز")
                alert.setTitle("الغاء؟")
                alert.setCancelable(false)
                alert.setIcon(R.mipmap.false_state)


                alert.setPositiveButton("نعم") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    viewModel.cancelBookingForDoctor(args.bookingId,binding.txtCauseToCancel.text.toString(),isAttended,args.status)
                }


                alert.setNegativeButton("لا") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }

                alert.create().show()

            }else{
                CommonConstants.showCustomToast(requireActivity(),"يرجى ادخال سبب الالغاء","warning")
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

    private fun getCancelBookingForDoctorResponse() {
        viewModel.getCancelBookingForDoctorResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    if (args.isPopToDoctorHome){
                        findNavController().popBackStack(R.id.doctorHomeFragment,false)
                    }else{
                        findNavController().popBackStack()
                    }
                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    CommonConstants.showCustomToast(requireActivity(),it.message.toString(),"error")
                    CommonConstants.hideProgressDialog()
                }
            }
        }
    }
}