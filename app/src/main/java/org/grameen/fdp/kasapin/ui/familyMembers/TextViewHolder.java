package org.grameen.fdp.kasapin.ui.familyMembers;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;


/**
 * Created by aangjnr on 22/02/2018.
 */

public class TextViewHolder extends AbstractViewHolder {
    static FdpCallbacks.UpdateJsonArray updateJsonArrayListener;

    public final LinearLayout cell_container;


    TextView editText = null;


    public static void UpdateJsonArrayListener(FdpCallbacks.UpdateJsonArray listener) {
        updateJsonArrayListener = listener;

    }


    public TextViewHolder(View itemView, String type) {
        super(itemView);
        Log.i("CELL VIEW HOLDER TAG", itemView.getTag().toString());
        Log.i("CELL VIEW HOLDER TYPE", type);

        cell_container = itemView.findViewById(R.id.cell_container);


        editText = itemView.findViewById(R.id.cell_data);


    }

    public void setData(int rowPosition, Question data) {

        //cell_textview.setText(String.valueOf(data));


        if (editText != null)
            bindEditTextView(editText, data, rowPosition);


    }


    void bindEditTextView(TextView editText, final Question q, final int rowPosition) {

        Log.i("TEXT Cell VH", "NOT NULL");
        editText.setText(q.getHelpTextC());
        editText.setTextSize(12);

        //cell_container.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        //cell_container.getLayoutParams().height = 70;

        editText.requestLayout();


    }


}