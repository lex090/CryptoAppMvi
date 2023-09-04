package com.crypto.app.network


interface NetworkServiceGenerator {

    fun <ServiceType : Any> create(
        remoteService: Class<ServiceType>
    ): ServiceType
}