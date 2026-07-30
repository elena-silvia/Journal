package com.example.journal.ui.theme

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.journal.data.Note
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel: ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()
    
    init {
        fetchNotes()
    }
    
    private fun fetchNotes(){
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("HomeViewModel", "No user logged in!")
            return
        }
        
        Log.d("HomeViewModel", "Fetching notes for user: ${userId}")
        
        firestore.collection("notes")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("HomeViewModel", "Error fetching notes", exception)
                    return@addSnapshotListener
                }
                
                if (snapshot == null) {
                    Log.d("HomeViewModel", "Snapshot is null")
                    return@addSnapshotListener
                }

                val loadedNotes = snapshot.documents.mapNotNull { doc ->
                    try {
                        Note(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            text = doc.getString("text") ?: "",
                            timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                            mood = doc.getString("mood") ?: "",
                            moodScore = doc.getLong("score")?.toInt() ?: 0,
                            aiAdvice = doc.getString("aiAdvice") ?: ""
                        )
                    } catch (e: Exception) {
                        Log.e("HomeViewModel", "Error parsing note ${doc.id}", e)
                        null
                    }
                }
                Log.d("HomeViewModel", "Loaded ${loadedNotes.size} notes")
                _notes.value = loadedNotes
            }
    }

    fun deleteNote(noteId: String){
        if(noteId.isBlank()) return
        firestore.collection("notes").document(noteId)
            .delete()

    }
}
