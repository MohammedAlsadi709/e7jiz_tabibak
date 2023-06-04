package com.alsadimoh.graduationproject.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.adapters.CategoriesDialogAdapter
import com.alsadimoh.graduationproject.databinding.CustomChooseCategoryDialogBinding
import com.alsadimoh.graduationproject.databinding.CustomChooseGenderDialogBinding
import com.alsadimoh.graduationproject.databinding.CustomChooseRangeByDialogBinding
import com.alsadimoh.graduationproject.databinding.FragmentSearchFilterBinding
import com.alsadimoh.graduationproject.mapsUtils.LocationHelper
import com.alsadimoh.graduationproject.mapsUtils.LocationManager
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.userHome.SpecializationModel
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory
import com.google.android.gms.maps.model.LatLng
import java.util.*


class SearchFilterFragment : Fragment(R.layout.fragment_search_filter) {

    private lateinit var locationHelper: LocationHelper
    private var lng: Double? = null
    private var lat: Double? = null
    private var oldPositionForSelect = -1
    private var selectedCategory =""
    private var rangeBy = "desc"
    var point : LatLng? = null
    private var rangeType = "rate" // rate distance
    private lateinit var viewModel: MyViewModel
    lateinit var binding: FragmentSearchFilterBinding
    private val text = "SearchFilterFragment"
    lateinit var data:MutableList<SpecializationModel>
    private var isNeedToMoveSearchFragment = false // لان الفراقمنت بعمل ريلود وبالتالي حيكون مكيش عملية البحث انها ناجحة وحيرجع ينتقل للسيرش فراقمنت


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchFilterBinding.inflate(inflater, container, false)

        initViewModel()
        getAllCategories()
        getSearchByFilterResponse()
        viewModel.getAllSpecializations()


        binding.sliderRangeDuration.valueFrom = 0.0F
        binding.sliderRangeDuration.valueTo = 100.0F
        val values = mutableListOf<Float>()
        values.add(20.0F)
        values.add(60.0F)
        binding.sliderRangeDuration.values = values

        binding.sliderRangeDuration.setLabelFormatter { value: Float ->
             when (value) {
                in 0F..10F -> "${value.toInt()}دقائق"
                else -> "${value.toInt()}دقيقة"
            }
        }


        binding.sliderRangePrice.valueFrom = 0.0F
        binding.sliderRangePrice.valueTo = 100.0F
        val values2 = mutableListOf<Float>()
        values2.add(20.0F)
        values2.add(60.0F)
        binding.sliderRangePrice.values = values2

        binding.sliderRangePrice.setLabelFormatter { value: Float ->
           "${value.toInt()}شيكل"
        }


        binding.txtChooseCategory.setOnClickListener {
            val dialog = Dialog(requireActivity())
            val dialogBinding = CustomChooseCategoryDialogBinding.inflate(LayoutInflater.from(activity))
            dialog.setContentView(dialogBinding.root)

            val adapter = CategoriesDialogAdapter(requireActivity(),data)
            dialogBinding.rvCategories.adapter = adapter
            dialogBinding.rvCategories.layoutManager = LinearLayoutManager(requireActivity())

            adapter.onClickCategoryItem={position:Int,model: SpecializationModel ->
                if (!(adapter.data[position].isSelected)) {
                    if (oldPositionForSelect != -1) {
                        adapter.data[oldPositionForSelect].isSelected = false
                        selectedCategory = ""
                        adapter.notifyItemChanged(oldPositionForSelect)
                    }
                    adapter.data[position].isSelected = true
                    adapter.notifyItemChanged(position)
                    selectedCategory = model.title
                    oldPositionForSelect = position

                } else {
                    adapter.data[position].isSelected = false
                    selectedCategory = ""
                    adapter.notifyItemChanged(position)
                }
            }

            dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialogBinding.btnDone.setOnClickListener {
               if (selectedCategory.isNotEmpty()) {
                   binding.txtChooseCategory.text = selectedCategory
                   dialog.dismiss()
               }else{
                   CommonConstants.showCustomToast(requireActivity(), "يرجى اختيار التخصص","warning")
               }
            }
            dialog.show()
        }

        binding.txtChooseGender.setOnClickListener {
            val dialog = Dialog(requireActivity())
            val dialogBinding =
                CustomChooseGenderDialogBinding.inflate(LayoutInflater.from(activity))
            dialog.setContentView(dialogBinding.root)
            dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialogBinding.btnDone.setOnClickListener {
                binding.txtChooseGender.text =
                    if (dialogBinding.rbGroup.checkedRadioButtonId == R.id.rbMale) {
                        "ذكر"
                    } else {
                        "أنثى"
                    }
                dialog.dismiss()
            }
            dialog.show()
        }

