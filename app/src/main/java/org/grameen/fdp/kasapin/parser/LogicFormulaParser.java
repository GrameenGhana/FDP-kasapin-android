package org.grameen.fdp.kasapin.parser;


import org.grameen.fdp.kasapin.exceptions.ParserException;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.ComputationUtils;
import org.grameen.fdp.kasapin.utilities.Tokenizer;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class LogicFormulaParser extends Tokenizer {
    private String TAG = this.getClass().getSimpleName();
    private String FORMULA = null;
    private JSONObject jsonObject = null;
    private JSONObject allValuesJson = null;
    private ScriptEngine _engine = new ScriptEngineManager().getEngineByName("rhino");

    private LogicFormulaParser() {
    }

    public static LogicFormulaParser getInstance() {
        return new LogicFormulaParser();
    }

    public Tokenizer getTokenizer() {
        return null;
    }

    @Override
    public void tokenize(String str) {
    }

    @Override
    public LinkedList<Token> getTokens() {
        return null;
    }


    public String evaluate(String _formula) {
        FORMULA = _formula;
        return evaluate();
    }

    private String evaluate() {
        int count = 0;
        String returnValue = "";
        boolean isNestedIfFormula = false;

        AppLogger.e(TAG, "**** UN PARSED EQUATION " + FORMULA);
        String[] nestedFormulas = FORMULA.split("else");

        for (String nestedFormula : nestedFormulas) {
            count += 1;

            if (nestedFormulas.length > 1) {
                isNestedIfFormula = true;
                AppLogger.e(TAG, "==============   FORMULA IS A NESTED IF WITH " + nestedFormulas.length + " PARTS   ================");
            }

            System.out.println("NESTED FORMULA PART " + count + " >>>> " + nestedFormula);

            //BREAK DOWN INTO SECTIONS AND EVALUATE INDIVIDUAL PARTS
            String rawFormula = nestedFormula
                    .replace("I", "")
                    .replace("F(", "");

            String[] sections = rawFormula.split(",");

            String formulaToEvaluate = "(" + sections[0].replace(" ", "") + ")";

            String trueValue = sections[1].replace("'", "").replace("(", "").replace(")", "");

            String falseValue = "";
            try {
                falseValue = sections[2].replace("'", "").replace("(", "").replace(")", "");
            } catch (Exception ignored) {
            }

            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String tmp_key = iterator.next();

                if (formulaToEvaluate.contains(tmp_key))
                    formulaToEvaluate = formulaToEvaluate.replace(tmp_key, getValue(tmp_key));
            }

            if (allValuesJson != null) {
                iterator = allValuesJson.keys();
                while (iterator.hasNext()) {
                    String tmp_key = iterator.next();

                    if (formulaToEvaluate.contains(tmp_key))
                        formulaToEvaluate = formulaToEvaluate.replace(tmp_key, getValue(tmp_key));
                }
            }

            String[] subSections = formulaToEvaluate.split("&&|\\|\\|");

            String evaluatedFormula = formulaToEvaluate;
            for (int i = 0; i < subSections.length; i++) {
                String v = subSections[i].replace("(", "").replace(")", "").replace(" ", "");
                Boolean answerValue = ComputationUtils.parseEquation(v.replace("\"", ""), _engine);
                AppLogger.e(TAG, "SECTION " + (i + 1) + " =>>> " + v + " Answer >>>  " + answerValue);
                evaluatedFormula = evaluatedFormula.replace(v, String.valueOf(answerValue));
                AppLogger.i(TAG, "### Replacing " + v + " with Answer >>>  " + answerValue);
            }

            Boolean valueAfterParsing = ComputationUtils.parseLogicalEquation(evaluatedFormula.replace("))", ")"));

            if (valueAfterParsing != null && valueAfterParsing) {
                returnValue = trueValue.replace("\"", "");
                break;
            } else {

                if (!isNestedIfFormula) {
                    returnValue = falseValue.replace("\"", "");
                    break;
                } else {

                    if (count == nestedFormulas.length) {
                        returnValue = falseValue.replace("\"", "");
                        break;
                    }
                }
            }
        }
        //Check if it contains any arithmetic equation

        if (Pattern.compile("[-+*/]").matcher(returnValue).find()) {
            MathFormulaParser mathFormulaParser = MathFormulaParser.getInstance();
            mathFormulaParser.setJsonObject(jsonObject);
            mathFormulaParser.setMathFormula(returnValue);

            return mathFormulaParser.evaluate();

        } else if (jsonObject.has(returnValue))
            try {
                returnValue = jsonObject.getString(returnValue);
            } catch (JSONException ignored) {
            }

        return returnValue;
    }


    public String evaluateComplexFormula(String complexFormula) {
        AppLogger.e(TAG, "********************  FORMULA IS A COMPLEX FORMULA  ********************");
        AppLogger.e(TAG, "********************  Evaluating the logic part/sections of the complex formula  ********************");

        String[] formulaSections = complexFormula.split("[-+*/]");

        for (String formula : formulaSections) {
            String answer = this.evaluate(formula);
            complexFormula = complexFormula.replace(formula, answer);
        }

        //Todo Remove logs
        //Logs only in debug mode
        AppLogger.e(TAG, "");
        AppLogger.e(TAG, "COMPLEX FORMULA AFTER EVALUATING LOGIC SECTIONS");
        AppLogger.e(TAG, "Complex Formula >>> " + complexFormula);

        AppLogger.e(TAG, "********************  Evaluating the arithmetic part/sections of the complex formula  ********************");

        if (Pattern.compile("[-+*/]").matcher(complexFormula).find()) {
            MathFormulaParser mathFormulaParser = MathFormulaParser.getInstance();
            mathFormulaParser.setJsonObject(jsonObject);
            mathFormulaParser.setMathFormula(complexFormula);
            return mathFormulaParser.evaluate();
        } else
            return complexFormula;
    }


    private String getValue(String id) {
        if (jsonObject == null)
            throw new ParserException("Json Object is null or was not provided");
        try {
            if (jsonObject.has(id))
                return jsonObject.get(id).toString();
            else if (allValuesJson.has(id))
                return allValuesJson.get(id).toString();
            else return "--";
        } catch (JSONException ignore) {
            return "--";
        }
    }

    public void setAllValuesJsonObject(JSONObject jsonObject) {
        this.allValuesJson = jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        AppLogger.e(TAG, "JSON DATA IS " + jsonObject);
        this.jsonObject = jsonObject;
    }

    public void setFormula(String formula) {
        this.FORMULA = formula;
    }
}

