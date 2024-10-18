package com.onourem.android.activity.ui.audio_exo.internal.exoplayer;

import android.os.Handler;

import androidx.media3.exoplayer.ExoPlayer;


public class ProgressTracker implements Runnable {

    private final ExoPlayer player;
    private final Handler handler;
    private final PositionListener positionListener;

    public ProgressTracker(ExoPlayer player, PositionListener positionListener) {
        this.player = player;
        this.positionListener = positionListener;
        handler = new Handler();
        handler.post(this);
    }

    public void run() {
        long position = player.getCurrentPosition();
        long total = player.getDuration();
        positionListener.progress(position, total);
        handler.postDelayed(this, 1000);
    }

    public void purgeHandler() {
        handler.removeCallbacks(this);
    }

    public interface PositionListener {
        void progress(long currentPosition, long total);
    }
}