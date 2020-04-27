package org.grameen.fdp.kasapin.ui.familyMembers;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by aangjnr on 22/02/2018.
 */

public class CellViewHolder extends AbstractViewHolder {
    private static FdpCallbacks.UpdateJsonArray updateJsonArrayListener;
    private MaterialEditText editText;

    static void UpdateJsonArrayListener(FdpCallbacks.UpdateJsonArray listener){
        updateJsonArrayListener = listener;
    }
    CellViewHolder(View itemView) {
        super(itemView);
        editText = itemView.findViewById(R.id.cell_data);
    }

    public void setData(int rowPosition, Question data) {
        if(editText != null)
            bindEditTextView(editText, data, rowPosition);
    }

    private  void bindEditTextView(final MaterialEditText editText, final Question q, final int rowPosition) {
        if (q.getTypeC().equalsIgnoreCase(AppConstants.TYPE_NUMBER))
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        else if (q.getTypeC().equalsIgnoreCase(AppConstants.TYPE_NUMBER_DECIMAL)) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else if (q.getTypeC().equalsIgnoreCase(AppConstants.TYPE_TEXT))
            editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        editText.setHint(FamilyMembersActivity.getValue(rowPosition, q.getLabelC()));
        editText.setTextSize(12);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (updateJsonArrayListener != null)
                    updateJsonArrayListener.onItemValueChanged(rowPosition, q.getLabelC(), s.toString());
            }
        });

        if (q.getTypeC().equalsIgnoreCase(AppConstants.TYPE_NUMBER_DECIMAL)) {
            editText.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus && editText.getText() != null && !editText.getText().toString().isEmpty()) {
                    NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                    DecimalFormat formatter = (DecimalFormat) nf;
                    formatter.applyPattern("#,###,###.##");
                    Double doubleValue = Double.parseDouble(editText.getText().toString().replace(",", ""));
                    editText.setText(formatter.format(doubleValue));
                }
            });
        }

        if (updateJsonArrayListener != null)
            updateJsonArrayListener.onItemValueChanged(rowPosition, q.getLabelC(), FamilyMembersActivity.getValue(rowPosition, q.getLabelC()));
        editText.requestLayout();
    }
}