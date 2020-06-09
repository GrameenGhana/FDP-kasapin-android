package org.grameen.fdp.kasapin.utilities;

import android.view.View;

import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.SkipLogic;
import org.grameen.fdp.kasapin.ui.form.MyFormController;
import org.grameen.fdp.kasapin.ui.form.model.FormModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.annotation.Nullable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ComputationUtils {

    static String TAG = "ComputationUtilities";
    private MyFormController formController;
    private  ScriptEngine engine;


    private ComputationUtils(@Nullable MyFormController controller) {
        engine = new ScriptEngineManager().getEngineByName("rhino");
        this.formController = controller;

    }

    public static ComputationUtils newInstance(MyFormController myFormController) {
        return new ComputationUtils(myFormController);

    }

    public static String getValue(String label, JSONObject ANSWERS_JSON) {
        String defVal;
        try {
            if (ANSWERS_JSON.has(label)) {
                defVal = ANSWERS_JSON.get(label).toString().trim();
            } else
                defVal = "--";
        } catch (JSONException ignored) {
            defVal = "--";
        }
        AppLogger.e("Computation Utils", "GETTING VALUE FOR " + label + " --> Value = " + defVal);
        return defVal;
    }


    public static String getDataValue(Question q, JSONObject ANSWERS_JSON) {
        String defVal;
        try {
            if (ANSWERS_JSON.has(q.getLabelC())) {
                defVal = ANSWERS_JSON.get(q.getLabelC()).toString();
            } else
                defVal = q.getDefaultValueC();
        } catch (JSONException ignored) {
            defVal = q.getDefaultValueC();
        }

        AppLogger.e("Computation Utils", "GETTING VALUE FOR " + q.getLabelC() + " --> Value = " + defVal);
        return defVal;
    }

    public static boolean parseEquation(String equation, ScriptEngine _engine) {
        AppLogger.i(ComputationUtils.class.getSimpleName(), "Equation is " + equation);
        boolean value = false;
        try {
            value = (Boolean) _engine.eval(equation);
        } catch (ScriptException | NumberFormatException ignored) {
            System.out.println("************** Evaluating boolean ************* ");

            if (equation.contains("==")) {

                String[] disposableValues = equation.split("==");

                value = disposableValues[0].equalsIgnoreCase(disposableValues[1]);
            } else if (equation.contains("!=")) {
                String[] disposableValues = equation.split("!=");
                value = !disposableValues[0].equalsIgnoreCase(disposableValues[1]);
            }
        } finally {
            System.out.println( "FORMULA  >>>>>  " + equation  + " ---> " + value);
            System.out.println("---------------------------------------------------");

        }
        return value;
    }

    public static Boolean parseLogicalEquation(String equation) {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");
        Boolean answer = null;
        try {
            answer = (boolean) engine.eval(equation);
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        System.out.println("---------------------------------------------------");
        System.out.println( "FORMULA  >>>>>  " + equation  + " ---> " + answer);
        System.out.println("---------------------------------------------------");
        return answer;
    }

    public String getValue(Question q, JSONObject ANSWERS_JSON) {
        String defVal;
        try {
            if (ANSWERS_JSON.has(q.getLabelC())) {
                defVal = ANSWERS_JSON.get(q.getLabelC()).toString();
                if (defVal.trim().isEmpty() ||  defVal.equalsIgnoreCase("null"))
                    defVal = q.getDefaultValueC();
                if (getModel() != null)
                    getModel().setValue(q.getLabelC(), defVal);
            } else
                defVal = q.getDefaultValueC();
        } catch (JSONException ignored) {
            defVal = q.getDefaultValueC();
        }
        AppLogger.i(getClass().getSimpleName(), "GETTING VALUE FOR " + q.getLabelC() + " --> Value = " + defVal);
        return defVal;
    }

    FormModel getModel() {
        return formController.getModel();
    }

    private MyFormController getFormController() {
        return formController;
    }


    public void setUpPropertyChangeListeners(String questionToHide, List<SkipLogic> skipLogics) {
        if (skipLogics != null && skipLogics.size() > 0) {
            for (SkipLogic sl : skipLogics) {
                String[] values = sl.getFormula().replace("\"", "").split(" ");
                sl.setComparingQuestion(values[0]);
                sl.setLogicalOperator(values[1]);
                sl.setAnswerValue(values[2]);
                getModel().addPropertyChangeListener(sl.getComparingQuestion(), event -> {
                    AppLogger.i("PROPERTY CHANGE ", " FOR QUESTION " + sl.getComparingQuestion() + " -----  Value was: " + event.getOldValue() + ", now: " + event.getNewValue());
                    try {
                        showOrHideView(compareValues(sl, String.valueOf(event.getNewValue())), questionToHide);
                    } catch (Exception ignored) {
                    }
                });
            }
        }
    }

    public void initiateSkipLogicAndHideViews(String label, List<SkipLogic> skipLogics) {
        if (skipLogics != null && skipLogics.size() > 0) {
            for (final SkipLogic sl : skipLogics) {
                String[] values = sl.getFormula().replace("\"", "").split(" ");
                sl.setComparingQuestion(values[0]);
                sl.setLogicalOperator(values[1]);
                sl.setAnswerValue(values[2]);
                try {
                    showOrHideView(compareValues(sl, formController.getModel().getValue(sl.getComparingQuestion()).toString()), label);
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void showOrHideView(Boolean shouldHide, String labelToHide) {
        AppLogger.i(getClass().getSimpleName(), "COMPARING VALUES EVALUATED TO " + shouldHide);
        formController.getElement(labelToHide).setHidden(shouldHide);
        AppLogger.e(TAG, labelToHide + " is hidden? == " + getFormController().getElement(labelToHide).isHidden());

    }
    public void applyFormulas(Question question) {
        if (!question.getFormulaC().isEmpty() && !question.getFormulaC().equalsIgnoreCase("null")) {
            AppLogger.e("Computation Utils", "Question = " + question.getLabelC() + " Formula = " + question.getFormulaC());
            List<String> operands = getOperands(question.getFormulaC());
            for (String questionToListenTo : operands) {
                getModel().addPropertyChangeListener(questionToListenTo, propertyChangeEvent -> {
                    String equation = question.getFormulaC();
                    try {
                        for (String operand : operands) {
                            equation = equation.replace(operand, getFormController().getModel().getValue(operand).toString());
                        }
                    } catch (Exception i) {
                        i.printStackTrace();
                        equation = "0";
                    }
                    AppLogger.e("Computation Utils", "Applying Formula calc " + equation);
                    String newValue = "0.00";
                    try {
                        newValue = calculate(equation.replace(",", ""));
                        getModel().setValue(question.getLabelC(), newValue);
                    } catch (ScriptException e) {
                        e.printStackTrace();
                        getModel().setValue(question.getLabelC(), newValue);
                    }
                    System.out.println("####### NEW VALUE IS " + newValue);
                });
            }
        }
    }

    private String calculate(String equation) throws ScriptException {
        Double value = (Double) engine.eval(equation.trim().replace(",", ""));
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat formatter = (DecimalFormat) nf;
        formatter.applyPattern("#,###,###.##");
        return (formatter.format(value));
    }

    private Boolean compareValues(SkipLogic sl, String newValue) {
        String equation = sl.getAnswerValue() + sl.getLogicalOperator() + newValue;
        boolean value = false;
        try {
            value = (Boolean) engine.eval(equation.trim());
        } catch (ScriptException | NumberFormatException e) {
            System.out.println("******* EXCEPTION ****** " + e.getMessage());
            value = sl.getAnswerValue().equalsIgnoreCase(newValue);
        } finally {
            System.out.println(equation + " --> " + value);
        }
        return value;
    }

    public static Boolean compareValues(SkipLogic sl, String newValue, ScriptEngine _engine) {
        String equation = sl.getAnswerValue() + sl.getLogicalOperator() + newValue;
        boolean value = false;
        try {
            value = (Boolean) _engine.eval(equation.trim());
        } catch (ScriptException | NumberFormatException e) {
            System.out.println("******* EXCEPTION ****** " + e.getMessage());
            value = sl.getAnswerValue().equalsIgnoreCase(newValue);
        } finally {
            System.out.println(equation + " --> " + value);
        }
        return value;
    }


    private List<String> getOperands(String formula) {
        List<String> operandList = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(formula, "+-*/", true);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (!"+-/*".contains(token))
                operandList.add(token);
        }
        return operandList;
    }
}
