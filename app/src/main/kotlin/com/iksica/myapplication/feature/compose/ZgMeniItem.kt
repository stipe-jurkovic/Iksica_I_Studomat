package com.iksica.myapplication.feature.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iksica.myapplication.feature.zgMeni.dataModels.MenuItem
import com.tstudioz.fax.fme.feature.menza.view.MeniTextIksica

@Composable
fun ZgMeniItem(name: String, meni: List<MenuItem>, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Top, modifier = modifier
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 10.dp)
        )
        meni.forEachIndexed { index, it ->
            var toptext = ""
            if (it.weight.isNotEmpty()) toptext = it.weight + "g"
            if (it.weight.isNotEmpty() && it.allergens.isNotEmpty()) toptext = "$toptext | "
            if (it.allergens.isNotEmpty()) toptext = toptext + it.allergens
            MeniTextIksica(it.title, toptext, index != (meni.size - 1))
        }

        Spacer(Modifier.height(5.dp))
    }
}