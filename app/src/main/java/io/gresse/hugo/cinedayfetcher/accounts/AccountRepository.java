package io.gresse.hugo.cinedayfetcher.accounts;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.securepreferences.SecurePreferences;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.gresse.hugo.cinedayfetcher.tracking.EventTracker;

/**
 * Manage storage of Orange accounts
 * <p>
 * Created by Hugo Gresse on 24/12/2016.
 */

public class AccountRepository {

    public static final String PREF_KEY = AccountRepository.class.getCanonicalName();

    @Nullable
    private static AccountRepository sInstance;

    private SecurePreferences mSecurePreferences;

    public static AccountRepository getInstance() {
        if (sInstance == null) {
            sInstance = new AccountRepository();
        }

        return sInstance;
    }

    private AccountRepository() {

    }

    public void init(Context context) {
        if (mSecurePreferences == null) {
            mSecurePreferences = new SecurePreferences(context);
        }
    }

    public List<AccountModel> getAccounts(Context context) {
        init(context);

        Type listType = new TypeToken<ArrayList<AccountModel>>() {
        }.getType();

        List<AccountModel> lists = new Gson().fromJson(mSecurePreferences.getString(PREF_KEY, ""), listType);

        return (lists == null) ? new ArrayList<AccountModel>() : lists;
    }

    public AccountModel addAccount(Context context, String email, String password) {
        init(context);

        List<AccountModel> accountModelList = getAccounts(context);

        AccountModel accountModel = new AccountModel(email, password);

        accountModelList.add(accountModel);

        mSecurePreferences.edit().putString(PREF_KEY, new Gson().toJson(accountModelList)).apply();

        EventTracker.trackAccountAdd();

        return accountModel;
    }

    public void saveAccounts(Context context, List<AccountModel> accountModels) {
        init(context);

        mSecurePreferences.edit().putString(PREF_KEY, new Gson().toJson(accountModels)).apply();
    }

    public List<AccountModel> deleteAccount(Context context, AccountModel accountModel) {
        init(context);

        List<AccountModel> accountModelList = getAccounts(context);

        accountModelList.remove(accountModel);

        mSecurePreferences.edit().putString(PREF_KEY, new Gson().toJson(accountModelList)).apply();

        EventTracker.trackAccountDelete();

        return accountModelList;
    }

    public void updateAccount(Context context, long accountId, String email, String password) {
        init(context);

        List<AccountModel> accountModelList = getAccounts(context);

        for (AccountModel accountModel : accountModelList) {
            if (accountModel.id == accountId) {
                accountModel.accountName = email;
                accountModel.accountPassword = password;
                accountModel.cleanFields();
                break;
            }
        }

        EventTracker.trackAccountEdit();

        mSecurePreferences.edit().putString(PREF_KEY, new Gson().toJson(accountModelList)).apply();
    }

    public void saveAccount(Context context, AccountModel accountModel) {
        init(context);

        List<AccountModel> accountModelList = getAccounts(context);

        int index = accountModelList.indexOf(accountModel);
        if(index >= 0){
            accountModelList.set(index, accountModel);
        }

        mSecurePreferences.edit().putString(PREF_KEY, new Gson().toJson(accountModelList)).apply();
    }

}
