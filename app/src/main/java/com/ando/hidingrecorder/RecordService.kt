package com.ando.hidingrecorder

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import kotlin.coroutines.coroutineContext

class RecordService : Service(), RecordListener {
    companion object{
        private const val TAG = "RecordService"
        private const val NT_ID = 1
    }

    private val scope = CoroutineScope(Dispatchers.Main)
    private val recordingState by lazy { MutableStateFlow(RecordState.None) }

    private var recorder :MediaRecorder? = null
    private var fileName = ""

    private val receiver : BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.i(TAG, "브로드캐스트 수신: ${intent!!.getStringExtra("content")}")
                handleCommand(intent.getStringExtra("content")?:"none")
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate() {
        super.onCreate()
        collectState()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, IntentFilter(Intent.ACTION_SEND), Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(receiver, IntentFilter(Intent.ACTION_SEND))
        }

//        unregisterReceiver(receiver)

        fileName = "${this.filesDir}/recorded_audio.3gp"
        Log.i(TAG,"fileName : $fileName")
        val file = File(fileName)

        if (file.exists()) {
            val content = file.name
            Log.i(TAG,"file : $content")
        } else {
            Log.i(TAG,"File does not exist.")
        }

//        debugCode()
    }

    private fun collectState() {
        scope.launch {
            recordingState.collect{
                when(it){
                    RecordState.Standby -> {
                        stopRecording()
                    }
                    RecordState.Recording -> {
                        startRecording()
                    }
                    else ->{
                        Log.e(TAG,"state is ERROR!!!")
                    }
                }
            }
        }
    }

    fun handleCommand(cmd : String){
        when(cmd){
            RecorderCommand.StartRecord.name->{
                recordingState.update { RecordState.Recording }
            }
            RecorderCommand.StopRecord.name ->{
                recordingState.update { RecordState.Standby }
            }
            RecorderCommand.RequestRecordingStatus.name->{
                sendBroadcast(Intent(Intent.ACTION_SEND).putExtra("content",recordingState.value))
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let{
            val data = it.getStringExtra("data")

            Log.i(TAG, "data : ${data ?: ""}")
        }
        return START_STICKY
    }


    fun onRecord(start : Boolean) = if(start){
        startRecording()
    }
    else{
        stopRecording()
    }

    private fun startRecording(){
        if(recorder != null) {
            stopRecording()
            return
        }


        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try{
                prepare()
            }catch (e : IOException){
                Log.e(TAG, "prepare() failed")
            }
            Log.i(TAG,"Start Recording...!")

            start()
        }
    }
    private fun stopRecording(){
        recorder?.apply{
            stop()
            release()
        }
        recorder = null
        Log.i(TAG,"Stop Recording...!")
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecording()
    }

    fun debugCode(){
        CoroutineScope(Dispatchers.IO).launch{
            while(true){
                delay(3000L)
                startRecording()
                delay(3000L)
                stopRecording()
            }
        }
    }
}

interface RecordListener{


}