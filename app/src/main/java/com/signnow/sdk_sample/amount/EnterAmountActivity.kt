package com.signnow.sdk_sample.amount

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.signnow.sdk.SNLoadingHelper
import com.signnow.sdk.SigningSession
import com.signnow.sdk.data.external.SNFieldValue
import com.signnow.sdk.data.external.SNRecipient
import com.signnow.sdk.data.external.SNSigningSessionResult
import com.signnow.sdk.editor.StartEditorContract
import com.signnow.sdk_sample.MainActivity
import com.signnow.sdk_sample.R
import com.signnow.sdk_sample.clients.Client
import com.signnow.sdk_sample.clients.ClientSelectionActivity
import com.signnow.utils.extensions.string
import kotlinx.android.synthetic.main.activity_amount.*
import kotlinx.android.synthetic.main.view_loading.*

class EnterAmountActivity : AppCompatActivity(), SNLoadingHelper {

    private val editorLauncher = registerForActivityResult(StartEditorContract()) {
        if (it == SNSigningSessionResult.SIGNED) {
            Toast.makeText(this, "Congratulations! Contract was signed", Toast.LENGTH_SHORT).show()
        }
        val intent = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_amount)
        setSupportActionBar(toolbar_amount)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        et_amount?.requestFocus()

        btn_amount_continue?.setOnClickListener {
            val templateId = "c88948a7e5294674a1f30b72b0609b1baf96ffd9"
            val email = "sckalper@gmail.com"
            val recipients = listOf(SNRecipient(email, "Signer 1"), SNRecipient("anarak@mail4gmail.com", "Signer 2"))
            SigningSession.startSigning(editorLauncher, templateId, recipients, getPreFillValues(), this)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onLoadingStarted() {
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val loadingView = View.inflate(this, R.layout.view_loading, null)
        addContentView(loadingView, params)
        tv_progress_message?.text = "Preparing contract"
    }

    override fun onLoadingFinished() {
        val parent = findViewById<FrameLayout>(android.R.id.content)
        parent.removeViewAt(parent.childCount - 1)
    }

    private fun getPreFillValues(): List<SNFieldValue> {
        val client = intent.getParcelableExtra<Client>(ClientSelectionActivity.EXTRA_CLIENT)!!
        val firstName = SNFieldValue("first_name", client.name.split(" ").first())
        val secondName = SNFieldValue("second_name", client.name.split(" ").last())
        val emailValue = SNFieldValue("email", "${firstName.value}.${secondName.value}@mail.com")
        val amountValue = SNFieldValue("amount", et_amount.string)
        return listOf(firstName, secondName, emailValue/*, periodValue, interestValue*/, amountValue)
    }

    companion object {

        private const val RC = 2123
        private const val EXTRA_PERIOD = "dsad"
        private const val EXTRA_RATE = "iuh87iuhdsjk"

        fun startForResult(activity: Activity, client: Client, period: Int, interestRate: Int) = with(activity) {
            val intent = Intent(this, EnterAmountActivity::class.java)
                .putExtra(ClientSelectionActivity.EXTRA_CLIENT, client)
                .putExtra(EXTRA_PERIOD, period)
                .putExtra(EXTRA_RATE, interestRate)
            startActivityForResult(intent, RC)
        }
    }

}