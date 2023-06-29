package com.example.QuickStartUiKit

import android.app.Application
import com.ringcentral.video.GrantType
import com.ringcentral.video.OauthOptions
import com.ringcentral.video.RcvEngine

class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        /*
        * Creating the RCV engine instance with the client ID and client secret, the client ID and
        * client secret must be provided when integrating with the RingCentral video client SDK,
        * otherwise, the video service will not work properly.
        * isShareUsageData, the switch to send important usage data back to RingCentral
        */
        RcvEngine.create(this.applicationContext,
            getString(R.string.clientId),
            getString(R.string.clientSecret), false
        )

        /*
         * There are two options to do the authentication.
         * (1) Using the JWT or password OAuth flow,
         * the client SDK will take care the authorization process.
         */
        val oauthOptions = OauthOptions.create()
        // Using the JWT flow
//        oauthOptions.grantType = GrantType.JWT
//        oauthOptions.jwt = getString(R.string.personalJwt)
        // Or using the password flow
        oauthOptions.grantType = GrantType.PASSWORD
        oauthOptions.userName = getString(R.string.userName)
        oauthOptions.password = getString(R.string.password)
        RcvEngine.instance().authorize(oauthOptions)

        // (2) You already have the auth token pair, pass it to the SDK as below.
        //RcvEngine.instance().setAuthToken(getString(R.string.ringcentral_video_auth_token), true)
    }
}