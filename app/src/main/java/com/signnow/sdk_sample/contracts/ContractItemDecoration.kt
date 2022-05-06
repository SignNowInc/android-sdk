package com.signnow.sdk_sample.contracts

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ContractItemDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition == 0) {
            outRect.top = 24
        }
        outRect.bottom = 24
        outRect.left = 24
        outRect.right = 24
    }
}