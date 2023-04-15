package com.ixxc.uiot;

import android.content.Context;
import android.os.Handler;

public class Utils {

    public static Handler delayHandler;

    // Format camel case back to normal string
    public static String formatString(String s) {
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

    // Convert dp to px
    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
