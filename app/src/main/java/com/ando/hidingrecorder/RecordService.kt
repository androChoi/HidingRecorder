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
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class RecordService : Service(), RecordListener {
    companion object{
        private const val TAG = "RecordService"
        private const val NT_ID = 1
    }

    private var recordingState = MutableStateFlow(RecordState.None.status)

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

    fun handleCommand(cmd : String){
        when(cmd){
            //TODO UI와의 시차로 start를 2번 내릴 경우에 예외 처리를 진행하자
            RecorderCommand.StartRecord.name->{
                startRecording()
            }
            RecorderCommand.StopRecord.name ->{
                stopRecording()
            }
            RecorderCommand.RequestRecordingStatus.name->{
                sendBroadcast(Intent(Intent.ACTION_SEND).putExtra("content",recordingState.value))
            }
            RecorderCommand.RequestServiceState.name->{
                //On이 오면 Service가 켜져있는 경우...
                //Off일 경우는 없지만 On이라고 알려주는 케이스는 분명히 존재하기 때문에 무조건 있어야 합니당.
                sendBroadcast(Intent(Intent.ACTION_SEND).putExtra("content","On!"))
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate() {
        super.onCreate()

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