package com.example.journal.ui.theme


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import java.text.SimpleDateFormat
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Date
import java.util.Locale


@SuppressLint("FrequentlyChangingValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigatetoAddNote: ()->Unit,
    onNavigatetoHome: () -> Unit,
    onNavigatetoLogin: () -> Unit,
    onNoteClick: (String) ->Unit,
    onNavigatetoCalendar: () ->Unit,
    viewModel: HomeViewModel = viewModel()
){
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    var noteToDeleteId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Journal") },
                navigationIcon = {
                    IconButton(onClick = onNavigatetoLogin) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },

        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick =onNavigatetoHome,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = {Text("Home")}
                )
                NavigationBarItem(
                    selected = true,
                    onClick =onNavigatetoCalendar,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Calendar"
                        )
                    },
                    label = {Text("Calendar of Moods")}
                )
                NavigationBarItem(
                    selected = true,
                    onClick =onNavigatetoAddNote,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add"
                        )
                    },
                    label = {Text("Add Note")}
                )
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ){
            if (notes.isEmpty()){
                Text("No notes yet. Tap + to add one.")
            } else {

                val listState = rememberLazyListState()
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)

                ) {
                    items(notes){note->
                        val sdf = SimpleDateFormat("dd MM yyyy", Locale.getDefault())
                        val dateString = sdf.format(Date(note.timestamp))

                        Card(
                            onClick = {onNoteClick(note.id)},
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = CardDefaults.cardColors()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = note.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                text = dateString,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                                Text(
                                    text = note.text,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            IconButton(onClick = {noteToDeleteId = note.id}
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Note",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

        }
        if(noteToDeleteId !=null){
            ConfirmationDialog(
                onConfirm = {
                    noteToDeleteId?.let {id->
                        viewModel.deleteNote(id)
                    }
                    noteToDeleteId=null
                },
                onDismiss = {
                    noteToDeleteId=null
                }
            )
        }
    }
}

