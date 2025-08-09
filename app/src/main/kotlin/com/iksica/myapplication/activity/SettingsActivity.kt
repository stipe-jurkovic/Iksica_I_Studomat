package com.iksica.myapplication.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.iksica.myapplication.SettingsCompose
import com.iksica.myapplication.navigation.SettingsRouter
import com.tstudioz.fax.fme.feature.settings.SettingsViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val router: SettingsRouter by inject()
    private val settingsViewModel : SettingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        router.register(this)

        onBack()
        routeToLoginListener()

        setContent {
            SettingsCompose(router = router)
        }
    }

    private fun routeToLoginListener() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsViewModel.routeToLogin.collect {
                    if (it) {
                        router.routeToLogin()
                    }
                }
            }
        }
    }

    private fun onBack() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                router.popToHome()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                router.popToHome()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}