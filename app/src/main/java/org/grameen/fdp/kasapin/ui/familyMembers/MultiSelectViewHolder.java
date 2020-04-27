package org.grameen.fdp.kasapin.ui.familyMembers;

import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;

import java.util.List;

/**
 * Created by aangjnr on 22/02/2018.
 */

public class MultiSelectViewHolder extends AbstractViewHolder {
    private static FdpCallbacks.UpdateJsonArray updateJsonArrayListener;
    private MaterialEditText textView;

    MultiSelectViewHolder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.cell_data);
    }

    static void UpdateJsonArrayListener(FdpCallbacks.UpdateJsonArray listener) {
        updateJsonArrayListener = listener;
    }

    public void setData(int rowPosition, Question data) {
        if (textView != null)
            bindEditTextView(data, rowPosition);
    }

    private void bindEditTextView(final Question q, final int rowPosition) {
        textView.setHint(FamilyMembersActivity.getValue(rowPosition, q.getLabelC()));
        textView.setFocusable(false);
        textView.setOnClickListener(v -> showCheckboxDialog(q, rowPosition));

        textView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showCheckboxDialog(q, rowPosition);
            }
        });

        if (updateJsonArrayListener != null)
            updateJsonArrayListener.onItemValueChanged(rowPosition, q.getLabelC(), FamilyMembersActivity.getValue(rowPosition, q.getLabelC()));
        textView.requestLayout();
    }


    private void showCheckboxDialog(Question question, final int rowPosition) {
        List<String> items = question.formatQuestionOptions();

        String oldValue = FamilyMembersActivity.getValue(rowPosition, question.getLabelC());
        final boolean[] checked = new boolean[items.size()];

        for (int index = 0; index < checked.length; index++)
            checked[index] = oldValue.contains(items.get(index));

        AlertDialog.Builder builder = new AlertDialog.Builder(textView.getContext(), R.style.AppDialog);
        builder.setMultiChoiceItems(items.toArray(new String[0]), checked, (dialog, which, isChecked) -> {
        });

        builder.setCancelable(false);
        builder.setTitle("Select options");
        builder.setPositiveButton("DONE", (dialog, which) -> {

            StringBuilder newValue = new StringBuilder();
            for (int i = 0; i < checked.length; i++) {
                if (checked[i]) {
                    newValue.append(items.get(i));

                    if (i < checked.length - 1)
                        newValue.append(", ");
                }
            }

            if (updateJsonArrayListener != null) {
                updateJsonArrayListener.onItemValueChanged(rowPosition, question.getLabelC(), newValue.toString());
            }
            textView.setText(newValue.toString());
            textView.requestLayout();
        });

        // Set the negative/no button click listener
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Do something when click the negative button
            dialog.dismiss();
        });

        AlertDialog dialog1 = builder.create();
        dialog1.show();
    }
}