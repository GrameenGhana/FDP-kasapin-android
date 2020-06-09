package org.grameen.fdp.kasapin.ui.familyMembers;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evrencoskun.tableview.ITableView;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractSorterViewHolder;
import com.evrencoskun.tableview.sort.SortState;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.ui.base.model.ColumnHeader;


/**
 * Created by aangjnr on 22/02/2018.
 */
public class ColumnHeaderViewHolder extends AbstractSorterViewHolder {

    private static final String LOG_TAG = ColumnHeaderViewHolder.class.getSimpleName();
    public final ITableView tableView;
    private final LinearLayout column_header_container;
    private final TextView column_header_textview;
    private final TextView helper;


    ColumnHeaderViewHolder(View itemView, ITableView tableView) {
        super(itemView);
        this.tableView = tableView;
        column_header_textview = itemView.findViewById(R.id.column_header_textView);
        helper = itemView.findViewById(R.id.helper);

        column_header_container = itemView.findViewById(R.id.column_header_container);
        // Set click listener to the sort button
        //column_header_sortButton.setOnClickListener(mSortButtonClickListener);
    }


    /**
     * This method is calling from onBindColumnHeaderHolder on TableViewAdapter
     */
    void setColumnHeader(ColumnHeader columnHeader) {
        column_header_textview.setText(String.valueOf(columnHeader.getData()));
        helper.setText(String.valueOf(columnHeader.getFilterKeyword()));


        // If your TableView should have auto resize for cells & columns.
        // Then you should consider the below lines. Otherwise, you can ignore them.

        // It is necessary to remeasure itself.
        column_header_container.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        helper.requestLayout();

        //column_header_sortButton.requestLayout();
        column_header_textview.requestLayout();
        itemView.requestLayout();
    }

    @Override
    public void onSortingStatusChanged(SortState sortState) {
        super.onSortingStatusChanged(sortState);
    }
}