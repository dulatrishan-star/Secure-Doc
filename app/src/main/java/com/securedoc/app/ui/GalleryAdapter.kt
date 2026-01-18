package com.securedoc.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.securedoc.app.databinding.ItemImageBinding
import com.securedoc.app.drive.DriveImage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class GalleryAdapter(
    private val onItemClick: (DriveImage) -> Unit,
    private val onLoadImage: suspend (String) -> ByteArray,
    private val watermarkProvider: () -> String
) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    private val items = mutableListOf<DriveImage>()
    private val scope = MainScope()

    fun submitList(newItems: List<DriveImage>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        scope.cancel()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding = ItemImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class GalleryViewHolder(
        private val binding: ItemImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: DriveImage) {
            binding.imageName.text = image.name
            binding.root.setOnClickListener { onItemClick(image) }
            binding.watermarkView.updateWatermark(watermarkProvider())

            scope.launch {
                val bytes = onLoadImage(image.id)
                if (bytes.isNotEmpty()) {
                    Glide.with(binding.root)
                        .load(bytes)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(false)
                        .into(binding.imageThumbnail)
                }
            }
        }
    }
}
