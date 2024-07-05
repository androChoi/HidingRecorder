package com.ando.hidingrecorder.ui.layouts

import android.widget.ImageButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ando.hidingrecorder.R

@Composable
fun BaseTopLayout(icon: Painter, title : String, backClick : () -> Unit, iconClick : () -> Unit){
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround

    ) {
        IconButton(onClick = backClick) {
            Icon(painter = painterResource
                (id = R.drawable.arrow_back_24px),
                contentDescription = "backArrow")
        }
        Text(text = title,)

        IconButton(onClick = iconClick, modifier = Modifier.alpha(0f)) {
            Icon(painter = icon,
                contentDescription = "backArrow")
            }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewLayout(){
    BaseTopLayout(icon = painterResource(id = R.drawable.app_icon),"Invisible Recorder",{},{})
}