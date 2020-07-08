package org.grameen.fdp.kasapin.ui.form.model;


import android.annotation.SuppressLint;
import android.content.res.Resources;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.ui.form.ValidationError;

public class RequiredField extends ValidationError {
    private String messageToDisplay = null;
    /**
     * Creates a new instance with the specified field name.
     *
     * @param fieldName  the field name
     * @param fieldLabel the field label
     */
    public RequiredField(String fieldName, String fieldLabel) {
        super(fieldName, fieldLabel);
    }

    public RequiredField(String fieldName, String fieldLabel, String message) {
        super(fieldName, fieldLabel);
        messageToDisplay = message;
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public String getMessage(Resources resources) {
        return (messageToDisplay == null) ?
                String.format(resources.getString(R.string.required_field_error_msg), getFieldLabel())
                : messageToDisplay;
    }
}
