package com.ringcentral.video.quickstart.ui.waitingroom

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ringcentral.video.quickstart.databinding.FragmentWaitingRoomBinding
import com.ringcentral.video.quickstart.R
import com.ringcentral.video.quickstart.service.MeetingService
import com.ringcentral.video.quickstart.ui.active.ActiveMeetingFragment
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class WaitingRoomFragment : Fragment() {
    private var _binding: FragmentWaitingRoomBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WaitingRoomViewModel by viewModels()
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
        _binding = FragmentWaitingRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val isJoinBeforeHost = arguments?.getBoolean(ARG_IS_JOIN_BEFORE_HOST) ?: false
        val messageText = if (isJoinBeforeHost) {
            R.string.meeting_exception_waiting_for_host
        } else {
            R.string.meeting_exception_in_waiting_room
        }
        binding.messageText.setText(messageText)
        binding.leaveWaitingRoomButton.setOnClickListener {
            meetingService.leaveMeeting()
        }
        listenEvents()
    }

    private fun listenEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { listenLeaveMeetingEvent() }
                launch { listenJoinMeetingEvent() }
                launch { listenMoveToWaitingRoomEvent() }
            }
        }
    }

    private suspend fun listenMoveToWaitingRoomEvent() {
        viewModel.waitingUiEvent
            .map { it.isEnterWaitingRoom }
            .distinctUntilChanged()
            .collect {
                if (it) {
                    binding.messageText.setText(R.string.meeting_exception_in_waiting_room)
                }
            }
    }

    private suspend fun listenJoinMeetingEvent() {
        viewModel.waitingUiEvent
            .map { it.isMeetingStarted to it.meetingId }
            .distinctUntilChanged()
            .collect {
                val isJoined = it.first
                val meetingId = it.second
                if (!isJoined) return@collect
                val bundle = bundleOf(ActiveMeetingFragment.ARG_MEETING_ID to meetingId)
                findNavController().navigate(
                    R.id.action_waitingRoomFragment_to_activeMeetingFragment,
                    bundle
                )
            }
    }

    private suspend fun listenLeaveMeetingEvent() {
        viewModel.waitingUiEvent
            .map { it.isUserLeaveMeeting }
            .distinctUntilChanged()
            .collect {
                if (it) {
                    findNavController().navigate(
                        R.id.action_waitingRoomFragment_to_mainFragment
                    )
                }
            }
    }

    companion object {
        const val ARG_MEETING_ID = "meeting_id"
        const val ARG_IS_JOIN_BEFORE_HOST = "join_before_host"
    }
}