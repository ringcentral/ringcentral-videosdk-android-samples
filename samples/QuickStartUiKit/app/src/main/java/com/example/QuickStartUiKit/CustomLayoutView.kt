package com.ringcentral.video.uikit

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.QuickStartUiKit.databinding.CustomLayoutViewBinding
import com.ringcentral.video.*
import com.ringcentral.video.uikit.ui.component.GalleryPageHeadportrait

class CustomLayoutView@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attributeSet, defStyleAttr) {

    private val viewBinding: CustomLayoutViewBinding =  CustomLayoutViewBinding.inflate(LayoutInflater.from(context), this)
    private val localVideoView: RcvVideoView get() = viewBinding.localVideoView
    private val remoteVideoView: RcvVideoView get() = viewBinding.remoteVideoView
    private val localAvatarView: GalleryPageHeadportrait get() = viewBinding.localAvatarView
    private val remoteAvatarView: GalleryPageHeadportrait get() = viewBinding.remoteAvatarView
    private var meetingId: String = ""
    private var localCanvas: RcvVideoCanvas? = null
    private var remoteCanvas: RcvVideoCanvas? = null
    private var localUser: IParticipant? = null
    private var remoteUser: IParticipant? = null


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        println("custom layout view width: $widthSize height: $heightSize")

        measureChild(localVideoView, widthMeasureSpec, heightMeasureSpec)
        measureChild(remoteVideoView, widthMeasureSpec, heightMeasureSpec)

        measureChild(localAvatarView, widthMeasureSpec, heightMeasureSpec)
        measureChild(remoteAvatarView, widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        localAvatarView.layout(
            (width / 2 - localAvatarView.measuredWidth) / 2,
            (height / 2 - localAvatarView.measuredHeight) / 2 + height / 4,
            (width / 2 + localAvatarView.measuredWidth) / 2,
            (height / 2 + localAvatarView.measuredHeight) / 2 + height / 4
        )

        localVideoView.layout(
            0,
            (height / 2 - localAvatarView.measuredHeight) / 2 + height / 4,
            width / 2,
            (height / 2 + localAvatarView.measuredHeight) / 2 + height / 4
        )

        remoteAvatarView.layout(
            (width / 2 - localAvatarView.measuredWidth) / 2 + width / 2,
            (height / 2 - localAvatarView.measuredHeight) / 2 + height / 4,
            (width / 2 + localAvatarView.measuredWidth) / 2 + width / 2,
            (height / 2 + localAvatarView.measuredHeight) / 2 + height / 4
        )

        remoteVideoView.layout(
            width / 2,
            (height / 2 - localAvatarView.measuredHeight) / 2 + height / 4,
            width,
            (height / 2 + localAvatarView.measuredHeight) / 2 + height / 4
        )
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (changedView == this) {
            if (visibility == View.VISIBLE) {
                showLocal()
                showRemote()
            } else {
                hide()
            }
        }
    }

    fun setActiveMeetingId(meetingId: String) {
        this.meetingId = meetingId
    }

    fun updateLocalUser(user: IParticipant) {
        localAvatarView.setDisplayName(user.initialsAvatarName)
        localAvatarView.setHeadportraitURL(user.headshotUrl)
        localAvatarView.setName(user.displayName())

        localUser = user
    }

    fun updateRemoteUser(user: IParticipant) {
        remoteAvatarView.setDisplayName(user.initialsAvatarName)
        remoteAvatarView.setHeadportraitURL(user.headshotUrl)
        remoteAvatarView.setName(user.displayName())
        remoteAvatarView.invalidate()

        remoteUser = user
    }

    fun showLocal() {
        if (visibility != View.VISIBLE) {
            return
        }

        if (localUser?.isVideoOn == true) {
            val meetingController = RcvEngine.instance().getMeetingController(meetingId)
            val videoController = meetingController?.videoController

            videoController?.removeLocalVideo()
            videoController?.setupLocalVideo(localCanvas)
            
            localCanvas?.isMirrorMode = false;
            localCanvas?.attachView(localVideoView)
            localVideoView.visibility = View.VISIBLE
            localAvatarView.visibility = View.GONE
        }
        else {
            localVideoView.visibility = View.GONE
            localAvatarView.visibility = View.VISIBLE
        }
    }

    fun showRemote() {
        if (visibility != View.VISIBLE) {
            return
        }

        if (remoteUser?.isVideoOn == true) {
            var meetingController = RcvEngine.instance().getMeetingController(meetingId)
            var videoController = meetingController?.videoController
            videoController?.setupActiveVideo(remoteCanvas)

            remoteCanvas?.isMirrorMode = false;
            remoteCanvas?.attachView(remoteVideoView)

            remoteVideoView.visibility = View.VISIBLE
            remoteAvatarView.visibility = View.GONE
        }
        else {
            remoteVideoView.visibility = View.GONE
            remoteAvatarView.visibility = View.VISIBLE
        }
    }

    private fun hide() {
        var meetingController = RcvEngine.instance().getMeetingController(meetingId)
        var videoController = meetingController?.videoController

        videoController?.removeLocalVideo()
        localVideoView.visibility = View.GONE
        localAvatarView.visibility = View.GONE

        videoController?.removeActiveVideo()
        remoteVideoView.visibility = View.GONE
        remoteAvatarView.visibility = View.GONE
    }

    init {
        this.localCanvas = RcvVideoCanvas(null)
        this.localCanvas?.renderMode = RenderMode.FIT

        this.remoteCanvas = RcvVideoCanvas(null)
        this.remoteCanvas?.renderMode = RenderMode.FIT

        visibility = View.GONE
    }
}