package com.cafe.dghackathon.shimhg02.dghack



import android.location.Location

interface GPSUpdateListener {
    fun onInit() { }
    fun onFirstChange(location: Location) { }
    fun onChange(location: Location)
}