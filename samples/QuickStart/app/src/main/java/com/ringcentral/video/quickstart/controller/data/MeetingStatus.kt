package com.ringcentral.video.quickstart.controller.data

import com.ringcentral.video.ErrorCodeType

sealed class MeetingStatus(val meetingId: String? = null) {
    class Idle : MeetingStatus()
    class Connecting(meetingId: String? = null) : MeetingStatus(meetingId)
    class JoinBeforeHost(meetingId: String? = null) : MeetingStatus(meetingId)
    class WaitingRoom(meetingId: String? = null) : MeetingStatus(meetingId)
    class RequirePassword(meetingId: String? = null) : MeetingStatus(meetingId)
    class InMeeting(meetingId: String? = null) : MeetingStatus(meetingId)
    class Leaving(meetingId: String? = null) : MeetingStatus(meetingId)
    class Error(meetingId: String?, val code: ErrorCodeType) : MeetingStatus(meetingId)
}
