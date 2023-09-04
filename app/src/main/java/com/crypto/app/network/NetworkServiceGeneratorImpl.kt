package com.crypto.app.network

import retrofit2.Retrofit

internal class NetworkServiceGeneratorImpl(
    private val retrofit: Retrofit
) : NetworkServiceGenerator {

    override fun <ServiceType : Any> create(
        remoteService: Class<ServiceType>
    ): ServiceType =
        retrofit.create(remoteService)
}