package com.ando.hidingrecorder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.os.Parcel
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Cyan
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class RecordService : Service(), RecordListener {
    private var recorder : MediaRecorder? = null
    private var fileName = ""

    companion object{
        private const val TAG = "RecordService"
        private const val NT_ID = 1
    }

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented...? that!")
    }

    override fun onCreate() {
        super.onCreate()

        fileName = "${this.filesDir}/recorded_audio.3gp"
        Log.i(TAG,"fileName : $fileName")
        val file = File(fileName)

        if (file.exists()) {
            // 파일이 존재할 경우 파일을 읽어옵니다.
            // 여기서는 파일의 내용을 출력하는 예시를 보여줍니다.
            val content = file.name
            Log.i(TAG,"file : $content")
        } else {
            Log.i(TAG,"File does not exist.")
        }

        debugCode()
    }


    fun onRecord(start : Boolean) = if(start){
        startRecording()
    }
    else{
        stopRecording()
    }

    private fun startRecording(){
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