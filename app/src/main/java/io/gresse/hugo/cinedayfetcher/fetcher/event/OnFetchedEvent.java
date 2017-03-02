package io.gresse.hugo.cinedayfetcher.fetcher.event;

/**
 *
 *
 * Created by Hugo Gresse on 13/02/2017.
 */

public class OnFetchedEvent {

    public final boolean isValid;
    public final FetchEvent fetchEvent;
    public final String result;

    public OnFetchedEvent(FetchEvent fetchEvent, boolean isValid, String result) {
        this.fetchEvent = fetchEvent;
        this.isValid = isValid;
        this.result = result;
    }
}
