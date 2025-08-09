package com.iksica.myapplication.application

import android.app.Application
import com.iksica.myapplication.HomeViewModel
import com.iksica.myapplication.LoginViewModel
import com.iksica.myapplication.navigation.AppRouter
import com.iksica.myapplication.navigation.HomeRouter
import com.iksica.myapplication.navigation.LoginRouter
import com.iksica.myapplication.navigation.Router
import com.iksica.myapplication.navigation.SettingsRouter
import com.tstudioz.fax.fme.di.module
import com.tstudioz.fax.fme.feature.home.di.homeModule
import com.tstudioz.fax.fme.feature.iksica.di.iksicaModule
import com.tstudioz.fax.fme.feature.iksica.services.IksicaLoginServiceInterface
import com.tstudioz.fax.fme.feature.login.di.loginModule
import com.tstudioz.fax.fme.feature.menza.di.menzaModule
import com.tstudioz.fax.fme.feature.studomat.di.studomatModule
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.networking.InternetConnectionObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.binds
import org.koin.dsl.module
import kotlin.text.contains
import kotlin.text.substringAfter

class IksicaAndStudomat : Application() {

    @OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()

        InternetConnectionObserver.init(this)

        startKoin {
            androidLogger(level = Level.ERROR)
            androidContext(this@IksicaAndStudomat)
            modules(
                module,
                iksicaModule,
                loginModule,
                homeModule,
                menzaModule,
                studomatModule,
                appModule,
            )
        }
    }
}

@OptIn(InternalCoroutinesApi::class)
val appModule = module {
    single { Router(get()) } binds arrayOf(
        LoginRouter::class,
        SettingsRouter::class,
        HomeRouter::class,
        AppRouter::class
    )
    viewModel { HomeViewModel(androidApplication(), get(), get(), get()) }
    viewModel { LoginViewModel(androidApplication(), get(), get(), get()) }
    single<IksicaLoginServiceInterface> { IksicaLoginService(get(), null, "", "") }

}

class IksicaLoginService(
    private val client: OkHttpClient,
    private var currentUrl: HttpUrl?,
    private var authState: String,
    private var sAMLResponse: String
) : IksicaLoginServiceInterface {

    private var successfulLoginAlready: Boolean = false

    override suspend fun getAuthState(): NetworkServiceResult.IksicaResult {
        val request = Request.Builder()
            .url("https://issp.srce.hr/auth/loginaai")
            .build()

        val response = client.newCall(request).execute()
        val success = response.isSuccessful
        val body = response.body?.string() ?: ""
        val doc = Jsoup.parse(body)

        successfulLoginAlready = doc.selectFirst("div[class=onscript-msg]")
            ?.text()?.contains("Uspješno ste autenticirani.", true) == true

        response.close()

        if (successfulLoginAlready) {
            doc.select("input[name=SAMLResponse]").forEach { sAMLResponse = it.attr("value") }
            return NetworkServiceResult.IksicaResult.Success("Success")
        }

        if (!success || body.isEmpty()) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable("Failed to get AuthState"))
        }

        authState = response.request.url.queryParameter("AuthState") ?: ""
        currentUrl = response.request.url

        return NetworkServiceResult.IksicaResult.Success("Success")
    }

    override suspend fun login(email: String, password: String): NetworkServiceResult.IksicaResult {
        if (successfulLoginAlready) {
            successfulLoginAlready = false
            return NetworkServiceResult.IksicaResult.Success("Success login")
        }

        val formBody = FormBody.Builder()
            .add("username", email)
            .add("password", password)
            .add("AuthState", authState)
            .add("Submit", "")
            .build()

        val request = Request.Builder()
            .url(currentUrl!!)
            .post(formBody)
            .build()

        val response = client.newCall(request).execute()
        val doc = Jsoup.parse(response.body?.string() ?: "")
        sAMLResponse = doc.select("input[name=SAMLResponse]").attr("value")

        val content = doc.selectFirst("p.content_text")?.text()
        val submit = doc.selectFirst("button[type=submit]")?.text()
        val error = doc.selectFirst("div.error")?.text()

        if (content != null && content.contains("Uspješno", true)
            || submit != null && submit.contains("Nastavak", true)
        ) {
            return NetworkServiceResult.IksicaResult.Success("Success login")
        }

        if (error != null && error.contains("Greška", true)) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable(error))
        }

        return NetworkServiceResult.IksicaResult.Failure(Throwable("Failure login"))
    }

    override suspend fun getAspNetSessionSAML(): NetworkServiceResult.IksicaResult {
        val formBody = FormBody.Builder()
            .add("SAMLResponse", sAMLResponse)
            .add("Submit", "")
            .build()

        val request = Request.Builder()
            .url("https://issp.srce.hr/auth/prijavakorisnika")
            .post(formBody)
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: ""
        response.close()
        val doc = Jsoup.parse(body)
        val error = doc.selectFirst(".alert-danger")?.text()

        val name = doc.selectFirst("h2.card-title")?.text() ?: ""
        if (error != null && error.contains("Greška", true)) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable(error.substringAfter("error_outline ")))
        }

        if (!response.isSuccessful) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable("Failure getAspNetSessionSAML"))
        }

        return NetworkServiceResult.IksicaResult.Success(name)
    }
}