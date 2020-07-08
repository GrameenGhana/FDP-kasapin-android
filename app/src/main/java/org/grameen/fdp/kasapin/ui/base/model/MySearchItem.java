package org.grameen.fdp.kasapin.ui.base.model;

import ir.mirrajabi.searchdialog.core.Searchable;

public class MySearchItem implements Searchable {
    String mExtId;
    private String mName;

    public MySearchItem(String extId, String name) {
        mName = name;
        mExtId = extId;
    }

    @Override
    public String getTitle() {
        return mName;
    }

    public String getName() {
        return mName;
    }

    public MySearchItem setName(String title) {
        mName = title;
        return this;
    }

    public String getExtId() {
        return mExtId;
    }
}