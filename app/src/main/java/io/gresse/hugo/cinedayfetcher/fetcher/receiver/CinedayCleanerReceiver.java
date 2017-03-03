package io.gresse.hugo.cinedayfetcher.fetcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.gresse.hugo.cinedayfetcher.fetcher.service.CleaningService;

/**
 * Receive network connection change
 *
 * Created by Hugo Gresse on 07/12/2016.
 */
public class CinedayCleanerReceiver extends BroadcastReceiver {

    public static final String TAG = CinedayCleanerReceiver.class.toString();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Cleaning cineday now");

        Intent msgIntent = new Intent(context, CleaningService.class);
        context.startService(msgIntent);
    }

}
