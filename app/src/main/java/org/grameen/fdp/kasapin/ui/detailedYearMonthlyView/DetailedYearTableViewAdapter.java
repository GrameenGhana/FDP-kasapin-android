package org.grameen.fdp.kasapin.ui.detailedYearMonthlyView;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.ui.base.model.TableData;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnModel;
import de.codecrafters.tableview.toolkit.LongPressAwareTableDataAdapter;

import static org.grameen.fdp.kasapin.utilities.AppConstants.TAG_ICON_VIEW;
import static org.grameen.fdp.kasapin.utilities.AppConstants.TAG_OTHER_TEXT_VIEW;
import static org.grameen.fdp.kasapin.utilities.AppConstants.TAG_RESULTS;


public class DetailedYearTableViewAdapter extends LongPressAwareTableDataAdapter<TableData> {

    private static final int TEXT_SIZE = 10;
    private static final int TITLE_TEXT_SIZE = 14;

    Context context;

    boolean showIcons = true;

    public DetailedYearTableViewAdapter(final Context context, final List<TableData> data, final TableView<TableData> tableView) {
        super(context, data, tableView);
        this.context = context;
    }

    public DetailedYearTableViewAdapter(final Context context, final List<TableData> data,
                                        final TableView<TableData> tableView, boolean _showIcons) {
        this(context, data, tableView);
        this.showIcons = _showIcons;
    }

    @Override
    public View getDefaultCellView(int i, int i1, ViewGroup viewGroup) {
        final TableData myTableData = getRowData(i);
        View renderedView;
        renderedView = renderCalculatedValuesForYear(myTableData, i1);
        return renderedView;
    }

    @Override
    public View getLongPressCellView(int i, int i1, ViewGroup viewGroup) {
        return null;
    }


    private View renderCalculatedValuesForYear(final TableData data, int year) {
        List<String> calculationsForTheYears = data.getYearsDataFormula();

        if (data.getTag().equals(TAG_ICON_VIEW)) {
            if (showIcons && data.getImageBitmaps() != null) {
                ImageView imageView = new ImageView(getContext());
                imageView.setAdjustViewBounds(true);
                imageView.setImageBitmap(data.getImageBitmaps().get(year));
                imageView.setPadding(10, 5, 10, 5);
                return imageView;
            }
        } else if (calculationsForTheYears != null) {
            TextView itemView = null;
            itemView = new TextView(getContext());
            itemView.setPadding(20, 10, 20, 10);
            if (data.getTag().equals(TAG_RESULTS))
                itemView.setTypeface(itemView.getTypeface(), Typeface.BOLD);
            itemView.setTextSize(TEXT_SIZE);

            try {
                Double value = Double.parseDouble(calculationsForTheYears.get(year).replace(",", ""));
                NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                DecimalFormat formatter = (DecimalFormat) nf;
                formatter.applyPattern("#,###,###.##");
                itemView.setText(formatter.format(value));
            } catch (Exception ignored) {
                itemView.setText(calculationsForTheYears.get(year));
            }

            return itemView;
        } else {
            if (year == 0) {
                TextView itemView = null;
                itemView = new TextView(getContext());
                itemView.setText(data.getLabel());
                itemView.setPadding(20, 10, 20, 10);
                itemView.setTextSize(TITLE_TEXT_SIZE);
                itemView.setTypeface(itemView.getTypeface(), Typeface.BOLD);
                itemView.setTextColor(ContextCompat.getColor(getContext(),    (data.getTag().equals(TAG_OTHER_TEXT_VIEW)) ? R.color.text_black_87 : R.color.colorAccent)  );
                return itemView;
            }
        }
        return null;
    }
}
