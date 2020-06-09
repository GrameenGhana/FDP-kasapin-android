package org.grameen.fdp.kasapin.ui.familyMembers;

import android.view.View;
import android.widget.CheckBox;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;

/**
 * Created by aangjnr on 22/02/2018.
 */

public class CheckBoxViewHolder extends AbstractViewHolder {
    private static FdpCallbacks.UpdateJsonArray updateJsonArrayListener;
    private CheckBox checkBox;

    CheckBoxViewHolder(View itemView) {
        super(itemView);
        checkBox = itemView.findViewById(R.id.cell_data);
    }

    static void UpdateJsonArrayListener(FdpCallbacks.UpdateJsonArray listener) {
        updateJsonArrayListener = listener;
    }

    public void setData(int rowPosition, Question data) {
        if (checkBox != null)
            bindCheckBox(checkBox, data, rowPosition);
    }

    private void bindCheckBox(final CheckBox checkBox, final Question q, final int rowPosition) {
        checkBox.setText(checkBox.getContext().getText(R.string.yes));
        String defVal = FamilyMembersActivity.getValue(rowPosition, q.getLabelC());
        if (defVal != null && defVal.equalsIgnoreCase("yes")) {
            checkBox.setChecked(true);
            //if(updateJsonArrayListener != null)
            //  updateJsonArrayListener.onItemValueChanged(rowPosition, q.getId(), "YES");
        } else {
            checkBox.setChecked(false);
            // if(updateJsonArrayListener != null)
            //   updateJsonArrayListener.onItemValueChanged(rowPosition, q.getId(), "NO");
        }

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String value;
            if (isChecked) value = "YES";
            else value = "NO";
            //q.setDefault_value__c(value);
            if (updateJsonArrayListener != null)
                updateJsonArrayListener.onItemValueChanged(rowPosition, q.getLabelC(), value);
        });
        checkBox.requestLayout();
    }
}