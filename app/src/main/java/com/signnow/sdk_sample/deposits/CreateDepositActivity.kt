package com.signnow.sdk_sample.deposits

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.signnow.sdk_sample.R
import com.signnow.sdk_sample.amount.EnterAmountActivity
import com.signnow.sdk_sample.clients.Client
import com.signnow.sdk_sample.clients.ClientSelectionActivity
import com.signnow.sdk_sample.clients.ClientsActivityOpenMode
import kotlinx.android.synthetic.main.activity_deposit.*

class CreateDepositActivity : AppCompatActivity() {

    private var monthCounter: Int = 12
        set(value) {
            if (value > 12 || value < 1) return

            updateMonthCounter(value)
            field = value
        }

    private lateinit var selectedClient: Client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deposit)
        setSupportActionBar(toolbar_deposit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        setClient(intent)
        updateMonthCounter(monthCounter)
        iv_deposit_decrease_month?.setOnClickListener { monthCounter-- }
        iv_deposit_increase_month?.setOnClickListener { monthCounter++ }
        card_deposit_client_picker?.setOnClickListener {
            ClientSelectionActivity.startActivity(this, ClientsActivityOpenMode.PICK)
        }
        btn_create_deposit?.setOnClickListener {
            if (!::selectedClient.isInitialized) {
                Toast.makeText(this, "You need to select client", Toast.LENGTH_SHORT).show()
            } else {
                val interestRate = tv_deposit_percent.text.removeSuffix("%").toString().toInt()
                EnterAmountActivity.startForResult(this, selectedClient, monthCounter, interestRate)
            }
        }
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

    private fun setClient(intent: Intent) {
        val client = intent.getParcelableExtra<Client>(ClientSelectionActivity.EXTRA_CLIENT) ?: return
        selectedClient = client
        iv_deposit_selected_client?.setImageResource(client.avatar)
        tv_deposit_selected_client?.text = client.name
        tv_deposit_selected_client_score?.text = getString(R.string.deposit_activity_credit_score, client.creditScore)
    }

    private fun updateMonthCounter(number: Int) {
        iv_deposit_increase_month?.isEnabled = number < 12
        iv_deposit_decrease_month?.isEnabled = number > 1

        val month = resources.getQuantityString(R.plurals.deposit_activity_month, number, number)
        tv_deposit_month?.text = month
        val percent = number / 2
        val result = if (percent <= 0) 1 else percent
        val percentage = getString(R.string.deposit_activity_percentage, result)
        tv_deposit_percent?.text = percentage
        val interestRate = getString(R.string.deposit_activity_interest_rate, result)
        tv_deposit_calculated_percent?.text = interestRate
    }

    companion object {

        fun start(activity: AppCompatActivity, client: Client? = null) = with(activity) {
            val intent = Intent(this, CreateDepositActivity::class.java)
                .putExtra(ClientSelectionActivity.EXTRA_CLIENT, client)
            startActivity(intent)
        }
    }

}