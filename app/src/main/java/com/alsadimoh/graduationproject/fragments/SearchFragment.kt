package com.alsadimoh.graduationproject.fragments

import android.annotation.SuppressLint
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
import com.alsadimoh.graduationproject.databinding.FragmentSearchBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.userHome.DoctorModelForUserHome
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory


class SearchFragment : Fragment(R.layout.fragment_search) {

    private val text= "SearchFragment"
    private lateinit var viewModel: MyViewModel
    lateinit var binding: FragmentSearchBinding
    val args: SearchFragmentArgs by navArgs()
    lateinit var adapter: MostRatedDoctorsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =  FragmentSearchBinding.inflate(inflater, container, false)

        initViewModel()
        getSearchWithWordsResponse()

        adapter = MostRatedDoctorsAdapter(requireActivity(),args.data.toMutableList())
        binding.rvSearch.adapter = adapter
        binding.rvSearch.layoutManager = LinearLayoutManager(activity)
        adapter.onClickItem = {model->
            val action =
                SearchFragmentDirections.actionSearchFragmentToDoctorProfileFragment(model.name,model.id)
            findNavController().navigate(action)
        }

        if(args.data.isEmpty()){
            binding.layoutNoItems.visibility = View.VISIBLE
        }


        binding.txtSearch.setOnEditorActionListener { _, _, _ ->
            //   if (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER){ }
            if ( binding.txtSearch.text.isNotEmpty()){
                viewModel.searchWithWords( binding.txtSearch.text.toString())
            }else{
                CommonConstants.showCustomToast(requireActivity(), "لا يمكن البحث عن نص فارغ","warning")
            }
            return@setOnEditorActionListener true
        }

        binding.btnFilter.setOnClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToSearchFilterFragment()
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

    @SuppressLint("NotifyDataSetChanged")
    private fun getSearchWithWordsResponse() {
        viewModel.searchWithWordsResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    CommonConstants.hideProgressDialog()
                    val response = it.data
                    adapter.data = response!!.items as MutableList<DoctorModelForUserHome>
                    adapter.notifyDataSetChanged()
                    if (response.items.isEmpty()){
                        binding.layoutNoItems.visibility = View.VISIBLE
                    }else{
                        binding.layoutNoItems.visibility = View.GONE
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
}