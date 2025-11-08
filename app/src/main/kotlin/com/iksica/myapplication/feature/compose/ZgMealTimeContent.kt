package com.iksica.myapplication.feature.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iksica.myapplication.feature.zgMeni.dataModels.Meal
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.feature.menza.models.MealTime

@Composable
fun ZgMealTimeContent(menza: Meal?, mealTime: MealTime) {

    if (menza == null) return

    val mealModifier = Modifier
        .padding(bottom = 16.dp)
        .fillMaxWidth()
    Column (
        Modifier
            .padding(bottom = 16.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(MaterialTheme.colorScheme.surfaceDim)
            .padding(24.dp, 8.dp)
            .fillMaxWidth()
    ) {
        Row(
            Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (mealTime == MealTime.LUNCH) stringResource(R.string.lunch_title)
                else stringResource(R.string.dinner_title),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        ZgMeniItem("Menu", menza.menu, mealModifier)
        ZgMeniItem("Vegeterijanski menu", menza.vegeMenu, mealModifier)
        ZgMeniItem("Izbor", menza.izbor, mealModifier)
        ZgMeniItem("Prilozi", menza.prilozi, mealModifier)
    }
}