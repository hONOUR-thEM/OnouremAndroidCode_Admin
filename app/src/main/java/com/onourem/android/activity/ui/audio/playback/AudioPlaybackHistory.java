package com.onourem.android.activity.ui.audio.playback;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AudioPlaybackHistory {
    @PrimaryKey
    @NonNull
    private String tempId;
    private String audioId;
    private String audioDuration;
    private String userId;

    public AudioPlaybackHistory(@NonNull String tempId, String audioId, String audioDuration, String userId) {
        this.tempId = tempId;
        this.audioId = audioId;
        this.audioDuration = audioDuration;
        this.userId = userId;
    }

    @NonNull
    public String getTempId() {
        return tempId;
    }

    public void setTempId(@NonNull String tempId) {
        this.tempId = tempId;
    }

    public String getAudioId() {
        return audioId;
    }

    public void setAudioId(@NonNull String audioId) {
        this.audioId = audioId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAudioDuration() {
        return audioDuration;
    }

    public void setAudioDuration(String audioDuration) {
        this.audioDuration = audioDuration;
    }
}
