package com.signnow.sdk_sample.contracts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.signnow.sdk.SNLoadingHelper
import com.signnow.sdk.SignnowSDK
import com.signnow.sdk.data.external.SNResultCallback
import com.signnow.sdk.data.external.document.SNDocument
import com.signnow.sdk.editor.EditorLauncher
import com.signnow.sdk_sample.R
import kotlinx.android.synthetic.main.activity_contracts.*

class ContractsActivity : AppCompatActivity(), SNLoadingHelper {

    private val onContractSelected: (SNDocument) -> Unit = { document ->
        EditorLauncher.startActivity(this, document.id, 34)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contracts)

        initUi()

        fetchDocuments()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_contracts, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onLoadingStarted() {
        pb_contracts.visibility = View.VISIBLE
    }

    override fun onLoadingFinished() {
        pb_contracts.visibility = View.INVISIBLE
    }

    private fun fetchDocuments() {
        val documentsFolderId = "ac12882357db13107f663a6981775e9690242dbf"
        SignnowSDK.documentsProvider.getTemplates(documentsFolderId,
            3,
            0,
            loadingHelper = this,
            resultCallback = object : SNResultCallback<List<SNDocument>> {
                override fun onResult(value: List<SNDocument>) {
                    val adapter = ContractsAdapter(value, onContractSelected)
                    rv_contracts.adapter = adapter
                }

                override fun onError(error: Throwable?) {
                    Log.e(SignnowSDK.LOG_TAG, "Couldn't load templates", error)
                }
            })
    }

    private fun initUi() {
        setSupportActionBar(toolbar_contracts)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        rv_contracts.layoutManager = LinearLayoutManager(this)
        rv_contracts.setHasFixedSize(true)
        rv_contracts.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
    }

    companion object {

        fun start(activity: AppCompatActivity) = with(activity) {
            val intent = Intent(this, ContractsActivity::class.java)
            startActivity(intent)
        }
    }

}