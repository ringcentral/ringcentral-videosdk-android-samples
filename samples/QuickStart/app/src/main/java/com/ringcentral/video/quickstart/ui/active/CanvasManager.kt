package com.ringcentral.video.quickstart.ui.active

import android.util.Log
import com.ringcentral.video.RcvEngine
import com.ringcentral.video.RcvVideoCanvas
import com.ringcentral.video.RcvVideoView

class CanvasManager(meetingId: String) {
    private val canvasMap = HashMap<Long, RcvVideoCanvas>(16)
    private var meetingController = RcvEngine.instance().getMeetingController(meetingId)
    private var videoController = meetingController?.videoController
    private var sharingController = meetingController?.sharingController

    fun updateLocalCanvas(view: RcvVideoView, modelId: Long) {
        if (canvasMap.containsKey(modelId)) return
        Log.d(TAG, "Update local canvas for view $view, modelId = $modelId")
        val canvas = RcvVideoCanvas(view, modelId)
        canvasMap[modelId] = canvas
        videoController?.setupLocalVideo(canvas)
    }

    fun updateRemoteCanvas(view: RcvVideoView, modelId: Long) {
        if (canvasMap.containsKey(modelId)) return
        Log.d(TAG, "Update remote canvas for view $view, modelId = $modelId")
        val canvas = RcvVideoCanvas(view, modelId)
        canvasMap[modelId] = canvas
        videoController?.setupRemoteVideo(canvas)
        sharingController?.setupRemoteSharing(canvas)
    }

    fun updateActiveCanvas(view: RcvVideoView) {
        if (canvasMap.containsKey(0L)) return
        Log.d(TAG, "Update active canvas for view $view")
        val canvas = RcvVideoCanvas(view)
        canvasMap[0L] = canvas
        videoController?.setupActiveVideo(canvas)
    }

    fun removeActiveCanvas() {
        Log.d(TAG, "Remove active canvas")
        canvasMap.remove(0L)
        videoController?.removeActiveVideo()
    }

    fun removeLocalCanvas(modelId: Long) {
        Log.d(TAG, "Remove local canvas, modelId = $modelId")
        videoController?.removeLocalVideo()
        canvasMap.remove(modelId)
    }

    fun removeRemoteCanvas(view: RcvVideoView, modelId: Long) {
        val canvas = canvasMap[modelId] ?: return
        Log.d(TAG, "Remove remote canvas for view $view, modelId = $modelId")
        videoController?.removeRemoteVideo(canvas)
        canvasMap.remove(modelId)
    }

    fun destroy() {
        canvasMap.clear()
        meetingController = null
        sharingController = null
        videoController = null
    }

    companion object {
        private const val TAG = "CanvasManager"
    }
}