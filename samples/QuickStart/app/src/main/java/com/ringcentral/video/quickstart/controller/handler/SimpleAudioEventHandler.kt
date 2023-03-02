package com.ringcentral.video.quickstart.controller.handler

import android.util.Log
import com.ringcentral.video.*

open class SimpleAudioEventHandler : AudioEventHandler() {
    override fun onLocalAudioStreamStateChanged(
        state: LocalAudioStreamState,
        error: LocalAudioError
    ) {
        Log.d(TAG, "onLocalAudioStreamStateChanged, state = $state, error = $error")
    }

    override fun onLocalAudioMuteChanged(muted: Boolean) {
        Log.d(TAG, "onLocalAudioMuteChanged, muted = $muted")
    }

    override fun onRemoteAudioMuteChanged(participant: IParticipant, muted: Boolean) {
        Log.d(
            TAG, "onRemoteAudioMuteChanged, " +
                    "participant = ${participant.displayName()}, muted = $muted"
        )
    }

    override fun onUnmuteAudioDemand() {
        Log.d(TAG, "onUnmuteAudioDemand")
    }

    override fun onAudioRouteChanged(routeType: AudioRouteType?) {
        Log.d(TAG, "onAudioRouteChanged: $routeType")
    }

    companion object {
        private const val TAG = "SimpleAudioEventHandler"
    }
}