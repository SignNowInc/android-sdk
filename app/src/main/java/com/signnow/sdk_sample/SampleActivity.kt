package com.signnow.sdk_sample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.signnow.sampletokenstorage.TokenStorage
import com.signnow.sdk.SigningSession
import com.signnow.sdk.SignnowSDK
import com.signnow.sdk.SignnowSession
import com.signnow.sdk.data.external.token.SNAccessToken
import com.signnow.sdk.editor.StartEditorContract
import com.signnow.sdk.network.listeners.OnTokenRefreshListener

class SampleActivity : AppCompatActivity(), OnTokenRefreshListener {

    private val tokenStorage: TokenStorage by lazy { TokenStorage(this) }

    private val someStartForResult = registerForActivityResult(StartEditorContract()) {
        Log.e(SignnowSDK.LOG_TAG, "Result: $it")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
        val accessToken = tokenStorage.accessToken
        val refreshToken = tokenStorage.refreshToken

        if (!accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()) {
            SignnowSession.startSession(accessToken, refreshToken)
            SignnowSession.addOnTokenRefreshListener(this)
        }

        findViewById<Button>(R.id.btn)?.setOnClickListener {
//            val documentId = "6eaa641e394647dba16acad4f74f4825229bf6da"
//            SignnowSDK.documentsProvider.getDocumentMetadata(documentId, result = object : SNResultCallback<SNDocument> {
//                override fun onResult(value: SNDocument) {
//                    val roleName = value.fields.first().role
//                    SigningSession.startSigning(someStartForResult, documentId, listOf(SNRecipient("sckalper@gmail.com", roleName)), listOf(
//                        SNFieldValue("first_name", "Roman")))
//                }
//
//                override fun onError(error: Throwable?) {
//                    Log.e(SignnowSDK.LOG_TAG, "Error thrown while trying to get document", error)
//                }
//            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(SignnowSDK.LOG_TAG, "Result: ${SigningSession.parseResult(data)}")
    }

    override fun onDestroy() {
        SignnowSession.removeTokenRefreshListener()
        super.onDestroy()
    }

    override fun onTokenRefreshed(accessToken: SNAccessToken) {
        tokenStorage.accessToken = accessToken.token
        tokenStorage.refreshToken = accessToken.refreshToken
    }

    override fun onTokenRefreshFailed(error: Throwable?) {
        Log.e(SignnowSDK.LOG_TAG, "Token refresh failed", error)
    }

}