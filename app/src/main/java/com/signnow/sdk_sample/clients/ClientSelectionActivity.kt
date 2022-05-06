package com.signnow.sdk_sample.clients

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateMargins
import androidx.recyclerview.widget.LinearLayoutManager
import com.signnow.sdk_sample.R
import com.signnow.sdk_sample.deposits.CreateDepositActivity
import com.signnow.sdk_sample.loan.CreateLoanActivity
import com.signnow.utils.extensions.string
import kotlinx.android.synthetic.main.activity_client_selector.*
import kotlinx.android.synthetic.main.dialog_create_client.view.*
import java.util.*

class ClientSelectionActivity : AppCompatActivity() {

    private val adapter: ClientsAdapter = ClientsAdapter { client ->
        intent.putExtra(EXTRA_CLIENT, client)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_selector)
        setSupportActionBar(toolbar_client_selector)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val openMode = intent.getSerializableExtra(EXTRA_MODE) as ClientsActivityOpenMode
        if (openMode == ClientsActivityOpenMode.PICK) {
            supportActionBar?.setTitle(R.string.clients_list_pick)
            btn_add_client?.visibility = View.GONE
            view_clients_btn_background?.visibility = View.GONE
            val marginLayoutParams = rv_clients.layoutParams as FrameLayout.LayoutParams
            marginLayoutParams.updateMargins(bottom = 0)
            rv_clients.layoutParams = marginLayoutParams
        } else {
            supportActionBar?.setTitle(R.string.clients_list_label)
            btn_add_client?.visibility = View.VISIBLE
        }
        rv_clients?.layoutManager = LinearLayoutManager(this)
        rv_clients?.setHasFixedSize(true)
        rv_clients?.adapter = adapter
        adapter.setItems(clientGenerator())
        btn_add_client?.setOnClickListener { createUserDialog() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.title) {
            getString(R.string.main_activity_item_deposit) -> CreateDepositActivity.start(this, adapter.selectedClient)
            getString(R.string.main_activity_item_loan) -> CreateLoanActivity.start(this, adapter.selectedClient)
        }
        return true
    }

    private fun clientGenerator(): List<Client> {
        return clientNames.map { entry ->
            Client(entry.key, generateCreditScore(), entry.value)
        }
    }

    private fun generateCreditScore(): Int {
        require(900 - 300 + 1 <= Int.MAX_VALUE) { "Illegal Argument" }
        return Random(System.nanoTime()).nextInt(900 - 300 + 1) + 300
    }

    private fun createUserDialog() {
        val inflatedView = View.inflate(this, R.layout.dialog_create_client, null)
        val dialog = AlertDialog.Builder(this)
            .setView(inflatedView)
            .show()
        inflatedView.btn_create_client?.setOnClickListener {
            val name = inflatedView.et_create_client.string
            clientNames[name] = R.drawable.ic_avatar_8
            adapter.addItem(Client(name, generateCreditScore(), R.drawable.ic_avatar_8))
            dialog.dismiss()
        }
    }

    companion object {

        val clientNames: HashMap<String, Int> = hashMapOf(
            "Devon Lane" to R.drawable.ic_avatar_1,
            "Courtney Henry" to R.drawable.ic_avatar_2,
            "Savannah Nguyen" to R.drawable.ic_avatar_3,
            "Jerome Bell" to R.drawable.ic_avatar_4,
            "Jacob Jones" to R.drawable.ic_avatar_5,
            "Darlene Robertson" to R.drawable.ic_avatar_6,
            "Jane Cooper" to R.drawable.ic_avatar_7,
            "Bessie Cooper" to R.drawable.ic_avatar_9,
            "Esther Howard" to R.drawable.ic_avatar_10,
        )

        const val RC = 9889
        const val EXTRA_CLIENT = "auhkjn"
        private const val EXTRA_MODE = "kajnsk"

        fun startActivity(activity: AppCompatActivity, openMode: ClientsActivityOpenMode) = with(activity) {
            val intent = Intent(this, ClientSelectionActivity::class.java)
                .putExtra(EXTRA_MODE, openMode)
            startActivityForResult(intent, RC)
        }
    }

}