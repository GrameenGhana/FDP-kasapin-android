package org.grameen.fdp.kasapin.parser;


import org.grameen.fdp.kasapin.exceptions.ParserException;
import org.grameen.fdp.kasapin.utilities.AppConstants;
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

/**
 * Created by AangJnr on 12, October, 2018 @ 10:57 AM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */



/*

^: Matches the beginning position of the string
-: The minus character
?: Indicates that there is zero or one of the preceding element

The double value can be positive or negative, so the string can have one or no minus character in the beginning.

\\d: Digits; [0-9] is an alternative
*: Matches the preceding element zero or more times.

Because ".1515674" is also a valid double value, \\d in the regexDecimal is succeeded by * instead of +.

\\.: The '.' character.

Since a '.' alone matches any single character in regular expression, so an escape character \\ should be added before '.' in order to successfully represent the dot character.

+: Matches the preceding element one or more times.

Since "234." is not a valid decimal number but "234.0" is, "\\.\\d" is succeeded by + instead of *.

$: Matches the ending position of the string

|: The alternation operator

*/


//    IF((farm_condition_ghana == "B") || (ao_tree_age_ghana > 30)  && (ao_planting_ghana == "G") && (ao_disease_ghana == "G")  || (ao_tree_density_ghana == "4x4"), G, B)


public class LogicFormulaParser extends Tokenizer {

    String TAG = this.getClass().getSimpleName();
    String formula = null;
    JSONObject jsonObject = null;
    JSONObject allValuesJson = null;

    Tokenizer TOKENIZER;
    ScriptEngine _engine = new ScriptEngineManager().getEngineByName("rhino");


    private LogicFormulaParser() {
        // TOKENIZER = initializeTokenizer();
    }

    public static LogicFormulaParser getInstance() {
        return new LogicFormulaParser();
    }

    private static Tokenizer initializeTokenizer() {
        Tokenizer tokenizer = new Tokenizer();
        tokenizer.add("IF", AppConstants.TOKEN_IF); // function
        tokenizer.add("\\(", AppConstants.TOKEN_BRACKET_OPEN); // open bracket
        tokenizer.add("\\)", AppConstants.TOKEN_BRAKET_CLOSED); // close bracket
        tokenizer.add("[+-]", AppConstants.TOKEN_ARITHMETIC_PLUS_MINUS); // plus or minus
        tokenizer.add(",", AppConstants.TOKEN_CHAR);
        tokenizer.add("==", AppConstants.TOKEN_OPERATOR_EQUAL_TO); // equals
        tokenizer.add("<=", AppConstants.TOKEN_OPERATOR_GREATER_THAN_EQUALS);
        tokenizer.add(">=", AppConstants.TOKEN_OPERATOR_LESS_THAN_EQUALS);
        tokenizer.add("<", AppConstants.TOKEN_OPERATOR_GREATER_THAN);
        tokenizer.add(">", AppConstants.TOKEN_OPERATOR_LESS_THAN);

        tokenizer.add("||", AppConstants.TOKEN_OPERATOR_OR);
        tokenizer.add("&&", AppConstants.TOKEN_OPERATOR_AND);

        tokenizer.add("\"", AppConstants.TOKEN_QUOTATION);


        tokenizer.add("[0-9]+|[a-zA-Z][a-zA-Z0-9_]*[[*/]-?[0-9]*\\.\\[0-9]+]*", AppConstants.TOKEN_VARIABLE); // variable
        return tokenizer;
    }

    public Tokenizer getTokenizer() {
        tokenize(formula);
        return TOKENIZER;
    }


    @Override
    public void tokenize(String str) {
        TOKENIZER.tokenize(str);
    }


    @Override
    public LinkedList<Token> getTokens() {
        return TOKENIZER.getTokens();
    }


    public String evaluate(String _formula) {
        formula = _formula;
        return evaluate();
    }


    private String evaluate() {
        int count = 0;

        String returnValue = "";
        boolean isNestedIfFormula = false;

       /* if (formula == null)
            throw new ParserException("Equation to evaluate is null or not in the right format");*/

        formula = formula.replace(" ", "");

        AppLogger.e(TAG, "**** UN PARSED EQUATION " + formula);


        String[] nestedFormulas = formula.split("else");

        for (String nestedFormula : nestedFormulas) {


            count += 1;

            if (nestedFormulas.length > 1) {
                isNestedIfFormula = true;
                AppLogger.e(TAG, "==============   FORMULA IS A NESTED IF WITH " + nestedFormulas.length + " PARTS   ================");
            }

            System.out.println("NESTED FORMULA PART " + count + " >>>> " + nestedFormula);

           //BREAK DOWN INTO SECTIONS AND EVALUATE

            String rawFormula = nestedFormula.replace(" ", "")
                    .replace("I", "")
                    .replace("F(", "");


            String[] sections = rawFormula.split(",");


            String formulaToEvaluate = "(" + sections[0] + ")";

            String trueValue = sections[1].replace("\'", "").replace("(", "").replace(")", "");

            String falseValue = "";
            try {
                falseValue = sections[2].replace("\'", "").replace("(", "").replace(")", "");
            } catch (Exception ignored) {
            }


            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String tmp_key = (String) iterator.next();

                if (formulaToEvaluate.contains(tmp_key))
                    formulaToEvaluate = formulaToEvaluate.replace(tmp_key, getValue(tmp_key));

            }

            if (allValuesJson != null) {
                iterator = allValuesJson.keys();
                while (iterator.hasNext()) {
                    String tmp_key = (String) iterator.next();

                    if (formulaToEvaluate.contains(tmp_key))
                        formulaToEvaluate = formulaToEvaluate.replace(tmp_key, getValue(tmp_key));

                }
            }


            String[] subSections = formulaToEvaluate.split("&&|\\|\\|");

            String evaluatedFormula = formulaToEvaluate;

            for (int i = 0; i < subSections.length; i++) {
                String v = subSections[i].replace("(", "").replace(")", "");


                Boolean answerValue = ComputationUtils.parseEquation(v.replace("\"", ""), _engine);

                AppLogger.e(TAG, "SECTION " + (i + 1) + " =>>> " + v + " Answer >>>  " + answerValue);

                evaluatedFormula = evaluatedFormula.replace(v, String.valueOf(answerValue));

                AppLogger.i(TAG, "### Replacing " + v + " with Answer >>>  " + answerValue);
            }

            System.out.println("EQUATION BEFORE REPLACEMENT >>> " + formulaToEvaluate);
            System.out.println("EQUATION AFTER REPLACEMENT >>> " + evaluatedFormula);


            boolean valueAfterParsing = ComputationUtils.parseLogicalEquation(evaluatedFormula.replace("))", ")"));


            AppLogger.e(TAG, "TRUE VALUE = " + trueValue);
            AppLogger.e(TAG, "FALSE VALUE = " + falseValue);


            if (valueAfterParsing) {
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
        this.formula = formula;
    }


}

