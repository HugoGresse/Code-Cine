package io.gresse.hugo.cinedayfetcher.fetcher.event;

import io.gresse.hugo.cinedayfetcher.accounts.AccountModel;

/**
 *
 *
 * Created by Hugo Gresse on 13/02/2017.
 */

public class OnServiceFetchedEvent {

    public final AccountModel accountModel;

    public OnServiceFetchedEvent(AccountModel accountModel) {
        this.accountModel = accountModel;
    }
}
