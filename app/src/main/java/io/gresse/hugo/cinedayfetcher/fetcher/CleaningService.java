package io.gresse.hugo.cinedayfetcher.fetcher;

import android.os.Message;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.gresse.hugo.cinedayfetcher.accounts.AccountModel;
import io.gresse.hugo.cinedayfetcher.accounts.AccountRepository;
import io.gresse.hugo.cinedayfetcher.fetcher.event.OnFetchedFromServiceEvent;

/**
 * Clean cineday
 *
 * Created by Hugo Gresse on 02/03/2016.
 */

public class CleaningService extends CustomIntentService {


    private static final String TAG = CleaningService.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public CleaningService() {
        super("Cineday.CleaningService");
    }


    @Override
    protected void onHandleMessage(Message message) {
        if (message == null) {
            return;
        }

        Log.d(TAG, "CleaningService onHandleMessage: " + message);

        List<AccountModel> accountModelList = AccountRepository.getInstance().getAccounts(this);

        for(final AccountModel accountModel : accountModelList){

            accountModel.cleanFields();

            AccountRepository.getInstance().saveAccount(CleaningService.this, accountModel);
            EventBus.getDefault().post(new OnFetchedFromServiceEvent(accountModel));
        }
    }
}
