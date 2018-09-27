package org.grameen.fdp.kasapin.ui.base.model;

import java.util.List;

/**
 * Created by aangjnr on 27/01/2018.
 */

public class DetailedYearData {

    String name;

    List<Data2> dataList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Data2> getDataList() {
        return dataList;
    }

    public void setDataList(List<Data2> dataList) {
        this.dataList = dataList;
    }
}
