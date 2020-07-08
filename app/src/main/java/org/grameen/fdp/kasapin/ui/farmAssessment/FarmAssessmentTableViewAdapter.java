package org.grameen.fdp.kasapin.ui.farmAssessment;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.jaredrummler.materialspinner.MaterialSpinner;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.model.HistoricalTableViewData;

import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.LongPressAwareTableDataAdapter;

public class FarmAssessmentTableViewAdapter extends LongPressAwareTableDataAdapter<HistoricalTableViewData> {
    private static final int TEXT_SIZE = 10;
    private static final int TITLE_TEXT_SIZE = 11;
    static MaterialSpinner.OnItemSelectedListener<View> itemSelectedListener;
    static View.OnClickListener mOnClickListener;
    String TAG = FarmAssessmentTableViewAdapter.class.getSimpleName();
    Context context;
    Integer dataSize;

    public FarmAssessmentTableViewAdapter(final Context context, final List<HistoricalTableViewData> data, final TableView<HistoricalTableViewData> tableView) {
        super(context, data, tableView);
        this.dataSize = data.size();
        this.context = context;
    }

    @Override
    public View getDefaultCellView(int i, int i1, ViewGroup viewGroup) {
        final HistoricalTableViewData myTableData = getRowData(i);
        View renderedView = new View(context);
        if (i1 == 0)
            renderedView = renderColumnLabelValues(myTableData);
        else if (i1 == 1)
            renderedView = renderColumnSingleValues(myTableData);
        return renderedView;
    }

    @Override
    public View getLongPressCellView(int i, int i1, ViewGroup viewGroup) {
        return null;
    }


    private View renderColumnLabelValues(final HistoricalTableViewData data) {
        View view;
        TextView textView = new TextView(getContext());
        textView.setText(data.getLabel());
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(TITLE_TEXT_SIZE);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.text_black_87));
        view = textView;
        return view;
    }

    private View renderColumnSingleValues(final HistoricalTableViewData data) {
        View view;
        TextView textView = new TextView(getContext());
        String text = "\n" + data.getSingleValue() + "\n";
        textView.setText(text);
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(TITLE_TEXT_SIZE);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        textView.setBackgroundColor(ContextCompat.getColor(getContext(), getBackgroundColor(data.getSingleValue())));
        view = textView;
        return view;
    }


    int getBackgroundColor(final String result) {
        switch (result.toLowerCase().trim()) {
            case "fail":
                return R.color.fail;
            case "pass":
                return R.color.pass;
            case "non critical fail":
                return R.color.non_critical_fail;
            default:
                return R.color.divider_2;
        }
    }
}
