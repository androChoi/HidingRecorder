package com.ando.hidingrecorder.ui.buttons

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun BaseButton(onClick : () -> Unit, text : String){
    val width = 10.dp
    val height = 10.dp
    Button(
        onClick = onClick,
        modifier = Modifier.apply {
            this.width(width)
            this.height(height)
        }
    ) {
        Text(text = text)
    }
}

@Composable
fun HomeButton(onClick: () -> Unit, text : String){
    BaseButton(onClick, text)
}