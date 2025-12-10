package com.ait.oncue.di

import com.ait.oncue.data.OnCueRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOnCueRepository(): OnCueRepository {
        return OnCueRepository()
    }
}