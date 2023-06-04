package com.alsadimoh.graduationproject.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.databinding.FragmentBookingSuccessfullyBinding


class BookingSuccessfullyFragment : Fragment(R.layout.fragment_booking_successfully) {

    lateinit var binding: FragmentBookingSuccessfullyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentBookingSuccessfullyBinding.inflate(inflater, container, false)
        binding.btnGoHome.setOnClickListener {
            findNavController().popBackStack()
        }
       return binding.root
    }
}