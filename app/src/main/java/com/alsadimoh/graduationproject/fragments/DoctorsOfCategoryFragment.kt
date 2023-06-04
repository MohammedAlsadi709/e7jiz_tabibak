package com.alsadimoh.graduationproject.fragments

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
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.adapters.MostRatedDoctorsAdapter
import com.alsadimoh.graduationproject.databinding.FragmentDoctorsOfCategoryBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.userHome.DoctorModelForUserHome
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory


class DoctorsOfCategoryFragment : Fragment(R.layout.fragment_doctors_of_category) {

    lateinit var binding: FragmentDoctorsOfCategoryBinding
    private lateinit var viewModel: MyViewModel
    private val text = "DoctorsOfCategoryFragment"
    val args: DoctorsOfCategoryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDoctorsOfCategoryBinding.inflate(inflater, container, false)

        initViewModel()
        getSpecializationDoctors()
        viewModel.getSpecializationDoctors(args.categoryId)


        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService))
        ).get(MyViewModel::class.java)
    }


    private fun getSpecializationDoctors() {
        viewModel.getSpecializationDoctorsResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val response = it.data
                    setDoctorsItems(response!!.items.doctors as MutableList<DoctorModelForUserHome>)
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

    private fun setDoctorsItems(data: MutableList<DoctorModelForUserHome>) {

        val mostRatedDoctorsAdapter = MostRatedDoctorsAdapter(requireActivity(), data)
        binding.rvAllDoctors.adapter = mostRatedDoctorsAdapter
        binding.rvAllDoctors.layoutManager = LinearLayoutManager(activity)

        if (mostRatedDoctorsAdapter.data.isNotEmpty()){
            binding.layoutEmpty.visibility = View.GONE
        }else{
            binding.layoutEmpty.visibility = View.VISIBLE
        }


        mostRatedDoctorsAdapter.onClickItem = {model->
            val action = DoctorsOfCategoryFragmentDirections.actionDoctorsOfCategoryFragmentToDoctorProfileFragment(model.name,model.id)
            findNavController().navigate(action)
        }

    }

}