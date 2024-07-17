package com.ando.hidingrecorder

enum class RService(val status : String) {
    None("None"),
    Standby("Standby"),
    Recording("Recording");

    companion object {
        fun getStatus(status: String)=
            when (status) {
                Recording.status -> Recording
                Standby.status -> Standby
                else -> None
            }

    }
}

enum class RecorderCommand{
    ServiceOn,
    ServiceOff,
    StartRecord,
    StopRecord,
    RequestRecordingStatus(),
    RequestServiceState(),

}