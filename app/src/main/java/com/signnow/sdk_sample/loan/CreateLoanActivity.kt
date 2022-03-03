package com.signnow.sdk_sample.loan

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
import com.signnow.sdk_sample.clients.ClientsActivityOpenMode
import com.signnow.utils.extensions.string
import kotlinx.android.synthetic.main.activity_create_loan.*
import kotlinx.android.synthetic.main.view_loading.*

class CreateLoanActivity : AppCompatActivity(), SNLoadingHelper {

    private lateinit var selectedClient: Client
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
        setContentView(R.layout.activity_create_loan)
        setSupportActionBar(toolbar_loan)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        setClient(intent)
        card_loan_client_picker?.setOnClickListener {
            ClientSelectionActivity.startActivity(this, ClientsActivityOpenMode.PICK)
        }
        btn_loan_continue?.setOnClickListener {
            if (!::selectedClient.isInitialized) {
                Toast.makeText(this, "You need to select client", Toast.LENGTH_SHORT).show()
            } else {
                val templateId = "8a900a73e8d242c2a8c316fe1b61325d40005046"
                val email = "sckalper@gmail.com"
                val recipients = listOf(SNRecipient(email, "Signer 1"), SNRecipient("anarak@mail4gmail.com", "Signer 2"),
                    SNRecipient("some@mail.com", "Signer 3"))
                SigningSession.startSigning(editorLauncher, templateId, recipients, getPreFillValues(), this)
            }
        }
        et_loan_size.requestFocus()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ClientSelectionActivity.RC && resultCode == RESULT_OK) {
            data?.let(::setClient)
        }
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
        val firstName = SNFieldValue("first_name", selectedClient.name.split(" ").first())
        val secondName = SNFieldValue("second_name", selectedClient.name.split(" ").last())
        val loanSize = et_loan_size.string
        val loanValue = SNFieldValue("loan_size", loanSize)
        val loanValue2 = SNFieldValue("loan_size_2", loanSize)
        return listOf(firstName, secondName, loanValue, loanValue2)
    }

    private fun setClient(intent: Intent) {
        val client = intent.getParcelableExtra<Client>(ClientSelectionActivity.EXTRA_CLIENT) ?: return
        selectedClient = client
        iv_loan_selected_client?.setImageResource(client.avatar)
        tv_loan_selected_client?.text = client.name
        tv_loan_selected_client_score?.text = getString(R.string.deposit_activity_credit_score, client.creditScore)
    }

    companion object {

        fun start(activity: AppCompatActivity, client: Client? = null) = with(activity) {
            val intent = Intent(this, CreateLoanActivity::class.java)
                .putExtra(ClientSelectionActivity.EXTRA_CLIENT, client)
            startActivity(intent)
        }
    }

}