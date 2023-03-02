package com.ringcentral.video.quickstart.controller.handler

import android.util.Log
import com.ringcentral.video.*

open class SimpleStatisticEventHandler : MeetingStatisticEventHandler() {

    override fun onLocalAudioStats(stats: LocalAudioStats?) {
        Log.d(TAG, "onLocalAudioStats, bytesSent = ${stats?.bytesSent}")
    }

    override fun onLocalVideoStats(stats: LocalVideoStats?) {
        Log.d(
            TAG,
            "onLocalVideoStats, size = ${stats?.width} x ${stats?.height}, codec = ${stats?.codec}"
        )
    }

    override fun onRemoteAudioStats(stats: RemoteAudioStats?) {
        Log.d(TAG, "onRemoteAudioStats, bytesReceived = ${stats?.bytesReceived}")
    }

    override fun onRemoteVideoStats(stats: RemoteVideoStats?) {
        Log.d(
            TAG,
            "onRemoteVideoStats, size = ${stats?.width} x ${stats?.height}, fps = ${stats?.fps}, codec = ${stats?.codec}"
        )
    }

    companion object {
        private const val TAG = "SimpleStatisticEventHandler"
    }
}