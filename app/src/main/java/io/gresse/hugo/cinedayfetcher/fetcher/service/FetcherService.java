package io.gresse.hugo.cinedayfetcher.fetcher.service;

import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.gresse.hugo.cinedayfetcher.accounts.AccountModel;
import io.gresse.hugo.cinedayfetcher.accounts.AccountRepository;
import io.gresse.hugo.cinedayfetcher.fetcher.Fetcher;
import io.gresse.hugo.cinedayfetcher.fetcher.event.OnServiceFetchedEvent;

/**
 * Manage retrieve of cineday in background
 *
 * Created by Hugo Gresse on 07/12/2016.
 */

public class FetcherService extends CustomIntentService {


    private static final String TAG = FetcherService.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public FetcherService() {
        super("Cineday.FetcherService");
    }


    @Override
    protected void onHandleMessage(Message message) {
        if (message == null) {
            return;
        }

        Log.d(TAG, "cinedayService onHandleMessage: " + message);

        List<AccountModel> accountModelList = AccountRepository.getInstance().getAccounts(this);

        for(final AccountModel accountModel : accountModelList){

            if(!accountModel.needAutoFetch()){
                Log.d(TAG, "cinedayService no need to autoFetch " + accountModel);
                return;
            }

            new Fetcher(new Fetcher.Listener() {
                @Override
                public void onProgress(int current, int number) {
                }

                @Override
                public void onFinish(boolean success, String result, @Nullable Exception exception) {
                    Log.d(TAG, "cinedayService updated " + accountModel.accountName + " success? " + success);
                    accountModel.inProgress = false;
                    if(success){
                        accountModel.setCineday(result);
                    } else {
                        accountModel.setError(result);
                    }
                    AccountRepository.getInstance().saveAccount(FetcherService.this, accountModel);
                    EventBus.getDefault().post(new OnServiceFetchedEvent(accountModel));
                }
            }).fetch(accountModel.accountName, accountModel.accountPassword);
        }
    }
}
