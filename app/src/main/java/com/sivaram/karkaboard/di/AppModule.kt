package com.sivaram.karkaboard.di

import android.content.Context
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.data.remote.repo.DatabaseRepositoryImpl
import com.sivaram.karkaboard.ui.applicationportal.repo.ApplicationPortalRepo
import com.sivaram.karkaboard.ui.applicationportal.repo.ApplicationPortalRepoImpl
import com.sivaram.karkaboard.ui.auth.repo.AuthRepository
import com.sivaram.karkaboard.ui.auth.repo.AuthRepositoryImpl
import com.sivaram.karkaboard.ui.base.NetworkConnectivityService
import com.sivaram.karkaboard.ui.managebatches.repo.ManageBatchesRepo
import com.sivaram.karkaboard.ui.managebatches.repo.ManageBatchesRepoImpl
import com.sivaram.karkaboard.ui.managestaffs.repo.ManageStaffRepo
import com.sivaram.karkaboard.ui.managestaffs.repo.ManageStaffRepoImpl
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
    fun provideAuthRepository(): AuthRepository = AuthRepositoryImpl()

    @Provides
    @Singleton
    fun provideDatabaseRepository(): DatabaseRepository = DatabaseRepositoryImpl()

    @Provides
    @Singleton
    fun provideNetworkService(@ApplicationContext context: Context ): NetworkConnectivityService = NetworkConnectivityService(context)

    @Provides
    @Singleton
    fun provideManageStaffRepo(): ManageStaffRepo = ManageStaffRepoImpl()

    @Provides
    @Singleton
    fun provideManageBatchesRepo(): ManageBatchesRepo = ManageBatchesRepoImpl()

    @Provides
    @Singleton
    fun provideApplicationPortalRepo(): ApplicationPortalRepo = ApplicationPortalRepoImpl()
}