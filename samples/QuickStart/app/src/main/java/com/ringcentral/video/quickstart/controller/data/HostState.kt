package com.ringcentral.video.quickstart.controller.data

data class HostState(
    val isHost: Boolean = false,
    val isTemporaryModerator: Boolean = false,
    val isModerator: Boolean = false,
)