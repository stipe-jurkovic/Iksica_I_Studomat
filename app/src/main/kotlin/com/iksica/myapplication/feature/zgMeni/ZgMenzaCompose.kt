package com.iksica.myapplication.feature.zgMeni

  import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import com.iksica.myapplication.feature.compose.GlideImageWithCache
import com.iksica.myapplication.feature.compose.ZgMeniCompose
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.BalloonIndicatorType
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(ExperimentalMaterial3Api::class, InternalCoroutinesApi::class)
@Composable
fun ZgMenzaCompose(zgMenzaViewModel: ZgMeniViewModel, paddingValues: PaddingValues) {

    val lifecycleState = LocalLifecycleOwner.current.lifecycle.currentStateAsState().value
    val menzas = zgMenzaViewModel.menies.observeAsState().value
    val zgMenzaLocations = zgMenzaViewModel.locationData.observeAsState().value

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        val pageCount = zgMenzaLocations?.size
        if (pageCount == null) return@Surface
        val state = rememberPagerState(
            initialPage = (pageCount.div(2)),
            pageCount = { pageCount }
        )
        DisposableEffect(lifecycleState) {
            onDispose {
                zgMenzaViewModel.closeMenza()
            }
        }
        Column {
            Row(
                horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(0.dp, 12.dp, 0.dp, 12.dp)
            ) {
                DotsIndicator(
                    dotCount = pageCount,
                    type = BalloonIndicatorType(
                        dotsGraphic = DotGraphic(
                            color = Color.White,
                            size = 6.dp
                        ),
                        balloonSizeFactor = 1.7f
                    ),
                    dotSpacing = 20.dp,
                    pagerState = state,
                )
            }
            HorizontalPager(state, pageSpacing = 16.dp) {
                BackHandler {
                    zgMenzaViewModel.closeMenza()
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .background(MaterialTheme.colorScheme.background),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val specificLocation = zgMenzaLocations[it]
                    GlideImageWithCache(url = specificLocation.image_url, contentDescription = "Meni location image")
                    ZgMeniCompose(menzas?.filter { it.id == specificLocation.id }, specificLocation)
                }
            }
        }
    }
}