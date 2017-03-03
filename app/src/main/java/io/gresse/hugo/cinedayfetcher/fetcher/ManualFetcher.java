package io.gresse.hugo.cinedayfetcher.fetcher;

import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.gresse.hugo.cinedayfetcher.fetcher.event.FetchEvent;
import io.gresse.hugo.cinedayfetcher.fetcher.event.OnManualFetchedEvent;

/**
 * Retrieve the cineday for one account
 *
 * Created by Hugo Gresse on 09/02/2017.
 */

public class ManualFetcher {

    private static final String TAG = ManualFetcher.class.getSimpleName();

    public void onResume(){
        EventBus.getDefault().register(this);
    }

    public void onPause(){
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onFetchEvent(final FetchEvent event){
        new FetcherTask(new Fetcher.Listener() {
            @Override
            public void onProgress(int current, int number) {
            }

            @Override
            public void onFinish(boolean success, String result, @Nullable Exception exception) {
                EventBus.getDefault().post(new OnManualFetchedEvent(event, success, result));
            }
        }).execute(event.accountModel.accountName, event.accountModel.accountPassword);
    }
}
