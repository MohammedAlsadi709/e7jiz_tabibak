package com.alsadimoh.graduationproject.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.databinding.FragmentOnBoardingBinding
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemChangeListener
import com.denzcoskun.imageslider.models.SlideModel

class OnBoardingFragment : Fragment(R.layout.fragment_on_boarding) {

    lateinit var binding: FragmentOnBoardingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOnBoardingBinding.inflate(inflater, container, false)

        val sliderImage = mutableListOf<SlideModel>()
        sliderImage.add(SlideModel(R.drawable.picture_banner1))
        sliderImage.add(SlideModel(R.drawable.picture_banner2))
        sliderImage.add(SlideModel(R.drawable.picture_banner3))
        //sliderImage.add(SlideModel(R.drawable.picture_banner4))

        //  slider library version 0.0.6
        //  true -> corpCenter
        //  binding.slider.setImageList(sliderImage,true)

        // slider library version 0.1.0
        binding.slider.setImageList(sliderImage, ScaleTypes.CENTER_CROP)

        binding.slider.setItemChangeListener(object :ItemChangeListener{
            override fun onItemChanged(position: Int) {
                when (position) {
                    0 -> {
                        binding.title.text = "احجز طبيبك بسهولة"
                        binding.desc.text = "قم بحجز أي طبيب تريده بجميع التخصصات من بيتك بكل سهولة"
                    }
                    1 -> {
                        binding.title.text = "اطلب استشارة طبية"
                        binding.desc.text = "اطلب اسشارتك الطبية بشكل مجاني"
                    }
                    2 -> {
                        binding.title.text = "راجع نتائج فحصك"
                        binding.desc.text = "راجع نتائج فحصك من خلال التطبيق وقم بعرض الأدوية الموصى بها"
                    }
                }
            }
        })

        binding.btnCreateAnAccount.setOnClickListener {
            val action =
                OnBoardingFragmentDirections.actionLoggingTypeFragmentToSignUpFirstFragment()
            findNavController().navigate(action)
        }
        binding.btnLogIn.setOnClickListener {
            val action = OnBoardingFragmentDirections.actionLoggingTypeFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        return binding.root
    }

}