package io.gresse.hugo.cinedayfetcher.fetcher.event;

/**
 * A base event which store email/password
 * Created by Hugo Gresse on 13/02/2017.
 */

public abstract class BaseAccountEvent {

    public long   id;
    public String email;
    public String password;

    public BaseAccountEvent(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public BaseAccountEvent(long accountId, String email, String password) {
        this(email, password);
        this.id = accountId;
    }
}
