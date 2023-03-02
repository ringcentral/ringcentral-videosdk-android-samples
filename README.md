# RingCentral Video Client SDK For Android

## Overview

This repository contains sample projects using the RingCentral video client SDK for Android. This tutorial guides you to get started in your development efforts to create an Android application with real-time audio/video communications.

With the video client SDK you can:

- Join or start a meeting with a valid access token.
- Join the meeting as a guest. (The app needs to get the authorization of the guest type first)
- Start audio/video communication with participants.
- Mute and unmute local audio.
- Start and stop the local video.
- Sharing the device screen or camera.
- Sends in-meeting private and public chat messages.
- Receive meeting and media event callback, such as participant joined/leave, audio/video muted/unmuted.
- Host or moderator privileges:
    - Mutes and unmutes a specific participant' audio or video.
    - Mutes and unmutes all meeting users' audio or video.
    - Locks/unlocks the meeting.
    - Starts and pauses the recording.
    - Assigns and revokes the moderator role with meeting users.
    - Puts in-meeting users into the waiting room.
    - Admits/denies a user from the waiting room.
    - Admits all users from the waiting room.
    - Stops remote users' sharing.
    - Locks and unlocks the meeting sharing function.
    - Removes the meeting user from the active meeting.

## Prerequisites

- Android Studio 4.2.+
- Physical Android device (Phone and Tablet) (simulator is also supported)
    - Android OS version 7.0+
    - Android SDK version 24+
- RingCentral Developer Account (free - https://app.ringcentral.com/signup)
- Access to [RingCentral Video Documentation](https://ringcentral-ringcentral-video-api-docs.readthedocs-hosted.com/en/latest/) (using password "workasone")

## How To Run The Sample Projects

The sample project can enable you to quickly get started in the development efforts using the RingCentral Video client SDK. For detailed information, check the README file of each sample project.

## How To Program Real-time Video With Android

To start using the Android programmable video client SDK in your applications, you need to perform a few steps as follows.

### Building A Project With Java/Kotlin


1. If you are using a Gradle version lower than 6.8.0, add the following lines to your **build.gradle** file. Instead, you can ignore this step.

    ```gradle
    allprojects {
        repositories {
            google()
            mavenCentral()
            maven { url 'https://s01.oss.sonatype.org/content/repositories/releases' }
        }
    }
    ```
2. Add it in your root **build.gradle** at the end of repositories.

    ```gradle
    allprojects {
        repositories {
            ...
            maven { url 'https://s01.oss.sonatype.org/content/repositories/releases' }
        }
    }
    ```

3. Add the following lines under **dependencies** of your app **build.gradle** file.

    ```gradle
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.5.20"
    implementation 'com.pubnub:pubnub-gson:4.29.2'
    implementation 'com.ringcentral.video:ringcentral-video-sdk:version'
    ```

4. Use your client ID and client secret to initial the RCV client SDK engine as code below, the client ID and client secret are required.

    ```java
    RcvEngine.create(this.getApplicationContext(), <#client ID#>, <#client secret#>);
    ```

5. If you intend to host a meeting or access your extension information, such as the meeting list or the recording list, etc, follow the steps in our [RingCentral Video client SDK Dev Guide](https://ringcentral-ringcentral-video-api-docs.readthedocs-hosted.com/en/latest/client-sdk/authentication/) to procure the RingCentral authorization tokens and invoke the API method as code below.

    ```java  
    RcvEngine.instance().setAuthToken(<#authorization token string#>, true);
    ```

6. Next, follow the dev guide and API documentation or the sample projects to build your video application, enjoy programming!

## Known Issues

- Some interfaces are not supported yet in the current beta version, they are being actively developed. For example, the breakout room, etc.
- You may encounter some problems while running the sample applications, such as abnormal UI or crash, etc.

## Contact Us

- Dev Support & Feedback: For feedback, questions or suggestions around SDK please email videosdkfeedback@ringcentral.com
