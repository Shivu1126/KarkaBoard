package com.sivaram.karkaboard.di

import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.data.remote.repo.DatabaseRepositoryImpl
import com.sivaram.karkaboard.ui.auth.repo.AuthRepository
import com.sivaram.karkaboard.ui.auth.repo.AuthRepositoryImpl
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
    fun provideAuthRepository(): AuthRepository = AuthRepositoryImpl()

    @Provides
    @Singleton
    fun provideDatabaseRepository(): DatabaseRepository = DatabaseRepositoryImpl()
}