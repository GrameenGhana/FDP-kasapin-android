package org.grameen.fdp.kasapin.ui.plotReview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;

import com.jaredrummler.materialspinner.MaterialSpinner;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.model.HistoricalTableViewData;

import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.LongPressAwareTableDataAdapter;

import static org.grameen.fdp.kasapin.utilities.AppConstants.BUTTON_VIEW;
import static org.grameen.fdp.kasapin.utilities.AppConstants.TAG_RESULTS;
import static org.grameen.fdp.kasapin.utilities.AppConstants.TAG_SPINNER_VIEW;
import static org.grameen.fdp.kasapin.utilities.AppConstants.TAG_TITLE_TEXT_VIEW;

public class PlotMonitoringTableViewAdapter extends LongPressAwareTableDataAdapter<HistoricalTableViewData> {
    private static final int TEXT_SIZE = 10;
    private static final int TITLE_TEXT_SIZE = 11;
    static MaterialSpinner.OnItemSelectedListener<Object> itemSelectedListener;
    static View.OnClickListener mOnClickListener;
    String TAG = PlotMonitoringTableViewAdapter.class.getSimpleName();
    Context context;
    String[] YEARS = {"YEAR 1", "YEAR 2", "YEAR 3", "YEAR 4", "YEAR 5"};
    Integer dataSize;

    public PlotMonitoringTableViewAdapter(final Context context, final List<HistoricalTableViewData> data, final TableView<HistoricalTableViewData> tableView) {
        super(context, data, tableView);
        this.dataSize = data.size();
        this.context = context;
    }

    @Override
    public View getDefaultCellView(int i, int i1, ViewGroup viewGroup) {
        final HistoricalTableViewData myTableData = getRowData(i);
        View renderedView = new View(context);
        //Table view with 2 columns
        if (getColumnModel().getColumnCount() < 3) {
            if (i1 == 0)
                renderedView = renderLabelColumnValues(myTableData);
            else if (i1 == 1)
                renderedView = renderValuesAtColumn1(myTableData);
        } else {
            if (i1 == 0)
                renderedView = renderValuesAtColumn1(myTableData);
            else if (i1 == 1)
                renderedView = renderValuesAtColumn2(myTableData);
            else if (i1 == 2)
                //Todo set Answer Values here
                renderedView = renderValuesAtColumn3(myTableData);
        }
        return renderedView;
    }

    @Override
    public View getLongPressCellView(int i, int i1, ViewGroup viewGroup) {
        return null;
    }

    private View renderLabelColumnValues(final HistoricalTableViewData data) {
        return getView(data.getTag(), data.getLabel());
    }

    private View renderValuesAtColumn1(final HistoricalTableViewData data) {
        return getView(data.getTag(), data.getValueAtColumn1());
    }

    private View renderValuesAtColumn2(final HistoricalTableViewData data) {
        return getView(data.getTag(), data.getValueAtColumn2());
    }

    private View renderValuesAtColumn3(final HistoricalTableViewData data) {
        return getView(data.getTag(), data.getValueAtColumn3());
    }

    private View getView(String viewType, String dataValue) {
        if (viewType == null)
            return getTextView(dataValue, null);
        switch (viewType) {
            case TAG_TITLE_TEXT_VIEW:
                return getTextView(dataValue, true);
            case BUTTON_VIEW:
                return getButtonView(dataValue);
            case TAG_SPINNER_VIEW:
                return getSpinnerView(dataValue);
            case TAG_RESULTS:
                return getTextView(dataValue, false);
            default:
                return getTextView(dataValue, null);
        }
    }

    private TextView getTextView(String value, Boolean isLabelText) {
        TextView textView = new TextView(getContext());
        textView.setText(value);
        textView.setMaxLines(2);
        textView.setMinLines(2);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(TEXT_SIZE);

        //If isLabelText is not null, then it's either the value is a label text (Values at column 0) or a results text
        //(Total Values at the bottom of the table)
        //These specific values must be highlighted in 2 ways,
        //If its a label text , set the text color to the accent color and make it bold
        //else set the text color to black and make it bold
        if (isLabelText != null) {
            if (isLabelText) {
                textView.setTextSize(TITLE_TEXT_SIZE);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            } else
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        }
        return textView;
    }

    private Button getButtonView(String label) {
        @SuppressLint("RestrictedApi")
        ContextThemeWrapper newContext = new ContextThemeWrapper(context, R.style.PrimaryButton);
        final Button button = new Button(newContext);
        button.setText(label);
        button.setTag(label);
        button.setPadding(20, 10, 20, 10);
        button.setTextSize(TEXT_SIZE);
        button.setOnClickListener(view12 -> {
            if (mOnClickListener != null)
                mOnClickListener.onClick(button);
        });
        return button;
    }

    private MaterialSpinner getSpinnerView(String label) {
        final MaterialSpinner spinner = new MaterialSpinner(getContext());
        spinner.setItems(YEARS);
        spinner.setTag(label);
        spinner.setPadding(20, 10, 20, 10);
        spinner.setTextSize(TEXT_SIZE);
        spinner.setOnItemSelectedListener((view1, position, id, item) -> {
            if (itemSelectedListener != null)
                itemSelectedListener.onItemSelected(view1, position, id, item);
        });
        try {
            spinner.setSelectedIndex(Integer.parseInt(label.split("_")[1]));
        } catch (Exception ignored) {
        }
        return spinner;
    }
}
