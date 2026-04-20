package com.example.dentalplus_frontend.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.dentalplus_frontend.R
import com.example.dentalplus_frontend.ui.*
import com.example.dentalplus_frontend.ui.AgendaScreen
import com.example.dentalplus_frontend.ui.theme.Blue40
import com.example.dentalplus_frontend.utils.Constants

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        composable(Routes.LOGIN) {
            LoginScreen(
                modifier = Modifier,
                onLoginClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(navController = navController)
        }

        composable(Routes.PATIENTS) {
            PatientListScreen(navController)
        }

        composable(Routes.PATIENT_DETAIL) {
            PatientScreen(navController)
        }

        composable(Routes.AGENDA) {
            AgendaScreen(navController)
        }

        composable(Routes.PROFILE) {
            ProfileScreen(navController)
        }
    }
}
@Composable
fun Header() {
    Surface(
        modifier = Modifier.fillMaxWidth().absoluteOffset(y = (-5).dp),
        shadowElevation = 9.dp
    ) {
        Box {
            Image(
                painter = painterResource(R.drawable.generic_header_wave),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun BottomBar(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
        shadowElevation = 9.dp
    ) {
        Box {
            Image(
                painter = painterResource(R.drawable.generic_footer_wave),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize()
            )

            NavigationBar(containerColor = Color.Transparent) {

                val currentRoute =
                    navController.currentBackStackEntryAsState().value?.destination?.route

                Constants.BottomNavItems.forEach { navItem ->
                    NavigationBarItem(
                        selected = currentRoute == navItem.route,
                        onClick = {
                            navController.navigate(navItem.route) {
                                popUpTo(Routes.HOME)
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(navItem.icon, contentDescription = navItem.label)
                        },
                        label = { Text(navItem.label) },
                        colors = NavigationBarItemColors(selectedIndicatorColor = Color.Transparent,
                            selectedIconColor = Blue40,
                            selectedTextColor = Blue40,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            disabledIconColor = Color.LightGray,
                            disabledTextColor = Color.LightGray)
                    )
                }
            }
        }
    }
}