package com.ringcentral.video.quickstart.controller.data

sealed class SharingStatus {
    object NotSharing : SharingStatus()
    object RemoteScreenSharing : SharingStatus()
    object LocalScreenSharing : SharingStatus()
    object Paused : SharingStatus()
}