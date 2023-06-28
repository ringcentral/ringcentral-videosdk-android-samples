package com.ringcentral.video.quickstart.controller.handler

import android.util.Log
import com.ringcentral.video.IParticipant
import com.ringcentral.video.MeetingUserEventHandler
import com.ringcentral.video.NqiState

open class SimpleMeetingUserEventHandler: MeetingUserEventHandler() {
    override fun onUserJoined(participant: IParticipant?) {
        Log.d(TAG, "onUserJoined, ${participant?.displayName()}")
    }

    override fun onUserUpdated(participant: IParticipant?) {
        Log.d(TAG, "onUserUpdated, ${participant?.displayName()}")
    }

    override fun onUserLeave(participant: IParticipant?) {
        Log.d(TAG, "onUserLeave, ${participant?.displayName()}")
    }

    override fun onUserRoleChanged(participant: IParticipant?) {
        Log.d(TAG, "onUserRoleChanged, ${participant?.displayName()}")
    }

    override fun onLocalNetworkQuality(nqi: NqiState?) {
        Log.d(TAG, "onLocalNetworkQuality, nqi=${nqi?.name}")
    }

    override fun onRemoteNetworkQuality(participant: IParticipant?, nqi: NqiState?) {
        Log.d(TAG, "onRemoteNetworkQuality, ${participant?.displayName()}, NQI = ${nqi?.name}")
    }

    override fun onCallOut(p0: String?, p1: Long) {
        TODO("Not yet implemented")
    }

    override fun onDeleteDial(p0: Long) {
        TODO("Not yet implemented")
    }

    override fun onActiveVideoUserChanged(participant: IParticipant?) {
        Log.d(TAG, "onActiveVideoUserChanged, ${participant?.displayName()}")
    }

    override fun onActiveSpeakerUserChanged(participant: IParticipant?) {
        Log.d(TAG, "onActiveSpeakerUserChanged, ${participant?.displayName()}")
    }

    companion object {
        private const val TAG = "SimpleMeetingUserEventHandler"
    }
}