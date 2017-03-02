package io.gresse.hugo.cinedayfetcher.accounts.addedit;

import io.gresse.hugo.cinedayfetcher.accounts.AccountModel;

/**
 * When we want to open the edit event view
 *
 * Created by Hugo Gresse on 25/12/2016.
 */

public class OpenEditAccountEvent {

    public final AccountModel accountModel;

    public OpenEditAccountEvent(AccountModel accountModel) {
        this.accountModel = accountModel;
    }
}
