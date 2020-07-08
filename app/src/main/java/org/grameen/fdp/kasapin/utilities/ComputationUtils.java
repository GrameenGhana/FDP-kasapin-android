package org.grameen.fdp.kasapin.utilities;

import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.SkipLogic;
import org.grameen.fdp.kasapin.ui.form.MyFormController;
import org.grameen.fdp.kasapin.ui.form.model.FormModel;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ComputationUtils {
    static String TAG = "ComputationUtilities";
    private MyFormController formController;
    private ScriptEngine engine;

    private ComputationUtils(MyFormController controller) {
        engine = new ScriptEngineManager().getEngineByName("rhino");
        this.formController = controller;
    }

    public static ComputationUtils newInstance(MyFormController myFormController) {
        return new ComputationUtils(myFormController);
    }

    public String getFormAnswerValue(Question q, JSONObject ANSWERS_JSON) {
        String defVal = getDataValue(q, ANSWERS_JSON);
        if (getModel() != null) {
            getModel().setValue(q.getLabelC(), defVal);
        }
        return defVal;
    }
    public static String getDataValue(Question q, JSONObject ANSWERS_JSON) {
        String defVal = q.getDefaultValueC();
        try {
            if (ANSWERS_JSON.has(q.getLabelC()) && !ANSWERS_JSON.getString(q.getLabelC()).trim().isEmpty() && !ANSWERS_JSON.getString(q.getLabelC()).equalsIgnoreCase("null")) {
                defVal = ANSWERS_JSON.get(q.getLabelC()).toString();
            }
        } catch (Exception ignored) {}
        return defVal;
    }

    public static boolean parseEquation(String equation, ScriptEngine _engine) {
        boolean value = false;
        try {
            value = (Boolean) _engine.eval(equation);
        } catch (ScriptException | NumberFormatException ignored) {
            if (equation.contains("==")) {
                String[] disposableValues = equation.split("==");
                value = disposableValues[0].equalsIgnoreCase(disposableValues[1]);
            } else if (equation.contains("!=")) {
                String[] disposableValues = equation.split("!=");
                value = !disposableValues[0].equalsIgnoreCase(disposableValues[1]);
            }
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
        return answer;
    }

    public static Boolean compareSkipLogicValues(SkipLogic sl, String newValue, ScriptEngine _engine) {
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
                        toggleViewVisibility(compareSkipLogicValues(sl, String.valueOf(event.getNewValue())), questionToHide);
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
                    toggleViewVisibility(compareSkipLogicValues(sl, formController.getModel().getValue(sl.getComparingQuestion()).toString()), label);
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void toggleViewVisibility(Boolean shouldHide, String labelToHide) {
        formController.getElement(labelToHide).setHidden(shouldHide);
    }

    public void applyAndParseFormulas(Question question) {
        if (!question.getFormulaC().isEmpty() && !question.getFormulaC().equalsIgnoreCase("null")) {
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
                    String newValue = "0.00";
                    try {
                        newValue = calculateStringEquation(equation.replace(",", ""));
                        getModel().setValue(question.getLabelC(), newValue);
                    } catch (ScriptException e) {
                        e.printStackTrace();
                        getModel().setValue(question.getLabelC(), newValue);
                    } });
            }
        }
    }

    private String calculateStringEquation(String equation) throws ScriptException {
        Double value = (Double) engine.eval(equation.trim().replace(",", ""));
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat formatter = (DecimalFormat) nf;
        formatter.applyPattern("#,###,###.##");
        return (formatter.format(value));
    }

    private Boolean compareSkipLogicValues(SkipLogic sl, String newValue) {
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