        binding.txtChooseDate.setOnClickListener {
            val mCurrentDate = Calendar.getInstance()
                val day = mCurrentDate.get(Calendar.DAY_OF_MONTH)
                val month = mCurrentDate.get(Calendar.MONTH)
                val year1 = mCurrentDate.get(Calendar.YEAR)
                val picker = DatePickerDialog(
                    requireActivity(),
                    { _, year, monthOfYear, dayOfMonth ->
                        binding.txtChooseDate.text = "$year-$monthOfYear-$dayOfMonth"
                    }, year1, month, day
                )
                picker.show()
        }



        binding.txtChooseRange.setOnClickListener {
            val dialog = Dialog(requireActivity())
            val dialogBinding =
                CustomChooseRangeByDialogBinding.inflate(LayoutInflater.from(activity))
            dialog.setContentView(dialogBinding.root)
            dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialogBinding.btnDone.setOnClickListener {
                    when (dialogBinding.rbGroup.checkedRadioButtonId) {
                        R.id.rbRateDesc -> {
                            rangeBy= "desc"
                            binding.txtChooseRange.text =    "الأعلى تقييماً"
                            rangeType = "rate"
                        }
                        R.id.rbRateAsc -> {
                            rangeBy= "asc"
                            binding.txtChooseRange.text =   "الأقل تقييماً"
                            rangeType = "rate"
                        }
                        R.id.rbNearAsc->{

                           if (CommonConstants.checkLocationPermissions(requireActivity())){
                               if (CommonConstants.isLocationEnabled(requireActivity())){
                                   rangeBy= "asc"
                                   binding.txtChooseRange.text =  "الأقرب لموقعي"
                                   rangeType = "distance"
                                   initGpsLocation()
                                   locationHelper.startLocationUpdates()

                               }else{
                                   CommonConstants.showCustomToast(requireActivity(), "يرجى تفعيل خدمة الموقع حتى يتم تفعيل هذا الخيار","warning")
                               }
                           }
                        }
                        R.id.rbNearDesc->{
                            if (CommonConstants.checkLocationPermissions(requireActivity())){
                                if (CommonConstants.isLocationEnabled(requireActivity())){
                                    rangeBy= "desc"
                                    binding.txtChooseRange.text =  "الأبعد لموقعي"
                                    rangeType = "distance"
                                    initGpsLocation()
                                    locationHelper.startLocationUpdates()
                                }else{
                                    CommonConstants.showCustomToast(requireActivity(), "يرجى تفعيل خدمة الموقع حتى يتم تفعيل هذا الخيار","warning")
                                }
                            }
                        }
                        else -> {
                            rangeBy= "desc"
                            binding.txtChooseRange.text =   "الأعلى تقييماً"
                            rangeType = "rate"
                        }
                    }
                dialog.dismiss()
            }
            dialog.show()
        }


        binding.btnSearch.setOnClickListener {
            if (selectedCategory.isNotEmpty()){
                if (binding.txtChooseDate.text != "----------"){
                    if (rangeType == "rate"){
                        isNeedToMoveSearchFragment = true
                        viewModel.searchWithFilter(selectedCategory,binding.txtChooseGender.text.toString(),binding.sliderRangeDuration.values[0],binding.sliderRangeDuration.values[1],binding.sliderRangePrice.values[0],binding.sliderRangeDuration.values[1],null,null,rangeType,rangeBy)
                    }else{
                        isNeedToMoveSearchFragment = true
                        viewModel.searchWithFilter(selectedCategory,binding.txtChooseGender.text.toString(),binding.sliderRangeDuration.values[0],binding.sliderRangeDuration.values[1],binding.sliderRangePrice.values[0],binding.sliderRangeDuration.values[1],point!!.latitude,point!!.longitude,rangeType,rangeBy)
                    }

                }else{
                    CommonConstants.showCustomToast(requireActivity(), "يرجى تحديد التاريخ","warning")
                }
            }else{
                CommonConstants.showCustomToast(requireActivity(), "يرجى تحديد تخصص البحث","warning")
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


    private fun getAllCategories() {
        viewModel.getAllSpecializationResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val response = it.data
                    data = response!!.items as MutableList<SpecializationModel>
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


    private fun getSearchByFilterResponse() {
        viewModel.searchWithFilterResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val response = it.data
                    if (isNeedToMoveSearchFragment){
                        isNeedToMoveSearchFragment = false
                        val action = SearchFilterFragmentDirections.actionSearchFilterFragmentToSearchFragment(response!!.items.toTypedArray())
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
                    CommonConstants.showCustomToast(requireActivity(),  it.message.toString(),"error")
                }
            }
        }
    }

    private fun initGpsLocation() {
        locationHelper = LocationHelper(requireActivity(), object : LocationManager {

            override fun onLocationChanged(location: Location?) {}

            override fun getLastKnownLocation(location: Location?) {
                    lat = location?.latitude
                    lng = location?.longitude
                    point = LatLng(lat!!, lng!!)
                locationHelper.stopLocationUpdates()
                locationHelper.stopLocationUpdates()
            }
        })
    }


}