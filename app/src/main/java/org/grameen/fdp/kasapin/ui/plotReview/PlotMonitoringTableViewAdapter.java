package org.grameen.fdp.kasapin.ui.plotReview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.model.HistoricalTableViewData;

import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.LongPressAwareTableDataAdapter;

import static org.grameen.fdp.kasapin.utilities.AppConstants.BUTTON_VIEW;
import static org.grameen.fdp.kasapin.utilities.AppConstants.TAG_OTHER_TEXT_VIEW;
import static org.grameen.fdp.kasapin.utilities.AppConstants.TAG_RESULTS;
import static org.grameen.fdp.kasapin.utilities.AppConstants.TAG_TITLE_TEXT_VIEW;
import static org.grameen.fdp.kasapin.utilities.AppConstants.TAG_VIEW;

/**
 * Created by aangjnr on 17/01/2018.
 */

public class PlotMonitoringTableViewAdapter extends LongPressAwareTableDataAdapter<HistoricalTableViewData> {

    private static final int TEXT_SIZE = 10;
    private static final int TITLE_TEXT_SIZE = 11;
    static MaterialSpinner.OnItemSelectedListener itemSelectedListener;
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

    public void setItemSelectedListener(final MaterialSpinner.OnItemSelectedListener mItemSelectedListener) {
        this.itemSelectedListener = mItemSelectedListener;
    }

    public void setClickistener(final View.OnClickListener listener) {
        this.mOnClickListener = listener;
    }

    @Override
    public View getDefaultCellView(int i, int i1, ViewGroup viewGroup) {

        final HistoricalTableViewData myTableData = getRowData(i);
        View renderedView = new View(context);


        if (getColumnModel().getColumnCount() < 3) {

            //// Log.i(TAG, "COLUMN COUNT LESS THAN 3 " +  getColumnModel().getColumnCount());


            if (i1 == 0) {
                //Todo set questions here

                renderedView = renderColumnLabelValues(myTableData);

            } else if (i1 == 1) {
                //Todo set Answer Values here
                renderedView = renderColumn0Values(myTableData);

            }


        } else {
            // Log.i(TAG, "COLUMN COUNT LESS >= 3 " +  getColumnModel().getColumnCount());


            if (i1 == 0) {
                //Todo set questions here

                renderedView = renderColumn0Values(myTableData);

            } else if (i1 == 1) {
                //Todo set Answer Values here
                renderedView = renderColumn1Values(myTableData);

            } else if (i1 == 2) {
                //Todo set Answer Values here
                renderedView = renderColumn2Values(myTableData);
            }


        }


        return renderedView;
    }

    @Override
    public View getLongPressCellView(int i, int i1, ViewGroup viewGroup) {
        return null;
    }


