package org.grameen.fdp.kasapin.ui.base.model;

import androidx.annotation.Nullable;

import java.util.List;

public class TableData {
    String label;
    String tag;
    List<String> yearsDataFormula;
    String singleValue;
    String v1;
    String v2;
    String v3;

    public TableData(String label, @Nullable List<String> formula, String tag) {
        this.label = label;
        this.yearsDataFormula = formula;
        this.tag = tag;
    }

    public TableData(String label, String s) {
        this.label = label;
        this.singleValue = s;
    }

//    public TableData(String label, String s1, String s2) {
//        this.label = label;
//        this.v1 = s1;
//        this.v2 = s2;
//    }

    public TableData(String label, String s1, String s2, String s3, @Nullable String TAG) {
        this.label = label;
        this.v1 = s1;
        this.v2 = s2;
        this.v3 = s3;
        this.tag = TAG;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String name) {
        this.label = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<String> getYearsDataFormula() {
        return yearsDataFormula;
    }

    public void setYearsDataFormula(List<String> yearsDataFormula) {
        this.yearsDataFormula = yearsDataFormula;
    }

    public String getSingleValue() {
        return singleValue;
    }

    public void setSingleValue(String singleValue) {
        this.singleValue = singleValue;
    }
}
