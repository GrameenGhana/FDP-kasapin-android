package org.grameen.fdp.kasapin.ui.familyMembers;

import android.view.View;
import android.widget.TextView;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import org.grameen.fdp.kasapin.R;

public class RowHeaderViewHolder extends AbstractViewHolder {
    public final TextView rowHeaderTextview;

    public RowHeaderViewHolder(View itemView) {
        super(itemView);
        rowHeaderTextview = itemView.findViewById(R.id.row_header_textview);
    }
}