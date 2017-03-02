package io.gresse.hugo.cinedayfetcher.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * General utility class
 *
 * Created by Hugo Gresse on 07/12/2016.
 */
public class Utils {

    /**
     * @return true if the phone network is available, false otherwise
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    /**
     * Copy given context to device clipboard
     *
     * @param context app context
     * @param title   title/label of this clipboard
     * @param content text to be copied
     */
    public static void copyToClipboard(@NonNull Context context, String title, String content){
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText(title, content);
        clipboard.setPrimaryClip(clip);
    }
}
