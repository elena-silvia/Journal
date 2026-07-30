package com.example.journal.ui.theme

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journal.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.ai.client.generativeai.type.generationConfig
import kotlin.time.Duration.Companion.seconds


sealed interface SaveNoteState{
    object Idle: SaveNoteState
    object Loading: SaveNoteState
    object Success: SaveNoteState
    data class Error(val message: String): SaveNoteState

}

class AddNoteViewModel: ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-flash-latest",
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
            maxOutputTokens = 2000
            temperature = 0.1f
        }
    )

    private val _saveState = MutableStateFlow<SaveNoteState>(SaveNoteState.Idle)
    val saveState: StateFlow<SaveNoteState> = _saveState.asStateFlow()

    fun saveNoteWithAi(title: String, text: String){
        if(text.isBlank()) return
        val userId = auth.currentUser?.uid ?: return

        if (BuildConfig.GEMINI_API_KEY.isBlank()) {
            Log.e("AddNoteViewModel", "Gemini API Key is missing in local.properties")
        }

        _saveState.value = SaveNoteState.Loading

        viewModelScope.launch {
            try {
                Log.d("AddNoteViewModel", "Starting AI Analysis...")
                val prompt = """
                            You are an advanced affective computing engine.
                            Your mission is to analyze the input text and to detect the emotional mood and score
                            and come back with 1 or 2 sentences with helpful advice.
                            
                            Evaluate emotions score from 1 to 5, respecting THESE rules:
                            - Score 1: Really sad, feeling awful, panic, failure, exhausted
                            - Score 2: Stress, boring, without energy, moderate upset
                            - Score 3: neutral feeling, a normal day, without strong emotions
                            - Score 4: feeling good, quiet, moderate optimistic
                            - Score 5: intense joy, a big realization, enthusiasm
                            
                            If the text includes massive failure, burnouts, or depletion, you MUST use score 1.

                            Analyze the following text and return the result ONLY as a JSON object.
                            Text: $text
                            
                            The JSON object must have exactly these keys:
                            {
                                   "mood": "a single emotion, for example: sad, frustrated, exhausted",
                                   "score": a number from 1 to 5 based on the rules above,
                                   "advice": "1 or 2 sentences with helpful advice"
                            }
                            """.trimIndent()

                val rawResponse = kotlinx.coroutines.withTimeout(45.seconds) {
                    generativeModel.generateContent(prompt)
                }.text ?: ""
                
                Log.d("AddNoteViewModel", "AI Response received: $rawResponse")

                // Robust JSON parsing
                val jsonStr = rawResponse.trim().removeSurrounding("```json", "```").removeSurrounding("```", "```").trim()
                
                if (jsonStr.isEmpty()) throw Exception("AI returned empty response")

                val jsonObject = org.json.JSONObject(jsonStr)
                val mood = jsonObject.optString("mood","Neutral")
                val score = jsonObject.optInt("score",3)
                val advice =jsonObject.optString("advice","Be safe!")

                val noteMap = hashMapOf(
                     "userId" to userId,
                     "title" to title,
                     "text" to text,
                     "timestamp" to System.currentTimeMillis(),
                     "mood" to mood,
                     "score" to score,
                    "aiAdvice" to advice
                )

        firestore.collection("notes").add(noteMap)
            .addOnSuccessListener {
                _saveState.value = SaveNoteState.Success
            }
            .addOnFailureListener { e->
                _saveState.value= SaveNoteState.Error(e.localizedMessage?:"Error saving note")
            }

            } catch (e: Exception) {
                Log.e("AddNoteViewModel", "Error generating AI. Model: gemini-flash-latest. Error: ${e.message}", e)

                 val fallbackMap = hashMapOf(
                     "userId" to userId,
                     "title" to title,
                     "text" to text,
                     "timestamp" to System.currentTimeMillis(),
                     "mood" to  "Neutral",
                     "score" to 3,
                     "aiAdvice" to "AI is unavailable: ${e.localizedMessage}"
                 )
                firestore.collection("notes").add(fallbackMap)
                    .addOnSuccessListener { _saveState.value= SaveNoteState.Success }
            }
            }
        }

}
