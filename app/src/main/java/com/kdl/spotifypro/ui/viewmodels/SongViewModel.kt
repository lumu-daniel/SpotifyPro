package com.kdl.spotifypro.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kdl.spotifypro.exoplayer.MusicService
import com.kdl.spotifypro.exoplayer.MusicServiceConnection
import com.kdl.spotifypro.exoplayer.currentPlaybackPosition
import com.kdl.spotifypro.other.Constants.UPDATE_PLAYER_POSITION_INTERVAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    musicServiceConnection: MusicServiceConnection
): ViewModel() {

    private val playbackState = musicServiceConnection.playbackState

    private val _curSongDuration = MutableLiveData<Long>()
    val curSongDuration : LiveData<Long> = _curSongDuration

    private val _curPlayerPosition = MutableLiveData<Long?>()
    val curPlayerPosition : LiveData<Long?> = _curPlayerPosition

    init {
        updateCurrentPlayerPosition()
    }

    private fun updateCurrentPlayerPosition(){
        viewModelScope.launch {
            while (true){
                val pos = playbackState.value?.currentPlaybackPosition
                if(curPlayerPosition.value != pos){
                    _curPlayerPosition.postValue(pos)
                    _curSongDuration.postValue(MusicService.currentSongDuration)
                    delay(UPDATE_PLAYER_POSITION_INTERVAL)
                }
            }
        }
    }
}