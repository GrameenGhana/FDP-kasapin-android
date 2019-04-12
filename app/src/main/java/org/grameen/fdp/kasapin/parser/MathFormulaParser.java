package org.grameen.fdp.kasapin.parser;


import org.grameen.fdp.kasapin.exceptions.ParserException;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.ComputationUtils;
import org.grameen.fdp.kasapin.utilities.Tokenizer;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.grameen.fdp.kasapin.ui.base.BaseActivity.getGson;

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

public class MathFormulaParser extends Tokenizer {

    String mathFormula = null;
    JSONObject jsonObject = null;
    Tokenizer TOKENIZER;
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");



    public static MathFormulaParser getInstance() {
        return new MathFormulaParser();
    }

    private MathFormulaParser() {
        TOKENIZER = initializeTokenizer();
    }
    private static Tokenizer initializeTokenizer() {

        Tokenizer tokenizer = new Tokenizer();
        /*tokenizer.add("IF", AppConstants.TOKEN_IF); // function
        tokenizer.add("==", AppConstants.TOKEN_OPERATOR_EQUAL_TO); // equals
        tokenizer.add("\\(", AppConstants.TOKEN_BRACKET_OPEN); // open bracket
        tokenizer.add("\\)", AppConstants.TOKEN_BRAKET_CLOSED); // close bracket
        tokenizer.add("[+-]", AppConstants.TOKEN_ARITHMETIC_PLUS_MINUS); // plus or minus
        tokenizer.add("[,]", AppConstants.TOKEN_CHAR); // plus or minus*/
        tokenizer.add("[0-9]+|[a-zA-Z][a-zA-Z0-9_]*[[*/]-?[0-9]*\\.[0-9]+]*", AppConstants.TOKEN_VARIABLE); // variable
        return tokenizer;
    }

     Tokenizer getTokenizer() {

        tokenize(mathFormula);

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


    public String evaluate() {
        List<String> sequenceList = new ArrayList<>();
        for (Token tok : getTokenizer().getTokens())
            if (Objects.equals(tok.token, AppConstants.TOKEN_VARIABLE))
                sequenceList.add(tok.sequence);


        AppLogger.e("###  MathFormulaParser >> ", "Sequence list is " + getGson().toJson(sequenceList));

        String newFormula = mathFormula;

        for(String label : sequenceList)
            newFormula = newFormula.replace(label, getValue(label));

        try {
            return calculate(newFormula);
        } catch (ScriptException e) {
            e.printStackTrace();
            return "0";
        }
    }


    public String evaluate(String formula){
        mathFormula = formula;
             AppLogger.e("###  MathFormulaParser >> ","Equation to evaluate is null");

            try {
                return calculate(mathFormula);
            } catch (ScriptException e) {
                e.printStackTrace();
                return "0";
            }
    }

    public String evaluateWithoutFormatting(String formula){
        mathFormula = formula;
        AppLogger.e("###  MathFormulaParser >> ","Equation to evaluate is null");

        try {
            return  String.valueOf(engine.eval(formula.trim().replace(",", "")));
        } catch (ScriptException e) {
            e.printStackTrace();
            return "0";
        }
    }


    private String getValue(String id) {

        try {
            if (jsonObject.has(id))
                return jsonObject.get(id).toString();
            else return id;
        } catch (JSONException ignore) {
            return "0";
        }
    }


    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }


    public void setMathFormula(String mathFormula) {
        this.mathFormula = mathFormula;
    }

    public String calculate(String equation) throws ScriptException {
        Double value = (Double) engine.eval(equation.trim().replace(",", ""));
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat formatter = (DecimalFormat) nf;
        formatter.applyPattern("#,###,###.##");
        return (formatter.format(value));
    }

}

