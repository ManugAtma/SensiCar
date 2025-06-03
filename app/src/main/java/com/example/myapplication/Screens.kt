package com.example.myapplication

sealed class Screen(val route: String) {
    object Menu : Screen("menu")
    object Game : Screen("game")
    object Settings : Screen("settings")
}
