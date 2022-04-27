package org.grameen.fdp.kasapin.ui.base.model;

import com.evrencoskun.tableview.filter.IFilterableModel;
import com.evrencoskun.tableview.sort.ISortableModel;

public class Cell implements ISortableModel, IFilterableModel {
    private String mId;
    private Object mData;
    private String mFilterKeyword;

    public Cell(String id) {
        this.mId = id;
    }

    public Cell(String id, Object data) {
        this.mId = id;
        this.mData = data;
        this.mFilterKeyword = String.valueOf(data);
    }

    public Cell(String id, Object data, String filterKeyword) {
        this.mId = id;
        this.mData = data;
        this.mFilterKeyword = filterKeyword;
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public Object getContent() {
        return mData;
    }

    public Object getData() {
        return mData;
    }

    public void setData(String data) {
        mData = data;
    }

    public String getFilterKeyword() {
        return mFilterKeyword;
    }

    @Override
    public String getFilterableKeyword() {
        return mFilterKeyword;
    }
}