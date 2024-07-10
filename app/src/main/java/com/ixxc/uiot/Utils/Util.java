package com.ixxc.uiot.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;

import androidx.core.content.res.ResourcesCompat;

public class Util {
    // TODO: Reduce the number of static variables and methods

    public static String LOG_TAG = "API_LOG";
    public static String WIDGET_KEY = "WIDGET";
    public static String baseUrl = "https://ixxc.id.vn/";
    public static final int UPDATE_DEVICE = 1001;

    // Delay handler for post delayed tasks
    public static Handler delayHandler;

    // Format camel case back to normal string
    public static String formatString(String s) {
        if (TextUtils.isEmpty(s)) return s;

        char first = Character.toUpperCase(s.charAt(0));
        return first + s.substring(1).replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }

    // TODO: Check if this is needed
    public static String capitalizeFirst(String s){
        if (s != null){
            s = s.replace("_"," ");
            return String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1).toLowerCase();
        }
        return null;
    }

    // Convert dp to px
    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    // Get input type for edit text based on data type
    public static int getInputType(String type) {
        switch (type) {
            case "JSONObject":
            case "JSONArray":
            case "GEO_JSONPoint":
            case "JSON":
            case "booleanMap":
            case "integerMap":
            case "numberMap":
            case "multivaluedTextMap":
            case "agentLink":
            case "attributeLink[]":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
            case "timestamp":
            case "timestampISO8601":
            case "dateAndTime":
            case "timeDurationISO8601":
            case "periodDurationISO8601":
            case "timeAndPeriodDurationISO8601":
            case "integer":
            case "long":
            case "bigInteger":
            case "number":
            case "bigNumber":
            case "TCP_IPPortNumber":
            case "positiveInteger":
            case "positiveNumber":
            case "negativeInteger":
            case "negativeNumber":
            case "integerByte":
            case "byte":
            case "boolean":
            case "Polling Millis":
                return InputType.TYPE_CLASS_NUMBER;
            case "attributeLink":
            case "HTTP_URL":
            case "WS_URL":
                return InputType.TYPE_TEXT_VARIATION_URI;
            case "email":
                return InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
            default:
                return InputType.TYPE_CLASS_TEXT;
        }
    }

    // Get color
    public static int getColor(Context context, int colorRes) {
        return ResourcesCompat.getColor(context.getResources(), colorRes, null);
    }

    // Edit saved preferences
    public static void savePreferences(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // Get saved preferences
    public static String getPreferences(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
//        sharedPreferences.edit().clear().apply();
        return sharedPreferences.getString(key, "");
    }

    public static ColorStateList getColorStateList(int currentColor) {
        return new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_enabled},
                        new int[]{-android.R.attr.state_enabled},
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_pressed}
                },
                new int[]{
                        currentColor,
                        currentColor,
                        currentColor,
                        currentColor
                }
        );
    }
}
