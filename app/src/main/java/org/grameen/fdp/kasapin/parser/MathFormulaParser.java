package org.grameen.fdp.kasapin.parser;

import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.Tokenizer;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class MathFormulaParser extends Tokenizer {

    String mathFormula = null;
    JSONObject jsonObject = null;
    JSONObject allValuesJson = null;
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");


    private MathFormulaParser() {}

    public static MathFormulaParser getInstance() {
        return new MathFormulaParser();
    }

    @Override
    public void tokenize(String str){}


    @Override
    public LinkedList<Token> getTokens() {
        return null;
    }


    public String evaluate() {
        AppLogger.e("###  MathFormulaParser >> ", "OLD FORMULA IS  " + mathFormula);

        String newFormula = mathFormula;
        if (jsonObject != null) {
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String tmp_key = (String) iterator.next();

                if (mathFormula.contains(tmp_key))
                    if (tmp_key.equals(mathFormula))
                        newFormula = getValue(tmp_key);
                    else
                        newFormula = newFormula.replace(tmp_key, getValue(tmp_key));
            }
        }

        if (allValuesJson != null) {
            Iterator<String> iterator = allValuesJson.keys();
            while (iterator.hasNext()) {
                String tmp_key = (String) iterator.next();
                if (mathFormula.contains(tmp_key)) {
                    if (tmp_key.equals(mathFormula))
                        newFormula = getValue(tmp_key);
                    else
                        newFormula = newFormula.replace(tmp_key, getValue(tmp_key));
                }
            }
        }

        AppLogger.e("###  MathFormulaParser >> ", "NEW FORMULA IS  " + newFormula);
        String value;
        try {
            value = calculate(newFormula);
        } catch (ScriptException e) {
            e.printStackTrace();
            value = "0";
        }
        AppLogger.e("###  MathFormulaParser >> ", newFormula + " === " + value);
        return value;
    }

    public String evaluate(String formula) {
        mathFormula = formula;
        AppLogger.e("###  MathFormulaParser >> ", "Equation to evaluate is " + mathFormula);
        try {
            return calculate(mathFormula);
        } catch (ScriptException e) {
            e.printStackTrace();
            return "0";
        }
    }

    public String evaluateWithFormatting(String formula) {
        mathFormula = formula;
        AppLogger.e("###  MathFormulaParser >> ", "Equation to evaluate is " + formula);
        try {
            Double value = (Double) engine.eval(mathFormula.trim().replace(",", ""));
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            DecimalFormat formatter = (DecimalFormat) nf;
            formatter.applyPattern("#,###,###.##");
            return (formatter.format(value));
        } catch (ScriptException e) {
            e.printStackTrace();
            return "0";
        }
    }

    private String getValue(String id) {
        try {
            if (jsonObject.has(id))
                return jsonObject.getString(id);
            else if (allValuesJson.has(id))
                return allValuesJson.getString(id);
            else return id;
        } catch (JSONException e) {
            AppLogger.e("###  MathFormulaParser >> ", e.getMessage());
            return "0";
        }
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public void setAllValuesJsonObject(JSONObject jsonObject) {
        this.allValuesJson = jsonObject;
    }

    public void setMathFormula(String mathFormula) {
        this.mathFormula = mathFormula;
    }

    String calculate(String equation) throws ScriptException {
        Double value = (Double) engine.eval(equation.trim().replace(",", ""));
        return (String.valueOf(value));
    }
}

