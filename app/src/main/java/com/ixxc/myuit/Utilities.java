package com.ixxc.myuit;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.util.DisplayMetrics;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utilities {
    public static SimpleDateFormat hhFormat = new SimpleDateFormat("HH");
    public static SimpleDateFormat hhmmFormat = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat ddFormat = new SimpleDateFormat("dd");
    public static SimpleDateFormat ddMMFormat = new SimpleDateFormat("dd-MM");
    public static SimpleDateFormat ddMMyyFormat = new SimpleDateFormat("dd-MM-yyyy");
    public static SimpleDateFormat fullDateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");

    public static Handler delayHandler;

//    public static DatabaseHelper db;
//
//    public static void doWriteDB(String key, String value) {
//        db.setConfigValue(key, value);
//    }
//
//    public static String doReadDB(String key) {
//        return db.getConfigValue(key);
//    }

    public static String validateString(String s) {
        StringBuilder rtnString = new StringBuilder();
        int index = 0;

        // Uppercase first letter and add space
        while (true) {
            s = s.substring(index);
            index = upperCharIndex(s);
            if (index < 0) {
                rtnString.append(s);
                break;
            } else if (index == 0) {
                rtnString.append(s.charAt(index));
                index++;
            } else {
                rtnString.append(s.substring(0, index)).append(" ");
            }
        }

        // Uppercase letter after space (if letter is not upper)
        int spaceIndex = spaceIndex(rtnString.toString());
        if (spaceIndex >= 0) {
            rtnString = new StringBuilder(rtnString.substring(0, 1).toUpperCase()
                    + rtnString.substring(1, spaceIndex + 1)
                    + rtnString.substring(spaceIndex + 1, spaceIndex + 2).toUpperCase()
                    + rtnString.substring(spaceIndex + 2));
        } else {
            rtnString = new StringBuilder(rtnString.substring(0, 1).toUpperCase() + rtnString.substring(1));
        }

        return rtnString.toString();
    }

    private static int upperCharIndex(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.isUpperCase(s.charAt(i))) { return i; }
        }
        return -1;
    }

    private static int spaceIndex(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ') { return i; }
        }
        return -1;
    }

//
//    public static void sendNoti(Context context, String title, String msg) {
//        if (MainActivity.isRunning) {
//            return;
//        }
//
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_foreground);
//
//        int notificationId;
//        try {
//            notificationId = Integer.parseInt(Utilities.doReadDB("notiId"));
//        } catch (NumberFormatException e) {
//            Log.e(JobService.LOG_TAG, "Error at sendNoti line 133" + e.getMessage());
//            notificationId = 0;
//        }
//
//        Intent notifIntent = new Intent(context, MainActivity.class);
//        notifIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//        PendingIntent notiPendingIntent = PendingIntent.getActivity(context, notificationId, notifIntent, PendingIntent.FLAG_ONE_SHOT);
//
//        Notification notification = new NotificationCompat.Builder(context, Notif.CHANNEL_1_ID)
//                .setSmallIcon(R.drawable.ic_weather)
//                .setLargeIcon(bitmap)
//                .setContentTitle(title)
//                .setContentText(msg)
//                .setContentIntent(notiPendingIntent)
//                .setColor(context.getResources().getColor(R.color.bg_secondary))
//                .setAutoCancel(true)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .build();
//
//        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
//        notificationManagerCompat.notify(notificationId, notification);
//
//        notificationId++;
//        if (notificationId > 20) {
//            notificationId = 0;
//        }
//
//        Utilities.doWriteDB("notiId", String.valueOf(notificationId));
//    }

    public static void setLocale(Activity activity, String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources res = activity.getResources();
        Configuration conf = res.getConfiguration();
        DisplayMetrics metrics = res.getDisplayMetrics();
        conf.setLocale(locale);
        res.updateConfiguration(conf, metrics);
    }

    public static void setSharedPreferences(Context ctx, String key, String value) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("MyUIT", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString(key, value);
        myEdit.apply();
    }

    public static String getSharedPreferences(Context ctx, String key) {
        SharedPreferences sh = ctx.getSharedPreferences("MyUIT", Context.MODE_PRIVATE);

        String defValue;
        switch (key) {
            case "langCode":
                defValue = "en";
                break;
            case "showMarkers":
            case "dailyRemind":
                defValue = "true";
                break;
            case "trackingMode":
            case "darkMode":
                defValue = "false";
                break;
            default:
                defValue = "";
                break;
        }


        return sh.getString(key, defValue);
    }
}
