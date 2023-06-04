package com.alsadimoh.graduationproject.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.databinding.FragmentWelcomePageForStartUpBinding

class WelcomePageFragmentForStartUp : Fragment(R.layout.fragment_welcome_page_for_start_up) {

    lateinit var binding: FragmentWelcomePageForStartUpBinding
    val args: WelcomePageFragmentForStartUpArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWelcomePageForStartUpBinding.inflate(inflater, container, false)

        when (args.pageNum) {
            1 -> {
                binding.imgPage.setImageResource(R.drawable.picture_banner1)
                binding.title.text = "احجز طبيبك بسهولة"
                binding.desc.text = "قم بحجز أي طبيب تريده بجميع التخصصات من بيتك بكل سهولة"
                binding.imageDots.setImageResource(R.mipmap.slider_icon_page1)

                binding.btnNext.setOnClickListener {
                    val action = WelcomePageFragmentForStartUpDirections.actionWelcomePageFragmentForStartUpSelf(args.pageNum + 1 )
                    findNavController().navigate(action)
                }
            }
            2 -> {
                binding.imgPage.setImageResource(R.drawable.picture_banner2)
                binding.title.text = "اطلب استشارة طبية"
                binding.desc.text = "اطلب اسشارتك الطبية بشكل مجاني"
                binding.imageDots.setImageResource(R.mipmap.slider_icon_page2)

                binding.btnNext.setOnClickListener {
                    val action = WelcomePageFragmentForStartUpDirections.actionWelcomePageFragmentForStartUpSelf(args.pageNum + 1 )
                    findNavController().navigate(action)
                }
            }
            3 -> {
                binding.imgPage.setImageResource(R.drawable.picture_banner3)
                binding.title.text = "راجع نتائج فحصك"
                binding.desc.text = "راجع نتائج فحصك من خلال التطبيق وقم بعرض الأدوية الموصى بها"
                binding.imageDots.setImageResource(R.mipmap.slider_icon_page3)
                binding.btnNext.text = "البدء"

                binding.btnNext.setOnClickListener {
                    val action = WelcomePageFragmentForStartUpDirections.actionWelcomePageFragmentForStartUpToRequestLocationPermissionFragment()
                    findNavController().navigate(action)
                }
            }
        }
        return binding.root
    }
}