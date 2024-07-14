package com.ando.hidingrecorder

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.ando.hidingrecorder.ui.theme.HidingRecorderTheme
import com.ando.hidingrecorder.viewmodels.ShareViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "MainActivity"
class MainActivity : ComponentActivity() {
    private var player : MediaPlayer? = null

    val prefs : Preference by lazy { Preference(context = this)}
    val shareViewModel : ShareViewModel by viewModels()

    private val receiver : BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                shareViewModel.serviceStatus.value = RService.getStatus(intent?.getStringExtra("content") ?: "")
            }
        }
    }

    fun setCommandRecorder(cmd : RecorderCommand){

        when(cmd){
            RecorderCommand.ServiceOn -> {
                Log.i(TAG,"NONE")
                val serviceIntent = Intent(this, RecordService::class.java)
                startService(serviceIntent)


            }
            RecorderCommand.ServiceOff -> {
                val serviceIntent = Intent(this, RecordService::class.java)
                stopService(serviceIntent)
            }
            RecorderCommand.StartRecord -> {
                val dataIntent = Intent(Intent.ACTION_SEND)
                dataIntent.putExtra("content",RecorderCommand.StartRecord.name)
                sendBroadcast(dataIntent)
            }
            RecorderCommand.StopRecord -> {
                val dataIntent = Intent(Intent.ACTION_SEND)
                dataIntent.putExtra("content",RecorderCommand.StopRecord.name)
                sendBroadcast(dataIntent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, IntentFilter(Intent.ACTION_SEND))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }


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