package org.grameen.fdp.kasapin.ui.base.model;

import androidx.annotation.Nullable;

/**
 * Created by aangjnr on 18/01/2018.
 */

public class Data2 {


    String label;

    String v1;
    String v2;
    String v3;
    String tag;

    String singleValue;


    public Data2(String label, String s1, String s2) {
        this.label = label;
        this.v1 = s1;
        this.v2 = s2;
    }

    public Data2(String label, String s1, String s2, String s3, @Nullable String TAG) {
        this.label = label;
        this.v1 = s1;
        this.v2 = s2;
        this.v3 = s3;
        this.tag = TAG;
    }


    public Data2(String label, String s) {
        this.label = label;
        this.singleValue = s;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getV3() {
        return v3;
    }

    public void setV3(String v3) {
        this.v3 = v3;
    }

    public String getSingleValue() {
        return singleValue;
    }

    public void setSingleValue(String singleValue) {
        this.singleValue = singleValue;
    }

    public String getV1() {
        return v1;
    }

    public void setV1(String v1) {
        this.v1 = v1;
    }

    public String getV2() {
        return v2;
    }

    public void setV2(String v2) {
        this.v2 = v2;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String name) {
        this.label = name;
    }

}
