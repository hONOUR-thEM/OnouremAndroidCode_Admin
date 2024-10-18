package com.onourem.android.activity.ui.audio_exo.internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

import androidx.annotation.DrawableRes;
import androidx.appcompat.content.res.AppCompatResources;

import com.onourem.android.activity.R;

import java.util.Objects;

final class Samples {

    static final Sample[] SAMPLES = new Sample[]{
            new Sample(
                    "https://storage.googleapis.com/automotive-media/Jazz_In_Paris.mp3",
                    "audio_1",
                    "Jazz in Paris",
                    "Jazz for the masses",
                    R.drawable.english_thumbnail),
            new Sample(
                    "https://storage.googleapis.com/automotive-media/The_Messenger.mp3",
                    "audio_2",
                    "The messenger",
                    "Hipster guide to London",
                    R.drawable.english_thumbnail),
            new Sample(
                    "https://storage.googleapis.com/automotive-media/Talkies.mp3",
                    "audio_3",
                    "Talkies",
                    "If it talks like a duck and walks like a duck.",
                    R.drawable.english_thumbnail),
    };

    static Bitmap getBitmap(Context context, @DrawableRes int bitmapResource) {
        return ((BitmapDrawable) Objects.requireNonNull(AppCompatResources.getDrawable(context, bitmapResource))).getBitmap();
    }

    public static final class Sample {
        public final Uri uri;
        public final String mediaId;
        public final String title;
        public final String description;
        public final int bitmapResource;

        public Sample(
                String uri, String mediaId, String title, String description, int bitmapResource) {
            this.uri = Uri.parse(uri);
            this.mediaId = mediaId;
            this.title = title;
            this.description = description;
            this.bitmapResource = bitmapResource;
        }

        @Override
        public String toString() {
            return title;
        }
    }

}
