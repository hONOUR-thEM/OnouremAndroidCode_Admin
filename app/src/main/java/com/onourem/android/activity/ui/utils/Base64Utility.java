package com.onourem.android.activity.ui.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.nio.charset.StandardCharsets;

public class Base64Utility {
    public static String decode(String text) {
        if (TextUtils.isEmpty(text)) return "";
        try {
            if (Base64CommonsCodec.isBase64(text)) {
                text = new String(Base64.decode(text.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    public static String encodeToString(String text) {
        return Base64.encodeToString(text.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
    }
}