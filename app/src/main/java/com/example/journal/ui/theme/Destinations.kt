package com.example.journal.ui.theme

sealed class Screen(val route: String){
    object Login: Screen("Login")
    object Home: Screen("Home")
    object AddNote: Screen("Add Note")
    object Insights: Screen("Insights")
    object Calendar: Screen("Calendar")
}