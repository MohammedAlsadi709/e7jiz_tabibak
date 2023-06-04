package com.alsadimoh.graduationproject.fragments

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.databinding.FragmentRequestLocationPermissionBinding


class RequestLocationPermissionFragment : Fragment(R.layout.fragment_request_location_permission) {

    lateinit var binding: FragmentRequestLocationPermissionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentRequestLocationPermissionBinding.inflate(inflater, container, false)

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                // PERMISSION GRANTED
                val action = RequestLocationPermissionFragmentDirections.actionRequestLocationPermissionFragmentToLoggingTypeFragment()
                findNavController().navigate(action)
            } else {
                // PERMISSION NOT GRANTED
            }
        }

        binding.btnAllowLocation.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        binding.btnSkip.setOnClickListener {
            val action = RequestLocationPermissionFragmentDirections.actionRequestLocationPermissionFragmentToLoggingTypeFragment()
            findNavController().navigate(action)
        }

        return  binding.root
    }

}