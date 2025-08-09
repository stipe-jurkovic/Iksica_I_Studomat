package com.iksica.myapplication.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.iksica.myapplication.navigation.MainCompose
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.navigation.Home
import com.tstudioz.fax.fme.navigation.Iksica
import com.tstudioz.fax.fme.navigation.Studomat
import com.tstudioz.fax.fme.navigation.TopLevelRoute
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.KoinAndroidContext

class MainActivity : ComponentActivity() {
    @OptIn(InternalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val topLevelRoutesIksicaAndStudomat = listOf(
            TopLevelRoute(R.string.tab_iksica, Iksica, R.drawable.icon_iksica),
            TopLevelRoute(R.string.tab_home, Home, R.drawable.icon_home),
            TopLevelRoute(R.string.tab_studomat, Studomat, R.drawable.icon_studomat),
        )
        setContent {
            MaterialTheme {
                KoinAndroidContext {
                    MainCompose(
                        startDestination = Home, topLevelRoutes = topLevelRoutesIksicaAndStudomat
                    )
                }
            }
        }
    }
}