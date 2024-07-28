package com.ando.hidingrecorder.ui.screens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ando.hidingrecorder.MainActivity
import com.ando.hidingrecorder.NavigationItem
import com.ando.hidingrecorder.R
import com.ando.hidingrecorder.RecordState
import com.ando.hidingrecorder.RecorderCommand
import com.ando.hidingrecorder.ui.layouts.BaseTopLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "HomeScreen"

@Composable
fun HomeScreen(navController: NavController) {
    val activity = LocalContext.current as MainActivity

    Column(modifier = Modifier.fillMaxSize()){
        HomeTopLayout(icon = painterResource(id = R.drawable.info_info_24px), LocalContext.current.getString(R.string.app_name),navController)
        HomeMidLayout(activity)
    }
}

@Composable
fun HomeMidLayout(activity : MainActivity){
    val shareViewModel = activity.shareViewModel
    var recordingText by remember{ mutableStateOf("record")}
    var clickLock by remember{ mutableStateOf(true)}

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            if(clickLock) {

                clickLock = false

                when(shareViewModel.serviceStatus.value) {
                    RecordState.None.status -> {
                        activity.startRecordingService()
                        recordingText = "record stop"
                        shareViewModel.recording.value = true
                        shareViewModel.serviceStatus.value = RecordState.None.status
                    }

                    RecordState.Standby.status -> {
                        Log.i(TAG,"Standby")
                        activity.setCommandRecorder(RecorderCommand.StartRecord)
                    }

                    RecordState.Recording.status -> {

                        activity.setCommandRecorder(RecorderCommand.StopRecord)
                        recordingText = "record"
                        shareViewModel.recording.value = false
                    }
                }

                CoroutineScope(Dispatchers.Main).launch {
                    delay(2000)
                    clickLock = true
                }
            }
        }) {
            Text(text = recordingText)
        }
    }
}


@Composable
fun HomeTopLayout(icon : Painter, title : String, nc : NavController){
    val backStackEntry by nc.currentBackStackEntryAsState()
    val current = LocalContext.current as Activity
    BaseTopLayout(
        icon = icon,title,
        {
            if(backStackEntry?.destination?.route == NavigationItem.Home.route){
                current.finish()
            }
            else {
                nc.popBackStack()
            }

        },{

        }
    )
}