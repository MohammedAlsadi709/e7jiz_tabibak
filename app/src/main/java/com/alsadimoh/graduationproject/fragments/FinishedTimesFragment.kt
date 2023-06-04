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
import com.alsadimoh.graduationproject.adapters.UserBookingsAdapter
import com.alsadimoh.graduationproject.databinding.FragmentFinishedTimesBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.userBooking.BookingModel
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory

class FinishedTimesFragment : Fragment(R.layout.fragment_finished_times) {

    private lateinit var viewModel: MyViewModel
    lateinit var binding: FragmentFinishedTimesBinding
    private  val  text = "FinishedTimesFragment"
    private var canSendRequest = true


    override fun onResume() {
        super.onResume()

        if (canSendRequest) {
            viewModel.getFinishedBookings()
            canSendRequest = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentFinishedTimesBinding.inflate(inflater, container, false)
        canSendRequest = true // عشان لما يرجع من شاشة اخرى يرفرش


        initViewModel()
        getFinishedBookingsResponse()

        return binding.root
    }
    private fun initViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService))
        ).get(MyViewModel::class.java)
    }

    private fun getFinishedBookingsResponse() {
        viewModel.getFinishedBookingsResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val response = it.data
                    if (response!!.items.isNotEmpty()){
                        setFinishedBookingsItems(response.items as MutableList<BookingModel>)
                        binding.layoutEmpty.visibility = View.GONE
                    }else{
                        binding.layoutEmpty.visibility = View.VISIBLE
                    }
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


    private fun setFinishedBookingsItems(data:MutableList<BookingModel>){
        val timesAdapter  = UserBookingsAdapter(requireActivity(),data,"finished")
        binding.rvFinishedTimes.adapter = timesAdapter
        binding.rvFinishedTimes.layoutManager = LinearLayoutManager(requireActivity())

        timesAdapter.onItemClick = { model->
            val action = FinishedTimesFragmentDirections.actionGlobalFinishedAppointmentDetailsFragment(model.id)
            findNavController().navigate(action)
        }
    }

}