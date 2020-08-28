package org.grameen.fdp.kasapin.data.db.model;

import androidx.annotation.Nullable;

import java.util.List;

public class HistoricalTableViewData {
    String label;
    String valueAtColumn1;
    String valueAtColumn2;
    String valueAtColumn3;
    String tag;
    String singleValue;

    String iconData;

    List<String> values;
    List<String> valueTags;

    public HistoricalTableViewData(String label, String s1, String s2) {
        this.label = label;
        this.valueAtColumn1 = s1;
        this.valueAtColumn2 = s2;
    }

    public HistoricalTableViewData(String label, String s1, String s2, String s3, @Nullable String TAG) {
        this.label = label;
        this.valueAtColumn1 = s1;
        this.valueAtColumn2 = s2;
        this.valueAtColumn3 = s3;
        this.tag = TAG;
    }

    public HistoricalTableViewData(List<String> _values) {
        this.values = _values;
    }

    public HistoricalTableViewData(String label, String s) {
        this.label = label;
        this.singleValue = s;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getValueAtColumn3() {
        return valueAtColumn3;
    }

    public void setValueAtColumn3(String valueAtColumn3) {
        this.valueAtColumn3 = valueAtColumn3;
    }

    public String getSingleValue() {
        return singleValue;
    }

    public void setSingleValue(String singleValue) {
        this.singleValue = singleValue;
    }

    public String getValueAtColumn1() {
        return valueAtColumn1;
    }

    public void setValueAtColumn1(String valueAtColumn1) {
        this.valueAtColumn1 = valueAtColumn1;
    }

    public String getValueAtColumn2() {
        return valueAtColumn2;
    }

    public void setValueAtColumn2(String valueAtColumn2) {
        this.valueAtColumn2 = valueAtColumn2;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String name) {
        this.label = name;
    }

    public void setIconData(String iconData) {
        this.iconData = iconData;
    }

    public String getIconData() {
        return iconData;
    }
}