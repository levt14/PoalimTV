package com.lev.poalimtv.di

import com.lev.poalimtv.data.repository.MovieRepository
import com.lev.poalimtv.data.repository.MovieRepositoryImpl
import com.lev.poalimtv.data.repository.TvRepository
import com.lev.poalimtv.data.repository.TvRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(impl: MovieRepositoryImpl): MovieRepository

    @Binds
    @Singleton
    abstract fun bindTvRepository(impl: TvRepositoryImpl): TvRepository
}
