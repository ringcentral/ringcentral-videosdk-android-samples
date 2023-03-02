package com.ringcentral.video.quickstart.ui.active

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ringcentral.video.quickstart.controller.MeetingDataController
import com.ringcentral.video.quickstart.controller.data.MeetingStatus
import com.ringcentral.video.quickstart.ui.active.state.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ActiveMeetingViewModel : ViewModel() {
    private val _meetingUiState = MutableStateFlow(MeetingUiState())
    val meetingUiState = _meetingUiState.asStateFlow()
    private val _activeVideoParticipant = MutableStateFlow(ParticipantUiState())
    val activeVideoParticipant = _activeVideoParticipant.asStateFlow()
    private val _remoteParticipants = MutableStateFlow(mapOf<Long, ParticipantUiState>())
    val remoteParticipants = _remoteParticipants.asStateFlow()

    fun setEventSource(controller: MeetingDataController) {
        updateMeetingId(controller)
        listenMeetingStatus(controller)
        listenActiveVideoParticipant(controller)
        listenLocalVideoState(controller)
        listenLocalAudioState(controller)
        listenHostState(controller)
        listenRemoteParticipantState(controller)
    }

    private fun listenHostState(controller: MeetingDataController) {
        viewModelScope.launch {
            controller.localHostState.collect { hostState ->
                _meetingUiState.update {
                    it.copy(isHostOrModerator = hostState.isHost || hostState.isModerator || hostState.isTemporaryModerator)
                }
            }
        }
    }

    private fun listenLocalAudioState(controller: MeetingDataController) {
        viewModelScope.launch {
            controller.localAudioState.collect { localAudioState ->
                _meetingUiState.update {
                    it.copy(
                        localAudioState = AudioUiState(
                            isMuted = localAudioState.muted,
                            level = localAudioState.level
                        )
                    )
                }
            }
        }
    }

    private fun listenRemoteParticipantState(controller: MeetingDataController) {
        viewModelScope.launch {
            controller.remoteParticipants.collect {
                _remoteParticipants.emit(it)
            }
        }
    }

    private fun listenLocalVideoState(controller: MeetingDataController) {
        viewModelScope.launch {
            controller.localVideoState.collect { localVideoState ->
                _meetingUiState.update {
                    it.copy(
                        localVideoState = VideoUiState(
                            modelId = localVideoState.modelId,
                            isVideoOff = localVideoState.muted,
                        )
                    )
                }
            }
        }
    }

    private fun listenActiveVideoParticipant(controller: MeetingDataController) {
        viewModelScope.launch {
            controller.activeVideoParticipant.collect {
                _activeVideoParticipant.emit(it)
            }
        }
    }

    private fun listenMeetingStatus(controller: MeetingDataController) {
        viewModelScope.launch {
            controller.status.collect { status ->
                if (status is MeetingStatus.Leaving) {
                    _meetingUiState.update { it.copy(leaveMeeting = true) }
                }
            }
        }
    }

    private fun updateMeetingId(controller: MeetingDataController) {
        viewModelScope.launch {
            controller.status.map { it.meetingId }
                .distinctUntilChanged()
                .collect { meetingId ->
                    _meetingUiState.update { it.copy(meetingId = meetingId.orEmpty()) }
                }
        }
    }
}