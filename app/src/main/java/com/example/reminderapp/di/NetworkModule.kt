package com.example.reminderapp.di

import com.example.reminderapp.data.api.BilsoftApiService
import com.example.reminderapp.data.local.AppDatabase
import com.example.reminderapp.data.local.ReminderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://apiv3.bilsoft.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideBilsoftApiService(retrofit: Retrofit): BilsoftApiService {
        return retrofit.create(BilsoftApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideAppDatabase(): AppDatabase {
        // This will be provided by the Application class
        throw UnsupportedOperationException("AppDatabase should be provided by Application class")
    }
    
    @Provides
    @Singleton
    fun provideReminderDao(database: AppDatabase): ReminderDao {
        return database.reminderDao()
    }
}
