package com.ringcentral.video.quickstart.controller.handler

import android.util.Log
import com.ringcentral.video.IParticipant
import com.ringcentral.video.VideoEventHandler

open class SimpleVideoEventHandler : VideoEventHandler() {
    override fun onLocalVideoMuteChanged(muted: Boolean) {
        Log.d(TAG, "onLocalVideoMuteChanged, muted = $muted")
    }

    override fun onUnmuteVideoDemand() {
        Log.d(TAG, "onUnmuteVideoDemand")
    }

    override fun onFirstLocalVideoFrame(width: Int, height: Int, elapsed: Int) {
        Log.d(TAG, "onFirstLocalVideoFrame, width = $width, height = $height, elapsed = $elapsed")
    }

    override fun onFirstRemoteVideoFrame(
        participant: IParticipant?,
        width: Int,
        height: Int,
        elapsed: Int
    ) {
        Log.d(
            TAG, "onFirstRemoteVideoFrame, participant = ${participant?.displayName()}," +
                    " width = $width, height = $height, elapsed = $elapsed"
        )
    }

    override fun onRemoteVideoMuteChanged(participant: IParticipant?, muted: Boolean) {
        Log.d(
            TAG, "onRemoteVideoStreamMuteChanged," +
                    " ${participant?.displayName()}, muted = $muted"
        )
    }

    companion object {
        private const val TAG = "SimpleVideoEventHandler"
    }
}