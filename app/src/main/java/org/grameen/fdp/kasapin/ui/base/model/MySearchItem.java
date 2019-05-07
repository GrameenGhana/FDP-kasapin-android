package org.grameen.fdp.kasapin.ui.base.model;


import ir.mirrajabi.searchdialog.core.Searchable;

/**
 * Created by AangJnr on 25, September, 2018 @ 12:09 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

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

    public MySearchItem setName(String title) {
        mName = title;
        return this;
    }

    public String getmName() {
        return mName;
    }

    public String getmExtId() {
        return mExtId;
    }

    public void setmExtId(String mExtId) {
        this.mExtId = mExtId;
    }
}