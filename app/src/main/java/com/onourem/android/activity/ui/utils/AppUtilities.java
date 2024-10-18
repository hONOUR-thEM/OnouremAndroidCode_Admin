package com.onourem.android.activity.ui.utils;


import static androidx.fragment.app.FragmentManager.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.onourem.android.activity.BuildConfig;
import com.onourem.android.activity.R;
import com.onourem.android.activity.ui.utils.snackbar.NoSwipeBehavior;
import com.onourem.android.activity.ui.utils.snackbar.OnouremSnackBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public final class AppUtilities {

    public static int getAppVersion() {
        return BuildConfig.VERSION_CODE;
    }


    public static String getAppVersionName(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getMessage(int msgCode, Context context, final Object[] arguments) {
        return getMessage(context.getString(msgCode), arguments);
    }

    public static void disableEmojis(EditText editText) {
        InputFilter emojiFilter = (source, start, end, dest, dStart, dEnd) -> {
            for (int index = start; index < end - 1; index++) {
                int type = Character.getType(source.charAt(index));

                if (type == Character.SURROGATE) {
                    return "";
                }
            }
            return null;
        };
        editText.setFilters(new InputFilter[]{emojiFilter});
    }

    public static boolean hasSpecialChars(String name) {
        Pattern regex = Pattern.compile("[$&+,:;=\\\\?@#|/'{}<>.^*()%!-]");
        return regex.matcher(name).find();
        //    return !name.matches("[/:*?\"|{}+\\-^()\\[\\].#$%!~£;,`=@]");
    }

    public static String getMessage(String message, final Object[] arguments) {
        if (arguments != null) {
            MessageFormat messageMF = new MessageFormat(message);
            message = messageMF.format(arguments);
        }

        return message;
    }

    public static void showLog(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }

    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null) {
            activity.getWindow().getDecorView();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public static String getTimeZone() {
        if (TimeZone.getDefault().getID() != null || !TimeZone.getDefault().getID().equalsIgnoreCase("")) {
            return TimeZone.getDefault().getID();
        } else {
            Calendar c = Calendar.getInstance();
            TimeZone tz = c.getTimeZone();
            return tz.getID();
        }
    }

    public static String getCountryCode(Context context) {
        String countryCode = "";
        try {
            countryCode = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkCountryIso();
            countryCode = countryCode.toUpperCase();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

//        if (TextUtils.isEmpty(countryCode)) {
//            if (Build.VERSION.SDK_INT > 23) {
//                countryCode = context.getResources().getConfiguration().getLocales().get(0).getCountry();
//            } else {
//                countryCode = context.getResources().getConfiguration().locale.getCountry();
//            }
//        }

        AppUtilities.showLog("***Country", "countryISO : " + countryCode);

        if (TextUtils.isEmpty(countryCode)) {
            return "IN";
        } else {
            return countryCode;
        }

//        val telephonyManager: TelephonyManager = getApplicationContext().applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager //gets the country mcc code
//        val countryISO = telephonyManager.networkCountryIso;
//        /*gets the mcc appended with the mnc. Note that mcc is always the first 3 digits returned with this method so to get the mnc you can just split the String or get the chars from the 3rd index to the last.*/
//        val countryMnc: String = telephonyManager.networkOperator
//

    }

    public static long getHourDifference(Date date1) {
        Date currentDate = Calendar.getInstance(Locale.getDefault()).getTime();
        if (currentDate.getTime() - date1.getTime() < 0) {
            Calendar c = Calendar.getInstance();
            c.setTime(currentDate);
            c.add(Calendar.DATE, 1);
            currentDate = c.getTime();
        }
        long ms = currentDate.getTime() - date1.getTime();
        return TimeUnit.HOURS.convert(ms, TimeUnit.MILLISECONDS);
    }

    public static long getDayDifference(Date date1) {
//        date1= new Date("Tue Jun 15 21:14:57 GMT+05:30 2020");
        Date currentDate = Calendar.getInstance(Locale.getDefault()).getTime();
        if (currentDate.getTime() - date1.getTime() < 0) {
            Calendar c = Calendar.getInstance();
            c.setTime(currentDate);
            c.add(Calendar.DATE, 1);
            currentDate = c.getTime();
        }
        long ms = currentDate.getTime() - date1.getTime();

        String pattern = "dd-MM-yyyy hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
        AppUtilities.showLog("MOOD_SYNC_latest", simpleDateFormat.format(currentDate));
        AppUtilities.showLog("MOOD_SYNC_prev", simpleDateFormat.format(date1));
        AppUtilities.showLog("MOOD_SYNC_diff", String.valueOf(ms));
        AppUtilities.showLog("MOOD_SYNC_days", String.valueOf(TimeUnit.DAYS.convert(ms, TimeUnit.MILLISECONDS)));


        return TimeUnit.DAYS.convert(ms, TimeUnit.MILLISECONDS);
    }

    public static boolean isSameDay(Date lastSyncDate, Date currentDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return simpleDateFormat.format(lastSyncDate).equals(simpleDateFormat.format(currentDate));
    }

    public static String getBase64String(Bitmap bitmap, final int intendedWidth) {
        if (bitmap == null) {
            return "";
        }

        try {
            bitmap = resizeImage(bitmap, intendedWidth);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (bitmap == null) {
            return "";
        } else {
            return bitmapToBase64(bitmap);
        }
    }

    private static Bitmap resizeImage(Bitmap btmp, int intendedWidth) {
        try {
            btmp = Bitmap.createScaledBitmap(
                    btmp,
                    intendedWidth,
                    getResizedHeightByIntendedWidth(btmp, intendedWidth),
                    true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return btmp;
    }

    private static String bitmapToBase64(Bitmap bitmap) {
        try {
            if (bitmap != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                return Base64.encodeToString(byteArray, Base64.NO_WRAP);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    private static int getResizedHeightByIntendedWidth(final Bitmap photo, final int intendedWidth) {
        if (photo == null) {
            return intendedWidth;
        } else {
            if (intendedWidth > photo.getWidth()) {
                float widthScalePercentage = (float) (intendedWidth - photo.getWidth()) * 100 / photo.getWidth();
                float increaseHeightBy = (float) photo.getHeight() * widthScalePercentage / 100;
                return photo.getHeight() + (int) increaseHeightBy;
            } else if (intendedWidth < photo.getWidth()) {
                float widthScalePercentage = (float) (photo.getWidth() - intendedWidth) * 100 / photo.getWidth();
                float decreaseHeightBy = (float) photo.getHeight() * widthScalePercentage / 100;
                return photo.getHeight() - (int) decreaseHeightBy;
            } else {
                return photo.getHeight();
            }
        }
    }

    public static void openKeyboard(Activity activity) {

        if (activity != null && activity.getWindow() != null) {
            activity.getWindow().getDecorView();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.toggleSoftInputFromWindow(
                    activity.getWindow().getDecorView().getApplicationWindowToken(),
                    InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static void showSnackbar(ViewGroup parentLayout, String message, int visibility, int duration, String buttonText, View.OnClickListener onClickListener) {
        OnouremSnackBar snackBar = OnouremSnackBar
                .make(parentLayout, duration)
                .setText(message)
                .setAction(buttonText, onClickListener, visibility)
                .setCloseAction(v -> {

                });
        (snackBar.getView()).getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        snackBar.setBehavior(new NoSwipeBehavior());
        snackBar.show();

    }

    /**
     * Saves the image as PNG to the app's private external storage folder.
     *
     * @param image Bitmap to save.
     * @return Uri of the saved file or null
     */
    public static Uri saveImageExternal(Context context, Bitmap image) {
        //TODO - Should be processed in another thread
        Uri uri = null;
        try {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "to-share.png");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
            uri = FileProvider.getUriForFile(context,
                    context.getApplicationContext().getPackageName() + ".GenericFileProvider", file);
        } catch (IOException e) {
            Log.d("Sharing", "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }

//    /**
//     * Saves the image as PNG to the app's private external storage folder.
//     * @param image Bitmap to save.
//     * @return Uri of the saved file or null
//     */
//    public static Uri saveVideoExternal(Context context, File videoFile) {
//        //TODO - Should be processed in another thread
//        Uri uri = null;
//        try {
//            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "to-share.mp4");
//            FileOutputStream stream = new FileOutputStream(file);
//            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            stream.close();
//            uri = FileProvider.getUriForFile(context,
//                    context.getApplicationContext().getPackageName() + ".GenericFileProvider", file);;
//        } catch (IOException e) {
//            Log.d(TAG, "IOException while trying to write file for sharing: " + e.getMessage());
//        }
//        return uri;
//    }

    public static String getRootDirPath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File file = ContextCompat.getExternalFilesDirs(context.getApplicationContext(),
                    null)[0];
            return file.getAbsolutePath();
        } else {
            return context.getApplicationContext().getFilesDir().getAbsolutePath();
        }
    }

    public static void setUserProfile(Context context, AppCompatImageView appCompatImageView, String userPicUrl) {

        final RequestOptions options = new RequestOptions()
                .fitCenter()
                .transform(new CircleCrop())
                .placeholder(R.drawable.default_user_profile_image)
                .error(R.drawable.default_user_profile_image);

        Glide.with(context)
                .load(userPicUrl)
                .apply(options)
                .into(appCompatImageView);
    }

    public static void setUserProfile(Context context, AppCompatImageView appCompatImageView, Drawable userPicUrl) {

        final RequestOptions options = new RequestOptions()
                .fitCenter()
                .transform(new CircleCrop())
                .placeholder(R.drawable.default_user_profile_image)
                .error(R.drawable.default_user_profile_image);

        Glide.with(context)
                .load(userPicUrl)
                .apply(options)
                .into(appCompatImageView);
    }

    public static String getProgressDisplayLine(long currentBytes, long totalBytes) {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes);
    }

    public static String appendToString(String text) {
        return text;
    }

    public static String getBytesToMBString(long bytes) {
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00));
    }
}

