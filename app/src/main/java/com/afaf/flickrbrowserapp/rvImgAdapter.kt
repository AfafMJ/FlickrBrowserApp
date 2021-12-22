package com.afaf.flickrbrowserapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afaf.flickrbrowserapp.databinding.ImgRowBinding
import com.bumptech.glide.Glide


class rvImgAdapter(val activity: MainActivity, val photos: ArrayList<Image>): RecyclerView.Adapter<rvImgAdapter.ItemViewHolder>() {
    class ItemViewHolder(val binding: ImgRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(ImgRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val photo = photos[position]

        holder.binding.apply {
            tvName.text = photo.title
            Glide.with(activity).load(photo.link).into(pic)
            LinImgrow.setOnClickListener { activity.viewImg(photo.link) }
        }
    }

    override fun getItemCount() = photos.size
}