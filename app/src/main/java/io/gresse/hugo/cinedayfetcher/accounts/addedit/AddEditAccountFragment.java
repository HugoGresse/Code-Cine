package io.gresse.hugo.cinedayfetcher.accounts.addedit;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import io.gresse.hugo.cinedayfetcher.R;
import io.gresse.hugo.cinedayfetcher.accounts.AccountModel;
import io.gresse.hugo.cinedayfetcher.fetcher.AccountCheckerFetcher;
import io.gresse.hugo.cinedayfetcher.tracking.EventTracker;
import io.gresse.hugo.cinedayfetcher.utils.Utils;

/**
 * Add a new Orange account to the app
 * Created by Hugo Gresse on 25/12/2016.
 */

public class AddEditAccountFragment extends Fragment {

    private static final String TAG = AddEditAccountFragment.class.getSimpleName();

    public static final String EXTRA_ACCOUNTID = "id";
    public static final String EXTRA_EMAIL     = "email";
    public static final String EXTRA_PASSWORD  = "pwd";

    @BindView(R.id.emailContainer)
    TextInputLayout mEmailTextInputLayout;

    @BindView(R.id.emailEditText)
    EditText mEmailEditText;

    @BindView(R.id.passwordContainer)
    TextInputLayout mPasswordTextInputLayout;

    @BindView(R.id.passwordEditText)
    EditText mPasswordEditText;

    @BindView(R.id.verifResultTextView)
    TextView mVerifResultTextView;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.okButton)
    Button mOkButton;

    private long                   mAccountId;
    @Nullable
    private String                mEmail;
    @Nullable
    private String                mPassword;
    @Nullable
    private AccountCheckerFetcher mAccountCheckerFetcher;
    private Unbinder              mUnbinder;
    private boolean               mEditMode;

    public static AddEditAccountFragment newInstance(@Nullable AccountModel accountModel) {
        AddEditAccountFragment fragment = new AddEditAccountFragment();

        if (accountModel != null) {
            Bundle args = new Bundle();
            args.putLong(EXTRA_ACCOUNTID, accountModel.id);
            args.putString(EXTRA_EMAIL, accountModel.accountName);
            args.putString(EXTRA_PASSWORD, accountModel.accountPassword);
            fragment.setArguments(args);
        }

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mAccountId = getArguments().getLong(EXTRA_ACCOUNTID);
            mEmail = getArguments().getString(EXTRA_EMAIL);
            mPassword = getArguments().getString(EXTRA_PASSWORD);
        }

        if (!TextUtils.isEmpty(mEmail) && !TextUtils.isEmpty(mPassword)) {
            mEditMode = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_addedit_account, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mEditMode) {
            mOkButton.setText(R.string.action_account_save);
        } else {
            mOkButton.setText(R.string.action_account_add);
        }

        if (mEmail != null) {
            mEmailEditText.setText(mEmail);
        }

        if (mPassword != null) {
            mPasswordEditText.setText(mPassword);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventTracker.trackFragmentView(this, null, null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick(R.id.verifButton)
    public void onVerifyClick() {
        hideVerifyFeedback();
        String dataError = isAccountViewValid();
        if (dataError != null) {
            Toast.makeText(getActivity(), dataError, Toast.LENGTH_SHORT).show();
            return;
        }

        checkAccount(mEmail, mPassword);
    }

    @OnClick(R.id.okButton)
    public void onOkClick() {
        String dataError = isAccountViewValid();
        if (dataError != null) {
            Toast.makeText(getActivity(), dataError, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mEditMode) {
            EventBus.getDefault().post(new EditAccountEvent(mAccountId, mEmail, mPassword));
        } else {
            EventBus.getDefault().post(new AddAccountEvent(mEmail, mPassword));
        }
    }

    @OnTextChanged(R.id.emailEditText)
    public void onEmailChange(CharSequence value, int start, int before, int count) {
        mEmail = value.toString().trim();
    }

    @OnTextChanged(R.id.passwordEditText)
    public void onPasswordChange(CharSequence value, int start, int before, int count) {
        mPassword = value.toString().trim();
    }

    @Nullable
    private String isAccountViewValid() {
        if (TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mPassword)) {
            return getString(R.string.new_account_add_error_data);
        }

        if (!Utils.isValidEmail(mEmail)) {
            return getString(R.string.new_account_add_error_email);
        }

        return null;
    }

    private void hideVerifyFeedback() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        mVerifResultTextView.setVisibility(View.GONE);
    }

    private void displayVerifyFeedback(boolean validConnection) {
        int drawableResId;
        int colorResId;

        mVerifResultTextView.setVisibility(View.VISIBLE);

        if (validConnection) {
            mVerifResultTextView.setText(R.string.new_account_add_check_valid);
            colorResId = R.color.colorValid;
            drawableResId = R.drawable.ic_check_green_24dp;
        } else {
            mVerifResultTextView.setText(R.string.new_account_add_check_error);
            colorResId = R.color.colorError;
            drawableResId = R.drawable.ic_error_red_24dp;
        }

        // Set Color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            colorResId = getResources().getColor(colorResId, null);
        } else {
            //noinspection deprecation
            colorResId = getResources().getColor(colorResId);
        }
        mVerifResultTextView.setTextColor(colorResId);

        // Set icon
        Drawable drawable;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            drawable = getResources().getDrawable(drawableResId, null);
        } else {
            //noinspection deprecation
            drawable = getResources().getDrawable(drawableResId);
        }
        mVerifResultTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    private void checkAccount(String email, String password) {
        if (mAccountCheckerFetcher != null) {
            mAccountCheckerFetcher.stop();
        }
        mProgressBar.setVisibility(View.VISIBLE);
        mAccountCheckerFetcher = new AccountCheckerFetcher(email, password);
        mAccountCheckerFetcher.run(new AccountCheckerFetcher.CheckerListener() {
            @Override
            public void onAccountChecked(boolean isValid) {
                mProgressBar.setVisibility(View.GONE);
                displayVerifyFeedback(isValid);
            }
        });
    }
}
