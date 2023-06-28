package com.ringcentral.video.quickstart.controller

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.ringcentral.video.*
import com.ringcentral.video.quickstart.controller.data.*
import com.ringcentral.video.quickstart.controller.handler.*
import com.ringcentral.video.quickstart.extension.unsafeLazy
import com.ringcentral.video.quickstart.ui.active.state.ParticipantUiState
import com.ringcentral.video.quickstart.ui.active.state.toParticipantUiState
import kotlinx.coroutines.flow.*
import java.util.ArrayList

class MeetingDataController : LifecycleOwner {
    // Data part
    var meetingId: String? = null
    private var lifecycleRegistry = LifecycleRegistry(this)

    // Event part
    var startTime: Long = -1
        private set
    private val _token = MutableSharedFlow<TokenRequestResult>()
    val token = _token.asSharedFlow()
    private val _status = MutableStateFlow<MeetingStatus>(MeetingStatus.Idle())
    val status = _status.asStateFlow()
    private val _activeVideoParticipant = MutableSharedFlow<ParticipantUiState>()
    val activeVideoParticipant = _activeVideoParticipant.asSharedFlow()
    private val _localAudioState = MutableStateFlow(AudioState())
    val localAudioState = _localAudioState.asStateFlow()
    private val _localVideoState = MutableStateFlow(VideoState())
    val localVideoState = _localVideoState.asStateFlow()
    private val _localHostState = MutableStateFlow(HostState())
    val localHostState = _localHostState.asStateFlow()
    private val currentList = mutableMapOf<Long, ParticipantUiState>()
    private val _remoteParticipants = MutableStateFlow(mapOf<Long, ParticipantUiState>())
    val remoteParticipants = _remoteParticipants.asStateFlow()

    // Data source part
    private val engineEventHandler = EngineEventHandlerImpl()
    private val userEventHandler by unsafeLazy { UserEventHandlerImpl() }
    private val meetingEventHandler by unsafeLazy { MeetingEventHandlerImpl() }
    private val audioEventHandler by unsafeLazy { AudioEventHandlerImpl() }
    private val videoEventHandler by unsafeLazy { VideoEventHandlerImpl() }
    private val statEventHandler by unsafeLazy { SimpleStatisticEventHandler() }
    private var meetingController: MeetingController? = null

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    fun joinMeeting(id: String, pwd: String? = null, displayName: String? = null) {
        markConnect()
        val meetingOptions = MeetingOptions.create().apply {
            password = pwd
            userName = displayName
        }
        RcvEngine.instance().joinMeeting(id, meetingOptions)
    }

    fun startInstantMeeting() {
        markConnect()
        RcvEngine.instance().startInstantMeeting()
    }

    private fun markConnect() {
        RcvEngine.instance().registerEventHandler(engineEventHandler)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        _status.tryEmit(MeetingStatus.Connecting())
    }

    fun switchCamera() = meetingController?.videoController?.switchCamera()

    fun toggleAudioSwitch() {
        val audioController = meetingController?.audioController ?: return
        if (audioController.isMuted) {
            audioController.unmuteLocalAudioStream()
        } else {
            audioController.muteLocalAudioStream()
        }
    }

    fun toggleVideoSwitch() {
        val videoController = meetingController?.videoController ?: return
        if (videoController.isMuted) {
            videoController.unmuteLocalVideoStream()
        } else {
            videoController.muteLocalVideoStream()
        }
    }

    fun leaveMeeting() = meetingController?.leaveMeeting()

    fun endMeeting() = meetingController?.endMeeting()

