package com.alsadimoh.graduationproject.classes

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.alsadimoh.graduationproject.MainActivity
import com.alsadimoh.graduationproject.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random


private const val CHANNEL_ID = "graduation_project"

class FirebaseNotificationsService:FirebaseMessagingService() {

    companion object{
        var token:String?
        get() {
            return CommonConstants.myShared.getString(CommonConstants.firebaseToken,"")
        }
        set(value) {
            CommonConstants.putStrPref(CommonConstants.firebaseToken,value)
        }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
    }

    override fun handleIntent(intent: Intent?) {
        super.handleIntent(intent)
        // تنفيذ الاون ماسيج ريسيف بيصير في حال كان التطبيق شغال .. فقط اما في حال لو كان مطفي ما بهندل النوتيفيكيشن من الاون ماسيج ريسيف بل بهندله في مكان اسمه tray system
        // لكن هنا في الهاندل انتنت بنقدر نهندل النوعين بشرط اننا نحذف الاصلي من التري سيستم عشان ميوصلش التلقائي والي هندلناه
        // وانا فقط هدفي هنا اني اعرف ان هل وصل نوتيفيكيشن في الباكجراوند بالعنوان التالي ولا لا

        val i = intent!!.extras
        val title = i?.get("gcm.notification.title")
        if (title == "نتمنى لك الشفاء العاجل"){
            val nameAndId = i.get("gcm.notification.icon").toString()
            val sb = nameAndId.split(",")
            val myShared = getSharedPreferences(CommonConstants.mySharedPrefKey,Context.MODE_PRIVATE)
            val editor = myShared.edit()
            editor.putString(CommonConstants.doctorNameFromNotification,sb[0])
            editor.putInt(CommonConstants.doctorIdFromNotification,sb[1].trim().toInt())
            editor.putBoolean(CommonConstants.isNotificationForDoneReceived,true)
            editor.apply()
        }
    }


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val i = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        // data when app is closed "background"
        //val title = message.data["title"] ///
       // val body = message.data["message"]

        // notification when app is running "foreground"
        val title = message.notification?.title
        val body = message.notification?.body
        val nameAndId = message.notification?.icon

        if (title=="نتمنى لك الشفاء العاجل"){
            val broadcast = Intent("com.alsadimoh.graduationproject.showratedialog")
            val sb = nameAndId!!.split(",")
            broadcast.putExtra(CommonConstants.doctorNameFromNotification,sb[0])
            broadcast.putExtra(CommonConstants.doctorIdFromNotification,sb[1].trim().toInt())
            sendBroadcast(broadcast)
        }

        Log.e("moh", "onMessageReceived: $title \n $body")

        val notificationCompat = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.logo_notifications2)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId,notificationCompat)

    }
}


private fun createNotificationChannel(notificationManager: NotificationManager) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(CHANNEL_ID, "حجوزات",
           // NotificationManager.IMPORTANCE_DEFAULT
                 NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description= "Graduation Project Notifications"
            enableLights(true)
            lightColor = Color.parseColor("#00A4CE")
        }
        notificationManager.createNotificationChannel(notificationChannel)
    }



}