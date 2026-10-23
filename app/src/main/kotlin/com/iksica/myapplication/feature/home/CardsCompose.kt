package com.iksica.myapplication.feature.home

import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iksica.myapplication.HomeViewModel
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.feature.home.sidePadding
import com.tstudioz.fax.fme.feature.iksica.compose.angledGradientBackground
import com.tstudioz.fax.fme.theme.lust
import com.tstudioz.fax.fme.theme.meniColor
import com.tstudioz.fax.fme.util.PreferenceHelper.get
import com.tstudioz.fax.fme.util.SPKey
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.compose.koinInject

@OptIn(InternalCoroutinesApi::class)
@Composable
fun ManyCardsCompose(
    openStMenza: () -> Unit,
    openZgMenza: () -> Unit,
    homeViewModel: HomeViewModel
) {
    val shPrefs: SharedPreferences = koinInject<SharedPreferences>()
    val testingProfileActive = shPrefs[SPKey.TEST_MODE, false]
    val internetAvailable = homeViewModel.internetAvailable.observeAsState().value ?: false
    Row(Modifier.padding(horizontal = sidePadding)) {
        Column(Modifier.weight(0.5f)) {
            CardCompose(
                stringResource(id = R.string.menza_title) + " ST",
                stringResource(id = R.string.menza_desc),
                if (internetAvailable) meniColor else Color.DarkGray,
                onClick = {
                    if (homeViewModel.internetAvailable.value == true || testingProfileActive) {
                        openStMenza()
                    }
                },
                height = 95.dp
            )
            Spacer(Modifier.height(10.dp))
            CardCompose(
                stringResource(id = R.string.menza_title) + " ZG",
                stringResource(id = R.string.menza_desc),
                if (internetAvailable) meniColor else Color.DarkGray,
                onClick = {
                    if (homeViewModel.internetAvailable.value == true || testingProfileActive) {
                        openZgMenza()
                    }
                },
                height = 95.dp
            )
        }
        Box(
            Modifier
                .weight(0.5f)
        ) {
            CardCompose(
                stringResource(id = R.string.ugovori_title),
                stringResource(id = R.string.ugovori_desc),
                MaterialTheme.colorScheme.secondaryContainer,
                lust,
                onClick = {
                    homeViewModel.launchStudentskiUgovoriApp()
                },
                ad = true
            )
        }
    }
}

@Composable
fun CardCompose(
    title: String,
    description: String,
    color1: Color,
    color2: Color = color1,
    onClick: () -> Unit = { },
    height: Dp = 200.dp,
    ad: Boolean = false
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .height(height)
            .angledGradientBackground(
                colors = listOf(color1, color2),
                degrees = 60f,
                true
            )
            .padding(15.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.titleSmall
            )
        }
        if (ad) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(stringResource(com.iksica.myapplication.R.string.oglas_ad))
            }
        }
    }
}