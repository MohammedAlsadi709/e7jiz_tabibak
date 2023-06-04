package com.alsadimoh.graduationproject.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.classes.CommonConstants.myPic
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.databinding.CustomChooseGenderDialogBinding
import com.alsadimoh.graduationproject.databinding.FragmentEditProfileBinding
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
import java.util.*


class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    lateinit var binding: FragmentEditProfileBinding
    private val text = "EditProfileFragment"
    val args : EditProfileFragmentArgs by navArgs()
    private lateinit var viewModel: MyViewModel
    var image: MultipartBody.Part? = null
    private var isPickedImage = false
    private var uri = ""
    var stringImage = myPic

    @SuppressLint("SetTextI18n", "IntentReset")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        initViewModel()
        getUpdateUserProfileResponse()

        CommonConstants.loadImageWithPicasso(args.model.image,binding.imgDoctor)
        binding.txtFullName.setText(args.model.name)
        binding.txtGender.text = args.model.gender
        binding.txtDateOfBirth.text = args.model.dateOfBirth
        binding.txtPhoneNumber.setText(args.model.phone_number)


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

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // There are no request codes
                    if (result.data != null) {
                        isPickedImage = true
                        uri = result.data!!.data.toString()
                        CommonConstants.loadImageWithPicasso(uri, binding.imgDoctor)

                        Picasso.get().load(uri).into(object : com.squareup.picasso.Target {
                            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
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

                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"

                resultLauncher.launch(Intent.createChooser(intent, "Select Picture"))
            }
        }



        binding.btnSave.setOnClickListener {

                if (binding.txtFullName.text.isNotEmpty()) {
                    if (binding.txtDateOfBirth.text != "--/--/----") {
                        if (binding.txtPhoneNumber.text.isNotEmpty()) {
                            if (binding.txtPhoneNumber.text.length == 10 && (binding.txtPhoneNumber.text.startsWith(
                                    "059"
                                ) || binding.txtPhoneNumber.text.startsWith("056"))
                            ) {



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
                                val name = RequestBody.create(MediaType.parse("text/plain"),  binding.txtFullName.text.toString())
                                val gender = RequestBody.create(MediaType.parse("text/plain"),  binding.txtGender.text.toString())
                                val phone = RequestBody.create(MediaType.parse("text/plain"),  binding.txtPhoneNumber.text.toString())
                                val dateOfBirth = RequestBody.create(MediaType.parse("text/plain"),  binding.txtDateOfBirth.text.toString())
                                viewModel.updateUserProfile( name,gender,dateOfBirth,phone,image)

                            } else {
                                CommonConstants.showCustomToast(requireActivity(), "يرجى ادخال رقم هاتف صالح","warning")
                            }
                        } else {
                            CommonConstants.showCustomToast(requireActivity(), "يرجى ادخال رقم الهاتف","warning")
                        }
                    } else {
                        CommonConstants.showCustomToast(requireActivity(), "يرجى ادخال تاريخ الميلاد","warning")
                    }
                } else {
                    CommonConstants.showCustomToast(requireActivity(), "يرجى ادخال الإسم كاملاً","warning")
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

    private fun getUpdateUserProfileResponse() {
        viewModel.getUpdateUserProfileResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val response = it.data
                    CommonConstants.putStrPref(CommonConstants.userImage,response!!.items.image)
                    CommonConstants.putStrPref(CommonConstants.userName,response.items.name)
                    CommonConstants.putStrPref(CommonConstants.userPhoneNumber,response.items.phone_number)
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
                    CommonConstants.showCustomToast(requireActivity(),  it.message.toString(),"error")
                }
            }
        }
    }

}