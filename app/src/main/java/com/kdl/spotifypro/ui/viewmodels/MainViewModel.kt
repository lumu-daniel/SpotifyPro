package com.kdl.spotifypro.ui.viewmodels

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kdl.spotifypro.data.entities.Song
import com.kdl.spotifypro.exoplayer.MusicServiceConnection
import com.kdl.spotifypro.exoplayer.isPlayEnabled
import com.kdl.spotifypro.exoplayer.isPlaying
import com.kdl.spotifypro.exoplayer.isPrepared
import com.kdl.spotifypro.other.Constants.MEDIA_ROOT_ID
import com.kdl.spotifypro.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
): ViewModel() {

    private val _mediaItems = MutableLiveData<Resource<List<Song>>>()
    val mediaItems: LiveData<Resource<List<Song>>> = _mediaItems

    val isConnected = musicServiceConnection.isConnected
    val networkError = musicServiceConnection.networkError
    val curPlayingSong = musicServiceConnection.curPlayingSong
    val playbackState = musicServiceConnection.playbackState

    init {
        _mediaItems.postValue(Resource.loading(null))
        musicServiceConnection.subscribe(MEDIA_ROOT_ID,object : MediaBrowserCompat.SubscriptionCallback(){
            override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
                super.onChildrenLoaded(parentId, children)
                val itemsList = children.map {
                    Song(
                        it.mediaId!!,
                        it.description.title.toString(),
                        it.description.subtitle.toString(),
                        it.description.mediaUri.toString(),
                        it.description.iconUri.toString()
                    )
                }
                _mediaItems.postValue(Resource.success(itemsList))
            }
        })
    }

    fun skipToNextSong() {
        musicServiceConnection.transportControls.skipToNext()
    }

    fun skipToPreviousSong() {
        musicServiceConnection.transportControls.skipToPrevious()
    }

    fun seekTo(pos: Long) {
        musicServiceConnection.transportControls.seekTo(pos)
    }

    fun playOrToggleSong(mediaItem: Song, toggle: Boolean = false){
        val isPrepared = playbackState.value?.isPrepared?:false
        if(isPrepared && mediaItem.mediaId == curPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)){
            playbackState.value?.let {
                when{
                    it.isPlaying -> if(toggle) musicServiceConnection.transportControls.pause()
                    it.isPlayEnabled -> musicServiceConnection.transportControls.play()
                    else -> Unit
                }
            }
        }else{
            musicServiceConnection.transportControls.playFromMediaId(mediaItem.mediaId,null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unSubscribe(MEDIA_ROOT_ID,object : MediaBrowserCompat.SubscriptionCallback(){})
    }

}