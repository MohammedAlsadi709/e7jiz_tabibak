package com.alsadimoh.graduationproject.fragments.doctorFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.adapters.CommentsAdapter
import com.alsadimoh.graduationproject.databinding.FragmentReviewsBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.userDoctors.CommentForUserModel
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory



class ReviewsFragment : Fragment(R.layout.fragment_reviews) {
    private lateinit var viewModel: MyViewModel
    lateinit var binding: FragmentReviewsBinding
    val args: ReviewsFragmentArgs by navArgs()
    val TAG = "ReviewsFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentReviewsBinding.inflate(inflater, container, false)

        initViewModel()
        getAllDoctorRatingsForUser()// بنعمل 2 اوبزيرفر وبنعمل اف الس وحجواها بنحط جلب الداتا
        getAllDoctorRatingsForDoctor()

        if (args.isUser){
           viewModel.getAllDoctorRatingsForUser(args.doctorId)
       }else{
            viewModel.getAllDoctorRatingsForDoctor()
       }

        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService))
        ).get(MyViewModel::class.java)
    }

    private fun getAllDoctorRatingsForUser() {
        viewModel.getAllDoctorRatingsForUserResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val data = it.data!!.items
                    if (data.isNotEmpty()){
                        setCommentsItems(data as MutableList<CommentForUserModel>)
                        binding.imgNoItems.visibility = View.GONE
                    }else{
                        binding.imgNoItems.visibility = View.VISIBLE
                    }

                }
                Status.LOADING -> {
                    Log.e(TAG, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(TAG, "response: ERROR")
                    CommonConstants.hideProgressDialog()
                    CommonConstants.showCustomToast(requireActivity(),  it.message.toString(),"error")
                }
            }
        }
    }

    private fun getAllDoctorRatingsForDoctor() {
        viewModel.getAllDoctorRatingsForDoctorResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val data = it.data!!.items
                    if (data.isNotEmpty()){
                        setCommentsItems(data as MutableList<CommentForUserModel>)
                        binding.imgNoItems.visibility = View.GONE
                    }else{
                        binding.imgNoItems.visibility = View.VISIBLE
                    }

                }
                Status.LOADING -> {
                    Log.e(TAG, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(TAG, "response: ERROR")
                    CommonConstants.hideProgressDialog()
                    CommonConstants.showCustomToast(requireActivity(),  it.message.toString(),"error")
                }
            }
        }
    }

    private fun setCommentsItems(data: MutableList<CommentForUserModel>) {
        val commentsAdapter = CommentsAdapter(requireActivity(),data)
        binding.rvReviews.adapter = commentsAdapter
        binding.rvReviews.layoutManager = LinearLayoutManager(requireActivity())
    }

}