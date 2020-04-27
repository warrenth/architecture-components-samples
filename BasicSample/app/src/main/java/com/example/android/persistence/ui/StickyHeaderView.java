package com.example.android.persistence.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.persistence.R;

import androidx.annotation.Nullable;

public class StickyHeaderView extends LinearLayout {

    private TextView mTextTitle;

    public StickyHeaderView(Context context) {
        super(context);
        initView(context);
    }

    public StickyHeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public StickyHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mTextTitle = (TextView) inflate(context, R.layout.sticky_item, null);
        setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(mTextTitle, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 200));
    }

    public void setTextTitle(String s) {
        mTextTitle.setText(s);
    }

}
