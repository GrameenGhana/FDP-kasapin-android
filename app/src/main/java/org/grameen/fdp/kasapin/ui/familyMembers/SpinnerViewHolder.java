package org.grameen.fdp.kasapin.ui.familyMembers;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;

import java.util.List;

public class SpinnerViewHolder extends AbstractViewHolder {
    private static FdpCallbacks.UpdateJsonArray updateJsonArrayListener;
    private Spinner spinner;

    SpinnerViewHolder(View itemView) {
        super(itemView);
        spinner = itemView.findViewById(R.id.cell_data);
    }

    static void UpdateJsonArrayListener(FdpCallbacks.UpdateJsonArray listener) {
        updateJsonArrayListener = listener;
    }

    public void setData(int rowPosition, Question data) {
        if (spinner != null)
            bindSpinnerView(spinner, data, rowPosition);
    }

    private void bindSpinnerView(final Spinner spinner, final Question q, final int rowPosition) {
        final String defaultValue = FamilyMembersActivity.getValue(rowPosition, q.getLabelC());
        final List<String> items = q.formatQuestionOptions();
        spinner.setPrompt("-select-");
        spinner.setTag(q.getId());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(spinner.getContext(), android.R.layout.simple_spinner_item, items) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    TextView itemView = (view.findViewById(android.R.id.text1));
                    itemView.setText("");
                    itemView.setHint(getItem(getCount()));
                }
                return view;
            }

            @Override
            public int getCount() {
                return super.getCount(); // don't display last item (it's used for the prompt)
            }
        };

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // if something is selected, set the value on the model
                q.setDefaultValueC(items.get(pos));
                if (updateJsonArrayListener != null) {
                    updateJsonArrayListener.onItemValueChanged(rowPosition, q.getLabelC(), items.get(pos));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        refresh(spinner, defaultValue, items);
        spinner.requestLayout();
    }

    private void refresh(Spinner spinner, @Nullable String defValue, List<String> items) {
        int selectionIndex = 0;    // index of last item shows the 'prompt'
        if (defValue != null && !defValue.equals("--") && !defValue.equals("-select-")) {
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).equals(defValue)) {
                    selectionIndex = i;
                    break;
                }
            }
            spinner.setSelection(selectionIndex);
        }
    }
}