package io.gresse.hugo.cinedayfetcher.accounts;

import android.text.TextUtils;

/**
 * Store an account credentials and cineday related
 * Created by Hugo Gresse on 24/12/2016.
 */

public class AccountModel {

    public long   id;
    public String accountName;
    public String accountPassword;
    public String tempCineday;
    public String error;
    // Timestamp
    public long updatedAt;
    public boolean cinedayLoaded;
    public boolean inProgress;

    public AccountModel(String accountName, String accountPassword) {
        this.id = System.nanoTime();
        this.accountName = accountName;
        this.accountPassword = accountPassword;
        this.cinedayLoaded = false;
    }

    private void updateUpdateField(){
        updatedAt = System.currentTimeMillis();
    }

    public void setError(String error){
        updateUpdateField();
        tempCineday =  null;
        this.error = error;
        cinedayLoaded = false;
    }

    public String getCinedayOrError(){
        if(TextUtils.isEmpty(error)){
            return tempCineday;
        } else {
            return error;
        }
    }

    public void setCineday(String cineday){
        updateUpdateField();
        error = null;
        cinedayLoaded = true;
        tempCineday = cineday;
    }

    public boolean isCinedayLoaded(){
        return cinedayLoaded;
    }

    public boolean needAutoFetch(){
        if(inProgress || tempCineday != null || cinedayLoaded || error != null){
            return false;
        }
        return true;
    }

    public void cleanFields() {
        updateUpdateField();
        error = null;
        cinedayLoaded = inProgress = false;
        tempCineday = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountModel that = (AccountModel) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return accountName.hashCode();
    }

    @Override
    public String toString() {
        return "AccountModel name:" + accountName;
    }

}
