package com.ringcentral.video.quickstart.controller.handler

import android.util.Log
import com.ringcentral.video.*

open class SimpleEngineEventHandler : EngineEventHandler() {
    override fun onMeetingJoin(meetingId: String, errorCode: Long) {
        Log.d(TAG, "onMeetingJoin, meetingId = $meetingId, errorCode = $errorCode")
    }

    override fun onMeetingLeave(meetingId: String, errorCode: Long, reason: LeaveReason?) {
        Log.d(
            TAG,
            "onMeetingLeave, meetingId = $meetingId, errorCode = $errorCode, reason = $reason"
        )
    }

    override fun onPersonalMeetingSettingsUpdate(
        code: Long,
        setting: PersonalMeetingSettings?
    ) {
        Log.d(TAG, "onPersonalMeetingSettingsUpdate, code = $code")
    }

    override fun onMeetingSchedule(p0: Long, settings: ScheduleMeetingSettings?) {
        Log.d(TAG, "onMeetingSchedule, settings = $settings")
    }

    override fun onAuthorization(newToken: String?) {
        Log.d(TAG, "onAuthorization, newToken = $newToken")
    }

    override fun onAuthorizationError(errorCode: Long) {
        Log.d(TAG, "onAuthorizationError, errorCode = $errorCode")
    }

    companion object {
        private const val TAG = "SimpleEngineEventHandler"
    }
}