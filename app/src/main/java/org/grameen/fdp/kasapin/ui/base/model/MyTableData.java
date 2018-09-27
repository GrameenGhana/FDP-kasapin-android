package org.grameen.fdp.kasapin.ui.base.model;

import java.util.List;

/**
 * Created by aangjnr on 17/01/2018.
 */

public class MyTableData {


    List<Data> dataList;


    public MyTableData(List<Data> dataList) {
        this.dataList = dataList;
    }

    public List<Data> getDataList() {
        return dataList;
    }

    public void setDataList(List<Data> dataList) {
        this.dataList = dataList;
    }


}
