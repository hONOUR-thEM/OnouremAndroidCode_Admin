package com.onourem.android.activity.ui.utils;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.onourem.android.activity.R;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public final class Utilities {
    private static final String APP_DISPLAY_DATE_FORMAT = "dd-MMM-yyyy";
    public static String APP_DISPLAY_DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
    public static String APP_DISPLAY_DATE_TIME_FORMAT_WITH_S = "yyyy-MM-dd hh:mm:ss.S";

    public static <T> String getTokenSeparatedString(final List<T> inputList, final String separator) {
        StringBuilder tokenSeparatedString = new StringBuilder();
        for (int i = 0; i < inputList.size(); i++) {
            tokenSeparatedString.append(inputList.get(i));
            if (i != (inputList.size() - 1)) {
                tokenSeparatedString.append(separator);
            }
        }
        return tokenSeparatedString.toString();
    }

    private static String getDateString(Date date) {
        SimpleDateFormat sf = new SimpleDateFormat(APP_DISPLAY_DATE_FORMAT, Locale.getDefault());
        return sf.format(date);
    }

    public static String getServerTimeZone(final String serverTime) {
        try {
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(APP_DISPLAY_DATE_FORMAT, Locale.getDefault());
            Date fixDateToCompare = ((simpleDateFormat1)).parse("01-JAN-2018");

            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(APP_DISPLAY_DATE_TIME_FORMAT, Locale.getDefault());
            Date serverDateToCompare = ((simpleDateFormat2)).parse(serverTime);

            if (serverDateToCompare != null && serverDateToCompare.before(fixDateToCompare)) {
                return "MST";
            } else {
                return "UTC";
            }
        } catch (ParseException e) {
            return "UTC";
        }
    }


// public static String getPrettyRelatedTime(final String serverTime) {
//     PrettyTime p = new PrettyTime();
//     SimpleDateFormat simpleDateFormat = new SimpleDateFormat(APP_DISPLAY_DATE_TIME_FORMAT, Locale.getDefault());
//     simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getServerTimeZone(serverTime)));
//
//     try {
//         Date serverDate = ((simpleDateFormat)).parse(serverTime);
//         return p.format(serverDate);
//     } catch (ParseException e) {
//         return "";
//     }
//
// }

    public static boolean checkVideoDuration(Context context, Uri uri) {

        String[] filePathColumn = {MediaStore.Video.Media.DATA};

        Cursor cursor = context.getContentResolver().query(uri,
                filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            try {
                if (filePath != null) {

                    MediaPlayer mp = MediaPlayer.create(context, Uri.parse(filePath));
                    int duration = mp.getDuration();
                    mp.release();

                    return (TimeUnit.MILLISECONDS.toSeconds(duration)) <= TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.convert(15, TimeUnit.MINUTES));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public static boolean checkVideoDuration444(Context context, String uri, String minutes) {

        try {
            if (uri != null && minutes != null && !minutes.equalsIgnoreCase("")) {

                long duration;
                duration = VideoDurationHelper.getVideoDuration(uri);
//                try (MediaMetadataRetriever retriever = new MediaMetadataRetriever()) {
//// There are other variations of setDataSource(), if you have a different input
//                    retriever.setDataSource(context, Uri.fromFile(new File(uri)));
//                    String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//                    duration = Long.parseLong(time);
//                    retriever.release();
//                }

                return (TimeUnit.MILLISECONDS.toSeconds(duration)) <= TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.convert(Integer.parseInt(minutes), TimeUnit.MINUTES));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }


    public static void writeFile(Context context, String str) {

        //Log.d("Compression", str);

    }

    public static void verifiedUserType(Context context, String userType, RoundedImageView roundedImageView) {
        //'1','Normal'
        //'2','Influencer'
        //'3','Ambassador'
        //'4','Intern'
        //'5','Admin'
        //'6','Onourem'
        //'7','Paid'
        //'8',''
        if (!TextUtils.isEmpty(userType) && userType.equalsIgnoreCase("1")) {
            roundedImageView.setVisibility(View.GONE);
        } else if (!TextUtils.isEmpty(userType) && userType.equalsIgnoreCase("2")) {
            roundedImageView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(R.drawable.ic_verified_icon_five)
                    .into(roundedImageView);
        } else if (!TextUtils.isEmpty(userType) && userType.equalsIgnoreCase("3")) {
            roundedImageView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(R.drawable.ic_verified_icon_five)
                    .into(roundedImageView);
        } else if (!TextUtils.isEmpty(userType) && userType.equalsIgnoreCase("4")) {
            roundedImageView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(R.drawable.ic_verified_icon_five)
                    .into(roundedImageView);
        } else if (!TextUtils.isEmpty(userType) && userType.equalsIgnoreCase("5")) {
            roundedImageView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(R.drawable.ic_verified_icon_five)
                    .into(roundedImageView);
        } else if (!TextUtils.isEmpty(userType) && userType.equalsIgnoreCase("6")) {
            roundedImageView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(R.drawable.ic_verified_icon_five)
                    .into(roundedImageView);
        }else {
            roundedImageView.setVisibility(View.GONE);
        }

    }

    public static String getMaxVideoDurationText(String minutes) {
        String s;
        if (Integer.parseInt(minutes) >= 1) {
            s = String.format("Maximum duration allowed for videos is %s minutes. Please select a different video.", minutes);
        } else {
            s = String.format("Maximum duration allowed for videos is %s minute. Please select a different video.", minutes);
        }
        return s;
    }

    public static String getMaxVideoDurationTextForPickerBottomSheet(String minutes) {
        String s;
        if (Integer.parseInt(minutes) >= 1) {
            s = String.format("(Max %s mins)", minutes);
        } else {
            s = String.format("(Max %s min)", minutes);
        }
        return s;
    }

//    private static Bitmap addBorder(Bitmap resource, Context context) {
//        int w = resource.getWidth();
//        int h = resource.getHeight();
//        int radius = Math.min(h / 2, w / 2);
//        Bitmap output = Bitmap.createBitmap(w + 8, h + 8, Bitmap.Config.ARGB_8888);
//        Paint p = new Paint();
//        p.setAntiAlias(true);
//        Canvas c = new Canvas(output);
//        c.drawARGB(0, 0, 0, 0);
//        p.setStyle(Paint.Style.FILL);
//        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);
//        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        c.drawBitmap(resource, 4, 4, p);
//        p.setXfermode(null);
//        p.setStyle(Paint.Style.STROKE);
//        p.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
//        p.setStrokeWidth(3);
//        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);
//        return output;
//    }


    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     */
    public static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    public static int dp2px(Context context, int dp) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return px;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    public static String serverDateConversionTo(String requiredDateFormat, String serverTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(APP_DISPLAY_DATE_TIME_FORMAT, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getServerTimeZone(serverTime)));
        SimpleDateFormat formatter = new SimpleDateFormat(requiredDateFormat, Locale.getDefault());

        try {
            Date serverDate = ((simpleDateFormat)).parse(serverTime);

            if (serverDate != null) {
                return formatter.format(serverDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

        return "";

    }


    public static String getRelatedTime(final String serverTime) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(APP_DISPLAY_DATE_TIME_FORMAT, Locale.getDefault());
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getServerTimeZone(serverTime)));

            Date serverDate = ((simpleDateFormat)).parse(serverTime);

            long duration = 0;
            if (serverDate != null && serverDate.before(new Date())) {
                Calendar calendar = Calendar.getInstance();
                duration = calendar.getTimeInMillis() - serverDate.getTime();
            }

            StringBuilder res = new StringBuilder();

            long oneDayMillis = TimeUnit.DAYS.toMillis(1);
            long dayDiff = duration / oneDayMillis;

            if (dayDiff > 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(serverDate);

                if (dayDiff <= 7) {
                    res.append(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US));
                } else {
                    res.append(Utilities.getDateString(serverDate));
                }
            } else {
                long oneMinuteMillis = TimeUnit.MINUTES.toMillis(1);
                long minuteDiff = duration / oneMinuteMillis;
                long hourDiff = minuteDiff / 60;
                int minThreshold = 3;
                if (minuteDiff < 60 && minuteDiff > minThreshold) {
                    res.append(minuteDiff).append(" minutes");
                } else if (hourDiff >= 1 && hourDiff <= 24) {
                    if (hourDiff == 1) {
                        res.append(hourDiff).append(" hour");
                    } else {
                        res.append(hourDiff).append(" hours");
                    }
                } else if (minuteDiff < minThreshold) {
                    res.append("Just now");
                }
            }

            if ("".equals(res.toString()))
                return "Just now";
            else
                return res.toString();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Function to get Progress percentage
     *
     * @param currentDuration
     * @param totalDuration
     */
    public int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     *
     * @param progress      -
     * @param totalDuration returns current duration in milliseconds
     */
    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }
}