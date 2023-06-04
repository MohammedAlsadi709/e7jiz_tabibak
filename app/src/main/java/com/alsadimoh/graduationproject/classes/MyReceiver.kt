package com.alsadimoh.graduationproject.classes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class MyReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {

        if(intent?.action.equals("com.alsadimoh.graduationproject.showratedialog")){
            //CommonConstants.putBooleanPref(CommonConstants.isNotificationForDoneReceived, false)
            val name = intent!!.getStringExtra(CommonConstants.doctorNameFromNotification)
            val id = intent.getIntExtra(CommonConstants.doctorIdFromNotification,-1)
            CommonConstants.onReceiverNotificationShowRatingDialog?.invoke(context!!,name!!,id)
        }else if (intent?.action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            CommonConstants.onWifiStateChanged?.invoke(CommonConstants.isNetworkAvailable(context))
        }

    }

}