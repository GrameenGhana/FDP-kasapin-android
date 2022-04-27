package org.grameen.fdp.kasapin.ui.form;

import android.content.Context;
import android.content.res.Resources;

import org.grameen.fdp.kasapin.ui.form.controller.MyFormElementController;
import org.grameen.fdp.kasapin.ui.form.controller.MyFormSectionController;

import java.util.List;

public class MyPerFieldValidationErrorDisplay implements ValidationErrorDisplay {
    private final Context context;
    private final MyFormController controller;

    MyPerFieldValidationErrorDisplay(Context context, MyFormController controller) {
        this.context = context;
        this.controller = controller;
    }

    @Override
    public void resetErrors() {
        for (MyFormSectionController section : controller.getSections()) {
            for (MyFormElementController elementController : section.getElements()) {
                elementController.setError(null);
            }
        }
    }

    @Override
    public void showErrors(List<ValidationError> errors) {
        Resources res = context.getResources();
        MyFormElementController element;
        for (ValidationError error : errors) {
            element = controller.getElement(error.getFieldName());
            element.setError(error.getMessage(res));
        }
    }
}
