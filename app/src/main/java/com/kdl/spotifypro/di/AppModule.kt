package com.kdl.spotifypro.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kdl.spotifypro.R
import com.kdl.spotifypro.exoplayer.MusicServiceConnection
import com.kdl.spotifypro.ui.adapters.SwipeSongAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSwipeSongAdapter() = SwipeSongAdapter()

    @Provides
    @Singleton
    fun provideMusicServiceConnection(@ApplicationContext context: Context) = MusicServiceConnection(context)

    @Singleton
    @Provides
    fun provideGlideInstance(
      @ApplicationContext  context:Context
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    )
}