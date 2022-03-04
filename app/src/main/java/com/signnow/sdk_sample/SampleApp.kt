package com.signnow.sdk_sample

import android.app.Application
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.CoilUtils
import com.signnow.sampletokenstorage.TokenStorage
import com.signnow.sdk.SignnowSDK
import com.signnow.sdk.SignnowSession
import com.signnow.sdk.data.external.SNGrantType
import com.signnow.sdk.data.external.SNResultCallback
import com.signnow.sdk.data.external.token.SNAccessToken
import okhttp3.OkHttpClient

class SampleApp : Application(), ImageLoaderFactory {

    private lateinit var tokenStorage: TokenStorage

    override fun onCreate() {
        super.onCreate()
        tokenStorage = TokenStorage(this)
        val clientId = "your clientId"
        val clientSecret = "your clientSecret"
        val grantType = SNGrantType.Credentials("sdk.sampletest@gmail.com", "passqwer1")

        val onAccessTokenReceived = object : SNResultCallback<SNAccessToken> {
            override fun onResult(value: SNAccessToken) {
                tokenStorage.accessToken = value.token
                tokenStorage.refreshToken = value.refreshToken
                SignnowSession.startSession(value.token, value.refreshToken)
            }

            override fun onError(error: Throwable?) {
                Log.e(SignnowSDK.LOG_TAG, "Failed to authorize", error)
            }
        }

        SignnowSDK.init(applicationContext, clientId, clientSecret)

        if (tokenStorage.accessToken.isNullOrBlank() || tokenStorage.refreshToken.isNullOrBlank()) {
            SignnowSDK.authorize(grantType, onAccessTokenReceived)
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .crossfade(true)
            .okHttpClient {
                val client = OkHttpClient.Builder()
                    .cache(CoilUtils.createDefaultCache(applicationContext))
                    .addInterceptor { chain ->
                        val (name, value) = SignnowSDK.getImageLoadingHeader(tokenStorage.accessToken!!)
                        val newRequest = chain.request().newBuilder()
                            .addHeader(name, value)
                            .build()
                        chain.proceed(newRequest)
                    }
                    .build()
                client
            }
            .build()
    }
}