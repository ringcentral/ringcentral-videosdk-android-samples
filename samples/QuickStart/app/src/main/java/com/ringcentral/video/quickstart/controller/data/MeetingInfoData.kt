package com.ringcentral.video.quickstart.controller.data

import com.ringcentral.video.MeetingDialCountryInfo
import com.ringcentral.video.MeetingDialInfo
import com.ringcentral.video.MeetingInfo

data class MeetingInfoData(
    val meetingName: String = "",
    val hostName: String = "",
    val meetingId: String = "",
    val meetingPassword: String = "",
    val meetingLink: String = "",
    val dialInfo: List<MeetingDialInfoData> = listOf(),
)

data class MeetingDialInfoData(
    val phoneNumber: String = "",
    val location: String = "",
    val password: String = "",
    val accessCode: String = "",
    val countryInfo: CountryInfo = CountryInfo()
)

data class CountryInfo(
    val id: String = "",
    val name: String = "",
    val isoCode: String = "",
    val callingCode: String = "",
)

fun MeetingDialCountryInfo?.toCountryInfo(): CountryInfo {
    this ?: return CountryInfo()
    return CountryInfo(id = id(), name = name(), isoCode = isoCode(), callingCode = callingCode())
}

fun ArrayList<MeetingDialInfo>?.toDialInfoList(): List<MeetingDialInfoData> {
    if (isNullOrEmpty()) return listOf()
    return map { it.toMeetingDialInfoData() }
}

fun MeetingDialInfo?.toMeetingDialInfoData(): MeetingDialInfoData {
    this ?: return MeetingDialInfoData()
    return MeetingDialInfoData(
        phoneNumber(), location(), password(), accessCode(), country().toCountryInfo()
    )
}

fun MeetingInfo?.toMeetingInfoData(): MeetingInfoData {
    this ?: return MeetingInfoData()
    return MeetingInfoData(
        meetingName(),
        hostName(),
        meetingId(),
        meetingPassword(),
        meetingLink(),
        dialInfo().toDialInfoList()
    )
}
