package com.example.flickrbrowserapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_row.view.*

class RVPhoto(var photos: ArrayList<Photo>, private val mainContext: Context):
    RecyclerView.Adapter<RVPhoto.ItemViewHolder>() {
    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_row,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var photo = photos[position]

        holder.itemView.apply {
            Glide.with(mainContext)
                .load(photo.url("t"))
                .into(ivPhotoThumbnail)
            tvTitle.text = photo.title

            rvLayout.setOnClickListener {
                val intent = Intent(mainContext, ShowPhoto::class.java)
                intent.putExtra("url", photo.url())
                mainContext.startActivity(intent)
            }
        }
    }

    override fun getItemCount() = photos.size
}