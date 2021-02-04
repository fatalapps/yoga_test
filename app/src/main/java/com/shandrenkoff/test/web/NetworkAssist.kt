package com.shandrenkoff.test.web

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log

class NetworkAssist(contextg: Context) {
    private var context: Context = contextg

    fun hasNetworkAvailable(): Boolean {
        val service = Context.CONNECTIVITY_SERVICE
        val manager = context.getSystemService(service) as ConnectivityManager?
        val network = manager?.activeNetworkInfo
        Log.d("", "hasNetworkAvailable: ${(network != null)}")
        return (network != null)
    }
}