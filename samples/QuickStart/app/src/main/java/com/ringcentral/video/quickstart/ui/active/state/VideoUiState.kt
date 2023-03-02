package com.ringcentral.video.quickstart.ui.active.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoUiState(
    val modelId: Long = -1,
    val isVideoOff: Boolean = true
) : Parcelable