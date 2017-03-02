package io.gresse.hugo.cinedayfetcher.fetcher.event;

import io.gresse.hugo.cinedayfetcher.accounts.AccountModel;

/**
 * When you want to manually fetch the cineday
 *
 * Created by Hugo Gresse on 13/02/2017.
 */

public class FetchEvent {

    public AccountModel accountModel;

    public FetchEvent(AccountModel accountModel) {
        this.accountModel = accountModel;
    }
}
