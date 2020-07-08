package org.grameen.fdp.kasapin.utilities;

import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.ui.form.FieldValidator;
import org.grameen.fdp.kasapin.ui.form.InputValidator;
import org.grameen.fdp.kasapin.ui.form.NumericalFieldValidator;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;

public class Validator {
    private JSONObject VALIDATIONS = new JSONObject();

    public Validator() {}

    public void addValidation(Question question) {
        HashSet<InputValidator> validation = new HashSet<>();
        if (question.isRequired()) {
            validation.add(new FieldValidator(question.getDefaultValueC(), question.getErrorMessage()));
            if (question.getTypeC().toLowerCase().equals(AppConstants.TYPE_NUMBER_DECIMAL)
                    || question.getTypeC().toLowerCase().equals(AppConstants.TYPE_NUMBER)) {
                validation.add(new NumericalFieldValidator(question.getMinValue(), question.getMaxValue(), question.getErrorMessage()));
            }
            try {
                VALIDATIONS.put(question.getValidationId(), validation);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //Some answers to questions require multiple variants of the same answer eg. Answers to Family Members questions of a farmer
    //Since the data is usually represented in a JsonArray for such cases, we use the indexes of the answer json in the array to distinguish
    //which answer of the same question is coming from which family member.

    public void addValidation(int position, Question question) {
        question.setValidationId(question.getLabelC() + position);
        addValidation(question);
    }

    void removeValidation(String label) {
        VALIDATIONS.remove(label);
    }

    public HashSet<InputValidator> getValidators(String key) {
        try {
            return (HashSet<InputValidator>) VALIDATIONS.get(key);
        } catch (JSONException ignore) {
            return null;
        }
    }
}
