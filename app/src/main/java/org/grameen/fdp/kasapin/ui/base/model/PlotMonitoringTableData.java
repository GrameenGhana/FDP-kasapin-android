package org.grameen.fdp.kasapin.ui.base.model;

import java.util.List;

/**
 * Created by aangjnr on 08/02/2018.
 */

public class PlotMonitoringTableData {

    Integer position;
    String title;
    List<Data2> tableData;



    public PlotMonitoringTableData(String title, List<Data2> tableData){this.title = title; this.tableData = tableData;}


    public void setTitle(String title) {
        this.title = title;
    }

    public void setTableData(List<Data2> tableData) {
        this.tableData = tableData;
    }

    public String getTitle() {
        return title;
    }

    public List<Data2> getTableData() {
        return tableData;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getPosition() {
        return position;
    }
}
