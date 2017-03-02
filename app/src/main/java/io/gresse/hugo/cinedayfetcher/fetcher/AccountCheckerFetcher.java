package io.gresse.hugo.cinedayfetcher.fetcher;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Use {@link Fetcher} to check if the given account is valid or not. The fetcher will fail but it should arrive at a
 * given progress to be valid.
 *
 * Created by Hugo Gresse on 08/02/2017.
 */

public class AccountCheckerFetcher {

    private String mEmail;
    private String mPassword;
    private FetcherTask mFetcher;

    public AccountCheckerFetcher(String email, String password) {
        mEmail = email;
        mPassword = password;
    }

    public void run(@NonNull final CheckerListener listener){

        mFetcher = new FetcherTask(new Fetcher.Listener() {
            boolean isValid = false;
            @Override
            public void onProgress(int current, int number) {
                if(current >= 4){
                    isValid = true;
                    listener.onAccountChecked(true);
                    mFetcher.cancel(true);
                }
            }

            @Override
            public void onFinish(boolean success, String result, @Nullable Exception exception) {
                if(isValid){
                    // Progress already catched the valid state.
                    return;
                }
                listener.onAccountChecked(success);
            }
        });
        mFetcher.execute(mEmail, mPassword);
    }

    public void stop(){
        mFetcher.cancel(true);
    }

    public interface CheckerListener{
        void onAccountChecked(boolean isValid);
    }

}
