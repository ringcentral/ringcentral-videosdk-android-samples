package com.ringcentral.video.quickstart.ui

import com.ringcentral.video.ErrorCodeType

data class RefreshTokenUiEvent(
    val isRefreshedSuccessfully: Boolean = false,
    val code: ErrorCodeType? = null
)