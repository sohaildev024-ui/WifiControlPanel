package com.wificontrolpanel.di

import com.wificontrolpanel.data.repository.RouterRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRouterRepository(): RouterRepository {
        return RouterRepository()
    }
}