package com.ringcentral.video.quickstart.ui.active

import android.app.AlertDialog
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ringcentral.video.quickstart.R
import com.ringcentral.video.quickstart.databinding.FragmentActiveMeetingBinding
import com.ringcentral.video.quickstart.service.MeetingService
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ActiveMeetingFragment : Fragment() {
    private var canvasManager: CanvasManager? = null
    private var _binding: FragmentActiveMeetingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ActiveMeetingViewModel by viewModels()
    private var meetingId: String? = null

    private lateinit var meetingService: MeetingService
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MeetingService.MeetingServiceBinder
            meetingService = binder.getService()
            viewModel.setEventSource(meetingService.controller)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        meetingId = arguments?.getString(ARG_MEETING_ID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Intent(requireContext(), MeetingService::class.java).also { intent ->
            requireContext().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        canvasManager = CanvasManager(meetingId.orEmpty())
        _binding = FragmentActiveMeetingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listenUiEvents()
        initControlButtons()
        bindActiveVideoCanvas()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        canvasManager?.destroy()
    }

    private fun bindActiveVideoCanvas() {
        canvasManager?.updateActiveCanvas(binding.activeVideoView)
    }

    private fun listenUiEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { listenMeetingUiState() }
                launch { listenLeaveMeetingEvent() }
                launch { listenLocalVideoChanges() }
                launch { listenRemoteVideoChanges() }
            }
        }
    }

    private fun initControlButtons() {
        binding.buttonMuteAudio.setOnClickListener { meetingService.toggleAudioSwitch() }
        binding.buttonMuteVideo.setOnClickListener { meetingService.toggleVideoSwitch() }
        binding.buttonLeaveMeeting.setOnClickListener {
            if (viewModel.meetingUiState.value.isHostOrModerator) {
                AlertDialog.Builder(context)
                    .setMessage(R.string.meeting_state_host_end_meeting)
                    .setNegativeButton(R.string.leave) { _: DialogInterface?, _: Int -> meetingService.leaveMeeting() }
                    .setPositiveButton(R.string.end) { _: DialogInterface?, _: Int -> meetingService.endMeeting() }
                    .show()
            } else {
                meetingService.leaveMeeting()
            }
        }
        binding.switchCameraView.setOnClickListener { meetingService.switchCamera() }
    }

    private fun listenMeetingUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.meetingUiState.collect {
                updateAudioButton(it.localAudioState.isMuted)
                updateVideoButton(it.localVideoState.isVideoOff)
                binding.textViewMeetingInfo.text = getString(R.string.meeting_id, it.meetingId)
                binding.localVideoView.isVisible = !it.localVideoState.isVideoOff
            }
        }
    }

    private fun updateAudioButton(muted: Boolean) {
        binding.buttonMuteAudio.apply {
            text = if (muted) {
                getString(R.string.unmute_audio)
            } else {
                getString(R.string.mute_audio)
            }
        }
    }

    private fun updateVideoButton(videoOff: Boolean) {
        binding.buttonMuteVideo.apply {
            text = if (videoOff) {
                getString(R.string.start_video)
            } else {
                getString(R.string.stop_video)
            }
        }
    }

    private fun listenLeaveMeetingEvent() {
        lifecycleScope.launchWhenStarted {
            viewModel.meetingUiState
                .map { it.leaveMeeting }
                .distinctUntilChanged()
                .collect { leaveMeeting ->
                    if (leaveMeeting) {
                        findNavController()
                            .navigate(R.id.action_ActiveMeetingFragment_to_mainFragment)
                    }
                }
        }
    }

    private suspend fun listenLocalVideoChanges() {
        viewModel.meetingUiState
            .map { it.localVideoState }
            .distinctUntilChanged()
            .collect {
                binding.localVideoView.isVisible = !it.isVideoOff
                if (it.isVideoOff) {
                    canvasManager?.removeLocalCanvas(it.modelId)
                } else {
                    canvasManager?.updateLocalCanvas(binding.localVideoView, it.modelId)
                }
            }
    }

    private suspend fun listenRemoteVideoChanges() {
        viewModel.remoteParticipants.collect {
            var hasVideo = false
            for (participant in it) {
                if(participant.value.isVideoOn) {
                    hasVideo = true
                }
            }

            if(!hasVideo) {
                binding.activeVideoView.alpha = 0F
            } else {
                binding.activeVideoView.alpha = 1F
            }
        }
    }

    companion object {
        const val ARG_MEETING_ID = "meeting_id"
    }
}