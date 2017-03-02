package io.gresse.hugo.cinedayfetcher.fetcher;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import io.gresse.hugo.cinedayfetcher.Configuration;

/**
 * Retrieve/scrap a cineday from the web using JSOUP
 * <p>
 * Created by Hugo Gresse on 06/12/2016.
 */
public class Fetcher {

    private static final String TAG     = Fetcher.class.getSimpleName();
    public static final  int    TIMEOUT = 10000;

    private Handler mMainHandler;

    @Nullable
    private Listener mListener;

    Fetcher(@Nullable Listener listener) {
        mListener = listener;
    }

    protected String fetch(String email, String password) {
        try {
            /*
             * 1. Get SSO Page
             * 2. Send credentials to SSO
             * 3. Get cineday homepage
             * 4. send ajax request
             * 5. parse result of the ajax request
             */
            final int STEP = 5;

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                throw new IllegalArgumentException("Missing credentials");
            }

            postProgress(0, STEP);

            Connection.Response loginForm = Jsoup.connect("https://ssl-sso.orange.fr/authM/bin/omlForm.cgi?return_url=http://cineday.orange.fr/cineday/")
                    .userAgent(Configuration.USER_AGENT)
                    .timeout(TIMEOUT)
                    .method(Connection.Method.GET)
                    .execute();

            postProgress(1, STEP);

            Connection.Response cinedayHomeResponse = Jsoup.connect("https://ssl-sso.orange.fr/authM/bin/omlForm.cgi?check=1_SEP_return_url=http://cineday.orange.fr/cineday/_SEP_MCO=OFR")
                    .userAgent(Configuration.USER_AGENT)
                    .data("credential", email)
                    .data("pwd", password)
                    .cookies(loginForm.cookies())
                    .method(Connection.Method.POST)
                    .execute();

            postProgress(2, STEP);

            Connection.Response responseCineday = Jsoup.connect("http://mdsp.orange.fr/cineday/commande/loadingWebPage")
                    .userAgent(Configuration.USER_AGENT)
                    .cookies(cinedayHomeResponse.cookies())
                    .method(Connection.Method.GET)
                    .execute();

            postProgress(3, STEP);

            Connection.Response finalResponse = Jsoup.connect("http://mdsp.orange.fr/cineday/commande/pinRequestWeb")
                    .userAgent(Configuration.USER_AGENT)
                    .data("portfolioId", "0")
                    .cookies(responseCineday.cookies())
                    .execute();

            // Check the cookie to see if the user is correctly logged or not
            if (TextUtils.isEmpty(finalResponse.cookie("wassup"))) {
                Log.i(TAG, "Missing wassup cookie on the final response, is the credentials valid?");
                postFinish(false, "Identifiants non valides", null);
                return "Identifiants non valides";
            }

            Document cinedayReceiver = finalResponse.parse();

            postProgress(4, STEP);

            Elements elements = cinedayReceiver.getElementsByAttributeValue("class", "imageCodeNotUsed");

            if (elements.size() > 0) {
                Log.d(TAG, elements.text());

                String cineday = elements.get(0).text();

                postFinish(true, cineday, null);
                return cineday;
            }

            Elements errorElements = cinedayReceiver.getElementsByAttributeValue("class", "errorBox");

            String error = "Erreur. " + errorElements.text().replace("08h00", "09h00");

            Log.i(TAG, "AsyncTask finish with an error");
            Log.d(TAG, cinedayReceiver.text());

            postFinish(false, error, null);

            return error;
        } catch (IOException e) {
            Log.e(TAG, "Fail to retrieve cineday, exception ", e);

            postFinish(false, e.toString(), null);

            return e.toString();
        }
    }

    private void postProgress(final int step, final int number) {
        if (mListener != null) {
            mListener.onProgress(step, number);
        }
    }

    private void postFinish(final boolean success, final String result, final @Nullable Exception e) {
        if (mListener != null) {
            mListener.onFinish(success, result, e);
        }
    }

    public interface Listener {
        void onProgress(int current, int number);

        void onFinish(boolean success, String result, @Nullable Exception exception);
    }
}
