package com.Haydar.smartalarm

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.provider.ContactsContract.ProfileSyncState.set
import android.provider.ContactsContract.SyncState.set
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.lang.reflect.Array.set
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val type = intent?.getIntExtra("type", -1)
        val message = intent?.getStringExtra("message")
        val title = if (type == 0)"one Time Alarm" else "Repeating Alarm"

        val notificationId = if (type == TYPE_ONE_TIME) ID_ONE_TIME else ID_REPEATING

        if (message != null) showAlarmNotification(context, title, message, notificationId)
    }



    @RequiresApi(Build.VERSION_CODES.S)
    fun setOneTimeAlarm(context: Context, type: Int, date: String, time: String, message: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(EXTRA_MESSAGE, message)
        intent.putExtra(EXTRA_TYPE, type)
        Log.e("ErrorSetOneTimeAlarm", "setOneTimeAlarm: $date $time")

        // date diterima = 2-2-2022
        // split untuk menghilang kan tanda '=' -> 2 2 2022
        val dateArray = date.split("-").toTypedArray()
        val convDateArray = ArrayList<Int>().toTypedArray()
        for (i in dateArray.indices) {

        }
        val timeArray = time.split(":")

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, dateArray[0].toInt())
        calendar.set(Calendar.MONTH, dateArray[1].toInt()-1)
        calendar.set(Calendar.YEAR, dateArray[2].toInt())
        // time
        calendar.set(Calendar.HOUR, timeArray[0].toInt())
        calendar.set(Calendar.MINUTE, timeArray[1].toInt())
        calendar.set(Calendar.SECOND, 0)

        val pendingIntent = PendingIntent.getBroadcast(context, ID_ONE_TIME, intent, PendingIntent.FLAG_MUTABLE)
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Toast.makeText(context, "Success Set One Time Alarm", Toast.LENGTH_SHORT).show()
        Log.i("setAlarmNotification", "setOneTimeAlarm : Alarm will rings on ${calendar.time}")
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun setRepeatingAlarm(context: Context, type: Int, time: String, message: String){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context,AlarmReceiver::class.java)
        intent.putExtra(EXTRA_MESSAGE,message)
        intent.putExtra(EXTRA_TYPE,type)

        val timeArray = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE,Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND, 0)

        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING,intent,PendingIntent.FLAG_MUTABLE)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        Toast.makeText(context,"Succes Set Up Repeating Alarm",Toast.LENGTH_SHORT).show()

    }


    fun cancelAlarm(context: Context, type: Int) {
    //alarm manager
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE)as AlarmManager
        // intent ke alarm receiver
        val intent =Intent(context,AlarmReceiver::class.java)

        // ambil request code \ ID_alarm bedasarkan tipe alarmnya
        val requestCode = if (type == TYPE_ONE_TIME) ID_ONE_TIME else ID_REPEATING

        // cancel pending inttent
        val pendingIntent = PendingIntent.getBroadcast(context,requestCode,intent,PendingIntent.FLAG_MUTABLE)

        // cancel pending intent
        alarmManager.cancel(pendingIntent)
        if (type == TYPE_ONE_TIME) {
            Toast.makeText(context, "Sucsesfully cancel One Time Alarm", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Sucsesfully cancel Repeating", Toast.LENGTH_SHORT).show()
        }
    }

    fun converterData(array: Array<String>): List<Int> {
        return array.map {
            it.toInt()
        }
    }

    private fun showAlarmNotification(
        context: Context?,
        title: String,
        message: String,
        notificationId: Int
    ) {
        val channelId = "chanel_w1"
        val channelName = "AlarmManager channel"

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_one_time)
            .setContentTitle(title)
            .setContentText(message)
            .setSound(alarmSound)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000)
            builder.setChannelId(channelId)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = builder.build()
        notificationManager.notify(notificationId, notification)
    }

    companion object {
        const val EXTRA_TYPE = "type"
        const val EXTRA_MESSAGE ="message"

        const val TYPE_ONE_TIME = 0
        const val TYPE_REPEATING =1

        const val ID_ONE_TIME =101
        const val ID_REPEATING =102

    }
}