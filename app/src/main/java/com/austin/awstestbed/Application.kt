package com.austin.awstestbed

import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify

class CustomApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(applicationContext)
            Log.i("bruh", "amplify initialized")
        } catch (e: AmplifyException) {
            Log.i("bruh", "amplify exception! ${e.localizedMessage}")
        }
    }
}