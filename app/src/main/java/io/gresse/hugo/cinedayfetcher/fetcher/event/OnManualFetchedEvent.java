package io.gresse.hugo.cinedayfetcher.fetcher.event;

/**
 * Fired from {@link io.gresse.hugo.cinedayfetcher.fetcher.ManualFetcher}
 *
 * Created by Hugo Gresse on 13/02/2017.
 */

public class OnManualFetchedEvent {

    public final boolean isValid;
    public final FetchEvent fetchEvent;
    public final String result;

    public OnManualFetchedEvent(FetchEvent fetchEvent, boolean isValid, String result) {
        this.fetchEvent = fetchEvent;
        this.isValid = isValid;
        this.result = result;
    }
}
