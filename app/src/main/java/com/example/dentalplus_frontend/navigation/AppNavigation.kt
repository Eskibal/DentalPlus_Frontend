package com.example.dentalplus_frontend.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dentalplus_frontend.R
import com.example.dentalplus_frontend.model.OdontogramType
import com.example.dentalplus_frontend.model.Quadrant
import com.example.dentalplus_frontend.session.SessionManager
import com.example.dentalplus_frontend.ui.AgendaScreen
import com.example.dentalplus_frontend.ui.HomeScreen
import com.example.dentalplus_frontend.ui.LoginScreen
import com.example.dentalplus_frontend.ui.OdontogramScreen
import com.example.dentalplus_frontend.ui.PatientListScreen
import com.example.dentalplus_frontend.ui.PatientScreen
import com.example.dentalplus_frontend.ui.ProfileScreen
import com.example.dentalplus_frontend.ui.QuadrantZoomedScreen
import com.example.dentalplus_frontend.ui.ToothDetailScreen
import com.example.dentalplus_frontend.ui.theme.Blue40
import com.example.dentalplus_frontend.utils.Constants
import com.example.dentalplus_frontend.viewmodel.LoginViewModel
import com.example.dentalplus_frontend.viewmodel.OdontogramViewModel

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val odontogramViewModel: OdontogramViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel()
    val context = LocalContext.current

    val isLoading by loginViewModel.isLoading.collectAsState()
    val errorMessage by loginViewModel.errorMessage.collectAsState()

    val startDestination = if (SessionManager(context).isLoggedIn()) {
        Routes.HOME
    } else {
        Routes.LOGIN
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(Routes.LOGIN) {
            LoginScreen(
                modifier = Modifier,
                isLoading = isLoading,
                errorMessage = errorMessage,
                onLoginClick = { identifier, password ->
                    loginViewModel.login(
                        context = context,
                        identifier = identifier,
                        password = password,
                        onSuccess = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        }
                    )
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

        composable("odontogram/{type}") { backStackEntry ->

            val type = OdontogramType.valueOf(
                backStackEntry.arguments?.getString("type")!!
            )

            OdontogramScreen(navController, type, odontogramViewModel)
        }

        composable("quadrant/{quadrant}/{type}") { backStackEntry ->

            val quadrant = Quadrant.valueOf(
                backStackEntry.arguments?.getString("quadrant")!!
            )

            val type = OdontogramType.valueOf(
                backStackEntry.arguments?.getString("type")!!
            )

            QuadrantZoomedScreen(navController, quadrant, type, odontogramViewModel)
        }

        composable("tooth/{toothNumber}") { backStackEntry ->
            val toothNumber = backStackEntry.arguments?.getString("toothNumber")?.toInt() ?: 0

            ToothDetailScreen(
                navController = navController,
                toothNumber = toothNumber,
                odontogramViewModel
            )
        }
    }
}

@Composable
fun Header() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .absoluteOffset(y = (-5).dp),
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
                        colors = NavigationBarItemColors(
                            selectedIndicatorColor = Color.Transparent,
                            selectedIconColor = Blue40,
                            selectedTextColor = Blue40,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            disabledIconColor = Color.LightGray,
                            disabledTextColor = Color.LightGray
                        )
                    )
                }
            }
        }
    }
}