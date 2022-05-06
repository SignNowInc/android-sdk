package com.signnow.sdk_sample.clients

import android.annotation.SuppressLint
import android.os.Parcelable
import android.view.ContextMenu
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.signnow.sdk_sample.R
import com.signnow.utils.extensions.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.item_client.view.*

class ClientsAdapter(private var onItemSelected: (Client) -> Unit) : RecyclerView.Adapter<ClientsAdapter.ClientViewHolder>() {

    private val items: MutableList<Client> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientsAdapter.ClientViewHolder {
        val view = parent.inflate(R.layout.item_client)
        return ClientViewHolder(view, onItemSelected)
    }

    override fun onBindViewHolder(holder: ClientsAdapter.ClientViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newData: List<Client>) {
        items.clear()
        items.addAll(newData)
        notifyDataSetChanged()
    }

    fun addItem(client: Client) {
        items.add(client)
        notifyItemInserted(items.size - 1)
    }

    var selectedClient: Client? = null
        private set

    inner class ClientViewHolder(override val containerView: View,
                                 private val onItemClickListener: (Client) -> Unit)
        : RecyclerView.ViewHolder(containerView), LayoutContainer, View.OnCreateContextMenuListener {

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu?.add(Menu.NONE, R.id.menu_create_deposit,
                Menu.NONE, R.string.main_activity_item_deposit)
            menu?.add(Menu.NONE, R.id.menu_create_loan,
                Menu.NONE, R.string.main_activity_item_loan)
        }

        fun bind(item: Client) = with(item) {
            containerView.setOnCreateContextMenuListener(this@ClientViewHolder)
            containerView.iv_client_avatar?.setImageResource(avatar)
            containerView.tv_client_name?.text = name
            containerView.tv_client_credit_score?.text = creditScore.toString()
            containerView.setOnClickListener { onItemClickListener(item) }
            containerView.setOnLongClickListener {
                selectedClient = item
                false
            }
        }
    }
}

@Parcelize
data class Client(val name: String, val creditScore: Int, val avatar: Int) : Parcelable