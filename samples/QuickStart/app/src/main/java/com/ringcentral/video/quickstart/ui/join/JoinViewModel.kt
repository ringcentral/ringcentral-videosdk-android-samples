package com.ringcentral.video.quickstart.ui.join

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ringcentral.video.ErrorCodeType
import com.ringcentral.video.quickstart.controller.MeetingDataController
import com.ringcentral.video.quickstart.controller.data.MeetingStatus
import com.ringcentral.video.quickstart.ui.RefreshTokenUiEvent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class JoinViewModel : ViewModel() {
    private val _refreshTokenUiEvent = MutableSharedFlow<RefreshTokenUiEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val refreshTokenUiEvent = _refreshTokenUiEvent.asSharedFlow()
    private val _joinUiEvent = MutableSharedFlow<JoinMeetingUiEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val joinUiEvent = _joinUiEvent.asSharedFlow()

    fun setEventSource(source: MeetingDataController) {
        Log.d("JoinViewModel", "Set event source for $source")
        viewModelScope.launch {
            source.status.collect {
                Log.d("JoinViewModel", "Collect status $it")
                val event = when (it) {
                    is MeetingStatus.JoinBeforeHost -> JoinMeetingUiEvent(waitHostToJoin = true)
                    is MeetingStatus.WaitingRoom -> JoinMeetingUiEvent(inWaitingRoom = true)
                    is MeetingStatus.RequirePassword -> JoinMeetingUiEvent(needPassword = true)
                    is MeetingStatus.Connecting -> JoinMeetingUiEvent(isJoining = true)
                    is MeetingStatus.InMeeting -> JoinMeetingUiEvent(
                        meetingId = it.meetingId,
                        isJoined = true
                    )
                    is MeetingStatus.Leaving -> JoinMeetingUiEvent(leaveMeeting = true)
                    else -> JoinMeetingUiEvent()
                }
                _joinUiEvent.emit(event)
            }
        }
        viewModelScope.launch {
            source.token.collect {
                Log.d("JoinViewModel", "Collect refresh token event $it")
                val event = RefreshTokenUiEvent(
                    isRefreshedSuccessfully = it.statusCode == ErrorCodeType.ERR_OK,
                    code = it.statusCode
                )
                _refreshTokenUiEvent.emit(event)
            }
        }
    }
}