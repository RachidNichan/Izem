package com.relyvo.izem.di

import android.content.Context
import com.relyvo.izem.data.AuthRepo
import com.relyvo.izem.data.FirestoreRepo
import com.relyvo.izem.data.SettingsRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideFirestoreRepo(): FirestoreRepo = FirestoreRepo()

    @Provides
    @Singleton
    fun provideAuthRepo(): AuthRepo = AuthRepo()

    @Provides
    @Singleton
    fun provideSettingsRepo(@ApplicationContext context: Context): SettingsRepo = SettingsRepo(context)
}
