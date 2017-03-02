package io.gresse.hugo.cinedayfetcher.utils;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.AttributeSet;

/**
 * A view that update itself to refresh it's relative time
 *
 * Created by Hugo Gresse on 01/03/2017.
 */

public class RelativeTextView extends android.support.v7.widget.AppCompatTextView {

    private long mTime;
    private Handler mHandler;

    public RelativeTextView(Context context) {
        super(context);
    }

    public RelativeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RelativeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTime(long time){
        mTime = time;
    }

    private void updateView(){
        this.setText( DateUtils.getRelativeTimeSpanString(
                mTime,
                System.currentTimeMillis(),
                0L,
                DateUtils.FORMAT_ABBREV_ALL));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(mHandler == null){
            mHandler = new Handler();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateView();
                mHandler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(true);
    }
}
