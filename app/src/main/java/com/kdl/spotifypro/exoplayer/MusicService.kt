package com.kdl.spotifypro.exoplayer

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.Listener
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.kdl.spotifypro.exoplayer.callbacks.MusicPlaybackPreparer
import com.kdl.spotifypro.exoplayer.callbacks.MusicPlayerEventListener
import com.kdl.spotifypro.exoplayer.callbacks.MusicPlayerNotificationListener
import com.kdl.spotifypro.other.Constants.MEDIA_ROOT_ID
import com.kdl.spotifypro.other.Constants.NETWORK_ERROR
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

private const val SERVICE_TAG = "MusicService"

@AndroidEntryPoint
class MusicService: MediaBrowserServiceCompat() {

    private lateinit var notificationManager: MusicNotificationManager

    @Inject lateinit var dataSourceFactory: DefaultDataSourceFactory

    @Inject lateinit var exoPlayer: SimpleExoPlayer

    @Inject lateinit var musicSource: FirebaseMusicSource

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    var isForegroundService = false

    private var currentSong : MediaMetadataCompat? = null

    private var isPlayerInitialized:Boolean = false

    private lateinit var musicPlayerEventListener: Listener

    companion object{
        var currentSongDuration = 0L
            private set
    }

    override fun onCreate() {
        super.onCreate()
        serviceScope.launch {
            musicSource.fetchMediaData()
        }
        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0,it, 0)
        }

        mediaSession = MediaSessionCompat(this,SERVICE_TAG).apply {
            setSessionActivity(activityIntent)
            isActive = true
        }

        sessionToken = mediaSession.sessionToken

        notificationManager = MusicNotificationManager(
            this,
            mediaSession.sessionToken,
            MusicPlayerNotificationListener(this)
        ){
            currentSongDuration = exoPlayer.duration
        }

        val musicPlaybackPreparer = MusicPlaybackPreparer(musicSource){
            currentSong = it
            preparePlayer(
                musicSource.songs,
                it,
                true
            )
        }

        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(musicPlaybackPreparer)
        mediaSessionConnector.setQueueNavigator(MusicQueueNavigator())
        mediaSessionConnector.setPlayer(exoPlayer)

        musicPlayerEventListener = MusicPlayerEventListener(this)
        exoPlayer.addListener(musicPlayerEventListener)
        notificationManager.showNotification(exoPlayer)
    }

    private inner class MusicQueueNavigator: TimelineQueueNavigator(mediaSession){
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return musicSource.songs[windowIndex].description
        }

    }

    private fun preparePlayer(
        songs: List<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat?,
        playNow: Boolean
    ){
        val curSongIndex = if(currentSong==null) 0 else songs.indexOf(itemToPlay)
        exoPlayer.prepare(musicSource.asMediaSource(dataSourceFactory))
        exoPlayer.seekTo(curSongIndex,0L)
        exoPlayer.playWhenReady = playNow
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        exoPlayer.removeListener(musicPlayerEventListener)
        exoPlayer.release()
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return BrowserRoot(MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        when(parentId){
            MEDIA_ROOT_ID -> {
                val resultsSent = musicSource.whenReady {isInitialized ->
                    if(isInitialized){
                        result.sendResult(musicSource.asMediaItems())
                        if(isPlayerInitialized && musicSource.songs.isNotEmpty()){
                            preparePlayer(musicSource.songs, musicSource.songs[0],false)
                            isPlayerInitialized = true
                        }
                    }else{
                        mediaSession.sendSessionEvent(NETWORK_ERROR,null)
                        result.sendResult(null)
                    }
                }

                if(!resultsSent){
                    result.detach()
                }
            }
        }
    }

}