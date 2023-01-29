package com.example.trapp.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import kotlin.reflect.KFunction0

class NetworkTracker {

    fun getInternet(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    companion object{

        @Volatile
        private var instance: NetworkTracker? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: NetworkTracker().also { instance = it }
            }
    }

}
