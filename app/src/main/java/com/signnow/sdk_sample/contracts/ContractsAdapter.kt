package com.signnow.sdk_sample.contracts

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.request.ImageRequest
import com.signnow.sdk.data.external.document.SNDocument
import com.signnow.sdk_sample.R
import com.signnow.sdk_sample.clients.ClientSelectionActivity
import com.signnow.utils.extensions.inflate

class ContractsAdapter(private val items: List<SNDocument>,
                       private val onItemClick: (SNDocument) -> Unit) : RecyclerView.Adapter<ContractsAdapter.ContractViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContractViewHolder {
        val view = parent.inflate(R.layout.item_contract)
        return ContractViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContractViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ContractViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val preview: ImageView = view.findViewById(R.id.item_contract_preview)
        private val documentName: TextView = view.findViewById(R.id.item_contract_name)
        private val signer: ImageView = view.findViewById(R.id.iv_item_contract_avatar)

        fun bind(item: SNDocument) = with(item) {
            val imageLoader = preview.context.imageLoader
            val request = ImageRequest.Builder(preview.context)
                .data(thumbnail?.medium!!)
                .target(preview)
                .build()
            imageLoader.enqueue(request)
            documentName.text = name
            signer.setImageResource(getAvatarRes())
            view.setOnClickListener { onItemClick(this) }
        }

        private fun getAvatarRes(): Int {
            val key = ClientSelectionActivity.clientNames.keys.random()
            return ClientSelectionActivity.clientNames.getValue(key)
        }
    }
}