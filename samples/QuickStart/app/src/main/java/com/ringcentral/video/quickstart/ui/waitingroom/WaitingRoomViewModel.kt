package com.ringcentral.video.quickstart.ui.waitingroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ringcentral.video.quickstart.controller.MeetingDataController
import com.ringcentral.video.quickstart.controller.data.MeetingStatus
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class WaitingRoomViewModel : ViewModel() {
    private val _waitingUiEvent = MutableSharedFlow<WaitingRoomUiEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val waitingUiEvent = _waitingUiEvent.asSharedFlow()

    fun setEventSource(source: MeetingDataController) {
        viewModelScope.launch {
            source.status.collect {
                when (it) {
                    is MeetingStatus.InMeeting -> notifyMeetingJoined(it.meetingId)
                    is MeetingStatus.WaitingRoom -> notifyInWaitingRoom()
                    is MeetingStatus.Leaving -> _waitingUiEvent.emit(WaitingRoomUiEvent(isUserLeaveMeeting = true))
                    else -> {}
                }
            }
        }
    }

    private suspend fun notifyInWaitingRoom() {
        _waitingUiEvent.emit(
            WaitingRoomUiEvent(
                isEnterWaitingRoom = true
            )
        )
    }

    private suspend fun notifyMeetingJoined(meetingId: String?) {
        _waitingUiEvent.emit(
            WaitingRoomUiEvent(
                isMeetingStarted = true,
                meetingId = meetingId
            )
        )
    }
}