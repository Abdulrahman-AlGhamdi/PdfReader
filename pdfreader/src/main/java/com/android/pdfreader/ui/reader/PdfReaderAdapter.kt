package com.android.pdfreader.ui.reader

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.android.pdfreader.databinding.RowPageItemBinding

internal class PdfReaderAdapter(
    private val bitmapList: List<Bitmap>
) : RecyclerView.Adapter<PdfReaderAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: RowPageItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(page: Bitmap) {
            binding.page.setImageBitmap(page)

            binding.root.setOnClickListener {
                val directions = PdfReaderFragmentDirections
                val action = directions.actionPdfReaderFragmentToPdfPageFragment(page)
                itemView.findNavController().navigate(action)
            }
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