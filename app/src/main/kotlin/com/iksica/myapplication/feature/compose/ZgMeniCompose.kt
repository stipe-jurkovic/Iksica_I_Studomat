package com.iksica.myapplication.feature.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import com.iksica.myapplication.feature.zgMeni.dataModels.MenuResponse
import com.iksica.myapplication.feature.zgMeni.dataModels.Post
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.feature.menza.models.MealTime

@Composable
fun ZgMeniCompose(meni: List<MenuResponse>?, location: Post) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            //.clip(RoundedCornerShape(30.dp, 30.dp, 0.dp, 0.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(16.dp)
            .heightIn(min = screenHeight.times(0.7f))
            .fillMaxWidth()
    ) {
        Text(
            text = location.title.rendered.drop(3).dropLast(4),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(8.dp, 8.dp, 0.dp, 0.dp)
        )
        Spacer(Modifier.height(12.dp))
        location.meta.restaurant_info?.forEach {
            Text(
                text = it.title,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(12.dp, 0.dp, 0.dp, 4.dp)
            )
        }
        meni?.sortedBy { it.slug }?.forEach {
            Text(
                text = HtmlCompat.fromHtml(it.title.rendered, HtmlCompat.FROM_HTML_MODE_LEGACY).toString(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp, 8.dp, 0.dp, 8.dp)
            )
            val menuProducts = it.meta.menuProducts
            ZgMealTimeContent(menuProducts?.rucak, MealTime.LUNCH)
            ZgMealTimeContent(menuProducts?.vecera, MealTime.DINNER)
        }
        if (meni.isNullOrEmpty()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.no_data_icon),
                    contentDescription = stringResource(R.string.page_not_found),
                    modifier = Modifier
                        .padding(12.dp, 80.dp, 12.dp, 12.dp)
                        .size(80.dp)
                )
                Text(stringResource(R.string.menza_no_data))
            }
        }
    }
}