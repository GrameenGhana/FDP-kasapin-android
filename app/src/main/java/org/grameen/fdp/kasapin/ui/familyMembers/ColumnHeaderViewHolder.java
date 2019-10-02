package org.grameen.fdp.kasapin.ui.familyMembers;

import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
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

    public final LinearLayout column_header_container;
    public final TextView column_header_textview;
    public final TextView helper;

    //public final ImageButton column_header_sortButton;
    public final ITableView tableView;

    public final Drawable arrow_up, arrow_down;

    public ColumnHeaderViewHolder(View itemView, ITableView tableView) {
        super(itemView);
        this.tableView = tableView;
        column_header_textview =   itemView.findViewById(R.id.column_header_textView);
        helper = itemView.findViewById(R.id.helper);

        column_header_container =   itemView.findViewById(R.id.column_header_container);
        //column_header_sortButton =  itemView.findViewById(R.id.column_header_sortButton);

        // initialize drawables
        arrow_up = ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_keyboard_arrow_up_black_24dp);
        arrow_down = ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_keyboard_arrow_down_black_24dp);

        // Set click listener to the sort button
        //column_header_sortButton.setOnClickListener(mSortButtonClickListener);
    }


    /**
     * This method is calling from onBindColumnHeaderHolder on TableViewAdapter
     */
    public void setColumnHeader(ColumnHeader columnHeader) {
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

    private View.OnClickListener mSortButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (getSortState() == SortState.ASCENDING) {
                tableView.sortColumn(getAdapterPosition(), SortState.DESCENDING);
            } else if (getSortState() == SortState.DESCENDING) {
                tableView.sortColumn(getAdapterPosition(), SortState.ASCENDING);
            } else {
                // Default one
                tableView.sortColumn(getAdapterPosition(), SortState.DESCENDING);
            }

        }
    };

    @Override
    public void onSortingStatusChanged(SortState sortState) {
      /*  Log.e(LOG_TAG, " + onSortingStatusChanged : x:  " + getAdapterPosition() + " old state "
                + getSortState() + " current state : " + sortState + " visiblity: " +
                column_header_sortButton.getVisibility());*/

        super.onSortingStatusChanged(sortState);

     /*   // It is necessary to remeasure itself.
        column_header_container.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;

        controlSortState(sortState);

        Log.e(LOG_TAG, " - onSortingStatusChanged : x:  " + getAdapterPosition() + " old state "
                + getSortState() + " current state : " + sortState + " visiblity: " +
                column_header_sortButton.getVisibility());

        helper.requestLayout();

        column_header_textview.requestLayout();
        column_header_sortButton.requestLayout();
        column_header_container.requestLayout();
        itemView.requestLayout();*/
    }

    private void controlSortState(SortState sortState) {
       /* if (sortState == SortState.ASCENDING) {
            column_header_sortButton.setVisibility(View.VISIBLE);
            column_header_sortButton.setImageDrawable(arrow_down);

        } else if (sortState == SortState.DESCENDING) {
            column_header_sortButton.setVisibility(View.VISIBLE);
            column_header_sortButton.setImageDrawable(arrow_up);
        } else {
            column_header_sortButton.setVisibility(View.INVISIBLE);
        }*/
    }
}