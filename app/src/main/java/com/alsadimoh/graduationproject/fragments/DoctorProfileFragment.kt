package com.alsadimoh.graduationproject.fragments

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.classes.CommonConstants.onPutDoctorToBookmarksForUser
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.adapters.CommentsAdapter
import com.alsadimoh.graduationproject.databinding.FragmentDoctorProfileBinding
import com.alsadimoh.graduationproject.mapsUtils.LocationHelper
import com.alsadimoh.graduationproject.mapsUtils.LocationManager
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.userBooking.ProfileToBooking
import com.alsadimoh.graduationproject.retrofit.models.userDoctors.DoctorProfileForUserResponse
import com.alsadimoh.graduationproject.retrofit.models.userDoctors.CommentForUserModel
import com.alsadimoh.graduationproject.retrofit.models.userDoctors.DoctorProfileForUserRequestModel
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class DoctorProfileFragment : Fragment(R.layout.fragment_doctor_profile), OnMapReadyCallback {

    private var lng: Double? = null
    private var lat: Double? = null
    private lateinit var locationHelper: LocationHelper
    lateinit var binding: FragmentDoctorProfileBinding
    private val args : DoctorProfileFragmentArgs by navArgs()
    private lateinit var viewModel: MyViewModel
    private lateinit var mMap: GoogleMap
    private val text = "DoctorProfileFragment"
    private var isShowMore = false
    private var isDoctorInBookmarks = false
    private var isLocationEnabled = false
    private var point:LatLng? = null
    private lateinit var doctorModel:ProfileToBooking
    private var phoneNumber = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDoctorProfileBinding.inflate(inflater, container, false)

        initViewModel()
        getDoctorProfile()
        putBookmarksResponse()

        onPutDoctorToBookmarksForUser = {
            viewModel.putUserInBookmarksForUser(args.doctorId)
        }

        binding.btnShowAllComments.setOnClickListener {
            val action = DoctorProfileFragmentDirections.actionDoctorProfileFragmentToReviewsFragment(args.doctorId)
            findNavController().navigate(action)
        }

        binding.btnTakeTime.setOnClickListener {
            val action =DoctorProfileFragmentDirections.actionDoctorProfileFragmentToBookingAppointmentFragment(doctorModel)
            findNavController().navigate(action)
        }

        binding.btnChat.setOnClickListener {
            CommonConstants.showProgressDialog(requireActivity())

            val db = Firebase.firestore
            if (phoneNumber.isNotEmpty()){
                db.collection("registeredUsers").whereEqualTo("email",
                    CommonConstants.emailPrefix+phoneNumber+ CommonConstants.emailSuffix).get().addOnSuccessListener { querySnapshot->
                    if (!querySnapshot.isEmpty){
                        for (doc in querySnapshot){
                            CommonConstants.hideProgressDialog()
                            val action = DoctorProfileFragmentDirections.actionDoctorProfileFragmentToChatFragment(doc.getString("username")!!,doc.getString("userId")!!)
                            findNavController().navigate(action)
                            break
                        }
                    }else{
                        CommonConstants.hideProgressDialog()
                        CommonConstants.showCustomToast(requireActivity(), "لم يكمل هذا الدكتور عملية التسجيل!!","info")
                    }
                }.addOnFailureListener {
                    CommonConstants.hideProgressDialog()
                }
            }else{
                CommonConstants.hideProgressDialog()
                CommonConstants.showCustomToast(requireActivity(), "لا يمكن الوصول لهذا الدكتور!!","warning")
            }
        }

        val mapFragment =
            childFragmentManager
                .findFragmentById(R.id.fragmentMap)
                    as SupportMapFragment

        mapFragment.getMapAsync(this)

        if (!(CommonConstants.isLocationEnabled(requireActivity()))){
            viewModel.getDoctorProfileForUser(DoctorProfileForUserRequestModel(args.doctorId,null,null))
        }



        return binding.root
    }


    private fun initViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService))
        ).get(MyViewModel::class.java)
    }

    private fun getDoctorProfile() {
        viewModel.getDoctorProfileForUserResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    binding.layoutContent.visibility = View.VISIBLE
                    binding.layoutEmpty.visibility = View.GONE
                    val response = it.data!!.items
                    phoneNumber = response.phone_number
                    doctorModel=ProfileToBooking(response.id,response.name,response.spec+" - "+response.clin,response.image,response.patient_examination_price.toFloat())
                    loadDataInThisPage(response)
                    isDoctorInBookmarks = if (response.is_bookmark){
                        CommonConstants.onOpenFragmentsUpdateMenuItemsIcons?.invoke(R.mipmap.bookmark_blue)
                        true
                    }else{
                        CommonConstants.onOpenFragmentsUpdateMenuItemsIcons?.invoke(R.mipmap.bookmark_gray)
                        false
                    }
                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    CommonConstants.hideProgressDialog()
                    binding.layoutEmpty.visibility = View.VISIBLE
                    binding.layoutContent.visibility = View.GONE
                }
            }
        }
    }

    private fun putBookmarksResponse() {
        viewModel.putUserInBookmarksForUserResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val response = it.data
                    CommonConstants.showCustomToast(requireActivity(), response?.message.toString(),"success")
                    isDoctorInBookmarks = if (isDoctorInBookmarks){
                        CommonConstants.onOpenFragmentsUpdateMenuItemsIcons?.invoke(R.mipmap.bookmark_gray)
                        false
                    }else{
                        CommonConstants.onOpenFragmentsUpdateMenuItemsIcons?.invoke(R.mipmap.bookmark_blue)
                        true
                    }
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

    @SuppressLint("SetTextI18n")
    private fun loadDataInThisPage(doctorData:DoctorProfileForUserResponse){
        CommonConstants.loadImageWithPicasso(doctorData.image,binding.imgDoctor)
        binding.txtDoctorName.text = doctorData.name
        binding.txtPrice.text = "${doctorData.patient_examination_price} شيكل"
        binding.txtDoctorAddress.text = doctorData.spec+" - "+doctorData.clin
        if (doctorData.bio.length > 100){
            binding.txtDoctorDescription.text = doctorData.bio.substring(0,100)
            binding.btnShowMoreText.visibility = View.VISIBLE
        }else{
            binding.txtDoctorDescription.text = doctorData.bio
            binding.btnShowMoreText.visibility = View.GONE
        }

        if (isLocationEnabled){
            if (point!= null) {
                binding.txtDistanceOfYou.text ="${((((doctorData.distance.toDouble()*100).toInt()).toDouble())/100)} كم"
            }else{
                binding.txtDistanceOfYou.text = "مشكلة في تحديد الموقع"
            }
        }else{
            binding.txtDistanceOfYou.text = "يرجى تفعيل الموقع"
        }

        if(doctorData.bio.length > 100){
            binding.txtDoctorDescription.text = doctorData.bio.substring(0,100)
        }else{
            binding.txtDoctorDescription.text = doctorData.bio
        }

        binding.btnShowMoreText.setOnClickListener {
            if (isShowMore){
               binding.txtDoctorDescription.text = doctorData.bio.substring(0,100)
                isShowMore = false
            }else{
                binding.txtDoctorDescription.text = doctorData.bio
                isShowMore = true
            }
        }
        binding.txtRatingStartsNumber.text = doctorData.rate.toString()
        binding.ratingBar.rating = doctorData.rate
        binding.txtReviewNumber.text = doctorData.num_ratings.toString()
        binding.txtDurationAverage.text = CommonConstants.getStringTimeFormat((doctorData.average_answer_time*60).toFloat())

        if (doctorData.worktime.isNotEmpty()){
            binding.txtTimesOfWork.text = "يوم ${doctorData.worktime[0].days} من ${doctorData.worktime[0].open_time} إلى ${doctorData.worktime[0].close_time}"
            val splDays = doctorData.worktime[0].days.split(",")
            // نعملها على اول اوبجكت في ارري الورك
            val today = SimpleDateFormat("EEEE", Locale("ar")).format(Date())
            val formatter = SimpleDateFormat("HH:mm:ss",Locale.US)
            for (i in splDays){
                if (i == today){
                    if(CommonConstants.checkTimeBetweenTwoTimesOrNot(formatter.format(Date()),doctorData.worktime[0].open_time,doctorData.worktime[0].close_time,formatter)){
                        binding.txtIsWorkingNow.text = "مفتوح الان"
                        binding.dotGreen.visibility = View.VISIBLE
                        binding.txtIsWorkingNow.setTextColor(requireActivity().getColor(R.color.greenText))
                        break
                    }
                }
            }
        }

        binding.txtAddress.text = doctorData.address

        val clinic = LatLng(doctorData.lat.toDouble(), doctorData.long.toDouble())
        mMap.addMarker(MarkerOptions().position(clinic).title("عيادة الطبيب ${doctorData.name}"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clinic, 14F))

        val commentsAdapter = CommentsAdapter(requireActivity(),
            doctorData.ratings as MutableList<CommentForUserModel>
        )
        binding.rvComments.adapter = commentsAdapter
        binding.rvComments.layoutManager = LinearLayoutManager(activity)
        binding.rvComments.isNestedScrollingEnabled = false
        if (commentsAdapter.data.isNotEmpty()){
            binding.imgNoItems.visibility = View.GONE
        }else{
            binding.imgNoItems.visibility = View.VISIBLE
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (CommonConstants.checkLocationPermissions(requireActivity())){
            initGpsLocation()
            locationHelper.startLocationUpdates()
        }else{
            viewModel.getDoctorProfileForUser(DoctorProfileForUserRequestModel(args.doctorId,null,null))
        }

    }


    private fun initGpsLocation() {

        locationHelper = LocationHelper(requireActivity(), object : LocationManager {

            override fun onLocationChanged(location: Location?) {}

            override fun getLastKnownLocation(location: Location?) {
                if (CommonConstants.isLocationEnabled(requireActivity())){
                    lat = location?.latitude
                    lng = location?.longitude
                    point = LatLng(lat!!, lng!!)
                    isLocationEnabled = true
                }else{
                    isLocationEnabled = false
                }
                locationHelper.stopLocationUpdates()
            }
        })
    }
}