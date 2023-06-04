package com.alsadimoh.graduationproject.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.classes.CommonConstants.putBooleanPref
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.databinding.FragmentSplashBinding

class SplashFragment : Fragment(R.layout.fragment_splash) {

    lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(inflater, container, false)

        val isLoggedIn = CommonConstants.myShared.getBoolean(CommonConstants.isSignedIn,false)
        val userType = CommonConstants.myShared.getString(CommonConstants.userType,"user")
        val isFirstTime = CommonConstants.myShared.getBoolean(CommonConstants.isFirstTime,true)

        Handler(Looper.getMainLooper()).postDelayed({
           if (isFirstTime){
               putBooleanPref(CommonConstants.isFirstTime,false)
               //val action = SplashFragmentDirections.actionSplashFragmentToWelcomePageFragmentForStartUp(1)
               //findNavController().navigate(action)
               lifecycleScope.launchWhenResumed {
                   findNavController().navigate(R.id.action_splashFragment_to_welcomePageFragmentForStartUp)
               }
           }else{
               if (isLoggedIn && userType == "user"){
                   ///val action = SplashFragmentDirections.actionSplashFragmentToHomeFragment()
                  // findNavController().navigate(action)
                   lifecycleScope.launchWhenResumed {
                       findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                   }

               }else if (isLoggedIn && userType == "doctor"){
                //   val action = SplashFragmentDirections.actionSplashFragmentToDoctorHomeFragment()
                 //  findNavController().navigate(action)
                   lifecycleScope.launchWhenResumed {
                       findNavController().navigate(R.id.action_splashFragment_to_doctorHomeFragment)
                   }
               }else {
                 //  val action = SplashFragmentDirections.actionSplashFragmentToLoggingTypeFragment()
                 //  findNavController().navigate(action)
                   lifecycleScope.launchWhenResumed {
                       findNavController().navigate(R.id.action_splashFragment_to_loggingTypeFragment)
                   }
               }
           }
        }, 3000)

        return binding.root
    }
}