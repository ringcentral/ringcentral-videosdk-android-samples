package com.example.QuickStartUiKit

import android.R
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
import androidx.navigation.fragment.findNavController
import com.example.QuickStartUiKit.databinding.FragmentJoinBinding
import com.ringcentral.video.*
import com.ringcentral.video.uikit.service.MeetingService


class JoinFragment : Fragment() {
    private var _binding: FragmentJoinBinding? = null
    private val binding: FragmentJoinBinding get() = _binding!!

    private lateinit var meetingService: MeetingService
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MeetingService.MeetingServiceBinder
            meetingService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Intent(this.context, MeetingService::class.java).also { intent ->
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
        setOnClickListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun joinMeeting() {
        val meetingId = binding.meetingIdTextField.editText?.text?.toString().orEmpty()
        val password = binding.passwordTextField.editText?.text?.toString()
        val displayName = binding.passwordTextField.editText?.text?.toString()

        val meetingOptions = MeetingOptions.create().apply {
            password
            displayName
        }
        RcvEngine.instance().joinMeeting(meetingId, meetingOptions)
    }

    private fun startInstantMeeting() {
        RcvEngine.instance().startInstantMeeting()
    }

    private fun setOnClickListeners() {
        binding.joinMeetingButton.setOnClickListener { joinMeeting() }
        binding.startMeetingButton.setOnClickListener { startInstantMeeting() }
    }

    companion object {
        private const val TAG = "JoinFragment"
    }
}