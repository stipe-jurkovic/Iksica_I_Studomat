package com.iksica.myapplication

import android.app.Application
import android.content.SharedPreferences
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.common.user.models.User
import com.tstudioz.fax.fme.common.user.models.UserRoom
import com.tstudioz.fax.fme.feature.iksica.services.IksicaLoginServiceInterface
import com.tstudioz.fax.fme.feature.login.dao.UserDao
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.networking.cookies.MonsterCookieJar
import com.tstudioz.fax.fme.util.PreferenceHelper.get
import com.tstudioz.fax.fme.util.PreferenceHelper.set
import com.tstudioz.fax.fme.util.SPKey
import com.tstudioz.fax.fme.util.SingleLiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

@InternalCoroutinesApi
class LoginViewModel(
    private val application: Application,
    private val sharedPreferences: SharedPreferences,
    private val iksicaLoginService: IksicaLoginServiceInterface,
    private val userDao: UserDao
) : AndroidViewModel(application) {

    var email = MutableLiveData("")
    var password = MutableLiveData("")
    val showLoading = MutableLiveData(false)
    val snackbarHostState: SnackbarHostState = SnackbarHostState()
    var passwordHidden = MutableLiveData(true)
    val monsterCookieJar: MonsterCookieJar by inject<MonsterCookieJar>(MonsterCookieJar::class.java)

    var firstTimeInApp = MutableLiveData(false)
        private set

    var loggedIn = SingleLiveEvent<Unit>()
        private set

    private val handler = CoroutineExceptionHandler { _, exception ->
        showSnackbar(application.getString(R.string.login_error_generic))
    }

    fun tryUserLogin() {
        var email = email.value?.trim()?.lowercase()
        val password = password.value?.trim()

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            showSnackbar(application.getString(R.string.login_error_empty_credentials))
            return
        }
        showLoading.value = true

        viewModelScope.launch(Dispatchers.IO + handler) {
            monsterCookieJar.clear()
            iksicaLoginService.getAuthState()
            iksicaLoginService.login(email, password)
            val success = iksicaLoginService.getAspNetSessionSAML()

            when (success) {
                is NetworkServiceResult.IksicaResult.Success -> {
                    if (success.data != "" && success.data.split(" ").size == 2) {
                        loggedIn.postValue(Unit)
                        userDao.insert(UserRoom(User(success.data, email, password)))
                        sharedPreferences[SPKey.LOGGED_IN] = true
                    } else {
                        showSnackbar(application.getString(R.string.login_error_invalid_credentials))
                    }
                }

                is NetworkServiceResult.IksicaResult.Failure -> {
                    showSnackbar(application.getString(R.string.login_error_invalid_credentials))
                }
            }
            showLoading.postValue(false)
        }
    }

    private fun showSnackbar(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            snackbarHostState.showSnackbar(message)
        }
    }

    fun checkIfFirstTimeInApp() {
        firstTimeInApp.value = sharedPreferences[SPKey.FIRST_TIME, true]
        sharedPreferences[SPKey.FIRST_TIME] = false
    }

    fun checkIfLoggedIn() {
        if (sharedPreferences[SPKey.LOGGED_IN, false]) {
            loggedIn.value = Unit
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}
