package org.grameen.fdp.kasapin.ui.form;

import org.grameen.fdp.kasapin.ui.form.model.RequiredField;

public class NumericalFieldValidator implements InputValidator {
    private String message;
    private String minimum;
    private String maximum;

    public NumericalFieldValidator(String _min, String _max, String _message){
        this.message = _message;
        this.maximum =  _max;
        this.minimum = _min;
    }

    @Override
    public ValidationError validate(Object value, String fieldName, String fieldLabel) {
        if(minimum == null || minimum.isEmpty() || maximum == null || maximum.isEmpty())
            return null;

        double min, max, actual;
        try{
            actual = Double.parseDouble(value.toString());
            min = Double.parseDouble(minimum);
            max = Double.parseDouble(maximum);

            if((actual >= min && actual <= max))
                return null;
        }catch(Exception ignore){}
        return new RequiredField(fieldName, fieldLabel, message);
    }

    /**
     * Makes every instances of {@link NumericalFieldValidator} equal.
     *
     * @param o The object to compare.
     * @return true if o is also an instance of RequiredFieldValidator, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        return super.equals(o) || o != null && getClass() == o.getClass();
    }

    /**
     * Every instance of {{@link NumericalFieldValidator}} share the same hashcode.
     *
     * @return 0
     */
    @Override
    public int hashCode() {
        return 0;
    }
}
