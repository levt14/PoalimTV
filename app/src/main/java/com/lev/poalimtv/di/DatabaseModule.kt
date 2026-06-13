package com.lev.poalimtv.di

import android.content.Context
import androidx.room.Room
import com.lev.poalimtv.data.local.AppDatabase
import com.lev.poalimtv.data.local.FavoritesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "poalimtv.db",
        ).build()

    @Provides
    @Singleton
    fun provideFavoritesDao(database: AppDatabase): FavoritesDao =
        database.favoritesDao()
}
