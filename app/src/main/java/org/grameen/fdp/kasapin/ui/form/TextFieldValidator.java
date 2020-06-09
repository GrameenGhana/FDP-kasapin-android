package org.grameen.fdp.kasapin.ui.form;

import org.grameen.fdp.kasapin.ui.form.model.RequiredField;

public class TextFieldValidator implements InputValidator {
    private String message;
    private String defaultValue;

    public TextFieldValidator(String _defaultValue, String _message) {
        this.message = _message;
        this.defaultValue = _defaultValue;
    }

    @Override
    public ValidationError validate(Object value, String fieldName, String fieldLabel) {

        if (!value.toString().matches("^[a-zA-Z 0-9]*$")) {
            return new RequiredField(fieldName, fieldLabel, message);
        }
        return null;
    }

    /**
     * Makes every instances of {@link TextFieldValidator} equal.
     *
     * @param o The object to compare.
     * @return true if o is also an instance of RequiredFieldValidator, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        return super.equals(o) || o != null && getClass() == o.getClass();
    }

    /**
     * Every instance of {{@link TextFieldValidator}} share the same hashcode.
     *
     * @return 0
     */
    @Override
    public int hashCode() {
        return 0;
    }
}
