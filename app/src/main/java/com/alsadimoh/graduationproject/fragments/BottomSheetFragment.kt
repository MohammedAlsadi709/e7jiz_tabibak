package com.alsadimoh.graduationproject.fragments

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.classes.CommonConstants.onOpenBottomSheetChangeText
import com.alsadimoh.graduationproject.classes.CommonConstants.onOpenBottomSheetSetItems
import com.alsadimoh.graduationproject.adapters.doctorsAdapters.BottomSheetAdapter
import com.alsadimoh.graduationproject.databinding.CustomAddNewMedicineDialogBinding
import com.alsadimoh.graduationproject.databinding.FragmentBottomSheetBinding
import com.alsadimoh.graduationproject.retrofit.ApiHelperImpl
import com.alsadimoh.graduationproject.retrofit.MyViewModel
import com.alsadimoh.graduationproject.retrofit.RetrofitBuilder
import com.alsadimoh.graduationproject.retrofit.models.doctorBooking.BottomSheetItems
import com.alsadimoh.graduationproject.retrofit.util.Status
import com.alsadimoh.graduationproject.retrofit.util.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var orgData: MutableList<BottomSheetItems>
    private val text = "BottomSheetFragment"
    private lateinit var viewModel: MyViewModel
    private var selectedPosition = -1
    lateinit var binding: FragmentBottomSheetBinding
    lateinit var adapter: BottomSheetAdapter
    private lateinit var bottomSheetFragment: BottomSheetFragment


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBottomSheetBinding.inflate(inflater, container, false)

        initViewModel()
        getSearchForMedicineForDoctorResponse()
        getAddNewMedicineToListResponse()

        binding.btnDone.setOnClickListener {
            bottomSheetFragment.dismiss()
        }

        binding.btnAddNewMedicine.setOnClickListener {
            val dialog = Dialog(requireActivity())
            val dialogBinding =
                CustomAddNewMedicineDialogBinding.inflate(LayoutInflater.from(activity))
            dialog.setContentView(dialogBinding.root)
            dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialogBinding.btnDone.setOnClickListener {
                if (dialogBinding.txtName.text.isNotEmpty()) {
                    if (dialogBinding.txtDescription.text.isNotEmpty()) {
                        viewModel.addNewMedicineToList(
                            dialogBinding.txtName.text.toString(),
                            dialogBinding.txtDescription.text.toString()
                        )
                        dialog.dismiss()
                    } else {
                        CommonConstants.showCustomToast(requireActivity(), "يرجى ادخال وصف الدواء الذي تود اضافته","warning")
                    }
                } else {
                    CommonConstants.showCustomToast(requireActivity(), "يرجى ادخال اسم الدواء الذي تود اضافته","warning")
                }

            }
            dialog.show()
            val window: Window = dialog.window!!
            window.setLayout(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT
            )
        }

        binding.txtSearch.setOnEditorActionListener { _, _, _ ->
            //   if (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER){ }
            if (binding.txtSearch.text.isNotEmpty()) {
                viewModel.searchForMedicineForDoctor(binding.txtSearch.text.toString())
            } else {
                CommonConstants.showCustomToast(requireActivity(), "لا يمكن البحث عن نص فارغ","warning")
            }
            return@setOnEditorActionListener true
        }

        val empty = mutableListOf<BottomSheetItems>()
        adapter = BottomSheetAdapter(requireActivity(),empty )
        binding.rvMedicines.adapter = adapter
        binding.rvMedicines.layoutManager = LinearLayoutManager(requireActivity())

        adapter.onClickItem = { model: BottomSheetItems, position ->
            if (selectedPosition != -1) {
                adapter.data[selectedPosition].isChecked = false
                adapter.notifyItemChanged(selectedPosition)
            }
            onOpenBottomSheetChangeText?.invoke(model.itemName, model.itemId.toString())

            adapter.data[position].isChecked = true
            selectedPosition = position
            adapter.notifyItemChanged(selectedPosition)
        }

        onOpenBottomSheetSetItems =
            { type: String, data: MutableList<BottomSheetItems>, textView, idTextView, sheetInterface: BottomSheetFragment ->
                if (type == "medicine") {
                    binding.txtSearch.visibility = View.VISIBLE
                    binding.btnAddNewMedicine.visibility = View.VISIBLE
                } else if (type == "period") {
                    binding.layoutSearch.visibility = View.GONE
                    binding.btnAddNewMedicine.visibility = View.GONE
                }

                bottomSheetFragment = sheetInterface
                orgData = data
                adapter.data = data
                adapter.notifyDataSetChanged()
                onOpenBottomSheetChangeText = { text, id ->
                    textView.text = text
                    idTextView.text = id
                }
            }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        try {
            adapter.data[selectedPosition].isChecked = false
        } catch (e: Exception) {
        }

    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelperImpl(RetrofitBuilder.apiService))
        ).get(MyViewModel::class.java)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getSearchForMedicineForDoctorResponse() {
        viewModel.getSearchForMedicineForDoctorResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val response = it.data
                    val data = mutableListOf<BottomSheetItems>()
                    if (response!!.items.isNotEmpty()) {
                        binding.txtEmptyItems.visibility = View.GONE
                        for (i in response.items) {
                            data.add(BottomSheetItems(i.id, i.name, i.description, i.deleted_at))
                        }

                    } else {
                        binding.txtEmptyItems.visibility = View.VISIBLE
                    }
                    adapter.data = data
                    adapter.notifyDataSetChanged()
                    CommonConstants.hideProgressDialog()
                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    CommonConstants.hideProgressDialog()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAddNewMedicineToListResponse() {
        viewModel.getAddNewMedicineToListResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val response = it.data!!.items
                    CommonConstants.hideProgressDialog()
                    orgData.add(
                        BottomSheetItems(
                            response.id,
                            response.name,
                            response.description,
                            response.deleted_at
                        )
                    )
                    adapter.notifyDataSetChanged()
                }
                Status.LOADING -> {
                    Log.e(text, "LOADING")
                    CommonConstants.showProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    Log.e(text, "response: ERROR")
                    CommonConstants.hideProgressDialog()
                }
            }
        }
    }

}