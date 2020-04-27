package org.grameen.fdp.kasapin.ui.form;

import org.grameen.fdp.kasapin.ui.form.model.RequiredField;
import org.grameen.fdp.kasapin.utilities.AppLogger;

import java.util.Objects;

public class FieldValidator implements InputValidator {
    private String message;
    private String defaultValue;

    public FieldValidator(String _defaultValue, String _message){
        this.message = _message;
        this.defaultValue = _defaultValue;
    }

    @Override
    public ValidationError validate(Object value, String fieldName, String fieldLabel) {
        AppLogger.e("FieldValidator", "Validation --->  " + fieldName + " |" + value + "==" + defaultValue + "|");

        if (value == null ||  value.toString().trim().isEmpty()
                || Objects.equals(value.toString(), "-select-")
                || Objects.equals(value.toString(), "--")
                || Objects.equals(value.toString(), "-")) {
            AppLogger.e("FieldValidator", "Validation didn't pass.");
            return new RequiredField(fieldName, fieldLabel, message);
        }
        return null;
    }

    /**
     * Makes every instances of {@link FieldValidator} equal.
     *
     * @param o The object to compare.
     * @return true if o is also an instance of RequiredFieldValidator, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        return super.equals(o) || o != null && getClass() == o.getClass();
    }

    /**
     * Every instance of {{@link FieldValidator}} share the same hashcode.
     *
     * @return 0
     */
    @Override
    public int hashCode() {
        return 0;
    }
}
