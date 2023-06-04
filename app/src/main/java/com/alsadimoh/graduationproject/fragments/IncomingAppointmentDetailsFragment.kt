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
import androidx.navigation.fragment.navArgs
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.databinding.FragmentIncomingAppointmentDetailsBinding
import com.alsadimoh.graduationproject.mapsUtils.LocationHelper
import com.alsadimoh.graduationproject.mapsUtils.LocationManager
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.userBooking.BookingModel
import com.alsadimoh.graduationproject.retrofit.models.userBooking.GetIncomingBookingDetailsRequestModel
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class IncomingAppointmentDetailsFragment :
    Fragment(R.layout.fragment_incoming_appointment_details) , OnMapReadyCallback {

    private lateinit var viewModel: MyViewModel
    lateinit var binding: FragmentIncomingAppointmentDetailsBinding
    private lateinit var mMap: GoogleMap
    val args:IncomingAppointmentDetailsFragmentArgs by navArgs()
    val text = "IncomingAppointmentDetailsFragment"
    private var lng: Double? = null
    private var lat: Double? = null
    private lateinit var locationHelper: LocationHelper
    private var isLocationEnabled = false
    private var point:LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentIncomingAppointmentDetailsBinding.inflate(inflater, container, false)

        val mapFragment =
            childFragmentManager
                .findFragmentById(R.id.fragmentMap)
                    as SupportMapFragment

        mapFragment.getMapAsync(this)

        initViewModel()
        getIncomingBookingDetailsForUserResponse()

        if (!(CommonConstants.isLocationEnabled(requireActivity()))){
            if(args.isIncoming){
                viewModel.getIncomingBookingDetailsForUser(GetIncomingBookingDetailsRequestModel(args.bookingId))
            }else{
                viewModel.getPendingBookingDetailsForUser(GetIncomingBookingDetailsRequestModel(args.bookingId))
            }
        }


        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (CommonConstants.checkLocationPermissions(requireActivity())){
            initGpsLocation()
            locationHelper.startLocationUpdates()
        }else{
            if(args.isIncoming){
                viewModel.getIncomingBookingDetailsForUser(GetIncomingBookingDetailsRequestModel(args.bookingId))
            }else{
                viewModel.getPendingBookingDetailsForUser(GetIncomingBookingDetailsRequestModel(args.bookingId))
            }        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService))
        ).get(MyViewModel::class.java)
    }


    private fun getIncomingBookingDetailsForUserResponse() {
        viewModel.getIncomingBookingDetailsForUserResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.layoutContent.visibility = View.VISIBLE
                    binding.layoutEmptyContent.visibility = View.GONE
                    CommonConstants.hideProgressDialog()
                    val response = it.data!!.items
                    loadDataToThePage(response)
                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    binding.layoutContent.visibility = View.GONE
                    binding.layoutEmptyContent.visibility = View.VISIBLE
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
                if (CommonConstants.isLocationEnabled(requireActivity())){
                    lat = location?.latitude
                    lng = location?.longitude
                    point = LatLng(lat!!, lng!!)
                    isLocationEnabled = true
                }else{
                    isLocationEnabled = false
                }

                if (point != null){
                    if(args.isIncoming){
                        viewModel.getIncomingBookingDetailsForUser(GetIncomingBookingDetailsRequestModel(args.bookingId,point!!.latitude, point!!.longitude))
                    }else{
                        viewModel.getPendingBookingDetailsForUser(GetIncomingBookingDetailsRequestModel(args.bookingId,point!!.latitude, point!!.longitude))
                    }
                }else{
                    if(args.isIncoming){
                        viewModel.getIncomingBookingDetailsForUser(GetIncomingBookingDetailsRequestModel(args.bookingId))
                    }else{
                        viewModel.getPendingBookingDetailsForUser(GetIncomingBookingDetailsRequestModel(args.bookingId))
                    }
                }

                locationHelper.stopLocationUpdates()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun loadDataToThePage(model:BookingModel){
        CommonConstants.loadImageWithPicasso(model.doctor.image,binding.imgDoctor)
        binding.txtDoctorName.text = model.doctor.name
        binding.txtDoctorTitle.text = model.doctor.specialization.title + " - " + model.doctor.clinic.name
        binding.txtDate.text = model.date
        binding.txtTime.text = model.time
        binding.txtPrice.text = model.price.toString()
        binding.txtAddress.text = model.doctor.address

        val clinic = LatLng(model.doctor.lat, model.doctor.long)
        mMap.addMarker(MarkerOptions().position(clinic).title("عيادة الطبيب ${model.doctor.name}"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clinic, 14F))

        if (isLocationEnabled){
            if (point!= null) {
                binding.txtDistance.text ="${((((model.doctor.distance!!.toDouble()*100).toInt()).toDouble())/100)} كم"
            }else{
                binding.txtDistance.text = "مشكلة في تحديد الموقع"
            }
        }else{
            binding.txtDistance.text = "يرجى تفعيل الموقع"
        }


    }

}