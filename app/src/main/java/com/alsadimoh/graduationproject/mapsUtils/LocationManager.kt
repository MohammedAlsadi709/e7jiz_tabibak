package com.alsadimoh.graduationproject.mapsUtils


import android.location.Location

interface LocationManager {
    fun onLocationChanged(location: Location?)

    fun getLastKnownLocation(location: Location?)
}