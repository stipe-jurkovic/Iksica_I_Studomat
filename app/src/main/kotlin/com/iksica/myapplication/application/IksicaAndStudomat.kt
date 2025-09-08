package com.iksica.myapplication.application

import android.app.Application
import android.content.SharedPreferences
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.iksica.myapplication.HomeViewModel
import com.iksica.myapplication.LoginViewModel
import com.iksica.myapplication.navigation.AppRouter
import com.iksica.myapplication.navigation.HomeRouter
import com.iksica.myapplication.navigation.LoginRouter
import com.iksica.myapplication.navigation.Router
import com.iksica.myapplication.navigation.SettingsRouter
import com.tstudioz.fax.fme.database.AppDatabase
import com.tstudioz.fax.fme.di.getRoomDatabase
import com.tstudioz.fax.fme.di.getSharedPreferences
import com.tstudioz.fax.fme.di.provideOkHttpClient
import com.tstudioz.fax.fme.feature.home.di.homeModule
import com.tstudioz.fax.fme.feature.iksica.di.iksicaModule
import com.tstudioz.fax.fme.feature.login.di.loginModule
import com.tstudioz.fax.fme.feature.menza.di.menzaModule
import com.tstudioz.fax.fme.feature.settings.SettingsViewModel
import com.tstudioz.fax.fme.feature.studomat.di.studomatModule
import com.tstudioz.fax.fme.networking.InternetConnectionObserver
import com.tstudioz.fax.fme.networking.cookies.MonsterCookieJar
import com.tstudioz.fax.fme.networking.session.SessionDelegate
import com.tstudioz.fax.fme.networking.session.SessionDelegateInterface
import com.tstudioz.fax.fme.util.PreferenceHelper.get
import com.tstudioz.fax.fme.util.SPKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.binds
import org.koin.dsl.module

class IksicaAndStudomat : Application() {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    @OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()

        InternetConnectionObserver.init(this)

        startKoin {
            androidLogger(level = Level.ERROR)
            androidContext(this@IksicaAndStudomat)
            modules(
                iksicaModule,
                loginModule,
                homeModule,
                menzaModule,
                studomatModule,
                appModule,// needs to be last as it overrides some dependencies
            )
        }
        observeUserDeleted()
    }

    private fun observeUserDeleted() {
        val sessionDelegate: SessionDelegateInterface by inject()
        val router: AppRouter by inject()
        val sharedPreferences: SharedPreferences by inject()

        scope.launch(Dispatchers.Main) {
            sessionDelegate.onUserDeleted
                .collect {
                    if (sharedPreferences[SPKey.LOGGED_IN, false]) {
                        router.routeToLogin()
                    }
                }
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
    single { MonsterCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(androidContext())) }
    single<OkHttpClient> { provideOkHttpClient(get()) }
    single<SessionDelegateInterface> { SessionDelegate(get(), get()) }
    factory<AppDatabase> { getRoomDatabase(get(), DATABASE_NAME) }
    single<SharedPreferences> { getSharedPreferences(androidContext()) }
    viewModel { SettingsViewModel(androidApplication(), get(), get()) }
    viewModel { HomeViewModel(androidApplication(), get(), get(), get()) }
    viewModel { LoginViewModel(androidApplication(), get(), get(), get(), get()) }
}

const val DATABASE_NAME = "iksicaAndStudomatDatabase"