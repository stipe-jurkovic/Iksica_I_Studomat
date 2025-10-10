package com.iksica.myapplication.feature.zgMeni.di

import com.iksica.myapplication.feature.zgMeni.ZgMeniViewModel
import com.iksica.myapplication.feature.zgMeni.repository.ZgMeniRepository
import com.iksica.myapplication.feature.zgMeni.services.ZgMeniService
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


val zgMeniModule = module {
    single(named("trustful")) { getZgMeniClient() }
    single { ZgMeniService(get(named("trustful"))) }
    single { ZgMeniRepository(get()) }
    viewModel { ZgMeniViewModel(get()) }
}

var trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return arrayOf()
    }
}
)

fun getZgMeniClient(): OkHttpClient {

    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, SecureRandom())

    val newBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
    newBuilder.sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
    newBuilder.hostnameVerifier { hostname: String?, session: SSLSession? -> true }

    return newBuilder.followRedirects(false).build()
}