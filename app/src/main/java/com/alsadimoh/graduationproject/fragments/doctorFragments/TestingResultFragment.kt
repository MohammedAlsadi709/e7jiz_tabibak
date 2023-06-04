package com.alsadimoh.graduationproject.fragments.doctorFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.adapters.doctorsAdapters.AddMedicineForBookingsAdapter
import com.alsadimoh.graduationproject.databinding.FragmentTestingResultBinding
import com.alsadimoh.graduationproject.fragments.BottomSheetFragment
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.doctorBooking.BottomSheetItems
import com.alsadimoh.graduationproject.retrofit.models.doctorBooking.MedicineCardModel
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory


class TestingResultFragment : Fragment(R.layout.fragment_testing_result) {

    private val text = "TestingResultFragment"
    private lateinit var viewModel: MyViewModel
    lateinit var binding: FragmentTestingResultBinding
    val args:TestingResultFragmentArgs by navArgs()
    private val dataForMedicines = mutableListOf<BottomSheetItems>()
    private val dataForPeriods = mutableListOf<BottomSheetItems>()
    private lateinit var cardsAdapter : AddMedicineForBookingsAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTestingResultBinding.inflate(inflater, container, false)

        initViewModel()
        getMedicineAndPeriodsResponse()
        getAddTreatmentForBookingResponse()
        getOldResultsToEditTreatmentResponse()

        if (args.isEdtting){
            viewModel.getOldResultsToEditTreatment(args.bookingId)
        }else{
            viewModel.getMedicineAndPeriods()
        }

        val cardsData = mutableListOf<MedicineCardModel>().apply {
            this.add(MedicineCardModel())
        }

        cardsAdapter = AddMedicineForBookingsAdapter(requireActivity(), cardsData)
        binding.rvAddMedicine.adapter = cardsAdapter
        binding.rvAddMedicine.layoutManager = LinearLayoutManager(requireActivity())


        binding.btnAddNewCard.setOnClickListener {
            cardsAdapter.data.add(MedicineCardModel())
            cardsAdapter.notifyDataSetChanged()
        }


        cardsAdapter.onClickItem = { textView: TextView, idTextView: TextView, type: String ->
            val bottomSheetFragment = BottomSheetFragment()
            bottomSheetFragment.show(requireActivity().supportFragmentManager, "bottomSheet")


            Handler(Looper.getMainLooper()).postDelayed({// بعد بأجزاء من الثانية عشان يلحق يعمل اينيشالايز للفراقمنت والمتغيرات
                if (type == "medicine") {
                    CommonConstants.onOpenBottomSheetSetItems?.invoke(
                        type,
                        dataForMedicines,
                        textView,
                        idTextView,
                        bottomSheetFragment
                    )
                } else if (type == "period") {
                    CommonConstants.onOpenBottomSheetSetItems?.invoke(
                        type,
                        dataForPeriods,
                        textView,
                        idTextView,
                        bottomSheetFragment
                    )
                }
            }, 100)
        }


