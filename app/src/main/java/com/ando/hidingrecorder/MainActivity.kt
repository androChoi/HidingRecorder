package com.ando.hidingrecorder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.ando.hidingrecorder.ui.theme.HidingRecorderTheme
import com.ando.hidingrecorder.viewmodels.ShareViewModel
import java.io.File
import java.io.IOException

private const val TAG = "MainActivity"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
class MainActivity : ComponentActivity() {
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    private var player : MediaPlayer? = null

    val prefs : Preference by lazy { Preference(context = this)}
    val shareViewModel : ShareViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            HidingRecorderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RequestPermissionInComposable(applicationContext)
                    AppNavHost(navController = rememberNavController())
                }
            }
        }
    }

    fun onPlay(start : Boolean, fileName : String) = if(start){
        startPlaying(fileName)
    } else{
        stopPlaying()
    }

    private fun startPlaying(fileName : String){
        player = MediaPlayer().apply{
            try{
                setDataSource(fileName)
                prepare()
                start()
            } catch (e : IOException){
                Log.e(TAG, "i love you~")
            }
        }
    }

    private fun stopPlaying(){
        player?.release()
        player = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlaying()
    }

}

@Composable
fun RequestPermissionInComposable(context: Context){
    Log.i(TAG,"RequestPermissionInComposable Start")

    val permissionList = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.FOREGROUND_SERVICE
    )

    val launcherMultiplePermissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions())
    { permissionMap ->

        val granted = permissionMap.values.all{it}
        if(granted){
            Log.i(TAG,"granted true!")
        }else{

            Log.i(TAG,"granted false!")
        }
    }
    LaunchedEffect(Unit) {
        if (permissionList.any { ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED }) {
            launcherMultiplePermissions.launch(permissionList)
            Log.i(TAG, "일부 권한이 승인되지 않아 권한 요청 시작")
        } else {
            Log.i(TAG, "모든 권한이 이미 승인됨")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppNavHost(navController = rememberNavController())
}