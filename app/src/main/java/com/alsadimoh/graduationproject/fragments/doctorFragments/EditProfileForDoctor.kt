package com.alsadimoh.graduationproject.fragments.doctorFragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.databinding.FragmentEditProfileForDoctorBinding
import com.alsadimoh.graduationproject.mapsUtils.LocationHelper
import com.alsadimoh.graduationproject.mapsUtils.LocationManager
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class EditProfileForDoctor : Fragment(R.layout.fragment_edit_profile_for_doctor) {

    private lateinit var viewModel: MyViewModel
    lateinit var binding: FragmentEditProfileForDoctorBinding
    val args: EditProfileForDoctorArgs by navArgs()
    val text = "EditProfileForDoctor"
    private var isPickedImage = false
    private var uri = ""
    var image: MultipartBody.Part? = null
    var stringImage = CommonConstants.myPic
    private lateinit var locationHelper: LocationHelper
    var latPre = ""
    var longPre = ""

    @SuppressLint("IntentReset")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileForDoctorBinding.inflate(inflater, container, false)

        initViewModel()
        getUpdateDoctorProfileResponse()

        CommonConstants.loadImageWithPicasso(args.model.image, binding.imgDoctor)
        binding.txtAddress.setText(args.model.address)
        binding.txtBio.setText(args.model.bio)
        binding.txtPrice.setText(args.model.price.toString())
        binding.txtTimeForAnswer.setText(args.model.waitForAnswer.toString())
        binding.txtWaitingDur.setText(args.model.waitingTime.toString())



        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    if (result.data != null) {
                        isPickedImage = true
                        uri = result.data!!.data.toString()
                        CommonConstants.loadImageWithPicasso(uri, binding.imgDoctor)

                        Picasso.get().load(uri).into(object : com.squareup.picasso.Target {
                            override fun onBitmapLoaded(
                                bitmap: Bitmap?,
                                from: Picasso.LoadedFrom?
                            ) {
                                stringImage = CommonConstants.bitmapToString(bitmap!!)
                            }

                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
                        })
                    }
                }
            }

        binding.txtChangeProfilePicture.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1000
                )
            } else {

                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"

                resultLauncher.launch(Intent.createChooser(intent, "Select Picture"))
            }
        }




        binding.btnSave.setOnClickListener {

            if (binding.txtPrice.text.isNotEmpty()) {
                if (binding.txtWaitingDur.text.isNotEmpty()) {
                    if (binding.txtTimeForAnswer.text.isNotEmpty()) {
                        if (binding.txtAddress.text.isNotEmpty()) {
                            if (binding.txtBio.text.isNotEmpty()) {


                                if (isPickedImage) {
                                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                                    val cursor: Cursor? = requireActivity().contentResolver.query(
                                        Uri.parse(uri),
                                        filePathColumn,
                                        null,
                                        null,
                                        null
                                    )
                                    if (cursor != null) {
                                        cursor.moveToFirst()

                                        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                                        val filePath = cursor.getString(columnIndex)
                                        cursor.close()

                                        val file = File(filePath)

                                        //val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
                                        val requestFile = RequestBody.create(
                                            MediaType.parse("multipart/form-data"),
                                            file
                                        )
                                        image = MultipartBody.Part.createFormData(
                                            "image",
                                            file.name,
                                            requestFile
                                        )

                                    }

                                }
                                val price = RequestBody.create(
                                    MediaType.parse("text/plain"),
                                    binding.txtPrice.text.toString()
                                )
                                val waitingDur = RequestBody.create(
                                    MediaType.parse("text/plain"),
                                    binding.txtWaitingDur.text.toString()
                                )


                                val timeForAnswer = RequestBody.create(
                                    MediaType.parse("text/plain"),
                                    binding.txtTimeForAnswer.text.toString()
                                )
                                val address = RequestBody.create(
                                    MediaType.parse("text/plain"),
                                    binding.txtAddress.text.toString()
                                )
                                val bio = RequestBody.create(
                                    MediaType.parse("text/plain"),
                                    binding.txtBio.text.toString()
                                )

                                var lat = RequestBody.create(
                                    MediaType.parse("text/plain"),
                                   args.model.lat
                                )

                                var long = RequestBody.create(
                                    MediaType.parse("text/plain"),
                                    args.model.long
                                )


                                if (binding.cbChangeLatLong.isChecked){
                                    if (CommonConstants.checkLocationPermissions(requireActivity())){
                                        if (CommonConstants.isLocationEnabled(requireActivity())){
                                            initGpsLocation()
                                            locationHelper.startLocationUpdates()
                                            if (latPre.isNotEmpty()&& longPre.isNotEmpty()){
                                                 lat = RequestBody.create(
                                                    MediaType.parse("text/plain"),
                                                     latPre
                                                )

                                                 long = RequestBody.create(
                                                    MediaType.parse("text/plain"),
                                                     longPre
                                                )
                                            }
                                        }else{
                                            CommonConstants.showCustomToast(requireActivity(), "لم يتم تغيير الاحداثيات لانه لم يتم تشغيل الموقع","warning")

                                        }
                                    }else{
                                        CommonConstants.showCustomToast(requireActivity(), "لم يتم تغيير الاحداثيات لانه لم يتم اعطاء الاذن للتطبيق بالوصول للموقع","warning")
                                    }
                                }


                                viewModel.updateDoctorProfileForDoctor(bio,price,timeForAnswer,waitingDur,address,lat,long,image)

                            } else {
                                CommonConstants.showCustomToast(requireActivity(), "يرجى ادخال النبذة التعريفية","warning")

                            }
                        } else {
                            CommonConstants.showCustomToast(requireActivity(), "يرجى ادخال العنوان","warning")

                        }
                    } else {
                        CommonConstants.showCustomToast(requireActivity(), "يرجى ادخال معدل مدة الرد","warning")
                    }
                } else {
                    CommonConstants.showCustomToast(requireActivity(), "يرجى ادخال مدة الانتظار","warning")
                }
            } else {
                CommonConstants.showCustomToast(requireActivity(), "يرجى ادخال سعر الكشفية","warning")
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

    private fun getUpdateDoctorProfileResponse() {
        viewModel.updateDoctorProfileForDoctorResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                   // val response = it.data
                    CommonConstants.showCustomToast(requireActivity(), "تم تعديل الملف الشخصي","success")
                    findNavController().popBackStack()

                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    CommonConstants.hideProgressDialog()
                    CommonConstants.showCustomToast(requireActivity(), it.message,"error")
                }
            }
        }
    }



    private fun initGpsLocation() {
        locationHelper = LocationHelper(requireActivity(), object : LocationManager {

            override fun onLocationChanged(location: Location?) {}

            override fun getLastKnownLocation(location: Location?) {
                latPre = location?.latitude.toString()
                 longPre = location?.longitude.toString()
                locationHelper.stopLocationUpdates()
            }
        })
    }
}