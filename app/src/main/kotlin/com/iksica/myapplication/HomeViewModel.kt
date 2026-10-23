package com.iksica.myapplication

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.feature.attendance.models.Note
import com.tstudioz.fax.fme.feature.home.models.WeatherDisplay
import com.tstudioz.fax.fme.feature.home.repository.NoteRepositoryInterface
import com.tstudioz.fax.fme.feature.home.repository.WeatherRepositoryInterface
import com.tstudioz.fax.fme.networking.InternetConnectionObserver
import com.tstudioz.fax.fme.user.UserRepositoryInterface
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.Locale


@InternalCoroutinesApi
class HomeViewModel(
    application: Application,
    private val noteRepository: NoteRepositoryInterface,
    private val weatherRepository: WeatherRepositoryInterface,
    private val userRepository: UserRepositoryInterface,
) : AndroidViewModel(application) {

    val internetAvailable: LiveData<Boolean> = InternetConnectionObserver.get()

    private val _error = MutableSharedFlow<String?>()
    val error: SharedFlow<String?> = _error

    private suspend fun showSnackbar(message: String) {
        _error.emit(message)
    }

    private val _weatherDisplay = MutableLiveData<WeatherDisplay>()
    private val _notes = MutableLiveData<List<Note>>()
    val nameOfUser = MutableLiveData<String>()
    val weatherDisplay: LiveData<WeatherDisplay> = _weatherDisplay
    val notes: LiveData<List<Note>> = _notes

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.d("HomeViewModel", "Caught $exception")
        viewModelScope.launch(Dispatchers.Main) {
            showSnackbar(
                getApplication<Application>().applicationContext.getString(
                    R.string.general_error
                )
            )
        }
    }

    init {
        getNotes()
        getForecast()
        loadUsersName()
    }

    private fun getForecast() {
        if (internetAvailable.value == false) return
        viewModelScope.launch(Dispatchers.IO + handler) {
            try {
                weatherRepository.fetchWeatherDetails()?.let { _weatherDisplay.postValue(it) }
            } catch (e: Exception) {
                showSnackbar(
                    getApplication<Application>().applicationContext.getString(
                        R.string.weather_error
                    )
                )
            }
        }
    }

    fun insert(note: Note) {
        if (_notes.value?.any { it.id == note.id } == true)
            _notes.value?.map {
                if (it.id == note.id) {
                    it.checked = note.checked
                }
            }
        else {
            _notes.value = _notes.value?.plus(note)
        }
        viewModelScope.launch(Dispatchers.IO + handler) {
            noteRepository.insert(note)
        }
    }

    fun delete(note: Note) {
        _notes.value = _notes.value?.minus(note)
        viewModelScope.launch(Dispatchers.IO + handler) {
            noteRepository.delete(note)
        }
    }

    private fun getNotes() {
        viewModelScope.launch(Dispatchers.IO + handler) {
            val notes = noteRepository.getNotes()
            _notes.postValue(notes)
        }
    }

    fun launchStudentskiUgovoriApp() {
        val context = getApplication<Application>().applicationContext
        val appPackageName = "com.ugovori.studentskiugovori"
        val intent = context.packageManager.getLaunchIntentForPackage(appPackageName) ?: Intent(
            Intent.ACTION_VIEW,
            "https://play.google.com/store/apps/details?id=$appPackageName".toUri()
        )
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun loadUsersName() {
        viewModelScope.launch(Dispatchers.IO + handler) {
            val name = userRepository.getCurrentUser().fullName.split(" ").firstOrNull() ?: ""
            nameOfUser.postValue(name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                else it.toString()
            })
        }
    }
}