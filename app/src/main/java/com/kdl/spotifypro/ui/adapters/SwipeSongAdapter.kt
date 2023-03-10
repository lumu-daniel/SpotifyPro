package com.kdl.spotifypro.ui.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import com.kdl.spotifypro.R
import kotlinx.android.synthetic.main.list_item.view.*

class SwipeSongAdapter : BaseSongAdapter(R.layout.swipe_item){

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.itemView.apply {

            val text = "${song.title} - ${song.subtitle}"
            tvPrimary.text = text

            setOnClickListener{
                onItemClickListener?.let {
                    it(song)
                }
            }
        }
    }
}