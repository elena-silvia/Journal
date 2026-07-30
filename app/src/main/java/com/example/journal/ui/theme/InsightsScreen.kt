package com.example.journal.ui.theme

import android.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

data class SingleNoteInsight(
    val mood: String="",
    val score: Int=3, //its neutral
    val aiAdvice: String=""
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    onNavigatetoAddNote: () -> Unit,
    noteId: String){

    var noteData by remember { mutableStateOf<SingleNoteInsight?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val firestore = FirebaseFirestore.getInstance()

    LaunchedEffect(noteId) {
        if(noteId.isNotEmpty()){
            firestore.collection("notes").document(noteId)
                .get()
                .addOnSuccessListener { document->
                    if(document.exists()){
                        noteData = SingleNoteInsight(
                            mood = document.getString("mood")?:"",
                            score = document.getLong("score")?.toInt() ?:3,
                            aiAdvice = document.getString("aiAdvice")?:""
                        )
                    }
                    isLoading=false
                }
                .addOnFailureListener {
                    isLoading=false
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analyzing your note") },
                navigationIcon = {
                    IconButton(onClick = onNavigatetoAddNote) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
                )
        }

    ) {paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)

        ){
           if(isLoading){
               CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
           }else if(noteData == null){
               Text("Note not found")
           }else{
               val note =noteData!!

               Card(
                   modifier = Modifier.fillMaxWidth(),
                   colors = CardDefaults.cardColors(
                       containerColor = MaterialTheme.colorScheme.primaryContainer
                   )
               ){
                   Column(modifier = Modifier.padding(16.dp)) {
                       Text(
                           text = "AI Analysis",
                           style = MaterialTheme.typography.titleMedium,
                           color = MaterialTheme.colorScheme.primary
                       )

                       Spacer(modifier = Modifier.height(8.dp))

                       if(note.mood.isNotEmpty()){
                           Text(
                               text = "Detected mood: ${note.mood} (score: ${note.score}/5)",
                               style = MaterialTheme.typography.titleMedium,
                               color = MaterialTheme.colorScheme.primary
                           )
                       }
                       Spacer(modifier = Modifier.height(8.dp))
                       if(note.aiAdvice.isNotEmpty()){
                           Text(
                               text = "AI Advice:",
                               style = MaterialTheme.typography.labelMedium,
                               color = MaterialTheme.colorScheme.onPrimaryContainer
                           )

                           Text(
                               text = note.aiAdvice,
                               color = MaterialTheme.colorScheme.onPrimaryContainer,
                               style = MaterialTheme.typography.bodyMedium
                           )
                       }
                   }
               }
           }
        }
    }
}