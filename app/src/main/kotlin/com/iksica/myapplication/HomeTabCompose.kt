package com.iksica.myapplication

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.iksica.myapplication.feature.home.ManyCardsCompose
import com.iksica.myapplication.feature.zgMeni.ZgMeniViewModel
import com.iksica.myapplication.feature.zgMeni.ZgMenzaCompose
import com.iksica.myapplication.navigation.HomeRouter
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.database.models.Note
import com.tstudioz.fax.fme.feature.home.compose.NotesCompose
import com.tstudioz.fax.fme.feature.home.models.WeatherDisplay
import com.tstudioz.fax.fme.feature.home.view.WeatherCompose
import com.tstudioz.fax.fme.feature.menza.view.MenzaCompose
import com.tstudioz.fax.fme.feature.menza.view.MenzaViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class, InternalCoroutinesApi::class)
@Composable
fun HomeTabCompose(
    homeViewModel: HomeViewModel = koinViewModel(),
    menzaViewModel: MenzaViewModel = koinViewModel(),
    zgMenzaViewModel: ZgMeniViewModel = koinViewModel(),
    innerPaddingValues: PaddingValues,
    router: HomeRouter = koinInject<HomeRouter>(),
) {

    val weather: LiveData<WeatherDisplay> = homeViewModel.weatherDisplay
    val notes: LiveData<List<Note>> = homeViewModel.notes
    val insertNote: (note: Note) -> Unit = homeViewModel::insert
    val deleteNote: (note: Note) -> Unit = homeViewModel::delete

    AppTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = homeViewModel.snackbarHostState) },
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0.dp)
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxHeight()) {
                if (menzaViewModel.menzaOpened.observeAsState().value == true) {
                    MenzaCompose(menzaViewModel, innerPaddingValues)
                    return@Scaffold
                }
                if (zgMenzaViewModel.menzaOpened.observeAsState().value == true) {
                    ZgMenzaCompose(zgMenzaViewModel, innerPaddingValues)
                    return@Scaffold
                }
                LazyColumn(
                    Modifier
                        .padding(innerPaddingValues)
                        .padding(paddingValues)
                ) {
                    item {
                        Row(
                            Modifier
                                .height(54.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.settings_icon),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(top = 10.dp, end = 10.dp)
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        router.routeToSettings()
                                    }
                            )
                        }
                    }
                    item {
                        WeatherCompose(
                            weather.observeAsState().value,
                            homeViewModel.nameOfUser.observeAsState().value ?: ""
                        )
                    }
                    item {
                        NotesCompose(
                            notes = notes.observeAsState().value ?: emptyList(),
                            insertNote,
                            deleteNote
                        )
                    }
                    item {
                        ManyCardsCompose(
                            { menzaViewModel.openMenza() },
                            { zgMenzaViewModel.openMenza() },
                            homeViewModel
                        )
                    }
                }
            }
        }
    }
}