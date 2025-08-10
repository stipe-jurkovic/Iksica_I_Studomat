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
import com.tstudioz.fax.fme.feature.login.di.loginModule
import com.tstudioz.fax.fme.feature.menza.di.menzaModule
import com.tstudioz.fax.fme.feature.studomat.di.studomatModule
import com.tstudioz.fax.fme.networking.InternetConnectionObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.binds
import org.koin.dsl.module

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
}