    private View renderColumnLabelValues(final HistoricalTableViewData data) {

        View view = null;

        if (data.getTag() != null) {

            if (data.getTag().equals(TAG_TITLE_TEXT_VIEW)) {
                TextView textView = new TextView(getContext());
                textView.setText(data.getLabel());
                textView.setPadding(20, 10, 20, 10);
                textView.setTextSize(TITLE_TEXT_SIZE);
                textView.setMaxLines(2);
                textView.setMinLines(2);

                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

                view = textView;

            } else if (data.getTag().equals(TAG_OTHER_TEXT_VIEW)) {

                TextView textView = new TextView(getContext());
                textView.setText(data.getLabel());
                textView.setMaxLines(2);
                textView.setMinLines(2);

                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setPadding(20, 10, 20, 10);
                textView.setTextSize(TEXT_SIZE);
                view = textView;


            } else if (data.getTag().equals(BUTTON_VIEW)) {

                @SuppressLint("RestrictedApi") ContextThemeWrapper newContext = new ContextThemeWrapper(context, R.style.PrimaryButton);

                final Button button = new Button(newContext);
                // button.setBackgroundResource(R.drawable.button_background_accent);
                button.setText(data.getLabel().split("_")[1]);
                // button.setTextColor(ContextCompat.getColor(context, R.color.white));
                button.setTag(data.getLabel());
                button.setPadding(20, 10, 20, 10);
                button.setTextSize(TEXT_SIZE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (mOnClickListener != null)
                            mOnClickListener.onClick(button);


                    }
                });
                view = button;


            } else if (data.getTag().equals(TAG_VIEW)) {
                final MaterialSpinner spinner = new MaterialSpinner(getContext());
                spinner.setItems(YEARS);
                spinner.setTag(data.getLabel());

                spinner.setPadding(20, 10, 20, 10);
                spinner.setTextSize(TEXT_SIZE);
                spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {

                        Log.i("TABLE ADAPTER", "Spinner item selected with tag " + spinner.getTag());

                        if (itemSelectedListener != null)
                            itemSelectedListener.onItemSelected(view, position, id, item);

                    }
                });
                try {
                    spinner.setSelectedIndex(Integer.parseInt(data.getLabel().split("_")[1]));
                } catch (Exception ignored) {
                    ignored.getMessage();
                }


                view = spinner;


            } else if (data.getTag().equals(TAG_RESULTS)) {
                TextView textView = new TextView(getContext());

                textView.setText(data.getLabel());
                textView.setPadding(20, 10, 20, 10);
                textView.setTextSize(TITLE_TEXT_SIZE);
                textView.setMaxLines(2);
                textView.setMinLines(2);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                view = textView;

            }
        } else {

            TextView textView = new TextView(getContext());
            textView.setText(data.getLabel());
            textView.setMaxLines(2);
            textView.setMinLines(2);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setPadding(20, 10, 20, 10);
            textView.setTextSize(TEXT_SIZE);
            view = textView;
        }


        return view;
    }

    private View renderColumn0Values(final HistoricalTableViewData data) {

        View view = null;

        if (data.getTag() != null) {

            if (data.getTag().equals(TAG_TITLE_TEXT_VIEW)) {
                TextView textView = new TextView(getContext());
                textView.setText(data.getV1());
                textView.setPadding(20, 10, 20, 10);
                textView.setMaxLines(2);
                textView.setMinLines(2);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setTextSize(TITLE_TEXT_SIZE);
                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

                view = textView;

            } else if (data.getTag().equals(TAG_OTHER_TEXT_VIEW)) {

                TextView textView = new TextView(getContext());
                textView.setText(data.getV1());
                textView.setPadding(20, 10, 20, 10);
                textView.setMaxLines(2);
                textView.setMinLines(2);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setTextSize(TEXT_SIZE);
                view = textView;


            } else if (data.getTag().equals(BUTTON_VIEW)) {

                @SuppressLint("RestrictedApi") ContextThemeWrapper newContext = new ContextThemeWrapper(context, R.style.PrimaryButton);

                final Button button = new Button(newContext);
                // button.setBackgroundResource(R.drawable.button_background_accent);
                button.setText(data.getV1().split("_")[1]);
                // button.setTextColor(ContextCompat.getColor(context, R.color.white));
                button.setTag(data.getV1());
                button.setPadding(20, 10, 20, 10);
                button.setTextSize(TEXT_SIZE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (mOnClickListener != null)
                            mOnClickListener.onClick(button);


                    }
                });
                view = button;


            } else if (data.getTag().equals(TAG_VIEW)) {
                final MaterialSpinner spinner = new MaterialSpinner(getContext());
                spinner.setItems(YEARS);
                spinner.setTag(data.getV1());
                spinner.setPadding(20, 10, 20, 10);
                spinner.setTextSize(TEXT_SIZE);
                spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {

                        Log.i("TABLE ADAPTER", "Spinner item selected with tag " + spinner.getTag());

                        if (itemSelectedListener != null)
                            itemSelectedListener.onItemSelected(view, position, id, item);

                    }
                });
                try {
                    spinner.setSelectedIndex(Integer.parseInt(data.getV1().split("_")[1]));
                } catch (Exception ignored) {
                    ignored.getMessage();
                }


                view = spinner;


            } else if (data.getTag().equals(TAG_RESULTS)) {
                TextView textView = new TextView(getContext());

                textView.setText(data.getV1());
                textView.setPadding(20, 10, 20, 10);
                textView.setTextSize(TITLE_TEXT_SIZE);
                textView.setMaxLines(2);
                textView.setMinLines(2);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                view = textView;

            }
        } else {

            TextView textView = new TextView(getContext());
            textView.setText(data.getV1());
            textView.setMaxLines(2);
            textView.setMinLines(2);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setPadding(20, 10, 20, 10);
            textView.setTextSize(TEXT_SIZE);
            view = textView;
        }


        return view;
    }

    private View renderColumn1Values(final HistoricalTableViewData data) {

        View view = null;

        if (data.getTag() != null) {

            if (data.getTag().equals(TAG_TITLE_TEXT_VIEW)) {
                TextView textView = new TextView(getContext());
                textView.setText(data.getV2());
                textView.setMinLines(2);
                textView.setMaxLines(2);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setPadding(20, 10, 20, 10);
                textView.setTextSize(TITLE_TEXT_SIZE);
                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

                view = textView;

            } else if (data.getTag().equals(TAG_OTHER_TEXT_VIEW)) {

                TextView textView = new TextView(getContext());
                textView.setText(data.getV2());
                textView.setMinLines(2);
                textView.setMaxLines(2);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setPadding(20, 10, 20, 10);
                textView.setTextSize(TEXT_SIZE);
                view = textView;


            } else if (data.getTag().equals(BUTTON_VIEW)) {

                @SuppressLint("RestrictedApi") ContextThemeWrapper newContext = new ContextThemeWrapper(context, R.style.PrimaryButton);

                final Button button = new Button(newContext);
                // button.setBackgroundResource(R.drawable.button_background_accent);
                button.setText(data.getV2().split("_")[1]);
                // button.setTextColor(ContextCompat.getColor(context, R.color.white));
                button.setTag(data.getV2());
                button.setPadding(20, 10, 20, 10);
                button.setTextSize(TEXT_SIZE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (mOnClickListener != null)
                            mOnClickListener.onClick(button);


                    }
                });
                view = button;


            } else if (data.getTag().equals(TAG_VIEW)) {
                final MaterialSpinner spinner = new MaterialSpinner(getContext());
                spinner.setItems(YEARS);
                spinner.setTag(data.getV2());
                spinner.setPadding(20, 10, 20, 10);
                spinner.setTextSize(TEXT_SIZE);
                spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {

                        Log.i("TABLE ADAPTER", "Spinner item selected with tag " + spinner.getTag());

                        if (itemSelectedListener != null)
                            itemSelectedListener.onItemSelected(view, position, id, item);

                    }
                });
                try {
                    spinner.setSelectedIndex(Integer.parseInt(data.getV2().split("_")[1]));
                } catch (Exception ignored) {
                    ignored.getMessage();
                }


                view = spinner;


            } else if (data.getTag().equals(TAG_RESULTS)) {
                TextView textView = new TextView(getContext());


                textView.setPadding(20, 10, 20, 10);
                textView.setTextSize(TITLE_TEXT_SIZE);
                textView.setMinLines(2);
                textView.setMaxLines(2);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                textView.setText(data.getV2());
                view = textView;

            }
        } else {

            TextView textView = new TextView(getContext());
            textView.setText(data.getV2());
            textView.setPadding(20, 10, 20, 10);
            textView.setMinLines(2);
            textView.setMaxLines(2);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setTextSize(TEXT_SIZE);
            view = textView;
        }


        return view;
    }

    private View renderColumn2Values(final HistoricalTableViewData data) {

        View view = null;

        if (data.getTag() != null) {

            if (data.getTag().equals(TAG_TITLE_TEXT_VIEW)) {
                TextView textView = new TextView(getContext());
                textView.setText(data.getV3());
                textView.setPadding(20, 10, 20, 10);
                textView.setTextSize(TITLE_TEXT_SIZE);
                textView.setMinLines(2);
                textView.setMaxLines(2);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

                view = textView;

            } else if (data.getTag().equals(TAG_OTHER_TEXT_VIEW)) {

                TextView textView = new TextView(getContext());
                textView.setText(data.getV3());
                textView.setMinLines(2);
                textView.setMaxLines(2);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setPadding(20, 10, 20, 10);
                textView.setTextSize(TEXT_SIZE);
                view = textView;


            } else if (data.getTag().equals(BUTTON_VIEW)) {

                @SuppressLint("RestrictedApi") ContextThemeWrapper newContext = new ContextThemeWrapper(context, R.style.PrimaryButton);

                final Button button = new Button(newContext);
                // button.setBackgroundResource(R.drawable.button_background_accent);
                button.setText(data.getV3().split("_")[1]);
                // button.setTextColor(ContextCompat.getColor(context, R.color.white));
                button.setTag(data.getV3());
                button.setPadding(20, 10, 20, 10);
                button.setTextSize(TEXT_SIZE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (mOnClickListener != null)
                            mOnClickListener.onClick(button);


                    }
                });
                view = button;


            } else if (data.getTag().equals(TAG_VIEW)) {
                final MaterialSpinner spinner = new MaterialSpinner(getContext());
                spinner.setItems(YEARS);
                spinner.setTag(data.getV3());
                spinner.setPadding(20, 10, 20, 10);
                spinner.setTextSize(TEXT_SIZE);
                spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {

                        Log.i("TABLE ADAPTER", "Spinner item selected with tag " + spinner.getTag());

                        if (itemSelectedListener != null)
                            itemSelectedListener.onItemSelected(view, position, id, item);

                    }
                });
                try {
                    spinner.setSelectedIndex(Integer.parseInt(data.getV3().split("_")[1]));
                } catch (Exception ignored) {
                    ignored.getMessage();
                }


                view = spinner;


            } else if (data.getTag().equals(TAG_RESULTS)) {
                TextView textView = new TextView(getContext());

                textView.setText(data.getV3());
                textView.setPadding(20, 10, 20, 10);
                textView.setTextSize(TITLE_TEXT_SIZE);
                textView.setMinLines(2);
                textView.setMaxLines(2);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                view = textView;

            }
        } else {

            TextView textView = new TextView(getContext());
            textView.setText(data.getV3());
            textView.setPadding(20, 10, 20, 10);
            textView.setMinLines(2);
            textView.setMaxLines(2);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setTextSize(TEXT_SIZE);
            view = textView;
        }


        return view;
    }


}
