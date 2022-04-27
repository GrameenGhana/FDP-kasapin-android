package org.grameen.fdp.kasapin.ui.familyMembers;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evrencoskun.tableview.listener.ITableViewListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.grameen.fdp.kasapin.R;

public class TableViewListener implements ITableViewListener {
    String TAG = TableViewListener.class.getSimpleName();

    /**
     * Called when user click any cell item.
     *
     * @param cellView       : Clicked Cell ViewHolder.
     * @param columnPosition : X (Column) position of Clicked Cell item.
     * @param rowPosition    : Y (Row) position of Clicked Cell item.
     */
    @Override
    public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int columnPosition, int
            rowPosition) {
        // Do what you want.
        View view = cellView.itemView;
        View childView = view.findViewById(R.id.cell_data);
        if (childView instanceof MaterialEditText) {
            childView.requestFocus();
            InputMethodManager imm = (InputMethodManager) childView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(childView, InputMethodManager.SHOW_IMPLICIT);
        } else if (childView instanceof CheckBox) {
            CheckBox c = (CheckBox) childView;
            c.setChecked(!c.isChecked());
        } else if (childView instanceof Spinner) {
            Spinner spinner = (Spinner) childView;
            spinner.performClick();
        }
    }

    /**
     * Called when user long press any cell item.
     *
     * @param cellView : Long Pressed Cell ViewHolder.
     * @param column   : X (Column) position of Long Pressed Cell item.
     * @param row      : Y (Row) position of Long Pressed Cell item.
     */
    @Override
    public void onCellLongPressed(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
    }

    /**
     * Called when user click any column header item.
     *
     * @param columnHeaderView : Clicked Column Header ViewHolder.
     * @param columnPosition   : X (Column) position of Clicked Column Header item.
     */
    @Override
    public void onColumnHeaderClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int
            columnPosition) {
        // Do what you want.
    }

    /**
     * Called when user click any column header item.
     *
     * @param columnHeaderView : Long pressed Column Header ViewHolder.
     * @param columnPosition   : X (Column) position of Clicked Column Header item.
     */
    @Override
    public void onColumnHeaderLongPressed(@NonNull RecyclerView.ViewHolder columnHeaderView, int
            columnPosition) {
        // Do what you want.
    }

    /**
     * Called when user click any Row Header item.
     *
     * @param rowHeaderView : Clicked Row Header ViewHolder.
     * @param rowPosition   : Y (Row) position of Clicked Row Header item.
     */
    @Override
    public void onRowHeaderClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int
            rowPosition) {
        // Do what you want.
    }

    /**
     * Called when user click any Row Header item.
     *
     * @param rowHeaderView : Long pressed Row Header ViewHolder.
     * @param rowPosition   : Y (Row) position of Clicked Row Header item.
     */
    @Override
    public void onRowHeaderLongPressed(@NonNull RecyclerView.ViewHolder rowHeaderView, int
            rowPosition) {
        // Do what you want.
    }
}