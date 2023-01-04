package com.kdl.spotifypro.ui.fragments

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.kdl.spotifypro.R
import com.kdl.spotifypro.data.entities.Song
import com.kdl.spotifypro.exoplayer.isPlaying
import com.kdl.spotifypro.exoplayer.toSong
import com.kdl.spotifypro.other.Status.SUCCESS
import com.kdl.spotifypro.ui.viewmodels.MainViewModel
import com.kdl.spotifypro.ui.viewmodels.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_song.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment:Fragment(R.layout.fragment_song) {

    @Inject
    lateinit var glide: RequestManager

    private lateinit var mainViewModel: MainViewModel
    private val songViewModel: SongViewModel by viewModels()

    private var curPlayingSong: Song? = null

    private var playbackState: PlaybackStateCompat? = null

    private var shouldUpdateSeekBar = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        subscribeToObservers()

        ivPlayPauseDetail.setOnClickListener{
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it,true)
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(p2){
                    setCurrentPlayerTimeToTestView(p1.toLong())
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                shouldUpdateSeekBar = false
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                seekBar?.let {
                    mainViewModel.seekTo(it.progress.toLong())
                    shouldUpdateSeekBar = true
                }
            }

        })

        ivSkipPrevious.setOnClickListener{
            mainViewModel.skipToPreviousSong()
        }

        ivSkip.setOnClickListener{
            mainViewModel.skipToNextSong()
        }
    }

    private fun updateTitleAndSongImage(song:Song){
        val title = "${song.title} - ${song.subtitle}"
        tvSongName.text = title
        glide.load(song.imageUrl).into(ivSongImage)
    }

    private fun subscribeToObservers(){
        mainViewModel.mediaItems.observe(viewLifecycleOwner){
            it?.let {
                when(it.status){
                    SUCCESS ->{
                        it.data?.let { songs ->
                            if(curPlayingSong==null && songs.isNotEmpty()){
                                curPlayingSong = songs[0]
                                updateTitleAndSongImage(curPlayingSong!!)
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }

        mainViewModel.curPlayingSong.observe(viewLifecycleOwner){
            if(it==null) return@observe
            curPlayingSong = it.toSong()
            updateTitleAndSongImage(curPlayingSong!!)
        }

        mainViewModel.playbackState.observe(viewLifecycleOwner){
            playbackState = it
            ivPlayPauseDetail.setImageResource(
                if(playbackState?.isPlaying==true)R.drawable.ic_pause else R.drawable.ic_play
            )
            seekBar.progress = it?.position?.toInt()?:0
        }

        songViewModel.curPlayerPosition.observe(viewLifecycleOwner){
            if(shouldUpdateSeekBar){
                seekBar.progress = it!!.toInt()
                setCurrentPlayerTimeToTestView(it)
            }
        }

        songViewModel.curSongDuration.observe(viewLifecycleOwner){
            seekBar.max = it.toInt()
            val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
            tvSongDuration.text = dateFormat.format(it)
        }
    }

    private fun setCurrentPlayerTimeToTestView(ms: Long) {
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        tvCurTime.text = dateFormat.format(ms)
    }
}