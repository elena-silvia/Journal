package com.example.journal.ui.theme

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        title = {Text ("Delete Note")},
        text = {Text("Are you sure you want to delete this note?")},
        onDismissRequest = {onDismiss()},
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Yes", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton={
            TextButton(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}