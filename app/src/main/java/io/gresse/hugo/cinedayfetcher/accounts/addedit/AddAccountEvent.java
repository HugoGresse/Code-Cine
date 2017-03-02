package io.gresse.hugo.cinedayfetcher.accounts.addedit;

import io.gresse.hugo.cinedayfetcher.fetcher.event.BaseAccountEvent;

/**
 * When we want to add a new account to the app.
 *
 * Created by Hugo Gresse on 08/02/2017.
 */

public class AddAccountEvent extends BaseAccountEvent {

    public AddAccountEvent(String email, String password) {
        super(email, password);
    }
}
