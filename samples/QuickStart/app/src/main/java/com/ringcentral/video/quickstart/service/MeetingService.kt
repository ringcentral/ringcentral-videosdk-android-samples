package com.ringcentral.video.quickstart.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ringcentral.video.quickstart.R
import com.ringcentral.video.quickstart.controller.MeetingDataController
import com.ringcentral.video.quickstart.ui.MainActivity

class MeetingService : Service() {
    private val binder = MeetingServiceBinder()
    val controller = MeetingDataController()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "$this OnStartCommand $intent, $flags, $startId")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun joinMeeting(id: String, pwd: String? = null, displayName: String? = null) {
        Log.d(TAG, "$this join meeting $id")
        showForegroundNotification()
        controller.joinMeeting(id, pwd, displayName)
    }

    fun startInstantMeeting() {
        showForegroundNotification()
        controller.startInstantMeeting()
    }

    fun toggleAudioSwitch() = controller.toggleAudioSwitch()

    fun toggleVideoSwitch() = controller.toggleVideoSwitch()

    fun switchCamera() = controller.switchCamera()

    fun leaveMeeting() {
        Log.d(TAG, "$this Leave meeting ${controller.meetingId}")
        controller.leaveMeeting()
        dismissForegroundNotification()
    }

    fun endMeeting() {
        Log.d(TAG, "$this End meeting ${controller.meetingId}")
        controller.endMeeting()
        dismissForegroundNotification()
    }

    private fun showForegroundNotification() {
        createNotificationChannel()
        val pendingIntent = PendingIntent.getActivities(
            this, 0,
            arrayOf(Intent(this, MainActivity::class.java)), PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.meeting_in_progress))
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        controller.clear()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            channel.description = descriptionText
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun dismissForegroundNotification() {
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    inner class MeetingServiceBinder : Binder() {
        fun getService(): MeetingService = this@MeetingService
    }

    companion object {
        private const val TAG = "MeetingService"
        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_CHANNEL_ID = "meeting_channel"
    }
}