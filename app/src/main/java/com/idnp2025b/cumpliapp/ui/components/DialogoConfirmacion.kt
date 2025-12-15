package com.idnp2025b.cumpliapp.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme

@Composable
fun DialogoConfirmacion(
    mostrar: Boolean,
    titulo: String,
    mensaje: String,
    onConfirmar: () -> Unit,
    onDismiss: () -> Unit,
    textoConfirmar: String = "Confirmar",
    textoCancelar: String = "Cancelar"
) {
    if (mostrar) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(titulo) },
            text = { Text(mensaje) },
            confirmButton = {
                TextButton(onClick = onConfirmar) {
                    Text(textoConfirmar, color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(textoCancelar)
                }
            }
        )
    }
}
