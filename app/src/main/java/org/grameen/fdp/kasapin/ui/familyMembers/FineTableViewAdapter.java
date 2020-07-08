package org.grameen.fdp.kasapin.ui.familyMembers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.ui.base.model.Cell;
import org.grameen.fdp.kasapin.ui.base.model.ColumnHeader;
import org.grameen.fdp.kasapin.ui.base.model.RowHeader;
import org.grameen.fdp.kasapin.utilities.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class FineTableViewAdapter extends AbstractTableAdapter<ColumnHeader, RowHeader, Cell> {
    // Cell View Types by Column Position
    private List<Question> questions;
    private ArrayList<List<View>> views;
    private int CURRENT_VISIBLE_COLUMN;

    FineTableViewAdapter(Context p_jContext, List<Question> questionList, int rowSize) {
        super(p_jContext);
        this.questions = questionList;
        views = new ArrayList<>();
        for (int i = 0; i < rowSize; i++)
            views.add(new ArrayList<>());
        CURRENT_VISIBLE_COLUMN = 0;
    }

    /**
     * This is where you create your custom Cell ViewHolder. This method is called when Cell
     * RecyclerView of the TableView needs a new RecyclerView.ViewHolder of the given type to
     * represent an item.
     *
     * @param viewType : This value comes from "getCellItemViewType" method to support different
     *                 type of viewHolder as a Cell item.
     * @see #getCellItemViewType(int);
     */
    @Override
    public RecyclerView.ViewHolder onCreateCellViewHolder(ViewGroup parent, int viewType) {
        String TYPE = questions.get(viewType).getTypeC().toLowerCase();
        switch (TYPE) {
            case AppConstants.TYPE_SELECTABLE:
                return new SpinnerViewHolder(getLayoutView(parent, AppConstants.TYPE_SELECTABLE));
            case AppConstants.TYPE_CHECKBOX:
                return new CheckBoxViewHolder(getLayoutView(parent, AppConstants.TYPE_CHECKBOX));
            case AppConstants.TYPE_MULTI_SELECTABLE:
                return new MultiSelectViewHolder(getLayoutView(parent, AppConstants.TYPE_MULTI_SELECTABLE));
            default:
                return new CellViewHolder(getLayoutView(parent, AppConstants.TYPE_TEXT));
        }
    }

    /**
     * That is where you set Cell View Model data to your custom Cell ViewHolder. This method is
     * Called by Cell RecyclerView of the TableView to display the data at the specified position.
     * This method gives you everything you need about a cell item.
     *
     * @param holder         : This is one of your cell ViewHolders that was created on
     *                       ```onCreateCellViewHolder``` method. In this example we have created
     *                       "CellViewHolder" holder.
     * @param cellItemModel  : This is the cell view model located on this X and Y position. In this
     *                       example, the model class is "Cell".
     * @param columnPosition : This is the X (Column) position of the cell item.
     * @param rowPosition    : This is the Y (Row) position of the cell item.
     * @see #onCreateCellViewHolder(ViewGroup, int) ;
     */

    @Override
    public void onBindCellViewHolder(AbstractViewHolder holder, Object cellItemModel, int columnPosition, int rowPosition) {
        CURRENT_VISIBLE_COLUMN = columnPosition;
        Cell cell = (Cell) cellItemModel;
        if (holder instanceof CellViewHolder) {
            CellViewHolder viewHolder = (CellViewHolder) holder;
            viewHolder.setData(rowPosition, (Question) cell.getData());
            viewHolder.itemView.setTag("edittext");
            views.get(rowPosition).add(columnPosition, viewHolder.itemView);
        } else if (holder instanceof CheckBoxViewHolder) {
            CheckBoxViewHolder viewHolder = (CheckBoxViewHolder) holder;
            viewHolder.setData(rowPosition, (Question) cell.getData());
            viewHolder.itemView.setTag("checkbox");
            views.get(rowPosition).add(columnPosition, viewHolder.itemView);
        } else if (holder instanceof SpinnerViewHolder) {
            SpinnerViewHolder viewHolder = (SpinnerViewHolder) holder;
            viewHolder.setData(rowPosition, (Question) cell.getData());
            viewHolder.itemView.setTag("spinner");
            views.get(rowPosition).add(columnPosition, viewHolder.itemView);
        } else if (holder instanceof MultiSelectViewHolder) {
            MultiSelectViewHolder viewHolder = (MultiSelectViewHolder) holder;
            viewHolder.setData(rowPosition, (Question) cell.getData());
            viewHolder.itemView.setTag("multi_select");
            views.get(rowPosition).add(columnPosition, viewHolder.itemView);
        }
    }

    /**
     * This is where you create your custom Column Header ViewHolder. This method is called when
     * Column Header RecyclerView of the TableView needs a new RecyclerView.ViewHolder of the given
     * type to represent an item.
     *
     * @param viewType : This value comes from "getColumnHeaderItemViewType" method to support
     *                 different type of viewHolder as a Column Header item.
     * @see #getColumnHeaderItemViewType(int);
     */
    @Override
    public RecyclerView.ViewHolder onCreateColumnHeaderViewHolder(ViewGroup parent, int viewType) {
        // Get Column Header xml Layout
        View layout = LayoutInflater.from(mContext).inflate(R.layout.table_view_column_header_layout, parent, false);
        return new ColumnHeaderViewHolder(layout, getTableView());
    }

    /**
     * That is where you set Column Header View Model data to your custom Column Header ViewHolder.
     * This method is Called by ColumnHeader RecyclerView of the TableView to display the data at
     * the specified position. This method gives you everything you need about a column header
     * item.
     *
     * @param holder                : This is one of your column header ViewHolders that was created
     *                              on ```onCreateColumnHeaderViewHolder``` method. In this example
     *                              we have created "ColumnHeaderViewHolder" holder.
     * @param columnHeaderItemModel : This is the column header view model located on this X
     *                              position. In this example, the model class is "ColumnHeader".
     * @param columnPosition        : This is the X (Column) position of the column header item.
     * @see #onCreateColumnHeaderViewHolder(ViewGroup, int) ;
     */
    @Override
    public void onBindColumnHeaderViewHolder(AbstractViewHolder holder, Object
            columnHeaderItemModel, int columnPosition) {
        ColumnHeader columnHeader = (ColumnHeader) columnHeaderItemModel;

        // Get the holder to update cell item text
        ColumnHeaderViewHolder columnHeaderViewHolder = (ColumnHeaderViewHolder) holder;
        columnHeaderViewHolder.setColumnHeader(columnHeader);
    }

    /**
     * This is where you create your custom Row Header ViewHolder. This method is called when
     * Row Header RecyclerView of the TableView needs a new RecyclerView.ViewHolder of the given
     * type to represent an item.
     *
     * @param viewType : This value comes from "getRowHeaderItemViewType" method to support
     *                 different type of viewHolder as a row Header item.
     * @see #getRowHeaderItemViewType(int);
     */

    @Override
    public RecyclerView.ViewHolder onCreateRowHeaderViewHolder(ViewGroup parent, int viewType) {
        // Get Row Header xml Layout
        View layout = LayoutInflater.from(mContext).inflate(R.layout.table_view_row_header_layout, parent, false);
        // Create a Row Header ViewHolder
        return new RowHeaderViewHolder(layout);
    }

    /**
     * That is where you set Row Header View Model data to your custom Row Header ViewHolder. This
     * method is Called by RowHeader RecyclerView of the TableView to display the data at the
     * specified position. This method gives you everything you need about a row header item.
     *
     * @param holder             : This is one of your row header ViewHolders that was created on
     *                           ```onCreateRowHeaderViewHolder``` method. In this example we have
     *                           created "RowHeaderViewHolder" holder.
     * @param rowHeaderItemModel : This is the row header view model located on this Y position. In
     *                           this example, the model class is "RowHeader".
     * @param rowPosition        : This is the Y (row) position of the row header item.
     * @see #onCreateRowHeaderViewHolder(ViewGroup, int) ;
     */
    @Override
    public void onBindRowHeaderViewHolder(AbstractViewHolder holder, Object rowHeaderItemModel, int rowPosition) {
        RowHeader rowHeader = (RowHeader) rowHeaderItemModel;
        // Get the holder to update row header item text
        RowHeaderViewHolder rowHeaderViewHolder = (RowHeaderViewHolder) holder;
        rowHeaderViewHolder.rowHeaderTextview.setText(String.valueOf(rowHeader.getData()));
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateCornerView() {
        // Get Corner xml layout
        return LayoutInflater.from(mContext).inflate(R.layout.table_view_corner_layout, null);
    }

    @Override
    public int getColumnHeaderItemViewType(int position) {
        // The unique ID for this type of column header item
        // If you have different items for Cell View by X (Column) position,
        // then you should fill this method to be able create different
        // type of CellViewHolder on "onCreateCellViewHolder"
        return 0;
    }

    @Override
    public int getRowHeaderItemViewType(int position) {
        // The unique ID for this type of row header item
        // If you have different items for Row Header View by Y (Row) position,
        // then you should fill this method to be able create different
        // type of RowHeaderViewHolder on "onCreateRowHeaderViewHolder"
        return 0;
    }

    @Override
    public int getCellItemViewType(int column) {
        return column;
    }

    private View getLayoutView(ViewGroup parent, String TYPE) {
        View view;
        switch (TYPE) {
            case AppConstants.TYPE_TEXT:
            case AppConstants.TYPE_MULTI_SELECTABLE:
                view = LayoutInflater.from(mContext).inflate(R.layout.table_view_edittext, parent, false);
                view.setTag(TYPE);
                break;
            case AppConstants.TYPE_SELECTABLE:
                view = LayoutInflater.from(mContext).inflate(R.layout.table_view_spinner, parent, false);
                view.setTag(TYPE);
                break;
            case AppConstants.TYPE_CHECKBOX:
                view = LayoutInflater.from(mContext).inflate(R.layout.table_view_checkbox, parent, false);
                view.setTag(TYPE);
                break;
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.table_view_cell_layout, parent, false);
                view.setTag(TYPE);
        }
        return view;
    }

    View getCellViews(int rowIndex, int columnIndex) {
        try {
            return views.get(rowIndex).get(columnIndex);
        } catch (Exception ignore) {
            return null;
        }
    }

    int getCurrentVisibleColumn() {
        return CURRENT_VISIBLE_COLUMN;
    }
}

