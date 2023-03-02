package com.ringcentral.video.quickstart.controller.data

import com.ringcentral.video.ErrorCodeType

data class TokenRequestResult(
    val token: String?,
    val statusCode: ErrorCodeType
)