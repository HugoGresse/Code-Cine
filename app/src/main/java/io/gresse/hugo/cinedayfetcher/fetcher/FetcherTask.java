package io.gresse.hugo.cinedayfetcher.fetcher;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Add an Async task to {@link Fetcher}
 *
 * Created by Hugo Gresse on 27/02/2017.
 */

public class FetcherTask  extends AsyncTask<String, Void, Void> implements Fetcher.Listener {

    private static final String TAG     = FetcherTask.class.getSimpleName();

    private Handler mMainHandler;

    @Nullable
    private Fetcher.Listener mListener;

    FetcherTask(@Nullable Fetcher.Listener listener) {
        mMainHandler = new Handler(Looper.getMainLooper());
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... params) {
        String email = String.valueOf(params[0]);
        String password = String.valueOf(params[1]);

        new Fetcher(this).fetch(email, password);

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        // Already done...
    }

    @Override
    public void onProgress(final int current, final int number) {
        Log.d(TAG, "progress : " + current + " number: " + number);
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onProgress(current, number);
                }
            }
        });
    }

    @Override
    public void onFinish(final boolean success, final String result, @Nullable final Exception exception) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onFinish(success, result, exception);
                }
            }
        });
    }
}
