package com.onourem.android.activity.ui.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppValidate {
    public static boolean isNotEmpty(String s) {
        return ((s != null) && (s.trim().length() > 0));
    }

    public static boolean isValidEmail(String email) {
        Pattern p = Pattern.compile("^[_A-Za-z0-9]+(\\.[_A-Za-z0-9]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
