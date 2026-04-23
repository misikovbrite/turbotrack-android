package com.britetodo.turbotrack.services

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    @Named("aviation")
    fun provideAviationRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://aviationweather.gov/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    @Named("openmeteo")
    fun provideOpenMeteoRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    @Named("aerodatabox")
    fun provideAeroDataBoxRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://aerodatabox.p.rapidapi.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideAviationWeatherService(@Named("aviation") retrofit: Retrofit): AviationWeatherService =
        retrofit.create(AviationWeatherService::class.java)

    @Provides
    @Singleton
    fun provideOpenMeteoService(@Named("openmeteo") retrofit: Retrofit): OpenMeteoService =
        retrofit.create(OpenMeteoService::class.java)

    @Provides
    @Singleton
    fun provideFlightNumberApiService(@Named("aerodatabox") retrofit: Retrofit): FlightNumberApiService =
        retrofit.create(FlightNumberApiService::class.java)
}
