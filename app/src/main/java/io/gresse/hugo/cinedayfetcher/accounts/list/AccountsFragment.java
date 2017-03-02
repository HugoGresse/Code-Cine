package io.gresse.hugo.cinedayfetcher.accounts.list;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.gresse.hugo.cinedayfetcher.R;
import io.gresse.hugo.cinedayfetcher.accounts.AccountModel;
import io.gresse.hugo.cinedayfetcher.accounts.AccountRepository;
import io.gresse.hugo.cinedayfetcher.accounts.addedit.OpenAddAccountEvent;
import io.gresse.hugo.cinedayfetcher.accounts.addedit.OpenEditAccountEvent;
import io.gresse.hugo.cinedayfetcher.fetcher.ManualFetcher;
import io.gresse.hugo.cinedayfetcher.fetcher.event.FetchEvent;
import io.gresse.hugo.cinedayfetcher.fetcher.event.OnFetchedEvent;
import io.gresse.hugo.cinedayfetcher.fetcher.event.OnFetchedFromServiceEvent;
import io.gresse.hugo.cinedayfetcher.utils.Utils;

/**
 * List saved accounts and display cineday if available
 * <p>
 * Created by Hugo Gresse on 08/12/2016.
 */

public class AccountsFragment extends Fragment implements AccountsAdapter.Listener {

    private static final String TAG = AccountsFragment.class.getSimpleName();


    @BindView(R.id.recyclerView)
    public RecyclerView mRecyclerView;

    private AccountsAdapter     mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ManualFetcher       mManualFetcher;
    private List<AccountModel> mAccountModels = new ArrayList<>();

    private Unbinder mUnbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mManualFetcher = new ManualFetcher();
    }


    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_accounts, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new AccountsAdapter(getContext(), this);

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        mManualFetcher.onResume();

        loadData();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        mManualFetcher.onPause();
    }

    private void loadData() {
        mAccountModels = AccountRepository.getInstance().getAccounts(getContext());

        if (mAdapter != null) {
            mAdapter.setData(mAccountModels);
        }

        Log.d(TAG, "loadData:  " + mAccountModels);
    }

    @Subscribe
    public void onAccountsChange(AccountChangeEvent accountChange) {
        Log.d(TAG, "account change");
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCinedayFetchedEvent(OnFetchedEvent event) {
        EventBus.getDefault().removeStickyEvent(event);

        AccountModel accountModel = event.fetchEvent.accountModel;

        accountModel.inProgress = false;

        if (event.isValid) {
            accountModel.setCineday(event.result);
        } else {
            accountModel.setError(event.result);
        }
        int position = mAccountModels.indexOf(accountModel);
        mAccountModels.set(position, accountModel);
        AccountRepository.getInstance().saveAccounts(getContext(), mAccountModels);
        mAdapter.updateData(mAccountModels, position);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFetcherServiceUpdate(OnFetchedFromServiceEvent event) {
        int position = mAccountModels.indexOf(event.accountModel);
        mAccountModels.set(position, event.accountModel);
        AccountRepository.getInstance().saveAccounts(getContext(), mAccountModels);
        mAdapter.updateData(mAccountModels, position);
    }

    @Override
    public void onAddClick() {
        EventBus.getDefault().post(new OpenAddAccountEvent());
    }

    @Override
    public void onRefreshClick(final int position) {
        final AccountModel accountModel = mAccountModels.get(position);

        accountModel.inProgress = true;
        mAccountModels.set(position, accountModel);
        mAdapter.updateData(mAccountModels, position);

        EventBus.getDefault().post(new FetchEvent(accountModel));
    }

    @Override
    public void onDeleteClick(final int position) {
        new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_delete)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete)
                .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AccountModel accountModel = mAccountModels.get(position);
                        mAccountModels = AccountRepository.getInstance().deleteAccount(getContext(), accountModel);
                        mAdapter.updateDataWithRemove(mAccountModels, position);
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    @Override
    public void onEditClick(int position) {
        EventBus.getDefault().post(new OpenEditAccountEvent(mAccountModels.get(position)));
    }

    @Override
    public void onShareClick(int position) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        sharingIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.app_name));

        sharingIntent.putExtra(
                Intent.EXTRA_TEXT,
                mAccountModels.get(position).getCinedayOrError());

        getActivity().startActivity(
                Intent.createChooser(
                        sharingIntent,
                        getActivity().getResources().getString(R.string.action_share)));
    }

    @Override
    public void onCopyClick(int position) {
        Utils.copyToClipboard(getContext(), getString(R.string.cineday), mAccountModels.get(position).getCinedayOrError());
        Toast.makeText(getContext(), getString(R.string.notice_copied), Toast.LENGTH_SHORT).show();
    }
}
