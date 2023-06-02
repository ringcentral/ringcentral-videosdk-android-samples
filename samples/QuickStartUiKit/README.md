# RingCentral Video Client SDK UIKit For Android

## Overview

This sample enables you to quickly get started in your development efforts to create an Android application with real-time audio/video communication using the RingCentral Video Client SDK UIKit.

With this sample app you can:

- Start / leave  meeting
- Mute / unmute local audio
- Start / stop local video
- Start / stop sharing
- Start / pause / resume recording
- Switch audio route
- Chat action
- Participant action
- Meeting info action

## Prerequisites

- Android Studio 4.2.+ or IntelliJ IDEA
- Physical Android device (Phone and Tablet). Simulators are also supported, however, a real device is recommended because of the performance consideration.
  - Android OS version 7.0+
  - Android SDK version 26+
- RingCentral Developer Account (free - https://app.ringcentral.com/signup)
- Access to [RingCentral Video Documentation](https://ringcentral-ringcentral-video-api-docs.readthedocs-hosted.com/en/latest/) (using password "workasone")

## Getting Start

The following steps show you how to prepare, build, and run the sample application.

1. If you already have the client ID and client secret, locate the file **app/src/main/res/values/strings.xml** and replace {Your client ID} and {Your client secret} with your client ID and secret which got from the RingCentral developer website.

  ```xml
  <!-- replace your client id and secret here for testing -->
  <string name="clientId">{your client id}</string>
  <string name="clientSecret">{your client secret}</string>
  ```

1. RingCentral uses auth tokens to authenticate users joining/starting a meeting which makes the communication secure. Follow our [RingCentral Video client SDK Dev Guide](https://ringcentral-ringcentral-video-api-docs.readthedocs-hosted.com/en/latest/client-sdk/authentication/) to procure the auth tokens and place the same inside of **app/src/main/res/values/strings.xml** file. Or set your personal JWT or user name and password, and the RCV client SDK will take care of the authorization process.

  ```xml
  <!-- place your personal JWT -->
  <string name="personalJwt"></string>
  <!-- place your user name and password -->
  <string name="userName"></string>
  <string name="password"></string>
  <!-- place your auth tokens here for testing -->
  <string name="ringcentral_video_auth_token">
    {
    \"access_token\": \"{access token}\",
    \"token_type\": \"bearer\",
    \"expires_in\": 3600,
    \"refresh_token\": \"{refresh token}\",
    \"refresh_token_expires_in\": 604800,
    \"scope\": \"{scope}\",
    \"owner_id\": \"{owner id}\",
    \"endpoint_id\": \"{endpoint ID}\"
    }
  </string>
  ```

### The video client SDK UIKit integration

This is the reference step for integrating the client SDK into the code in the sample project.

1. Add it in your root **setting.gradle** at the end of repositories.

    ```gradle
    allprojects {
        repositories {
            ...
            maven { url 'https://s01.oss.sonatype.org/content/repositories/releases' }
        }
    }
    ```

2. Add the following lines under **dependencies** of your app **build.gradle** file.

    ```gradle
    implementation 'com.pubnub:pubnub-gson:4.29.2'
    implementation 'com.squareup.okhttp3:okhttp:4.9.1'
    implementation 'com.ringcentral.video:ringcentral-video-sdk:version'
    implementation 'com.ringcentral.video:ringcentral-video-sdk-uikit:version'
    implementation "io.github.scwang90:refresh-layout-kernel:2.0.6"
    implementation "io.github.scwang90:refresh-header-classics:2.0.6"
    ```

3.  If the line `android.enableJetifier=true` and `android.useAndroidX=true` doesn't exist in the file, you can add it directly at the end. If the line already exists but its value is `false`, you can change it to `true`.

   ```groovy
   android.useAndroidX=true
   android.enableJetifier=true
   ```

### Run the Application

Ensure USB debugging is enabled on your device under **Settings > Developer options** and connect the device to your computer.

#### Using Android Studio

1. Open the sample application in Android Studio.
2. Select **File** > **Sync Project with Gradle Files**.
3. Build and run the sample project.

**Note:** The sample application should run on your device. Instead, check the errors to troubleshoot.

#### Using IntelliJ IDEA

Import the sample application into **IntelliJ IDEA** as a gradle project.

1. Select **File** > **Open** from the main menu.
2. In the dialog that opens, select the directory that contains the sample application and click **OK**.
3. Build and run the sample project.

**Note:** The sample application should run on your device. Instead, check the errors on your IDE console.

### Contact Us

- Dev Support & Feedback: For feedback, questions or suggestions around SDK please email videosdkfeedback@ringcentral.com