        binding.btnSend.setOnClickListener {
            if (!(binding.txtDoctorNotice.text.isNullOrEmpty())){
                var valid = true
                val invalidIndex = mutableListOf<Int>()
                var result = ""
                for (i in cardsAdapter.data.indices) {
                    result += if (i != cardsAdapter.data.lastIndex) {
                        if ((cardsAdapter.data[i].medicine_id != null && cardsAdapter.data[i].medicine_id != "لم يتم التحديد" && !(cardsAdapter.data[i].medicine_id.isNullOrEmpty())) && (cardsAdapter.data[i].duration != null && cardsAdapter.data[i].duration != "لم يتم التحديد" && !(cardsAdapter.data[i].duration.isNullOrEmpty())) && (cardsAdapter.data[i].period_id != null && cardsAdapter.data[i].period_id != "لم يتم التحديد" && !(cardsAdapter.data[i].period_id.isNullOrEmpty()))) {
                            "${cardsAdapter.data[i].medicine_id},${cardsAdapter.data[i].duration},${cardsAdapter.data[i].period_id}|"
                        } else {
                            valid = false
                            invalidIndex.add(i+1)
                            "null,null,null|"
                        }
                    } else {
                        if ((cardsAdapter.data[i].medicine_id != null && cardsAdapter.data[i].medicine_id != "لم يتم التحديد" && !(cardsAdapter.data[i].medicine_id.isNullOrEmpty())) && (cardsAdapter.data[i].duration != null && cardsAdapter.data[i].duration != "لم يتم التحديد" && !(cardsAdapter.data[i].duration.isNullOrEmpty())) && (cardsAdapter.data[i].period_id != null && cardsAdapter.data[i].period_id != "لم يتم التحديد" && !(cardsAdapter.data[i].period_id.isNullOrEmpty()))) {
                            "${cardsAdapter.data[i].medicine_id},${cardsAdapter.data[i].duration},${cardsAdapter.data[i].period_id}"
                        } else {
                            valid = false
                            invalidIndex.add(i+1)
                            "null,null,null"
                        }
                    }
                }


                    if (result.isNotEmpty()){
                        if (valid){
                            if (args.isEdtting){
                                viewModel.editTreatmentForBooking(args.bookingId,binding.txtDoctorNotice.text.toString(),result)
                            }else{
                                viewModel.addTreatmentForBooking(args.bookingId,binding.txtDoctorNotice.text.toString(),result)
                            }

                        }else{
                            val alert = AlertDialog.Builder(requireActivity())
                            alert.setMessage("الدواء رقم $invalidIndex يحتوي على حقل أو أكثر فارغ وربما لن يتم حفظ هذا الدواء \n هل تود الحفظ على أي حال؟")
                            alert.setTitle("تنبيه")
                            alert.setCancelable(false)
                            alert.setIcon(R.mipmap.help_gray)


                            alert.setPositiveButton("نعم، احفظ على أي حال") { dialogInterface, _ ->
                                if (args.isEdtting){
                                    viewModel.editTreatmentForBooking(args.bookingId,binding.txtDoctorNotice.text.toString(),result)
                                }else{
                                    viewModel.addTreatmentForBooking(args.bookingId,binding.txtDoctorNotice.text.toString(),result)
                                }
                                dialogInterface.dismiss()
                            }


                            alert.setNegativeButton("الغاء") { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            }

                            alert.create().show()

                        }
                    }else{
                        CommonConstants.showCustomToast(requireActivity(),  "لا يوجد ادوية","warning")
                    }


            }else{
                CommonConstants.showCustomToast(requireActivity(),  "يرجى ادخال ملاحظاتك","warning")
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


    private fun getMedicineAndPeriodsResponse() {
        viewModel.getMedicineAndPeriodsResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val response = it.data!!.items
                    if (response.medicines.isNotEmpty()) {
                        for (i in response.medicines) {
                            dataForMedicines.add(
                                BottomSheetItems(
                                    i.id,
                                    i.name,
                                    i.description,
                                    i.deleted_at
                                )
                            )
                        }
                    }
                    if (response.treatmentPeriods.isNotEmpty()) {
                        for (i in response.treatmentPeriods) {
                            dataForPeriods.add(
                                BottomSheetItems(
                                    i.id,
                                    i.medication_timings,
                                    i.description,
                                    i.deleted_at
                                )
                            )
                        }
                    }
                    CommonConstants.hideProgressDialog()
                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    CommonConstants.hideProgressDialog()
                    CommonConstants.showCustomToast(requireActivity(),  it.message.toString(),"error")
                }
            }
        }
    }

    private fun getAddTreatmentForBookingResponse() {
        viewModel.getAddTreatmentForBookingResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    if (args.isEdtting){
                        findNavController().popBackStack()
                    }else{
                        findNavController().popBackStack(R.id.doctorHomeFragment,false)
                    }
                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    CommonConstants.hideProgressDialog()
                    CommonConstants.showCustomToast(requireActivity(),  it.message.toString(),"error")
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getOldResultsToEditTreatmentResponse() {
        viewModel.getOldResultsToEditTreatmentResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val response = it.data!!.items

                    binding.txtDoctorNotice.setText(response.oldTreatment.doctor_notes)

                    val oldTreatments = mutableListOf<MedicineCardModel>()
                   if (response.oldTreatment.treatments.isNotEmpty()){
                       for (i in response.oldTreatment.treatments){
                           oldTreatments.add(MedicineCardModel(i.medicine.id.toString(),i.treatment_period.id.toString(),i.medicine.name,i.treatment_period.medication_timings,i.duration.toString()))
                       }
                   }

                    if (oldTreatments.isNotEmpty()){
                        cardsAdapter.data.clear()
                        cardsAdapter.data = oldTreatments
                        cardsAdapter.notifyDataSetChanged()
                    }


                    if (response.medicines.isNotEmpty()) {
                        for (i in response.medicines) {
                            dataForMedicines.add(
                                BottomSheetItems(
                                    i.id,
                                    i.name,
                                    i.description,
                                    i.deleted_at
                                )
                            )
                        }
                    }
                    if (response.treatmentPeriods.isNotEmpty()) {
                        for (i in response.treatmentPeriods) {
                            dataForPeriods.add(
                                BottomSheetItems(
                                    i.id,
                                    i.medication_timings,
                                    i.description,
                                    i.deleted_at
                                )
                            )
                        }
                    }
                    CommonConstants.hideProgressDialog()

                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    CommonConstants.hideProgressDialog()
                    CommonConstants.showCustomToast(requireActivity(),  it.message.toString(),"error")
                }
            }
        }
    }
    override fun onPause() {
        super.onPause()
        try {
            cardsAdapter.data.clear()
        } catch (e: Exception) {
        }

    }
}