    fun clear() {
        meetingController?.let {
            it.audioController?.unregisterEventHandler(audioEventHandler)
            it.videoController?.unregisterEventHandler(videoEventHandler)
            it.meetingUserController?.unregisterEventHandler(userEventHandler)
            it.unregisterMeetingStatisticEventHandler(statEventHandler)
            it.unregisterEventHandler(meetingEventHandler)
        }
        meetingController = null
        currentList.clear()
        RcvEngine.instance().unregisterEventHandler(engineEventHandler)
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    private fun initControllers(meetingId: String) {
        meetingController = RcvEngine.instance().getMeetingController(meetingId)?.also {
            it.registerEventHandler(meetingEventHandler)
            it.registerMeetingStatisticEventHandler(statEventHandler)
            it.meetingUserController?.registerEventHandler(userEventHandler)
            it.audioController?.registerEventHandler(audioEventHandler)
            it.videoController?.registerEventHandler(videoEventHandler)
        }
    }

    private fun updateLocalParticipantState(participant: IParticipant) {
        if (!participant.isMe) return
        val hostState = HostState(
            isHost = participant.isHost,
            isTemporaryModerator = participant.isTemporaryModerator,
            isModerator = participant.isModerator
        )
        _localHostState.tryEmit(hostState)
        val audioState = AudioState(
            muted = meetingController?.audioController?.isMuted ?: true,
            level = 0F,
        )
        _localAudioState.tryEmit(audioState)
        val videoState = VideoState(
            modelId = participant.modelId,
            muted = meetingController?.videoController?.isMuted ?: true
        )
        _localVideoState.tryEmit(videoState)
    }

    private fun updateParticipant(participant: IParticipant) {
        val uiState = participant.toParticipantUiState()
        if (participant.isDeleted) {
            onRemoteParticipantLeave(participant)
            return
        }
        currentList[uiState.modelId] = uiState
        _remoteParticipants.tryEmit(currentList.toMap())
    }

    private fun onRemoteParticipantLeave(participant: IParticipant) {
        if (participant.isMe) return
        val uiState = participant.toParticipantUiState()
        currentList.remove(uiState.modelId)
        _remoteParticipants.tryEmit(currentList.toMap())
    }

    inner class EngineEventHandlerImpl : SimpleEngineEventHandler() {
        override fun onMeetingJoin(meetingId: String, errorCode: Long) {
            super.onMeetingJoin(meetingId, errorCode)
            // The start time is earlier than the actual join events
            // but it's intended to keep the code clear.
            this@MeetingDataController.meetingId = meetingId
            initControllers(meetingId)
            startTime = System.currentTimeMillis()
            val joinResult = RcvEngine.getErrorType(errorCode)
            Log.d(TAG, "Enum of the error code is : ${joinResult.name}")
            val status = when (joinResult) {
                ErrorCodeType.ERR_OK -> MeetingStatus.InMeeting(meetingId)
                ErrorCodeType.ERR_NEED_PASSWORD -> MeetingStatus.RequirePassword(meetingId)
                ErrorCodeType.ERR_IN_WAITING_ROOM -> MeetingStatus.WaitingRoom(meetingId)
                ErrorCodeType.ERR_WAITING_HOST_JOIN_FIRST -> MeetingStatus.JoinBeforeHost(meetingId)
                else -> MeetingStatus.Error(meetingId, joinResult)
            }
            _status.tryEmit(status)
            val currentParticipants = meetingController
                ?.meetingUserController
                ?.meetingUserList
                ?.map { (key, value) ->
                    key to value.toParticipantUiState()
                }
                ?.toMap()
                ?: mapOf()
            _remoteParticipants.tryEmit(currentParticipants)
        }

        override fun onMeetingLeave(meetingId: String, errorCode: Long, reason: LeaveReason?) {
            super.onMeetingLeave(meetingId, errorCode, reason)
            this@MeetingDataController.meetingId = null
            _status.tryEmit(MeetingStatus.Leaving(meetingId))
            clear()
        }

        override fun onAuthorization(newToken: String?) {
            super.onAuthorization(newToken)
            val result = TokenRequestResult(newToken, ErrorCodeType.ERR_OK)
            _token.tryEmit(result)
        }

        override fun onAuthorizationError(errorCode: Long) {
            super.onAuthorizationError(errorCode)
            val code = RcvEngine.getErrorType(errorCode)
            val result = TokenRequestResult(null, code)
            _token.tryEmit(result)
        }
    }

    inner class UserEventHandlerImpl : SimpleMeetingUserEventHandler() {
        override fun onUserJoined(participant: IParticipant?) {
            super.onUserJoined(participant)
            participant ?: return
            updateLocalParticipantState(participant)
            updateParticipant(participant)
        }

        override fun onUserUpdated(participant: IParticipant?) {
            super.onUserUpdated(participant)
            participant ?: return
            updateParticipant(participant)
        }

        override fun onUserLeave(participant: IParticipant?) {
            super.onUserLeave(participant)
            participant ?: return
            onRemoteParticipantLeave(participant)
        }

        override fun onActiveVideoUserChanged(participant: IParticipant?) {
            super.onActiveVideoUserChanged(participant)
            _activeVideoParticipant.tryEmit(participant.toParticipantUiState())
        }

        override fun onCallOut(p0: String?, p1: Long) {
            TODO("Not yet implemented")
        }

        override fun onDeleteDial(p0: Long) {
            TODO("Not yet implemented")
        }
    }

    inner class MeetingEventHandlerImpl : SimpleMeetingEventHandler() {
        override fun onLiveTranscriptionDataChanged(
            data: LiveTranscriptionData?,
            type: LiveTranscriptionDataType?
        ) {
        }

        override fun onLiveTranscriptionSettingChanged(setting: LiveTranscriptionSetting?) {
        }

        override fun onLiveTranscriptionHistoryChanged(p0: ArrayList<LiveTranscriptionData>?) {
        }
    }

    inner class AudioEventHandlerImpl : SimpleAudioEventHandler() {
        override fun onLocalAudioMuteChanged(muted: Boolean) {
            super.onLocalAudioMuteChanged(muted)
            _localAudioState.update { it.copy(muted = muted) }
        }
    }

    inner class VideoEventHandlerImpl : SimpleVideoEventHandler() {
        override fun onLocalVideoMuteChanged(muted: Boolean) {
            super.onLocalVideoMuteChanged(muted)
            _localVideoState.update { it.copy(muted = muted) }
        }

        override fun onRemoteVideoMuteChanged(participant: IParticipant?, muted: Boolean) {
            super.onRemoteVideoMuteChanged(participant, muted)
            if (participant == null || participant.isDeleted) {
                // BUG: User who has left the meeting will trigger this callback
                return
            }
            val state = participant.toParticipantUiState()
            currentList[participant.modelId] = state
            _remoteParticipants.tryEmit(currentList.toMap())
        }
    }

    companion object {
        private const val TAG = "MeetingDataController"
    }
}