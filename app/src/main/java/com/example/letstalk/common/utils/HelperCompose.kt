package com.example.letstalk.common.utils

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun LetsTalkAlertDialog(
    title: @Composable () -> Unit,
    dismissText: String,
    confirmText: String,
    dismissRequest: (() -> Unit)? = null,
    confirm: () -> Unit,
    dismiss: () -> Unit
) {
    AlertDialog(onDismissRequest =
    { dismissRequest?.let { dismissRequest() }
    },
        confirmButton = {
            TextButton(onClick = {
                confirm()
            }) {
                Text(text = confirmText)
            }
        }, dismissButton = {
            TextButton(onClick = {
                dismiss()
            }) {
                Text(text = dismissText)
            }
        },title = { title })
}