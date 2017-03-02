package io.gresse.hugo.cinedayfetcher.accounts.addedit;

import io.gresse.hugo.cinedayfetcher.fetcher.event.BaseAccountEvent;

/**
 * When we want to edit a new account to the app.
 *
 * Created by Hugo Gresse on 08/02/2017.
 */

public class EditAccountEvent extends BaseAccountEvent {

    public EditAccountEvent(long accountId, String email, String password) {
        super(accountId, email, password);
    }
}
