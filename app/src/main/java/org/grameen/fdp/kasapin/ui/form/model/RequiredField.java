package org.grameen.fdp.kasapin.ui.form.model;


import android.annotation.SuppressLint;
import android.content.res.Resources;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.ui.form.ValidationError;

/**
 * Created by AangJnr on 27, September, 2018 @ 10:39 AM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */


public class RequiredField extends ValidationError {

    /**
     * Creates a new instance with the specified field name.
     *
     * @param fieldName     the field name
     * @param fieldLabel    the field label
     */
    public RequiredField(String fieldName, String fieldLabel) {
        super(fieldName, fieldLabel);
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public String getMessage(Resources resources) {
        return String.format(resources.getString(R.string.required_field_error_msg), getFieldLabel());
    }
}
