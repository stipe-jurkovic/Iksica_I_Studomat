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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iksica.myapplication.HomeViewModel
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.lust
import com.tstudioz.fax.fme.compose.meniColor
import com.tstudioz.fax.fme.feature.home.view.sidePadding
import com.tstudioz.fax.fme.feature.iksica.compose.angledGradientBackground
import com.tstudioz.fax.fme.util.SPKey
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.compose.koinInject
import com.tstudioz.fax.fme.util.PreferenceHelper.get

@OptIn(InternalCoroutinesApi::class)
@Composable
fun ManyCardsCompose(openStMenza: () -> Unit, openZgMenza: () -> Unit, homeViewModel: HomeViewModel) {
    val shPrefs: SharedPreferences = koinInject<SharedPreferences>()
    val testingProfileActive = shPrefs[SPKey.TEST_MODE, false]
    Row(Modifier.padding(horizontal = sidePadding)) {
        Column(Modifier.weight(0.5f)) {
            val noInternetMenza = stringResource(R.string.no_internet_menza)
            CardCompose(
                stringResource(id = R.string.menza_title) + " ST",
                stringResource(id = R.string.menza_desc),
                meniColor,
                meniColor,
                onClick = {
                    if (homeViewModel.internetAvailable.value == true || testingProfileActive) {
                        openStMenza()
                    } else {
                        homeViewModel.showSnackbar(message = noInternetMenza)
                    }
                },
                100.dp
            )
            Spacer(Modifier.height(10.dp))
            CardCompose(
                stringResource(id = R.string.menza_title) + " ZG",
                stringResource(id = R.string.menza_desc),
                meniColor,
                meniColor,
                onClick = {
                    if (homeViewModel.internetAvailable.value == true || testingProfileActive) {
                        openZgMenza()
                    } else {
                        homeViewModel.showSnackbar(message = noInternetMenza)
                    }
                },
                100.dp
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
    color2: Color,
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