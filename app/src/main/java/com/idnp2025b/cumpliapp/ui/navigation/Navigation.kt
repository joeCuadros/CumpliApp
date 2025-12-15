package com.idnp2025b.cumpliapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.idnp2025b.cumpliapp.ui.screens.completadas.CompletadasScreen
import com.idnp2025b.cumpliapp.ui.screens.configuracion.ConfiguracionScreen
import com.idnp2025b.cumpliapp.ui.screens.crear.CrearActividadScreen
import com.idnp2025b.cumpliapp.ui.screens.editar.EditarActividadScreen
import com.idnp2025b.cumpliapp.ui.screens.estadisticas.EstadisticasScreen
import com.idnp2025b.cumpliapp.ui.screens.lista.ListaActividadesScreen

object Rutas {
    const val LISTA = "lista"
    const val COMPLETADAS = "completadas"
    const val ESTADISTICAS = "estadisticas"
    const val CONFIGURACION = "configuracion"
    const val CREAR = "crear"
    const val EDITAR = "editar"
}

sealed class BottomNavItem( val route: String, val title: String, val icon: ImageVector) {
    object Lista : BottomNavItem(
        route = Rutas.LISTA,
        title = "Lista",
        icon = Icons.Default.DateRange
    )
    object Completadas : BottomNavItem(
        route = Rutas.COMPLETADAS,
        title = "Completadas",
        icon = Icons.Default.CheckCircle
    )

    object Estadisticas : BottomNavItem(
        route = Rutas.ESTADISTICAS,
        title = "Estadísticas",
        icon = Icons.Default.BarChart
    )

    object Configuracion : BottomNavItem(
        route = Rutas.CONFIGURACION,
        title = "Configuración",
        icon = Icons.Default.Settings
    )
}

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.LISTA,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Rutas.LISTA) {
                ListaActividadesScreen(
                    onNavigateToCreate = {
                        navController.navigate(Rutas.CREAR)
                    },
                    onNavigateToEdit = { id ->
                        navController.navigate("${Rutas.EDITAR}/$id")
                    }
                )
            }
            composable(route = Rutas.CREAR) {
                CrearActividadScreen(
                    onNavigateUp = { navController.popBackStack() }
                )
            }
            composable(
                route = "${Rutas.EDITAR}/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                EditarActividadScreen(
                    onNavigateUp = { navController.popBackStack() }
                )
            }
            composable(route = Rutas.COMPLETADAS) {
                CompletadasScreen()
            }
            composable(route = Rutas.ESTADISTICAS) {
                EstadisticasScreen()
            }
            composable(route = Rutas.CONFIGURACION) {
                ConfiguracionScreen()
            }
        }
    }
}
@Composable
fun BottomNavigationBar(
    navController: NavHostController
) {
    val items = listOf(
        BottomNavItem.Lista,
        BottomNavItem.Completadas,
        BottomNavItem.Estadisticas,
        BottomNavItem.Configuracion
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop hasta el inicio del grafo para evitar acumulación
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
