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
import com.ringcentral.video.uikit.base.PermissionRequestActivity
import com.ringcentral.video.uikit.ui.RCVMeetingView
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : PermissionRequestActivity() {
    private val engineEventHandler = EventHandlerImpl()

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
    }
    inner class EventHandlerImpl : EngineEventHandler() {
        @SuppressLint("ResourceType")
        override fun onMeetingJoin(meetingId: String?, errorCode: Long) {
            var joinResult = RcvEngine.getErrorType(errorCode)
            when (joinResult) {
                ErrorCodeType.ERR_OK -> {
                    val bundle = bundleOf(RCVMeetingView.ARG_MEETING_ID to meetingId)
                    val navController = Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment)
                    navController.setGraph(com.ringcentral.video.uikit.R.navigation.meeting_graph, bundle)
                    //navController.navigate(R.id.action_RCVMeetingView)
                }
                else -> {
                }
            }
        }

        override fun onMeetingLeave(meetingId: String?, errorCode: Long, reason: LeaveReason?) {
            var leaveResult = RcvEngine.getErrorType(errorCode)
            when (leaveResult) {
                ErrorCodeType.ERR_OK -> {
                    val navController = Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment)
                    navController.setGraph(R.navigation.nav_graph)
                    //navController.navigate(R.id.action_MeetingFragment_to_JoinFragment)
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

    companion object {
        private const val TAG = "MainActivity"
    }
}