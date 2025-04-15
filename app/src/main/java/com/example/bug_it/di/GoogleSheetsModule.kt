package com.example.bug_it.di

import android.content.Context
import com.example.bug_it.BuildConfig
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.ktor.client.plugins.HttpTimeout
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleSheetsModule {

    @Provides
    @Singleton
    fun provideGoogleCredentials(
        @ApplicationContext context: Context
    ): GoogleCredentials {
        val inputStream = context.assets.open("credentials.json")
        return GoogleCredentials.fromStream(inputStream)
            .createScoped(listOf("https://www.googleapis.com/auth/spreadsheets"))
    }

    @Provides
    @Singleton
    fun provideSheetsService(credentials: GoogleCredentials): Sheets {
        return Sheets.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            HttpCredentialsAdapter(credentials)
        )
            .setApplicationName("BugIt")
            .build()
    }

    @OptIn(SupabaseInternal::class)
    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.supabaseUrl,
            supabaseKey = BuildConfig.supabaseKey
        ) {
            httpConfig {
                install(Storage)
                install(HttpTimeout) {
                    requestTimeoutMillis = 30000
                }
            }
        }
    }
} 