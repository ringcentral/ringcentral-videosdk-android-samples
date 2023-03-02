@file:Suppress("DEPRECATION")

package com.ringcentral.video.quickstart.ui.join

import android.app.ProgressDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ringcentral.video.quickstart.R
import com.ringcentral.video.quickstart.databinding.FragmentJoinBinding
import com.ringcentral.video.quickstart.service.MeetingService
import com.ringcentral.video.quickstart.ui.active.ActiveMeetingFragment
import com.ringcentral.video.quickstart.ui.waitingroom.WaitingRoomFragment
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class JoinFragment : Fragment() {
    private var progressDialog: ProgressDialog? = null
    private val viewModel: JoinViewModel by viewModels()
    private var _binding: FragmentJoinBinding? = null
    private val binding: FragmentJoinBinding get() = _binding!!
    private lateinit var meetingService: MeetingService
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "$name connected")
            val binder = service as MeetingService.MeetingServiceBinder
            meetingService = binder.getService()
            viewModel.setEventSource(meetingService.controller)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "$name disconnected")
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
        _binding = FragmentJoinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenJoinMeetingUiEvent()
        setOnClickListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setOnClickListeners() {
        binding.joinMeetingButton.setOnClickListener { joinMeeting() }
        binding.startMeetingButton.setOnClickListener { startInstantMeeting() }
    }

    private fun joinMeeting() {
        val meetingId = binding.meetingIdTextField.editText?.text?.toString().orEmpty()
        val password = binding.passwordTextField.editText?.text?.toString()
        val displayName = binding.passwordTextField.editText?.text?.toString()
        meetingService.joinMeeting(meetingId, password, displayName)
    }

    private fun startInstantMeeting() {
        meetingService.startInstantMeeting()
    }

    private suspend fun listenTokenRefreshEvent() {
        viewModel.refreshTokenUiEvent.collect {
            val notifyStringRes = if (it.isRefreshedSuccessfully) {
                R.string.auth_token_renew_success
            } else {
                R.string.auth_token_renew_failure
            }
            Toast.makeText(requireContext(), notifyStringRes, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun listenJoinMeetingUiEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { listenLoadingDialogEvent() }
                launch { listenMeetingJoinedEvent() }
                launch { listenRequirePasswordEvent() }
                launch { listenWaitingRoomEvent() }
                launch { listenJoinBeforeHostEvent() }
                launch { listenTokenRefreshEvent() }
            }
        }
    }

    private suspend fun listenJoinBeforeHostEvent() {
        viewModel.joinUiEvent
            .map { it.waitHostToJoin to it.meetingId }
            .distinctUntilChanged()
            .collect {
                val isInWaitingRoom = it.first
                val meetingId = it.second
                if (isInWaitingRoom) {
                    val bundle = bundleOf(
                        WaitingRoomFragment.ARG_MEETING_ID to meetingId,
                        WaitingRoomFragment.ARG_IS_JOIN_BEFORE_HOST to true
                    )
                    findNavController().navigate(
                        R.id.action_mainFragment_to_waitingRoomFragment,
                        bundle
                    )
                }
            }
    }

    private suspend fun listenWaitingRoomEvent() {
        viewModel.joinUiEvent
            .map { it.inWaitingRoom to it.meetingId }
            .distinctUntilChanged()
            .collect {
                val isInWaitingRoom = it.first
                val meetingId = it.second
                if (isInWaitingRoom) {
                    val bundle = bundleOf(
                        WaitingRoomFragment.ARG_MEETING_ID to meetingId,
                        WaitingRoomFragment.ARG_IS_JOIN_BEFORE_HOST to false
                    )
                    findNavController().navigate(
                        R.id.action_mainFragment_to_waitingRoomFragment,
                        bundle
                    )
                }
            }
    }

    private suspend fun listenRequirePasswordEvent() {
        viewModel.joinUiEvent
            .map { it.needPassword }
            .distinctUntilChanged()
            .collect {
                if (it) showToast(R.string.meeting_exception_password_required)
            }
    }

    private suspend fun listenMeetingJoinedEvent() {
        viewModel.joinUiEvent
            .map { it.isJoined to it.meetingId }
            .distinctUntilChanged()
            .collect {
                val isJoined = it.first
                val meetingId = it.second
                if (isJoined) {
                    val bundle =
                        bundleOf(ActiveMeetingFragment.ARG_MEETING_ID to meetingId)
                    findNavController().navigate(
                        R.id.action_mainFragment_to_activeMeetingFragment,
                        bundle
                    )
                }
            }
    }

    private suspend fun listenLoadingDialogEvent() {
        viewModel.joinUiEvent
            .map { it.isJoining }
            .distinctUntilChanged()
            .collect { isJoining ->
                controlProgressDialog(isJoining)
            }
    }

    // We do need to use ProgressDialog here to block user action.
    @Suppress("DEPRECATION")
    private fun controlProgressDialog(show: Boolean) {
        progressDialog = if (show) {
            ProgressDialog(requireContext()).apply {
                setMessage(requireActivity().applicationContext.getString(R.string.connecting))
                setProgressStyle(ProgressDialog.STYLE_SPINNER)
                setCancelable(false)
                show()
            }
        } else {
            progressDialog?.dismiss()
            null
        }
    }

    private fun showToast(message: Int) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val TAG = "JoinFragment"
    }
}