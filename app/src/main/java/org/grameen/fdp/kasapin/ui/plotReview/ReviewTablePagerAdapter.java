package org.grameen.fdp.kasapin.ui.plotReview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.model.HistoricalTableViewData;
import org.grameen.fdp.kasapin.ui.base.model.PlotMonitoringTableData;

import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;

public class ReviewTablePagerAdapter extends PagerAdapter {
    List<PlotMonitoringTableData> plotMonitoringTableDataList;
    private Context mContext;
    public ReviewTablePagerAdapter(Context context, List<PlotMonitoringTableData> _plotMonitoringTableDataList) {
        mContext = context;
        this.plotMonitoringTableDataList = _plotMonitoringTableDataList;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {
        PlotMonitoringTableData dataList = plotMonitoringTableDataList.get(position);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.review_pager_table_view, collection, false);
        TableView<HistoricalTableViewData> tableView = layout.findViewById(R.id.tableView);
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

    void setAdapter(TableView<HistoricalTableViewData> tableView, PlotMonitoringTableData data) {
        tableView.setColumnCount(2);
        String[] TABLE_HEADERS = {"Plot name", data.getTitle()};
        TableColumnWeightModel columnModel = new TableColumnWeightModel(tableView.getColumnCount());
        columnModel.setColumnWeight(0, 1);
        columnModel.setColumnWeight(1, 1);
        tableView.setColumnModel(columnModel);
        HistoricalTableHeaderAdapter headerAdapter = new HistoricalTableHeaderAdapter(mContext, TABLE_HEADERS);
        tableView.setHeaderAdapter(headerAdapter);
        List<HistoricalTableViewData> aoHistoricalTableViewDataList = new ArrayList<>();

        for (HistoricalTableViewData q : data.getTableData()) {
            aoHistoricalTableViewDataList.add(new HistoricalTableViewData(q.getLabel(), q.getValueAtColumn1(), q.getValueAtColumn2(), q.getValueAtColumn3(), q.getTag()));
        }

        PlotMonitoringTableViewAdapter plotMonitoringTableViewAdapter = new PlotMonitoringTableViewAdapter(mContext, aoHistoricalTableViewDataList, tableView);
        tableView.setDataAdapter(plotMonitoringTableViewAdapter);
    }

    public List<PlotMonitoringTableData> getData() {
        return plotMonitoringTableDataList;
    }

    public void setData(List<PlotMonitoringTableData> _plotMonitoringTableData) {
        this.plotMonitoringTableDataList = _plotMonitoringTableData;
    }
}