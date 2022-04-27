package org.grameen.fdp.kasapin.ui.detailedYearMonthlyView;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.codecrafters.tableview.TableHeaderAdapter;

public class DetailedYearTableHeaderAdapter extends TableHeaderAdapter {
    static View.OnClickListener mHeaderClickListener;
    private final String[] headers;
    private int paddingLeft = 20;
    private int paddingTop = 20;
    private int paddingRight = 20;
    private int paddingBottom = 20;
    private int textSize = 16;
    private int typeface = Typeface.NORMAL;
    private int textColor = -1728053248;

    public DetailedYearTableHeaderAdapter(Context context, String... headers) {
        super(context);
        this.headers = headers;
    }

    public DetailedYearTableHeaderAdapter(Context context, int... headerStringResources) {
        super(context);
        this.headers = new String[headerStringResources.length];
        for (int i = 0; i < headerStringResources.length; ++i) {
            this.headers[i] = context.getString(headerStringResources[i]);
        }
    }

    public void setHeaderClickListener(final View.OnClickListener mItemSelectedListener) {
        mHeaderClickListener = mItemSelectedListener;
    }

    public void setPadding(int left, int top, int right, int bottom) {
        this.paddingLeft = left;
        this.paddingTop = top;
        this.paddingRight = right;
        this.paddingBottom = bottom;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setTypeface(int typeface) {
        this.typeface = typeface;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public View getHeaderView(final int columnIndex, ViewGroup parentView) {
        final TextView textView = new TextView(this.getContext());
        TypedValue outValue = new TypedValue();

        if (columnIndex < this.headers.length)
            textView.setText(this.headers[columnIndex]);

        textView.setPadding(this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom);
        textView.setTypeface(textView.getTypeface(), this.typeface);
        textView.setTextSize((float) this.textSize);
        textView.setTextColor(this.textColor);
        textView.setSingleLine();
        textView.setEllipsize(TextUtils.TruncateAt.END);
        return textView;
    }
}
