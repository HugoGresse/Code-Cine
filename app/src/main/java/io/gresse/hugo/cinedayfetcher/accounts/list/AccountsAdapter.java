package io.gresse.hugo.cinedayfetcher.accounts.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.gresse.hugo.cinedayfetcher.R;
import io.gresse.hugo.cinedayfetcher.accounts.AccountModel;
import io.gresse.hugo.cinedayfetcher.utils.RelativeTextView;

/**
 * Created by Hugo Gresse on 07/12/2016.
 */

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.Holder> {

    public static final String TAG = AccountsAdapter.class.getSimpleName();

    public static final int HOLDER_ACCOUNT = 1;
    public static final int HOLDER_ADD     = 2;

    private       List<AccountModel> mAccountList;
    @NonNull
    private final Listener           mListener;
    private final Animation          mRotateAnimation;
    private final int                mTextSizeNormal;
    private final int                mTextSizeBig;

    public AccountsAdapter(Context context, @NonNull Listener listener) {
        mListener = listener;
        mAccountList = new ArrayList<>();
        mRotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate);
        mTextSizeNormal = (int) context.getResources().getDimension(R.dimen.text_size_cineday_normal);
        mTextSizeBig = (int) context.getResources().getDimension(R.dimen.text_size_cineday_big);
    }

    public void setData(List<AccountModel> accountModels) {
        Log.d(TAG, "setData");
        mAccountList = accountModels;
    }

    public void updateData(List<AccountModel> accountModels, int positionChange) {
        Log.d(TAG, "updateData");
        mAccountList = accountModels;
        this.notifyItemChanged(positionChange);
    }

    public void updateDataWithRemove(List<AccountModel> accountModels, int positionChange) {
        Log.d(TAG, "updateDataWithRemove");
        mAccountList = accountModels;
        this.notifyItemRemoved(positionChange);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_account, parent, false);
                return new AccountHolder(v);
            case HOLDER_ADD:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_account_add, parent, false);
                return new AddHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if (position < mAccountList.size()) {
            holder.setData(mAccountList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mAccountList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mAccountList.size()) {
            return HOLDER_ADD;
        } else {
            return HOLDER_ACCOUNT;
        }
    }

    abstract class Holder extends RecyclerView.ViewHolder {


        Holder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void setData(@Nullable Object object) {
        }

    }

    class AddHolder extends Holder {


        AddHolder(View itemView) {
            super(itemView);
        }

        public void setData(@Nullable Object object) {
        }

        @OnClick(R.id.okButton)
        public void onAddClick(View view) {
            mListener.onAddClick();
        }
    }

    class AccountHolder extends Holder {

        @BindView(R.id.accountName)
        public TextView         mAccountName;
        @BindView(R.id.date)
        public RelativeTextView mDateTextView;
        @BindView(R.id.cineday)
        public TextView         mCinedayTextView;

        @BindView(R.id.refreshButton)
        public ImageButton mRefreshButton;

        public AccountHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void setData(Object object) {
            AccountModel accountModel = (AccountModel) object;
            mAccountName.setText(accountModel.accountName);

            if (accountModel.updatedAt != 0L) {
                mDateTextView.setVisibility(View.VISIBLE);
                mDateTextView.setTime(accountModel.updatedAt);
            } else {
                mDateTextView.setVisibility(View.GONE);
            }

            mCinedayTextView.setText(accountModel.getCinedayOrError());

            if (accountModel.isCinedayLoaded()) {
                mCinedayTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSizeBig);
            } else {
                mCinedayTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSizeNormal);
            }

            if (accountModel.inProgress) {
                mRefreshButton.startAnimation(mRotateAnimation);
            } else {
                mRefreshButton.clearAnimation();
            }
        }

        @OnClick(R.id.shareButton)
        public void onShareClick() {
            mListener.onShareClick(getAdapterPosition());
        }

        @OnClick(R.id.refreshButton)
        public void onRefreshClick() {
            mListener.onRefreshClick(getAdapterPosition());
        }

        @OnClick(R.id.optionButton)
        public void onOptionClick(View view) {
            PopupMenu popup = new PopupMenu(view.getContext(), view);
            popup.inflate(R.menu.menu_account);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_copy:
                            mListener.onCopyClick(getAdapterPosition());
                            break;
                        case R.id.action_edit:
                            mListener.onEditClick(getAdapterPosition());
                            break;
                        case R.id.action_delete:
                            mListener.onDeleteClick(getAdapterPosition());
                            break;
                    }
                    return false;
                }
            });
            popup.show();
        }
    }

    public interface Listener {
        void onAddClick();

        void onRefreshClick(int position);

        void onCopyClick(int position);

        void onDeleteClick(int position);

        void onEditClick(int position);

        void onShareClick(int position);

    }

}
