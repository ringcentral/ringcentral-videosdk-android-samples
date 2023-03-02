package com.ringcentral.video.quickstart.controller.handler

import android.util.Log
import com.ringcentral.video.*

open class SimpleMeetingEventHandler : MeetingEventHandler() {

    override fun onChatMessageSend(p0: Int, p1: Long) {
        Log.d(TAG, "onChatMessageSend")
    }

    override fun onChatMessageReceived(messages: ArrayList<MeetingChatMessage>?) {
    }

    override fun onRecordingStateChanged(state: RecordingState?) {
        Log.d(TAG, "onRecordingStateChanged, ${state?.name}")
    }

    override fun onMeetingApiExecuted(
        method: String?,
        errorCode: Long,
        result: MeetingApiExecuteResult?
    ) {
    }

    override fun onMeetingLockStateChanged(locked: Boolean) {
        Log.d(TAG, "onMeetingLockStateChanged, locked = $locked")
    }

    override fun onClosedCaptionsData(list: ArrayList<ClosedCaptionsData>?) {
        Log.d(TAG, "onClosedCaptionsData, list = $list")
    }

    override fun onClosedCaptionsStateChanged(state: ClosedCaptionsState?) {
        Log.d(TAG, "onClosedCaptionsStateChanged, state = $state")
    }

    override fun onLiveTranscriptionDataChanged(
        data: LiveTranscriptionData?,
        type: LiveTranscriptionDataType?
    ) {
    }

    override fun onLiveTranscriptionSettingChanged(setting: LiveTranscriptionSetting?) {
    }

    override fun onLiveTranscriptionHistoryChanged(
        data: java.util.ArrayList<LiveTranscriptionData>?,
        type: LiveTranscriptionUpdateHistoryType?
    ) {
    }

    override fun onRecordingAllowChanged(state: Boolean) {
        Log.d(TAG, "onRecordingAllowChanged, state = $state")
    }

    override fun onMeetingEncryptionStateChanged(state: EndToEndEncryptionState?) {
        Log.d(TAG, "onMeetingEncryptionStateChanged, state = $state")
    }

    override fun onMeetingStateChanged(state: MeetingState?) {
        Log.d(TAG, "onMeetingStateChanged, state = $state")
    }

    companion object {
        private const val TAG = "SimpleMeetingEventHandler"
    }
}