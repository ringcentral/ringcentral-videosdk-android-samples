package com.ringcentral.video.quickstart.ui

import android.os.Bundle
import com.ringcentral.video.quickstart.R
import com.ringcentral.video.quickstart.base.PermissionRequestActivity

open class MainActivity : PermissionRequestActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissionHostLayout = findViewById(R.id.nav_host_fragment)
    }
}