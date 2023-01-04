package com.kdl.spotifypro.exoplayer

import android.support.v4.media.MediaMetadataCompat
import com.kdl.spotifypro.data.entities.Song

fun MediaMetadataCompat.toSong(): Song? {
    return description?.let {
        Song(
            it.mediaId?:"",
            it.title.toString(),
            it.subtitle.toString(),
            it.mediaUri.toString(),
            it.iconUri.toString()
        )
    }
}