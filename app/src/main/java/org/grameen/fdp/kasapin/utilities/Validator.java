package org.grameen.fdp.kasapin.utilities;

import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.ui.form.FieldValidator;
import org.grameen.fdp.kasapin.ui.form.InputValidator;
import org.grameen.fdp.kasapin.ui.form.NumericalFieldValidator;
import org.grameen.fdp.kasapin.ui.form.ValidationError;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Validator {

    private JSONObject VALIDATIONS = new JSONObject();


    public Validator(){
    }




    public void addValidation(int rowIndex, Question question){
        HashSet<InputValidator> validation = new HashSet<>();
        if(question.isRequired()) {
            validation.add(new FieldValidator(question.getDefaultValueC(), question.getErrorMessage()));
            if(question.getTypeC().toLowerCase().equals(AppConstants.TYPE_NUMBER_DECIMAL)
                    || question.getTypeC().toLowerCase().equals(AppConstants.TYPE_NUMBER)){
                validation.add(new NumericalFieldValidator(question.getMinValue(), question.getMaxValue(), question.getErrorMessage()));
            }
            try {
                VALIDATIONS.put(question.getLabelC()+rowIndex, validation);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    void removeValidation(String label){
        VALIDATIONS.remove(label);
    }

    public HashSet<InputValidator> getValidators(String key){

        try {
            return (HashSet<InputValidator>) VALIDATIONS.get(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
