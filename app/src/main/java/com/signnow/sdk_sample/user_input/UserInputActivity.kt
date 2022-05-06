package com.signnow.sdk_sample.user_input

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.signnow.sdk.SNLoadingHelper
import com.signnow.sdk.SigningSession
import com.signnow.sdk.SignnowSDK
import com.signnow.sdk.data.external.SNFieldValue
import com.signnow.sdk.data.external.SNRecipient
import com.signnow.sdk.data.external.SNResultCallback
import com.signnow.sdk.data.external.SNSigningSessionResult
import com.signnow.sdk.data.external.document.SNDocument
import com.signnow.sdk.editor.StartEditorContract
import com.signnow.sdk_sample.R
import com.signnow.sdk_sample.result.SigningSessionResultActivity
import com.signnow.utils.extensions.string

class UserInputActivity : AppCompatActivity(), SNLoadingHelper {

    private val toolbar: Toolbar by lazy { findViewById(R.id.toolbar_user_input) }
    private val etFirstName: EditText by lazy { findViewById(R.id.et_user_input_name) }
    private val etSecondName: EditText by lazy { findViewById(R.id.et_user_input_surname) }
    private val etAge: EditText by lazy { findViewById(R.id.et_user_input_age) }
    private val etAmount: EditText by lazy { findViewById(R.id.et_user_input_amount) }
    private val etEmail: EditText by lazy { findViewById(R.id.et_user_input_email) }
    private val btnNext: Button by lazy { findViewById(R.id.btn_user_input_next) }
    private val progressBar: ProgressBar by lazy { findViewById(R.id.pb_user_input) }

    private val activityLauncher = registerForActivityResult(StartEditorContract()) { result ->
        if (result == SNSigningSessionResult.SIGNED) {
            startActivity(Intent(this@UserInputActivity, SigningSessionResultActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_input)
        setSupportActionBar(toolbar)
        val documentId = intent.getStringExtra(EXTRA_DOCUMENT_ID) ?: throw IllegalArgumentException("Wrong document id")

        btnNext.setOnClickListener {
            SignnowSDK.documentsProvider.getDocumentMetadata(documentId, this, object : SNResultCallback<SNDocument> {
                override fun onResult(value: SNDocument) {
                    val recipients = listOf(SNRecipient(etEmail.string, value.roles.first().name))
                    SigningSession.startSigning(activityLauncher, documentId, recipients, prepareFieldValues())
                }

                override fun onError(error: Throwable?) {
                    Log.e(SignnowSDK.LOG_TAG, "Couldn't get document metadata", error)
                }
            })
        }
    }

    override fun onLoadingStarted() {
        progressBar.visibility = View.VISIBLE
        btnNext.visibility = View.GONE
    }

    override fun onLoadingFinished() = Unit

    private fun prepareFieldValues(): List<SNFieldValue> {
        return listOf(
            SNFieldValue(LoanContractFieldName.FirstName.value, etFirstName.string),
            SNFieldValue(LoanContractFieldName.SecondName.value, etSecondName.string),
            SNFieldValue(LoanContractFieldName.Age.value, etAge.string),
            SNFieldValue(LoanContractFieldName.Amount.value, etAmount.string),
        )
    }

    companion object {

        private const val EXTRA_DOCUMENT_ID = "78hid"

        fun start(activity: AppCompatActivity, documentId: String) = with(activity) {
            val intent = Intent(this, UserInputActivity::class.java)
                .putExtra(EXTRA_DOCUMENT_ID, documentId)
            startActivity(intent)
        }
    }
}