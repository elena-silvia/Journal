package com.example.journal.ui.theme

import android.R
import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigateBack : () ->Unit,
    onNoteClick: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
){

    var calendarInstance by remember { mutableStateOf(Calendar.getInstance()) }
    val todayInstance = Calendar.getInstance()
    val daysInMonth = getDaysInMonth(calendarInstance)

    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendarInstance.time)


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mood Calendar") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ){
        paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically

            ){
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        val newCal = calendarInstance.clone() as Calendar
                        newCal.add(Calendar.MONTH,-1)
                        calendarInstance = newCal
                    }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Back")
                    }

                    Text(
                        text = monthName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal =4.dp )
                    )
                    IconButton(onClick = {
                        val newCal = calendarInstance.clone() as Calendar
                        newCal.add(Calendar.MONTH,1)
                        calendarInstance = newCal
                    }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next")
                    }
                }

                TextButton(onClick = {calendarInstance = Calendar.getInstance()}) {
                    Text(
                        text = "Today",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Magenta,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ){
                listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun").forEach { day->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(daysInMonth){ date->
                    if(date!=null){
                            val noteForThatDay = notes.find { isSameDay(it.timestamp,date.time) }
                            val moodColor = getMoodColor(noteForThatDay?.moodScore)
                            val hasNote = noteForThatDay != null
                            val isToday = isSameDay(date.time, todayInstance.timeInMillis)

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(
                                    when{
                                        hasNote ->moodColor
                                        isToday -> MaterialTheme.colorScheme.surfaceVariant
                                        else->Color.Transparent
                                    }
                                )
                                .clickable(enabled = hasNote){
                                    noteForThatDay?.let{onNoteClick(it.id)}
                                },
                            contentAlignment = Alignment.Center
                        ){
                            val dayNumber = SimpleDateFormat("d", Locale.getDefault()).format(date)
                            Text(
                                text = dayNumber,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isToday || hasNote) FontWeight.Bold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    else {
                        Box(modifier = Modifier.aspectRatio(1f))

                    }
                }
            }
        }
    }


}

private fun getMoodColor(score: Int?): Color{
    return when(score){
        1 -> Color(0xFFE57373) // Red
        2 -> Color(0xFFFFB74D) // Orange
        3 -> Color(0xFFFFF176) // Yellow
        4 -> Color(0xFF4FC3F7) // Blue
        5 -> Color(0xFF81C784) // Green
        else -> Color.Transparent
    }
}


private fun isSameDay(time1: Long,time2: Long): Boolean{
    val fmt = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    return fmt.format(Date(time1)) == fmt.format(Date(time2))
}

private fun getDaysInMonth(calendar: Calendar): List<Date?>{
    val tempCal = calendar.clone() as Calendar
    tempCal.set(Calendar.DAY_OF_MONTH,1)

    val monthdays = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK)-1

    val list = mutableListOf<Date?>()
    repeat((firstDayOfWeek+6)%7){
        list.add(null)
    }

    for(i in 1..monthdays){
        list.add(tempCal.time)
        tempCal.add(Calendar.DAY_OF_MONTH,1)
    }
    return list
}




