package io.gresse.hugo.cinedayfetcher;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.gresse.hugo.cinedayfetcher.about.AboutFragment;
import io.gresse.hugo.cinedayfetcher.accounts.AccountRepository;
import io.gresse.hugo.cinedayfetcher.accounts.addedit.AddAccountEvent;
import io.gresse.hugo.cinedayfetcher.accounts.addedit.AddEditAccountFragment;
import io.gresse.hugo.cinedayfetcher.accounts.addedit.EditAccountEvent;
import io.gresse.hugo.cinedayfetcher.accounts.addedit.OpenAddAccountEvent;
import io.gresse.hugo.cinedayfetcher.accounts.addedit.OpenEditAccountEvent;
import io.gresse.hugo.cinedayfetcher.accounts.list.AccountsFragment;
import io.gresse.hugo.cinedayfetcher.fetcher.CinedayCleanerReceiver;
import io.gresse.hugo.cinedayfetcher.fetcher.NetworkChangeReceiver;


/**
 * TODO:
 * - remove all cineday at the end of mardi
 * - tracking
 */
public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    protected Snackbar mSnackbar;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //ButterKnife.setDebug(true);

        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
            getSupportActionBar().setHomeButtonEnabled(true);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        changeFragment(AccountsFragment.instantiate(this, AccountsFragment.class.getName()), true, false);

        // Plan alarm here
        planCinedayAlarm();
        planCleanAlarm();
    }

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        int fragmentCount = getSupportFragmentManager().getBackStackEntryCount();
        if (fragmentCount == 1) {
            finish();
            return;
        }
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                changeFragment(
                        Fragment.instantiate(this, AboutFragment.class.getName()),
                        true,
                        true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Subscribe
    public void openAddAccountEvent(OpenAddAccountEvent event) {
        changeFragment(Fragment.instantiate(this, AddEditAccountFragment.class.getName(), null), true, true);
    }

    @Subscribe
    public void addAccountEvent(AddAccountEvent event) {
        AccountRepository.getInstance().addAccount(this, event.email, event.password);

        changeFragment(AccountsFragment.instantiate(this, AccountsFragment.class.getName()), true, true);
    }

    @Subscribe
    public void openEditAccountEvent(OpenEditAccountEvent event) {
        changeFragment(AddEditAccountFragment.newInstance(event.accountModel), true, true);
    }

    @Subscribe
    public void editAccountEvent(EditAccountEvent event) {
        AccountRepository.getInstance().updateAccount(this, event.id, event.email, event.password);

        changeFragment(AccountsFragment.instantiate(this, AccountsFragment.class.getName()), true, false);
    }

    /**
     * Change tu current displayed fragment by a new one.
     *
     * @param frag            the new fragment to display
     * @param saveInBackstack if we want the fragment to be in backstack
     * @param animate         if we want a nice animation or not
     */
    private void changeFragment(Fragment frag,
                                boolean saveInBackstack,
                                boolean animate) {
        String log = "changeFragment: ";
        String backStateName = ((Object) frag).getClass().getName();

        try {
            FragmentManager manager = getSupportFragmentManager();
            boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

            if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null) { //fragment not in back stack, create it.
                FragmentTransaction transaction = manager.beginTransaction();

                if (animate) {
                    log += " animate";
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                }

                transaction.replace(R.id.fragment_container, frag, backStateName);

                if (saveInBackstack) {
                    log += " addToBackTack(" + backStateName + ")";
                    transaction.addToBackStack(backStateName);
                } else {
                    log += " NO addToBackTack(" + backStateName + ")";
                }

                transaction.commit();

                // If some snackbar is display, hide it
                if (mSnackbar != null && mSnackbar.isShownOrQueued()) {
                    mSnackbar.dismiss();
                }

            } else if (!fragmentPopped && manager.findFragmentByTag(backStateName) != null) {
                log += " fragment not popped but finded: " + backStateName;
            } else {
                log += " nothing to do : " + backStateName + " fragmentPopped: " + fragmentPopped;
                // custom effect if fragment is already instanciated
            }
            Log.d(TAG, log);
        } catch (IllegalStateException exception) {
            Log.w(TAG, "Unable to commit fragment, could be activity as been killed in background. " + exception.toString());
        }
    }

    public void planCinedayAlarm() {
        Intent intent = new Intent(this, NetworkChangeReceiver.class);

        intent.setAction(NetworkChangeReceiver.class.getCanonicalName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);


        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pendingIntent);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);

        Log.i(TAG, "Planning alarm for " + calendar.toString());
    }

    public void planCleanAlarm() {
        Intent intent = new Intent(this, CinedayCleanerReceiver.class);
        intent.setAction(CinedayCleanerReceiver.class.getCanonicalName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pendingIntent);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);

        Log.i(TAG, "Planning cleaningalarm for " + calendar.toString());
    }
}
