package com.example.bug_it.di

import com.example.bug_it.data.repository.BugRepositoryImpl
import com.example.bug_it.domain.repository.BugRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideBugRepository(
        bugRepositoryImpl: BugRepositoryImpl
    ): BugRepository = bugRepositoryImpl
} 