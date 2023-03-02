package com.ringcentral.video.quickstart.ui.join

data class JoinMeetingUiEvent(
    val meetingId: String? = "",
    val isJoining: Boolean = false,
    val isJoined: Boolean = false,
    val leaveMeeting: Boolean = false,
    val needPassword: Boolean = false,
    val waitHostToJoin: Boolean = false,
    val inWaitingRoom: Boolean = false,
)