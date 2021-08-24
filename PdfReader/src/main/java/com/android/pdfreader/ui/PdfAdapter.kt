package com.android.pdfreader.ui

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.pdfreader.databinding.RowPageItemBinding

internal class PdfAdapter(
    private val bitmapList: List<Bitmap>
) : RecyclerView.Adapter<PdfAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: RowPageItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(page: Bitmap) {
            binding.page.setImageBitmap(page)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(RowPageItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(bitmapList[position])
    }

    override fun getItemCount() = bitmapList.size
}