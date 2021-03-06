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
    private ScriptEngine engine;


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
                defVal = ANSWERS_JSON.get(label).toString();
            } else
                defVal = "";


        } catch (JSONException ignored) {
            defVal = "";
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






    public String getValue(Question q, JSONObject ANSWERS_JSON) {
        String defVal;
        try {

            if (ANSWERS_JSON.has(q.getLabelC())) {
                defVal = ANSWERS_JSON.get(q.getLabelC()).toString();

                if(getModel() != null)
                    getModel().setValue(q.getLabelC(), defVal);


            } else
                defVal = q.getDefaultValueC();


        } catch (JSONException ignored) {
            defVal = q.getDefaultValueC();
        }

        AppLogger.i(getClass().getSimpleName(), "GETTING VALUE FOR " + q.getLabelC() + " --> Value = " + defVal);
        return defVal;
    }



    public static boolean parseEquation(String v1, String operator, String v2, ScriptEngine _engine) {
        String equation = v1 + operator + v2;
        AppLogger.e(ComputationUtils.class.getSimpleName(), "Equation is " + equation);
        boolean value = false;
        try {
            value = (Boolean) _engine.eval(equation.trim());
        } catch (ScriptException | NumberFormatException e) {
            System.out.println("******* Evaluating boolean ****** " + e.getMessage());
            value = v1.equalsIgnoreCase(v2);
        } finally {
            System.out.println(equation + " --> " + value);
        }
        return value;
    }

    public static boolean parseEquation(String equation, ScriptEngine _engine) {
        AppLogger.i(ComputationUtils.class.getSimpleName(), "Equation is " + equation);
        boolean value = false;
        try {
            value = (Boolean) _engine.eval(equation.trim());
        } catch (ScriptException | NumberFormatException ignored) {

            if (equation.contains("==")) {

                String[] disposableValues = equation.split("==");

                System.out.println("******* Evaluating boolean ****** ");
                value = disposableValues[0].equalsIgnoreCase(disposableValues[1]);
            } else if (equation.contains("!=")) {
                String[] disposableValues = equation.split("!=");

                System.out.println("******* Evaluating boolean ****** ");
                value = disposableValues[0].equalsIgnoreCase(disposableValues[1]);
            }
        } finally {
            System.out.println(equation + " --> " + value);
        }
        return value;
    }

    public static boolean parseBooleanEquation(String equation) {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");
        Boolean answer = null;
        try {
            answer = (Boolean) engine.eval(equation.trim());
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        AppLogger.e(TAG, "FORMULA IS >>>>>  " + equation);
        AppLogger.e(TAG, "ANSWER == " + answer);

        return answer;
    }

    FormModel getModel() {
        return formController.getModel();
    }

    public MyFormController getFormController() {
        return formController;
    }

    public boolean validate() throws JSONException {
        formController.resetValidationErrors();
        if (formController.isValidInput()) {

            //Send data to server here after getting JSON string

            //Toast.makeText(getContext(), getAllAnswersInJSON(), Toast.LENGTH_LONG).show();


        } else {

            // Whoaaaaaaa! There were some invalid inputs
            formController.showValidationErrors();
        }
        return true;
    }

    public String getAllAnswersAsJsonString(List<Question> QUESTIONS) {

        JSONObject jsonObject = new JSONObject();

        for (Question q : QUESTIONS) {
            try {
                jsonObject.put(String.valueOf(q.getId()), getModel().getValue(String.valueOf(q.getId())));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return jsonObject.toString();


    }

    public JSONObject getAllAnswersInJSONObject(List<Question> QUESTIONS) {


        JSONObject jsonObject = new JSONObject();

        for (Question q : QUESTIONS) {
            try {
                jsonObject.put(String.valueOf(q.getId()), getModel().getValue(String.valueOf(q.getId())));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return jsonObject;


    }



    public void setUpPropertyChangeListeners(String label, List<SkipLogic> skipLogics) {

        if (skipLogics != null && skipLogics.size() > 0) {
            getModel().addPropertyChangeListener(label, event -> {

                AppLogger.i("PROPERTY CHANGE ", " FOR QUESTION " + label + " -----  Value was: " + event.getOldValue() + ", now: " + event.getNewValue());

                for (SkipLogic sl : skipLogics) {
                    String[] values = sl.getFormula().replace("\"", "").split(" ");
                    sl.setComparingQuestion(values[0]);
                    sl.setLogicalOperator(values[1]);
                    sl.setAnswerValue(values[2]);

                    try {
                        if (compareValues(sl, String.valueOf(event.getNewValue()))) {

                            if (sl.shouldHide())
                                getFormController().getElement(label).getView().setVisibility(View.GONE);

                            else
                                getFormController().getElement(label).getView().setVisibility(View.VISIBLE);

                        } else {

                            if (sl.shouldHide())
                                getFormController().getElement(label).getView().setVisibility(View.VISIBLE);
                            else
                                getFormController().getElement(label).getView().setVisibility(View.GONE);
                        }
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
            });
        }
    }

    public void setUpPropertyChangeListeners2(String questiontoHide, List<SkipLogic> skipLogics) {

        if (skipLogics != null && skipLogics.size() > 0) {

            for (SkipLogic sl : skipLogics) {

                String[] values = sl.getFormula().replace("\"", "").split(" ");
                sl.setComparingQuestion(values[0]);
                sl.setLogicalOperator(values[1]);
                sl.setAnswerValue(values[2]);

                getModel().addPropertyChangeListener(sl.getComparingQuestion(), event -> {

                    AppLogger.i("PROPERTY CHANGE ", " FOR QUESTION " + sl.getComparingQuestion() + " -----  Value was: " + event.getOldValue() + ", now: " + event.getNewValue());


                    try {
                        if (compareValues(sl, String.valueOf(event.getNewValue()))) {

                            if (sl.shouldHide())
                                getFormController().getElement(questiontoHide).getView().setVisibility(View.GONE);

                            else
                                getFormController().getElement(questiontoHide).getView().setVisibility(View.VISIBLE);

                        } else {

                            if (sl.shouldHide())
                                getFormController().getElement(questiontoHide).getView().setVisibility(View.VISIBLE);
                            else
                                getFormController().getElement(questiontoHide).getView().setVisibility(View.GONE);
                        }
                    } catch (Exception ignored) {
                    }

                });

            }
        }
    }

    public void initiateSkipLogicsAndHideViews(String label, List<SkipLogic> skipLogics) {
        if (skipLogics != null && skipLogics.size() > 0) {

            for (final SkipLogic sl : skipLogics) {
                String[] values = sl.getFormula().replace("\"", "").split(" ");
                sl.setComparingQuestion(values[0]);
                sl.setLogicalOperator(values[1]);
                sl.setAnswerValue(values[2]);
                try {

                    AppLogger.i("SKIP LOGIC view to hide = ", label);

                    if (compareValues(sl, formController.getModel().getValue(sl.getComparingQuestion()).toString())) {

                        AppLogger.i(getClass().getSimpleName(), "COMPARING VALUES EVALUATED TO " + true);

                        if (sl.shouldHide())
                            formController.getElement(label).getView().setVisibility(View.GONE);
                        else
                            formController.getElement(label).getView().setVisibility(View.VISIBLE);

                    } else {

                        AppLogger.i(getClass().getSimpleName(), "COMPARING VALUES EVALUATED TO " + false);

                        if (sl.shouldHide())
                            formController.getElement(label).getView().setVisibility(View.VISIBLE);
                        else formController.getElement(label).getView().setVisibility(View.GONE);

                    }


                } catch (Exception ignored) {
                }


            }
        }


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

    public Boolean compareValues(SkipLogic sl, String newValue) {
        String equation = sl.getAnswerValue() + sl.getLogicalOperator() + newValue;
        AppLogger.e(getClass().getSimpleName(), "Equation is " + equation);

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

    List<String> getOperands(String formula) {
        List<String> operandList = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(formula, "+-*/", true);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (!"+-/*".contains(token)) {
                operandList.add(token);
            }
        }
        return operandList;
    }


}
