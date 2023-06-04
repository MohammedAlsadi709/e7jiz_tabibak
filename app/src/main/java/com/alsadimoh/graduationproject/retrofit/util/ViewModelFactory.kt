package com.alsadimoh.graduationproject.retrofit.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alsadimoh.graduationproject.retrofit.ApiHelper
import com.alsadimoh.graduationproject.retrofit.MyViewModel

class ViewModelFactory(private val apiHelper: ApiHelper) :
    ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyViewModel::class.java)) {
            return MyViewModel(apiHelper) as T
        }

        throw IllegalArgumentException("Unknown class name")
    }

}