package org.grameen.fdp.kasapin.ui.base.model;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by aangjnr on 18/01/2018.
 */

public class Data {


    String label;
    String tag;

    List<String> yearsDataFormula;
    String singleValue;

    String v1;
    String v2;


    public Data(String label, @Nullable List<String> formula, String tag) {
        this.label = label;
        this.yearsDataFormula = formula;
        this.tag = tag;
    }

    public Data(String label, String s) {
        this.label = label;
        this.singleValue = s;
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
