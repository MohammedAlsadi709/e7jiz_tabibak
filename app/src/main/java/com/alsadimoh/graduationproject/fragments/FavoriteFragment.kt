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
import com.alsadimoh.graduationproject.adapters.MostRatedDoctorsAdapter
import com.alsadimoh.graduationproject.databinding.FragmentFavoriteBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.userHome.DoctorModelForUserHome
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {

    private val text = "FavoriteFragment"
    private lateinit var viewModel: MyViewModel
    lateinit var binding: FragmentFavoriteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentFavoriteBinding.inflate(inflater, container, false)

        initViewModel()
        getUserBookmarksResponse()
        viewModel.getBookmarks()

        return binding.root
    }


    private fun initViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService))
        ).get(MyViewModel::class.java)
    }


    private fun getUserBookmarksResponse() {
        viewModel.getBookmarksResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val response = it.data
                    val data = mutableListOf<DoctorModelForUserHome>()
                    if (response!!.items.isNotEmpty()){
                        for (i in response.items){
                            data.add(i.doctor)
                        }
                    }
                    setFavoriteItems(data)
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

    private fun setFavoriteItems(list:MutableList<DoctorModelForUserHome>){
        val adapter = MostRatedDoctorsAdapter(requireActivity(),list,true)
        binding.rvFavorite.adapter = adapter
        binding.rvFavorite.layoutManager = LinearLayoutManager(requireActivity())

        if (adapter.data.isNotEmpty()){
            binding.layoutEmpty.visibility = View.GONE
        }else{
            binding.layoutEmpty.visibility = View.VISIBLE
        }

        adapter.onClickItem={model ->
            val action = FavoriteFragmentDirections.actionFavoriteFragmentToDoctorProfileFragment(model.name,model.id)
            findNavController().navigate(action)
        }
    }
}