package com.example.journal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.journal.ui.theme.AddNoteScreen
import com.example.journal.ui.theme.CalendarScreen
import com.example.journal.ui.theme.HomeScreen
import com.example.journal.ui.theme.InsightsScreen
import com.example.journal.ui.theme.JournalTheme
import com.example.journal.ui.theme.LoginScreen
import com.example.journal.ui.theme.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyJournalApp()
        }
    }
}

@Composable
fun MyJournalApp(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable (Screen.Login.route){
            LoginScreen(
                onLoginIn = {
                    navController.navigate(Screen.Home.route){
                        popUpTo(Screen.Login.route){inclusive=true}
                    }
                },
                onSignIn = {
                    navController.navigate(Screen.Home.route){
                        popUpTo(Screen.Login.route){inclusive=true}
                    }
                }
            )
        }

        composable(Screen.Home.route){
            HomeScreen(
                onNavigatetoAddNote = {navController.navigate(Screen.AddNote.route)},
                onNavigatetoLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNoteClick = { noteId ->
                    navController.navigate("insights/$noteId")
                },
                onNavigatetoHome = {
                    navController.navigate(Screen.Home.route)
                },
                onNavigatetoCalendar = {navController.navigate(Screen.Calendar.route)}
                )
        }

        composable(route = "insights/{noteId}") {backStackEntry->
            val noteId = backStackEntry.arguments?.getString("noteId") ?:""
            InsightsScreen(
                noteId = noteId,
                onNavigatetoAddNote = {navController.popBackStack()}
                )
        }

        composable(Screen.AddNote.route){
            AddNoteScreen(
                onNoteSaved = { navController.popBackStack() },
                onNavigateBack = {navController.popBackStack()}
            )
        }

        composable(Screen.Calendar.route){
            CalendarScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNoteClick = { noteId ->
                    navController.navigate("insights/$noteId")
                }
            )
        }



    }
}
