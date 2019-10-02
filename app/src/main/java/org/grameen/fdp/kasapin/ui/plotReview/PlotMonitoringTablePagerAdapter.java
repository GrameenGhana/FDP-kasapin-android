package org.grameen.fdp.kasapin.ui.plotReview;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.model.HistoricalTableViewData;
import org.grameen.fdp.kasapin.ui.base.model.PlotMonitoringTableData;

import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;

/**
 * Created by aangjnr on 08/02/2018.
 */

public class PlotMonitoringTablePagerAdapter extends PagerAdapter {

    List<PlotMonitoringTableData> plotMonitoringTableDataList;
    private Context mContext;

    public PlotMonitoringTablePagerAdapter(Context context, List<PlotMonitoringTableData> _plotMonitoringTableDataList) {
        mContext = context;
        this.plotMonitoringTableDataList = _plotMonitoringTableDataList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {


        PlotMonitoringTableData dataList = plotMonitoringTableDataList.get(position);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.viewpager_table_view_item, collection, false);
        TableView tableView = layout.findViewById(R.id.tableView);
        /*TextView titleTextView = layout.findViewById(R.id.title_view);
        titleTextView.setText(dataList.getTitle());*/
        layout.setTag(dataList.getTitle());
        setAdapter(tableView, dataList);
        collection.addView(layout);


        return layout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup collection, int position, @NonNull Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return plotMonitoringTableDataList.size();
    }


    @Override
    public int getItemPosition(@NonNull Object object) {
        //return super.getItemPosition(object);


//        PlotMonitoringTableData plotMonitoringTableData = (PlotMonitoringTableData) object;
        //int position = plotMonitoringTableDataList.indexOf(plotMonitoringTableData);
/*
        if (position >= 0) {
            // The current data matches the data in this active fragment, so let it be as it is.
            return position;
        } else {
            // Returning POSITION_NONE means the current data does not matches the data this fragment is showing right now.  Returning POSITION_NONE constant will force the fragment to redraw its view layout all over again and show new data.
            return POSITION_NONE;
        }*/

        return POSITION_NONE;


    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        PlotMonitoringTableData dataList = plotMonitoringTableDataList.get(position);
        return dataList.getTitle();
    }


    void setAdapter(TableView tableView, PlotMonitoringTableData data) {


        tableView.setColumnCount(3);

        String[] TABLE_HEADERS = {mContext.getString(R.string.ao), mContext.getString(R.string.farmer_competence), mContext.getString(R.string.reason_for_failure)};


        TableColumnWeightModel columnModel = new TableColumnWeightModel(tableView.getColumnCount());
        columnModel.setColumnWeight(0, 1);
        columnModel.setColumnWeight(1, 2);
        columnModel.setColumnWeight(2, 3);

        tableView.setColumnModel(columnModel);


        HistoricalTableHeaderAdapter headerAdapter = new HistoricalTableHeaderAdapter(mContext, TABLE_HEADERS);
        tableView.setHeaderAdapter(headerAdapter);


        List<HistoricalTableViewData> GENERAL_AO_MONITORING = new ArrayList<>();


        for (HistoricalTableViewData q : data.getTableData()) {

            //Todo get results
            GENERAL_AO_MONITORING.add(new HistoricalTableViewData("--", q.getV1(), q.getV2(), q.getV3(), q.getTag()));

        }


        PlotMonitoringTableViewAdapter plotMonitoringTableViewAdapter = new PlotMonitoringTableViewAdapter(mContext, GENERAL_AO_MONITORING, tableView);
        tableView.setDataAdapter(plotMonitoringTableViewAdapter);


    }

    public List<PlotMonitoringTableData> getData() {
        return plotMonitoringTableDataList;
    }

    public void setData(List<PlotMonitoringTableData> _plotMonitoringTableData) {
        this.plotMonitoringTableDataList = _plotMonitoringTableData;
    }


}