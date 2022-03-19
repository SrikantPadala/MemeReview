package com.ztute.memereview.di

import com.ztute.memereview.database.MemeDao
import com.ztute.memereview.domain.repository.MemeRepository
import com.ztute.memereview.network.DefaultMemeRepository
import com.ztute.memereview.network.MemeReviewApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import javax.inject.Singleton

@Module
@InstallIn(FragmentComponent::class)
object FragmentModule {
    @Provides
    @Singleton
    fun provideMemeRepository(
        memeDao: MemeDao,
        memeReviewApiService: MemeReviewApiService
    ) = DefaultMemeRepository(memeDao, memeReviewApiService) as MemeRepository
}