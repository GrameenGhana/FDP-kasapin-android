package org.grameen.fdp.kasapin.utilities;

import android.animation.Animator;
import android.view.View;

import org.grameen.fdp.kasapin.data.db.entity.Calculation;
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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ComputationUtils {

    private MyFormController formController;
    private ScriptEngine engine;



    public static ComputationUtils newInstance(MyFormController myFormController){
        return new ComputationUtils(myFormController);

    }


   private ComputationUtils(MyFormController controller){

        engine = new ScriptEngineManager().getEngineByName("rhino");
        this.formController = controller;

    }



    FormModel getModel(){return formController.getModel();}


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



    public String getValue(Question q, JSONObject ANSWERS_JSON) {
        String defVal;
        try {

            if (ANSWERS_JSON.has(q.getLabelC())) {
                defVal = ANSWERS_JSON.get(q.getLabelC()).toString();
                getModel().setValue(q.getLabelC(), defVal);
            } else
                defVal = "";


        } catch (JSONException ignored) {
            defVal = "";
        }

        AppLogger.i(getClass().getSimpleName(), "GETTING VALUE FOR " + q.getLabelC() + " --> Value = " + defVal);
        return defVal;
    }


    public void setUpPropertyChangeListeners(String label, List<SkipLogic> skipLogics) {

        if (skipLogics != null && skipLogics.size() > 0) {
           getModel().addPropertyChangeListener(label, event -> {

                AppLogger.i("PROPERTY CHANGE ", " FOR QUESTION " + label + " -----  Value was: " + event.getOldValue() + ", now: " + event.getNewValue());

                for (SkipLogic sl : skipLogics) {
                    String [] values = sl.getFormula().replace("\"", "").split(" ");
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

                String [] values = sl.getFormula().replace("\"", "").split(" ");
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
                    String [] values = sl.getFormula().replace("\"", "").split(" ");
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
        if(!question.getFormulaC().isEmpty() && !question.getFormulaC().equalsIgnoreCase("null")) {

            AppLogger.e("Computation Utils", "Question = " + question.getLabelC() + " Formula = " + question.getFormulaC());

            List<String> operands = getOperands(question.getFormulaC());
            for (String questionToListenTo : operands){
                getModel().addPropertyChangeListener(questionToListenTo, propertyChangeEvent -> {

                    String equation = question.getFormulaC();

                    try {
                        for (String operand : operands) {
                            equation = equation.replace(operand, getFormController().getModel().getValue(operand).toString());
                        }
                    } catch (Exception i) {
                        i.printStackTrace();
                        equation = "0";}

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

        } }
    }


    String calculate(String equation) throws ScriptException {

        Double value = (Double) engine.eval(equation.trim());
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat formatter = (DecimalFormat) nf;
        formatter.applyPattern("#,###,###.##");
        return (formatter.format(value));
    }

    Boolean compareValues(SkipLogic sl, String newValue) {
        String equation = sl.getAnswerValue() + sl.getLogicalOperator() +  newValue;
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


    List<String> getOperands(String formula){
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




    public static boolean parseEquation(String v1, String operator, String v2, ScriptEngine _engine){
        String equation = v1 + operator + v2;
        AppLogger.e(ComputationUtils.class.getSimpleName(), "Equation is " + equation);
        boolean value = false;
        try {
            value = (Boolean) _engine.eval(equation.trim());
        } catch (ScriptException | NumberFormatException e) {
            System.out.println("******* EXCEPTION ****** " + e.getMessage());
            value = v1.equalsIgnoreCase(v2);
        } finally {
            System.out.println(equation + " --> " + value);
        }
        return value;
    }






}
