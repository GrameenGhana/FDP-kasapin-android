package org.grameen.fdp.kasapin.ui.detailedYearMonthlyView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;

import com.jaredrummler.materialspinner.MaterialSpinner;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.ui.base.model.TableData;
import org.grameen.fdp.kasapin.utilities.AppLogger;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.LongPressAwareTableDataAdapter;

import static org.grameen.fdp.kasapin.utilities.AppConstants.BUTTON_VIEW;
import static org.grameen.fdp.kasapin.utilities.AppConstants.TAG_ICON_VIEW;
import static org.grameen.fdp.kasapin.utilities.AppConstants.TAG_OTHER_TEXT_VIEW;
import static org.grameen.fdp.kasapin.utilities.AppConstants.TAG_RESULTS;
import static org.grameen.fdp.kasapin.utilities.AppConstants.TAG_SPINNER_VIEW;
import static org.grameen.fdp.kasapin.utilities.AppConstants.TAG_TITLE_TEXT_VIEW;


public class DetailedYearTableViewAdapter extends LongPressAwareTableDataAdapter<TableData> {

    private static final int TEXT_SIZE = 10;
    private static final int TITLE_TEXT_SIZE = 14;
    static MaterialSpinner.OnItemSelectedListener itemSelectedListener;
    static View.OnClickListener mOnClickListener;

    Context context;
    String[] YEARS = {"YEAR 1", "YEAR 2", "YEAR 3", "YEAR 4", "YEAR 5"};

    boolean showIcons = true;

    public DetailedYearTableViewAdapter(final Context context, final List<TableData> data, final TableView<TableData> tableView) {
        super(context, data, tableView);
        this.context = context;
     }

    public DetailedYearTableViewAdapter(final Context context, final List<TableData> data, final TableView<TableData> tableView, boolean _showIcons) {
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

        if(data.getTag().equals(TAG_ICON_VIEW)) {
            if (showIcons && data.getImageBitmaps() != null) {
                ImageView imageView = new ImageView(getContext());
                imageView.setAdjustViewBounds(true);
                imageView.setImageBitmap(data.getImageBitmaps().get(year));
                return imageView;
            }
        }else if (calculationsForTheYears != null) {
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
                itemView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                return itemView;
            }
        }
        return null;
    }
}
