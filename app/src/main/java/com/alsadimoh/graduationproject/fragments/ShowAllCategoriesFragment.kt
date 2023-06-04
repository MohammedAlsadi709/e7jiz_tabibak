package com.alsadimoh.graduationproject.fragments

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
import com.alsadimoh.graduationproject.adapters.ShowAllCategoriesAdapter
import com.alsadimoh.graduationproject.databinding.FragmentShowAllCategoriesBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.userHome.SpecializationModel
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory


class ShowAllCategoriesFragment : Fragment(R.layout.fragment_show_all_categories) {


    lateinit var binding: FragmentShowAllCategoriesBinding
    private lateinit var viewModel: MyViewModel
    private val text = "ShowAllCategoriesFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentShowAllCategoriesBinding.inflate(inflater, container, false)
        initViewModel()
        getAllCategories()
        viewModel.getAllSpecializations()

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
                    setCategoriesItems(response!!.items as MutableList<SpecializationModel>)
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


    private fun setCategoriesItems(data:MutableList<SpecializationModel>){
        val showAllCategoriesAdapter = ShowAllCategoriesAdapter(requireActivity(),data)
        binding.rvCategories.adapter = showAllCategoriesAdapter
        binding.rvCategories.layoutManager = LinearLayoutManager(requireActivity())

        if (showAllCategoriesAdapter.data.isNotEmpty()){
            binding.layoutEmpty.visibility = View.GONE
        }else{
            binding.layoutEmpty.visibility = View.VISIBLE
        }

        showAllCategoriesAdapter.onCategoryClick = {model->
            val action = ShowAllCategoriesFragmentDirections.actionShowAllCategoriesFragmentToDoctorsOfCategoryFragment(model.title,model.id)
            findNavController().navigate(action)
        }

    }
}