package io.gresse.hugo.cinedayfetcher.tracking;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import io.gresse.hugo.cinedayfetcher.Configuration;


/**
 * Main entry point for tracking application activity. Sub flavors are responsable to implement
 * {@link EventSenderInterface} with a class named "EventSender" and send the proper information to each services they
 * manage.
 * <p/>
 * Created by Hugo Gresse on 25/04/16.
 */
public class EventTracker {

    public static final String TAG = EventTracker.class.getSimpleName();

    public static final int    TRACKING_FETCH_START    = 1;
    public static final int    TRACKING_FETCH_END      = 2;
    public static final int    TRACKING_FETCH_END_FAIL = 3;
    public static final String TRACKING_ACCOUNT_EDIT   = "Edit";
    public static final String TRACKING_ACCOUNT_SHARE  = "Share";
    public static final String TRACKING_ACCOUNT_COPY   = "Copy";
    public static final String TRACKING_ACCOUNT_DELETE = "Delete";
    public static final String TRACKING_ACCOUNT_ADD    = "Added";

    private static EventSenderInterface sEvent;

    public EventTracker(Context context) {
        if (!isEventEnable()) return;

        sEvent = new EventSender(context);
    }

    /**
     * Return true if event reporting is enable, checking the BuildConfig
     *
     * @return true if enable, false otherweise
     */
    public static boolean isEventEnable() {
        return !Configuration.DEBUG;
    }

    /**
     * Called by activities onStart
     */
    public static void onStart(Activity activity) {
        if (!isEventEnable()) return;

        sEvent.onStart(activity);
    }

    /**
     * Called by activties onStopre
     */
    public static void onStop() {
        if (!isEventEnable()) return;

        sEvent.onStop();
    }

    /**
     * Track fragment view, should be called in onResume
     *
     * @param fragment   the fragment name to track
     * @param screenName the additional name if any
     * @param subName    sub view name
     */
    public static void trackFragmentView(Fragment fragment, @Nullable String screenName, @Nullable String subName) {
        if (!isEventEnable()) return;

        String name;

        if (TextUtils.isEmpty(screenName)) {
            name = fragment.getClass().getSimpleName();
        } else {
            name = screenName;
        }

        if (TextUtils.isEmpty(name)) {
            name = "ERROR";
        }

        String detail = subName;
        if (TextUtils.isEmpty(detail)) {
            detail = "Fragment";
        }

        sEvent.sendView(name, detail);
    }

    /**
     * Track an undetermined error
     *
     * @param key   the key error
     * @param value the error detail
     */
    public static void trackError(String key, String value) {
        if (!isEventEnable()) return;

        sEvent.sendEvent("Error", key, value);
    }

    /**
     * Track when a new Fetch is done for an account
     */
    public static void trackAccountFetch(int state) {
        if (!isEventEnable()) return;

        switch (state) {
            case TRACKING_FETCH_START:
                sEvent.sendEvent("Account fetch", "Start", true);
                break;
            case TRACKING_FETCH_END:
                sEvent.sendEvent("Account fetch", "End", true);
                break;
            case TRACKING_FETCH_END_FAIL:
                sEvent.sendEvent("Account fetch", "End fail", true);
                break;
            default:
                // Should not be here
                Log.d(TAG, "trackAccountFetch with wrong state: " + state);
                break;
        }
    }

    /**
     * Track when a account is added
     */
    public static void trackAccountAdd() {
        if (!isEventEnable()) return;

        sEvent.sendEvent("Account", TRACKING_ACCOUNT_ADD);
    }

    /**
     * Track when a account is deleted
     */
    public static void trackAccountDelete() {
        if (!isEventEnable()) return;

        sEvent.sendEvent("Account", TRACKING_ACCOUNT_DELETE);
    }

    /**
     * Track when a account is edited
     */
    public static void trackAccountEdit() {
        if (!isEventEnable()) return;

        sEvent.sendEvent("Account", TRACKING_ACCOUNT_EDIT);
    }

    /**
     * Track when a account cineday is shared
     */
    public static void trackAccountShare() {
        if (!isEventEnable()) return;

        sEvent.sendEvent("Account", TRACKING_ACCOUNT_SHARE);
    }

    /**
     * Track when a account cineday is shared
     */
    public static void trackAccountCopy() {
        if (!isEventEnable()) return;

        sEvent.sendEvent("Account", TRACKING_ACCOUNT_COPY);
    }

}
