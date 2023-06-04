package com.alsadimoh.graduationproject.adapters.doctorsAdapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.fragments.doctorFragments.FinishedDoctorBookingFragment
import com.alsadimoh.graduationproject.fragments.doctorFragments.IncomingDoctorBookingFragment


class TimesViewPagerForDoctorAdapter (var context: Context, fm:FragmentManager):FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){

    private val TAB_TITLES = arrayOf(
            R.string.time_tab_text_1,
            R.string.time_tab_text_2
    )

    override fun getItem(position: Int): Fragment {

        var fragment: Fragment? =null

        when (position) {
            0 -> fragment = IncomingDoctorBookingFragment()
            1 -> fragment= FinishedDoctorBookingFragment()
        }

        return fragment!!
    }

    override fun getPageTitle(position: Int): CharSequence {
     return context.resources.getString(TAB_TITLES[position])
    //    return null //without any title , just icon
    }

    override fun getCount(): Int {
        return TAB_TITLES.size
    }

}