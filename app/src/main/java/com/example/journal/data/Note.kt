package com.example.journal.data

import com.google.api.Advice
import java.sql.Timestamp

data class Note (
    val id: String="",
    val text: String="",
    val title: String="",
    val timestamp: Long = System.currentTimeMillis(),

    val mood: String="",
    val moodScore: Int=0,
    val aiAdvice: String=""
)