package com.example.QuickStartUiKit

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.Window
import androidx.core.os.bundleOf

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import com.example.QuickStartUiKit.databinding.ActivityMainBinding
import com.ringcentral.video.*
import com.ringcentral.video.uikit.CustomLayoutView
import com.ringcentral.video.uikit.base.PermissionRequestActivity
import com.ringcentral.video.uikit.controller.handler.SimpleMeetingUserEventHandler
import com.ringcentral.video.uikit.controller.handler.SimpleVideoEventHandler
import com.ringcentral.video.uikit.ui.RCVMeetingView
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : PermissionRequestActivity() {
    private val engineEventHandler = EventHandlerImpl()
    private val userEventHandler = UserEventHandlerImpl()
    private val videoEventHandler = VideoEventHandlerImpl()
    private var customView: CustomLayoutView? = null
    private var meetingFragment: RCVMeetingView? = null
    private var curMeetingId: String = ""

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"onDestroy")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = Color.BLACK;
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

        permissionHostLayout = findViewById(R.id.nav_host_fragment)

        RcvEngine.instance().registerEventHandler(engineEventHandler)

        customView = CustomLayoutView(this)
    }

    private fun initControllers(meetingId: String) {
        RcvEngine.instance().getMeetingController(meetingId)?.also {
            it.meetingUserController?.registerEventHandler(userEventHandler)
            it.videoController?.registerEventHandler(videoEventHandler)
        }
    }

    inner class EventHandlerImpl : EngineEventHandler() {
        @SuppressLint("ResourceType")
        override fun onMeetingJoin(meetingId: String?, errorCode: Long) {
            var joinResult = RcvEngine.getErrorType(errorCode)
            when (joinResult) {
                ErrorCodeType.ERR_OK -> {
                    if (meetingId.isNullOrEmpty()) {
                        return
                    }
                    curMeetingId = meetingId
                    initControllers(meetingId)

                    val bundle = bundleOf(RCVMeetingView.ARG_MEETING_ID to meetingId)
                    //val navController = Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment)
                    //navController.setGraph(com.ringcentral.video.uikit.R.navigation.meeting_graph, bundle)

                    var transaction = supportFragmentManager.beginTransaction()
                    if(meetingFragment == null){
                        var bundle = bundleOf(RCVMeetingView.ARG_MEETING_ID to meetingId)
                        meetingFragment = RCVMeetingView()
                        meetingFragment!!.arguments = bundle
                        customView?.let { it ->
                            customView!!.setActiveMeetingId(meetingId)
                            RcvEngine.instance().getMeetingController(meetingId)?.also {meetingController ->
                                customView!!.updateLocalUser(meetingController.meetingUserController.myself)
                                customView!!.updateRemoteUser(meetingController.meetingUserController.activeVideoUser)
                            }

                            meetingFragment!!.setCustomLayoutView(it)
                        }
                        transaction.add(R.id.nav_host_fragment, meetingFragment!!)
                    } else {
                        transaction.show(meetingFragment!!)
                    }
                    transaction.commit()
                }
                else -> {
                }
            }
        }

        override fun onMeetingLeave(meetingId: String?, errorCode: Long, reason: LeaveReason?) {
            var leaveResult = RcvEngine.getErrorType(errorCode)
            when (leaveResult) {
                ErrorCodeType.ERR_OK -> {
                    //val navController = Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment)
                    //navController.setGraph(R.navigation.nav_graph)

                    curMeetingId = ""
                    val transaction = supportFragmentManager.beginTransaction()
                    meetingFragment?.let {
                        transaction.hide(it)
                        transaction.commit()
                    }
                }
                else -> {
                }
            }

        }

        override fun onAuthorization(newToken: String?) {
        }

        override fun onAuthorizationError(errorCode: Long) {
        }

        override fun onPersonalMeetingSettingsUpdate(p0: Long, p1: PersonalMeetingSettings?) {
        }

        override fun onMeetingSchedule(p0: Long, p1: ScheduleMeetingSettings?) {
        }
    }

    inner class UserEventHandlerImpl : SimpleMeetingUserEventHandler() {
        override fun onUserJoined(participant: IParticipant?) {
        }

        override fun onUserUpdated(participant: IParticipant?) {
        }

        override fun onUserLeave(participant: IParticipant?) {
        }

        override fun onUserRoleChanged(participant: IParticipant?) {
        }

        override fun onActiveVideoUserChanged(participant: IParticipant?) {
            if (participant != null) {
                customView?.updateRemoteUser(participant)
            }
        }

        override fun onCallOut(p0: String?, p1: Long) {
        }

        override fun onDeleteDial(p0: Long) {
        }
    }

    inner class VideoEventHandlerImpl : SimpleVideoEventHandler() {
        override fun onLocalVideoMuteChanged(muted: Boolean) {
            if (curMeetingId.isEmpty()) {
                return
            }

            RcvEngine.instance().getMeetingController(curMeetingId)?.also { it ->
                customView?.updateLocalUser(it.meetingUserController.myself)
                customView?.showLocal()
            }
        }

        override fun onRemoteVideoMuteChanged(participant: IParticipant?, muted: Boolean) {
            if (curMeetingId.isEmpty()) {
                return
            }

            RcvEngine.instance().getMeetingController(curMeetingId)?.also { it ->
                if (participant?.modelId == it.meetingUserController.activeVideoUser.modelId) {
                    customView?.updateRemoteUser(participant)
                    customView?.showRemote()
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}