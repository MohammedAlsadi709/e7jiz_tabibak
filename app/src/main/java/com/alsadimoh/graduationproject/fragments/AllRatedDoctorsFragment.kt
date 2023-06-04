package com.alsadimoh.graduationproject.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.adapters.MostRatedDoctorsAdapter
import com.alsadimoh.graduationproject.adapters.PaginationAdapter
import com.alsadimoh.graduationproject.databinding.FragmentAllRatedDoctorsBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.userDoctors.PaginationButtonModel
import com.alsadimoh.graduationproject.retrofit.models.userHome.DoctorModelForUserHome
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory


class AllRatedDoctorsFragment : Fragment(R.layout.fragment_all_rated_doctors) {

    private lateinit var viewModel: MyViewModel
    private val text = "AllRatedDoctorsFragment"
    lateinit var binding: FragmentAllRatedDoctorsBinding
    private var oldPositionForSelect = -1
    private var isFirstRequest = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAllRatedDoctorsBinding.inflate(inflater, container, false)

        initViewModel()
        getAllRatedDoctorsResponse()
        viewModel.getAllRatedDoctors("1")

        return binding.root
    }


    private fun initViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService))
        ).get(MyViewModel::class.java)
    }

    private fun getAllRatedDoctorsResponse() {
        viewModel.getAllRatedDoctorsResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val response = it.data
                    setAllRatedDoctor(response!!.items as MutableList<DoctorModelForUserHome>)

                    if (response.pages.last_page > 1) {
                        setPaginationItems(response.pages.last_page)
                    }else{
                        binding.layoutPagination.visibility = View.GONE
                    }
                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    CommonConstants.hideProgressDialog()
                    Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
                    binding.layoutPagination.visibility = View.GONE
                }
            }
        }
    }

    private fun setAllRatedDoctor(data: MutableList<DoctorModelForUserHome>) {
        val adapter = MostRatedDoctorsAdapter(requireActivity(), data)
        binding.rvAllRatedDoctors.adapter = adapter
        binding.rvAllRatedDoctors.layoutManager = LinearLayoutManager(requireActivity())

        if (adapter.data.isNotEmpty()) {
            binding.layoutEmpty.visibility = View.GONE
        } else {
            binding.layoutEmpty.visibility = View.VISIBLE
        }


        adapter.onClickItem = { model: DoctorModelForUserHome ->
            val action =
                AllRatedDoctorsFragmentDirections.actionAllRatedDoctorsFragmentToDoctorProfileFragment(
                    model.name,
                    model.id
                )
            findNavController().navigate(action)
        }
    }

    private fun setPaginationItems(pagesNumber: Int) {

        binding.layoutPagination.visibility = View.VISIBLE
        val data = mutableListOf<PaginationButtonModel>()
        for (i in 1..pagesNumber) {
            data.add(PaginationButtonModel(i))
        }

        if (isFirstRequest) {
            isFirstRequest = false
            oldPositionForSelect = 0
        }
        data[oldPositionForSelect].isSelected = true


        val adapter = PaginationAdapter(requireActivity(), data)
        binding.rvPaginationBtns.adapter = adapter
        binding.rvPaginationBtns.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

        adapter.onClickItem = { position: Int, _: PaginationButtonModel ->
            if (!(adapter.data[position].isSelected)) {
                if (oldPositionForSelect != -1) {
                    adapter.data[oldPositionForSelect].isSelected = false
                    adapter.notifyItemChanged(oldPositionForSelect)
                }
                adapter.data[position].isSelected = true
                adapter.notifyItemChanged(position)
                oldPositionForSelect = position
                viewModel.getAllRatedDoctors("${position + 1}")
            }
        }
    }

}