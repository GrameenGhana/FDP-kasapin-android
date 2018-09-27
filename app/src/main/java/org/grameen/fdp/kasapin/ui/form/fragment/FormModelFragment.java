package org.grameen.fdp.kasapin.ui.form.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import org.grameen.fdp.kasapin.ui.form.model.FormModel;

public class FormModelFragment extends Fragment {

    public static final String TAG = "nd_model";

    private FormModel model;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public FormModel getModel() {
        return model;
    }

    public void setModel(FormModel model) {
        this.model = model;
    }
}