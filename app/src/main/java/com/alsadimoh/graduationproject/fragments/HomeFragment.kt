package com.alsadimoh.graduationproject.fragments

import android.content.Intent
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
import com.alsadimoh.graduationproject.adapters.CategoriesAdapter
import com.alsadimoh.graduationproject.adapters.MostRatedDoctorsAdapter
import com.alsadimoh.graduationproject.databinding.FragmentHomeBinding
import com.alsadimoh.graduationproject.classes.CommonConstants.onEnterHomeUpdateDrawerHeader
import com.alsadimoh.graduationproject.classes.CommonConstants.updateMenuItemsForUser
import com.alsadimoh.graduationproject.chatUtils.models.UserModelForRegister
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.userHome.BannerImage
import com.alsadimoh.graduationproject.retrofit.models.userHome.DoctorModelForUserHome
import com.alsadimoh.graduationproject.retrofit.models.userHome.SpecializationModel
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private val text = "HomeFragment"
    private lateinit var viewModel: MyViewModel
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var isNeedToMoveSearchFragment =
        false // لان الفراقمنت بعمل ريلود وبالتالي حيكون مكيش عملية البحث انها ناجحة وحيرجع ينتقل للسيرش فراقمنت

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val name = CommonConstants.myShared.getString(CommonConstants.userName, null)
        val password = CommonConstants.myShared.getString(CommonConstants.userPassword, null)
        val email = CommonConstants.myShared.getString(CommonConstants.userEmail, null)
        val userType = CommonConstants.myShared.getString(CommonConstants.userType, "user")

        if (name != null && password != null && email != null) {
            val model = UserModelForRegister(
                null,
                null,
                name,
                email,
                CommonConstants.bitmapToString(requireActivity(), R.mipmap.ic_user_profile_default),
                "مرحباً أنا متوفر لمراسلتي!",
                "متوفر",
                null,
                userType
            )

            mAuth = FirebaseAuth.getInstance()
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    model.userId = mAuth.uid!!
                    addUserToFireStoreDatabase(model)
                }
            }.addOnFailureListener {
                mAuth.signInWithEmailAndPassword(email, password)
            }
        }

        initViewModel()
        getHomePageResponse()
        getSearchWithWordsResponse()

        viewModel.fetchUserHomepageData()

        onEnterHomeUpdateDrawerHeader?.invoke()
        updateMenuItemsForUser?.invoke()


        binding.txtSearch.setOnEditorActionListener { _, _, _ ->
            //   if (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER){ }
            if (binding.txtSearch.text.isNotEmpty()) {
                isNeedToMoveSearchFragment = true
                viewModel.searchWithWords(binding.txtSearch.text.toString())
            } else {
                CommonConstants.showCustomToast(requireActivity(),"لا يمكن البحث عن نص فارغ","warning")
            }
            return@setOnEditorActionListener true
        }

        binding.btnFilter.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSearchFilterFragment()
            findNavController().navigate(action)
        }


        binding.btnShowAllCategories.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToShowAllCategoriesFragment()
            findNavController().navigate(action)
        }

        binding.btnShowAllTheMostRating.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAllRatedDoctorsFragment()
            findNavController().navigate(action)
        }

        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService))
        ).get(MyViewModel::class.java)
    }


    private fun getHomePageResponse() {
        viewModel.getUserHomePageResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.layoutContent.visibility = View.VISIBLE
                    binding.layoutNoItems.visibility = View.GONE
                    CommonConstants.hideProgressDialog()
                    val response = it.data
                    setCategoriesItems(response!!.items.specializations as MutableList<SpecializationModel>)
                    setMostRatedDoctorsItems(response.items.doctor as MutableList<DoctorModelForUserHome>)
                    setSliderItems(response.items.ads as MutableList<BannerImage>)
                    checkRateNotifications()
                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    binding.layoutContent.visibility = View.INVISIBLE
                    binding.layoutNoItems.visibility = View.VISIBLE
                    CommonConstants.hideProgressDialog()
                    CommonConstants.showCustomToast(requireActivity(),it.message,"error")
                }
            }
        }
    }


    private fun getSearchWithWordsResponse() {
        viewModel.searchWithWordsResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val response = it.data
                    if (isNeedToMoveSearchFragment) {
                        isNeedToMoveSearchFragment = false
                        val action =
                            HomeFragmentDirections.actionHomeFragmentToSearchFragment(response!!.items.toTypedArray())
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
                    CommonConstants.showCustomToast(requireActivity(),it.message,"error")

                }
            }
        }
    }

    private fun setCategoriesItems(data: MutableList<SpecializationModel>) {
        val categoriesAdapter = CategoriesAdapter(requireActivity(), data)
        binding.rvCategories.adapter = categoriesAdapter
        binding.rvCategories.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        categoriesAdapter.onClickCategoryItem = { model ->
            val action = HomeFragmentDirections.actionHomeFragmentToDoctorsOfCategoryFragment(model.title, model.id)
            findNavController().navigate(action)
        }
    }

    private fun setMostRatedDoctorsItems(data: MutableList<DoctorModelForUserHome>) {
        val mostRatedDoctorsAdapter = MostRatedDoctorsAdapter(requireActivity(), data)
        binding.rvMostRating.adapter = mostRatedDoctorsAdapter
        binding.rvMostRating.layoutManager = LinearLayoutManager(activity)
        mostRatedDoctorsAdapter.onClickItem = { model ->
            val action = HomeFragmentDirections.actionHomeFragmentToDoctorProfileFragment(
                model.name,
                model.id
            )
            findNavController().navigate(action)
        }

    }

    private fun setSliderItems(data: MutableList<BannerImage>) {
        val sliderImage = mutableListOf<SlideModel>()
        for (i in data) {
            sliderImage.add(SlideModel(i.image))
        }
        //  slider library version 0.0.6
        // true -> corpCenter
        //  binding.slider.setImageList(sliderImage,true)

        // slider library version 0.1.0
        //binding.slider.setImageList(sliderImage, ScaleTypes.CENTER_CROP)
        binding.slider.setImageList(sliderImage, ScaleTypes.FIT)

        binding.slider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {
                val action = HomeFragmentDirections.actionHomeFragmentToDoctorProfileFragment(
                    " ",
                    data[position].doctor_id
                )
                findNavController().navigate(action)

            }
        })
    }


    private fun addUserToFireStoreDatabase(modelForRegister: UserModelForRegister) {
        db = Firebase.firestore
        db.collection("registeredUsers").add(modelForRegister)
    }

    private fun checkRateNotifications() {
        // الفحص اذا كان في نوتيفيكيشن وصل في الباكجراوند وبدنا نظهر ديالوج التقييم
        val isNotification = CommonConstants.myShared.getBoolean(
            CommonConstants.isNotificationForDoneReceived,
            false
        )
        if (isNotification) {
            val doctorName = CommonConstants.myShared.getString(
                CommonConstants.doctorNameFromNotification,
                "لا يوجد اسم"
            )
            val doctorId = CommonConstants.myShared.getInt(
                CommonConstants.doctorIdFromNotification,
                -1
            )

            val broadcast = Intent("com.alsadimoh.graduationproject.showratedialog")
            broadcast.putExtra(CommonConstants.doctorNameFromNotification, doctorName)
            broadcast.putExtra(CommonConstants.doctorIdFromNotification, doctorId)
            requireActivity().sendBroadcast(broadcast)
        }
    }

}