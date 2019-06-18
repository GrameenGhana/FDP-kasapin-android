package org.grameen.fdp.kasapin.data.db.model;

import java.util.List;

/**
 * Created by aangjnr on 08/02/2018.
 */

public class HistoricalViewPagerTableData {

    Integer position;
    String title;
    List<List<String>> tableData;


    public HistoricalViewPagerTableData(String title, List<List<String>> tableData) {
        this.title = title;
        this.tableData = tableData;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<List<String>> getTableData() {
        return tableData;
    }

    public void setTableData(List<List<String>> tableData) {
        this.tableData = tableData;

    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
