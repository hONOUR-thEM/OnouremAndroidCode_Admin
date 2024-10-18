package com.onourem.android.activity.ui.utils;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;

public class VideoDurationHelper {

    public static long getVideoDuration(String videoPath) {
        MediaExtractor mediaExtractor = new MediaExtractor();

        try {
            mediaExtractor.setDataSource(videoPath);

            int trackCount = mediaExtractor.getTrackCount();
            long duration = 0;

            for (int i = 0; i < trackCount; i++) {
                MediaFormat mediaFormat = mediaExtractor.getTrackFormat(i);
                if (mediaFormat.containsKey(MediaFormat.KEY_DURATION)) {
                    duration = mediaFormat.getLong(MediaFormat.KEY_DURATION) / 1000000; // Correct conversion to seconds
                    break;
                }
            }

            return duration;
        } catch (IOException e) {
            Log.e("VideoDuration", "Error reading video file", e);
            return 0;
        } finally {
            mediaExtractor.release();
        }
    }
}
