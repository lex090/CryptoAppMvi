package com.crypto.app.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.URL
import java.util.concurrent.TimeUnit


internal object NetworkModule {

    val networkServiceGenerator: NetworkServiceGenerator = NetworkServiceGeneratorImpl(
        retrofit = provideRetrofit(
            url = provideUrl(),
            okHttpClient = provideOkHttpClient(
                interceptor = provideLoggingInterceptor()
            ),
            converterFactory = provideConverterFactory(
                moshi = provideMoshi()
            )
        )
    )

    private fun provideRetrofit(
        url: URL,
        okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()

    private fun provideUrl(): URL =
        URL("https://api.coingecko.com/api/v3/")

    private fun provideOkHttpClient(
        interceptor: Interceptor
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addNetworkInterceptor(interceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .callTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

    private fun provideLoggingInterceptor(): Interceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    private fun provideMoshi(): Moshi =
        Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    private fun provideConverterFactory(
        moshi: Moshi
    ): Converter.Factory = MoshiConverterFactory.create(moshi)
}