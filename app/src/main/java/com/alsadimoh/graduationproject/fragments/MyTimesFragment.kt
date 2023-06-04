package com.alsadimoh.graduationproject.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.adapters.TimesViewPagerAdapter
import com.alsadimoh.graduationproject.databinding.FragmentMyTimesBinding


class MyTimesFragment : Fragment(R.layout.fragment_my_times) {

    lateinit var binding:FragmentMyTimesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentMyTimesBinding.inflate(inflater, container, false)

        val timesViewPagerAdapter = TimesViewPagerAdapter(requireActivity(),childFragmentManager)
        binding.viewPager.adapter = timesViewPagerAdapter

        binding.tabs.setupWithViewPager(binding.viewPager)

        return binding.root
    }
}