package com.signnow.sdk_sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.signnow.sampletokenstorage.TokenStorage
import com.signnow.sdk.SignnowSession
import com.signnow.sdk_sample.clients.ClientSelectionActivity
import com.signnow.sdk_sample.clients.ClientsActivityOpenMode
import com.signnow.sdk_sample.contracts.ContractsActivity
import com.signnow.sdk_sample.deposits.CreateDepositActivity
import com.signnow.sdk_sample.loan.CreateLoanActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val tokenStorage: TokenStorage by lazy { TokenStorage(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val accessToken = tokenStorage.accessToken
        val refreshToken = tokenStorage.refreshToken
        if (!accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()) {
            SignnowSession.startSession(accessToken, refreshToken)
        }
        tv_main_action_create_deposit.setOnClickListener {
            startActivity(Intent(this, CreateDepositActivity::class.java))
        }
        tv_main_action_create_loan.setOnClickListener {
            startActivity(Intent(this, CreateLoanActivity::class.java))
        }
        tv_main_action_clients.setOnClickListener {
            ClientSelectionActivity.startActivity(this, ClientsActivityOpenMode.ADD)
        }
        tv_main_action_contracts.setOnClickListener {
            ContractsActivity.start(this)
        }
    }
}