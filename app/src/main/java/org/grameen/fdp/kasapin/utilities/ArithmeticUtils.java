package org.grameen.fdp.kasapin.utilities;


import androidx.annotation.NonNull;
import android.util.Log;

import org.grameen.fdp.kasapin.data.db.entity.Logic;
import org.grameen.fdp.kasapin.data.db.entity.SkipLogic;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * Created by AangJnr on 22, September, 2018 @ 7:54 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class ArithmeticUtils {


    String TAG = "Arithmetic Utils";

    public static String calculateAndFormatDouble(@NonNull ScriptEngine scriptEngine, String equation) throws ScriptException {

        Double value = (Double) scriptEngine.eval(equation.trim());
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat formatter = (DecimalFormat) nf;
        formatter.applyPattern("#,###,###.##");


        return (formatter.format(value));
    }

    public static Boolean compareBooleanValues(@NonNull ScriptEngine engine, SkipLogic sl, String newValue) {
        boolean value = false;


    /*
        String equation = sl.getAnswerValue() + sl.getLogicalOperator() + String.valueOf(newValue);
        Log.i("BASE ACTIVITY", "Equation is " + equation);

        try {
            value = (Boolean) engine.eval(equation);

        } catch (ScriptException | NumberFormatException e) {
            System.out.println("******* EXCEPTION ****** " + e.getMessage());

            if (sl.getLogicalOperator().equalsIgnoreCase("=="))
                value = sl.getAnswerValue().equalsIgnoreCase(newValue);
            else value = !sl.getAnswerValue().equalsIgnoreCase(newValue);

        } finally {
            System.out.println(equation + " = " + value);
        }

     */

        return value;
    }

    public static double roundDoubleTo2dp(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    String calculate(@NonNull ScriptEngine scriptEngine, String equation) throws ScriptException {
        Double value = 0.0;
        try {
            value = (Double) scriptEngine.eval(equation.trim().replace(",", ""));
        } catch (Exception e) {
            e.printStackTrace();
            value = 0.0;
        }

        return String.valueOf(value);
    }

/*

    Boolean compareNumberValues(SkipLogic sl, String newValue) {

        engine = new ScriptEngineManager().getEngineByName("rhino");

        String equation = sl.getAnswerValue() + sl.getLogicalOperator() +  newValue;

        Log.i(TAG, "Equation is " + equation);

        boolean value = false;
        try {
            value = (Boolean) engine.eval(equation);

        } catch (ScriptException | NumberFormatException e) {
            System.out.println("******* EXCEPTION ****** " + e.getMessage());

            value = sl.getAnswerValue().equalsIgnoreCase(newValue);

        } finally {
            System.out.println(equation + " = " + value);
        }
        return value;
    }

    */

    Double calculateDouble(@NonNull ScriptEngine scriptEngine, String equation) {
        Log.i(TAG, "Evaluating " + equation);
        Double value;
        try {
            value = (Double) scriptEngine.eval(equation.trim().replace(",", ""));
        } catch (Exception e) {
            e.printStackTrace();
            value = null;
        }

        return value;
    }

    Boolean compareValues(@NonNull ScriptEngine engine, Logic logic, JSONObject ALL_MONITORING_VALUES_JSON) {

        Boolean value = null;
        String inputValue = getValue(logic.getQUESTION(), ALL_MONITORING_VALUES_JSON);
        Log.i(TAG, "EQUATION IS    ");


        Log.i(TAG, inputValue + "  " + logic.getLOGICAL_OPERATOR() + "  " + logic.getVALUE() + "\n\n");


        if (inputValue != null) {

            try {
                Double.parseDouble(inputValue.trim());

                value = (Boolean) engine.eval(inputValue + logic.getLOGICAL_OPERATOR() + logic.getVALUE());

                return value;

            } catch (ScriptException | NumberFormatException e) {

                System.out.println("**********  EXCEPTION ******************" + e.getMessage());


                if (logic.getLOGICAL_OPERATOR().equalsIgnoreCase("=="))
                    value = inputValue.equalsIgnoreCase(logic.getVALUE());
                else value = !inputValue.equalsIgnoreCase(logic.getVALUE());


                return value;
            } finally {
                System.out.println("*****************************LOGIC VALUE IS: " + value);

            }
        } else return null;


    }

    String getValue(String id, JSONObject jsonObject) {
        String value = null;


        try {

            value = jsonObject.get(id).toString();


        } catch (JSONException ignore) {
            Log.i(TAG, ignore.getMessage());
            value = "--";
        }


        return value;
    }


}
