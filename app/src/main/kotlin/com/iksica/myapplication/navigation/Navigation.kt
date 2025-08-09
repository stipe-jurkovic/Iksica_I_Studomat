package com.iksica.myapplication.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.iksica.myapplication.HomeTabCompose
import com.iksica.myapplication.HomeViewModel
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.feature.iksica.compose.IksicaCompose
import com.tstudioz.fax.fme.feature.iksica.view.IksicaViewModel
import com.tstudioz.fax.fme.feature.studomat.compose.StudomatCompose
import com.tstudioz.fax.fme.feature.studomat.view.StudomatViewModel
import com.tstudioz.fax.fme.navigation.Home
import com.tstudioz.fax.fme.navigation.Iksica
import com.tstudioz.fax.fme.navigation.NoInternetIcon
import com.tstudioz.fax.fme.navigation.Studomat
import com.tstudioz.fax.fme.navigation.TopLevelRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.koinViewModel

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun MainCompose(startDestination: Any, topLevelRoutes: List<TopLevelRoute<out Any>>) {
    val navController = rememberNavController()
    AppTheme {
        MainNavHost(
            navController = navController,
            startDestination = startDestination,
            topLevelRoutes = topLevelRoutes
        )
    }
}


@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun MainNavHost(
    navController: NavHostController,
    startDestination: Any,
    topLevelRoutes: List<TopLevelRoute<out Any>>,
    iksicaViewModel: IksicaViewModel = koinViewModel(),
    studomatViewModel: StudomatViewModel = koinViewModel(),
    homeViewModel: HomeViewModel = koinViewModel(),
) {
    val internetAvailable = homeViewModel.internetAvailable.observeAsState().value == true
    Scaffold(
        bottomBar = {
            MainBottomBar(
                navController = navController,
                topLevelRoutes = topLevelRoutes,
            )
        },
        floatingActionButton = {
            if (!internetAvailable) NoInternetIcon()
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                EnterTransition.None
            },
            exitTransition = {
                ExitTransition.None
            }) {
            composable<Iksica> {
                IksicaCompose(iksicaViewModel)
            }
            composable<Home> {
                HomeTabCompose(homeViewModel)
            }
            composable<Studomat> {
                StudomatCompose(studomatViewModel)
            }
        }
    }
}

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun MainBottomBar(
    navController: NavHostController,
    topLevelRoutes: List<TopLevelRoute<out Any>>
) {
    NavigationBar(
        contentColor = MaterialTheme.colorScheme.onSurface,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val currentDestinationTop =
            navController.currentBackStackEntryAsState().value?.destination?.route?.split(".")?.lastOrNull()
                ?: ""
        topLevelRoutes.forEach { topLevelRoute ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(topLevelRoute.iconId),
                        contentDescription = stringResource(topLevelRoute.nameId),
                        modifier = Modifier.size(30.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(topLevelRoute.nameId),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                },
                selected = currentDestination?.hierarchy?.any { it.hasRoute(topLevelRoute.route::class) } == true,
                alwaysShowLabel = false,
                onClick = {
                    if (currentDestinationTop != topLevelRoute.route.toString()) {
                        navController.navigate(topLevelRoute.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}