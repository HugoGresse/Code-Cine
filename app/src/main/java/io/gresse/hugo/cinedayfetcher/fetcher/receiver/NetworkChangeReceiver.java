package io.gresse.hugo.cinedayfetcher.fetcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.gresse.hugo.cinedayfetcher.fetcher.service.FetcherService;
import io.gresse.hugo.cinedayfetcher.utils.Utils;

/**
 * Receive network connection change
 *
 * Created by Hugo Gresse on 07/12/2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //if we have network we call
        if (Utils.isNetworkAvailable(context)) {
            Log.d("NetworkChangeReceiver", "cinedayService Network ok");
            // We start the service
            Intent msgIntent = new Intent(context, FetcherService.class);
            context.startService(msgIntent);
        }
    }

}
