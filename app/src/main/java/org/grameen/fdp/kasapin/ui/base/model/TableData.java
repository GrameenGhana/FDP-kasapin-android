package org.grameen.fdp.kasapin.ui.base.model;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import java.util.List;

public class TableData {
    String label;
    String tag;
    List<String> yearsDataFormula;
    String singleValue;
    List<Bitmap> imageBitmaps;

    public TableData(String label, @Nullable List<String> formula, String tag) {
        this.label = label;
        this.yearsDataFormula = formula;
        this.tag = tag;
    }

    public TableData(String label, @Nullable List<String> formula, List<Bitmap> _bitmaps, String tag) {
        this(label, formula, tag);
        this.imageBitmaps = _bitmaps;
    }

    public TableData(String label, String s) {
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

    public List<Bitmap> getImageBitmaps() {
        return imageBitmaps;
    }

    public void setImageBitmaps(List<Bitmap> imageBitmaps) {
        this.imageBitmaps = imageBitmaps;
    }
}
