package org.grameen.fdp.kasapin.ui.base.model;

import org.grameen.fdp.kasapin.data.db.model.HistoricalTableViewData;

import java.util.List;

/**
 * Created by aangjnr on 08/02/2018.
 */

public class PlotMonitoringTableData {

    Integer position;
    String title;
    List<HistoricalTableViewData> tableData;


    public PlotMonitoringTableData(String title, List<HistoricalTableViewData> tableData) {
        this.title = title;
        this.tableData = tableData;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<HistoricalTableViewData> getTableData() {
        return tableData;
    }

    public void setTableData(List<HistoricalTableViewData> tableData) {
        this.tableData = tableData;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
