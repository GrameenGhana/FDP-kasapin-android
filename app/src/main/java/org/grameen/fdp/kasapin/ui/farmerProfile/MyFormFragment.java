package org.grameen.fdp.kasapin.ui.farmerProfile;

import android.animation.Animator;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.dao.SkipLogicsDao;
import org.grameen.fdp.kasapin.data.db.entity.Calculation;
import org.grameen.fdp.kasapin.data.db.entity.Form;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.SkipLogic;
import org.grameen.fdp.kasapin.ui.form.fragment.FormFragment;
import org.grameen.fdp.kasapin.ui.form.MyFormController;
import org.grameen.fdp.kasapin.ui.form.controller.MyFormSectionController;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.ArithmeticUtils;
import org.grameen.fdp.kasapin.utilities.CustomToast;
import org.grameen.fdp.kasapin.ui.form.controller.view.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * Created by aangjnr on 01/12/2017.
 */


public class MyFormFragment extends FormFragment {

    List<Question> questions = new ArrayList<>();
    MyFormSectionController formSectionController;
    String formName = "";
    String farmerCode = "";
    boolean shouldLoadOldValues = false;
    boolean isEnabled;
    boolean IS_MONITORING;
    boolean IS_TRANSLATION;

    String TAG = "MYFORMFRAGMENT";

    MyFormSectionController PLOT_INFORMATION_CONTROLLER;
    MyFormSectionController AO_SECTION_CONTROLLER;
    MyFormSectionController AO_RESULTS_SECTION_CONTROLLER;
    MyFormSectionController ADDITIONAL_INTERVENTION_CONTROLLER;

    JSONObject ALL_ANSWERS_JSON_OBJECT;

    List<Question> plotInfoQuestions = new ArrayList<>();

    List<Question> aoQuestions;
    List<Question> aoResultsQuestions;
    List<Question> additionalInterventionQuestions;
    ScriptEngine engine;
    //String CURRENT_PLOT_ID;
    Plot PLOT;
    Form form;
    boolean isTranslation;

    AppDataManager appDataManager;

    SkipLogicsDao skipLogicsDao;

    public MyFormFragment() {

    }

    public static MyFormFragment newInstance(String formName, boolean shouldLoadOldValues, @Nullable String farmerCode, boolean disableFormControlller) {


        MyFormFragment formFragment = new MyFormFragment();

        Bundle bundle = new Bundle();
        bundle.putString("formName", formName);
        bundle.putBoolean("loadValues", shouldLoadOldValues);
        bundle.putBoolean("disable", disableFormControlller);

        bundle.putString("code", farmerCode);

        formFragment.setArguments(bundle);
        return formFragment;


    }

    @Override
    public void onAttach(Context context) {

        engine = getScriptEngine();
        appDataManager = getAppDataManager();

        formName = getArguments().getString("formName");
        shouldLoadOldValues = getArguments().getBoolean("loadValues");
        isEnabled = getArguments().getBoolean("disable");
        farmerCode = getArguments().getString("code");


        IS_MONITORING = appDataManager.isMonitoring();
        IS_TRANSLATION = appDataManager.isTranslation();

        skipLogicsDao = appDataManager.getDatabaseManager().skipLogicsDao();


        super.onAttach(context);
    }

    @Override
    protected void setUp(View view) {


    }

    @Override
    public void initForm(MyFormController controller) {
        Context context = getContext();


























    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (shouldLoadOldValues)
            new Handler().postDelayed(() -> initiateSkipLogicsAndHideViews(questions, getFormController()), 500);


    }


    // This method iterates through the questions list, append their respective answers and
    // parses the list into a JSON string

    public boolean validate() throws JSONException {
        getFormController().resetValidationErrors();
        if (getFormController().isValidInput()) {

            //Send data to server here after getting JSON string

            //Toast.makeText(getContext(), getAllAnswersInJSON(), Toast.LENGTH_LONG).show();

            CustomToast.makeToast(getActivity(), getAllAnswersInJSON(), Toast.LENGTH_LONG).show();

        } else {

            // Whoaaaaaaa! There were some invalid inputs
            getFormController().showValidationErrors();

        }
        return true;
    }

    public String getAllAnswersInJSON() {

        JSONObject jsonObject = new JSONObject();

        for (Question q : questions) {

            try {
                jsonObject.put(q.getId(), getModel().getValue(q.getId()));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return jsonObject.toString();


    }

    public JSONObject getAllAnswersInJSONObject() {


        JSONObject jsonObject = new JSONObject();

        for (Question q : questions) {


            try {
                jsonObject.put(q.getId(), getModel().getValue(q.getId()));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return jsonObject;


    }

    void loadQuestions(Context context, List<Question> questions, MyFormSectionController formSectionController) {


        for (final Question q : questions) {


            if(!q.getShouldHide()){


                //Todo, add checking of form type from Activity
                if(q.getForm().getType().equalsIgnoreCase("monitoring")) isEnabled = false;

                Log.d("MY FORM FRAG ", "TYPE IS " + q.getType());
            switch (q.getType().toLowerCase()) {

                case AppConstants.TYPE_TEXT:
                    formSectionController.addElement(new EditTextController(context, q.getId(),  (IS_TRANSLATION) ?  q.getTranslation() : q.getCaption(), q.getDefaultValue(), true, InputType.TYPE_CLASS_TEXT, !isEnabled, q.getHelpText()));
                    break;
                case AppConstants.TYPE_NUMBER:
                    formSectionController.addElement(new EditTextController(context, q.getId(), (IS_TRANSLATION) ?  q.getTranslation() : q.getCaption(), q.getDefaultValue(), true, InputType.TYPE_CLASS_NUMBER, !isEnabled, q.getHelpText()));

                    break;

                case AppConstants.TYPE_NUMBER_DECIMAL:
                    formSectionController.addElement(new EditTextController(context, q.getId(), (IS_TRANSLATION) ? q.getTranslation() : q.getCaption(), q.getDefaultValue(), true, InputType.TYPE_NUMBER_FLAG_DECIMAL, !isEnabled, q.getHelpText()));

                    break;

                case AppConstants.TYPE_SELECTABLE:
                    formSectionController.addElement(new SelectionController(context, q.getId(), (IS_TRANSLATION) ?  q.getTranslation() : q.getCaption(), true,  q.getDefaultValue()  , q.formatQuestionOptions(), true, !isEnabled, q.getHelpText()));

                    break;
                case AppConstants.TYPE_MULTI_SELECTABLE:
                    formSectionController.addElement(new CheckBoxController(context, q.getId(), (IS_TRANSLATION) ?  q.getTranslation() : q.getCaption(), true, q.formatQuestionOptions(), true, !isEnabled));

                    break;

                case AppConstants.TYPE_TIMEPICKER:
                    formSectionController.addElement(new TimePickerController(context, q.getId(), (IS_TRANSLATION) ?  q.getTranslation() : q.getCaption()));

                    break;
                case AppConstants.TYPE_DATEPICKER:
                    formSectionController.addElement(new DatePickerController(context, q.getId(), (IS_TRANSLATION) ?  q.getTranslation() : q.getCaption()));
                    break;

                case AppConstants.TYPE_MATH_FORMULA:
                    formSectionController.addElement(new EditTextController(context, q.getId(), (IS_TRANSLATION) ?  q.getTranslation() : q.getCaption(), q.getDefaultValue(), true, InputType.TYPE_CLASS_TEXT, false, q.getHelpText()));
                    applyCalculation(appDataManager.getDatabaseManager().calculationsDao().getCalculationById(q.getId()));

                    break;

                case AppConstants.TYPE_LOGIC_FORMULA:
                    formSectionController.addElement(new EditTextController(context, q.getId(), (IS_TRANSLATION) ?  q.getTranslation() : q.getCaption(), q.getDefaultValue(), true, InputType.TYPE_CLASS_TEXT, false, q.getHelpText()));
                    break;


                case AppConstants.TYPE_LOCATION:
                    formSectionController.addElement(new ButtonController(context, q.getId(), (IS_TRANSLATION) ?  q.getTranslation() : q.getCaption(), new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {

                            Log.i(TAG, "^^^^^^^^^^ LOCATION CHANGED ^^^^^^^^^^^^");

                            Log.i(TAG, "lat:" + location.getLatitude() + " lon:" + location.getLongitude());

                            getModel().setValue(q.getId(), location.getLatitude() + ", " + location.getLongitude());


                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {

                        }

                        @Override
                        public void onProviderEnabled(String s) {
                            Log.i(TAG, "^^^^^^^^^^ PROVIDER ENABLED ^^^^^^^^^^^^");

                        }

                        @Override
                        public void onProviderDisabled(String s) {
                            Log.i(TAG, "^^^^^^^^^^ PROVIDER DISABLED ^^^^^^^^^^^^");


                        }
                    }, !isEnabled));
                    break;


                case AppConstants.TYPE_PHOTO:
                    formSectionController.addElement(new PhotoButtonController(context, q.getId(), (IS_TRANSLATION) ? q.getTranslation() : q.getCaption(), (View.OnClickListener) v -> {
                        try{

                            startCameraIntent(q.getId());

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }, !isEnabled));
                    break;
            }
            }


            setUpSkipLogics(q);

        }


    }

    void loadQuestionsWithValues(Context context, List<Question> questions, MyFormSectionController formSectionController) {

        for (final Question q : questions) {

            if(q.getShouldHide()) {


                if (q.getForm().getType().equalsIgnoreCase("monitoring"))
                    isEnabled = false;

                Log.d("MYFORMFRAG ", "TYPE IS " + q.getType());

                String storedValue;
                storedValue = getValue(q);
                if (storedValue.equalsIgnoreCase(""))
                    storedValue = q.getDefaultValue();


                switch (q.getType().toLowerCase()) {

                    case AppConstants.TYPE_TEXT:
                        formSectionController.addElement(new EditTextController(context, q.getId(), (IS_TRANSLATION) ? q.getTranslation() : q.getCaption(), storedValue, true, InputType.TYPE_CLASS_TEXT, !isEnabled, q.getHelpText()));
                        //getValue(q);

                        break;
                    case AppConstants.TYPE_NUMBER:
                        formSectionController.addElement(new EditTextController(context, q.getId(), (IS_TRANSLATION) ? q.getTranslation() : q.getCaption(), storedValue, true, InputType.TYPE_CLASS_NUMBER, !isEnabled, q.getHelpText()));
                        //getValue(q);

                        break;

                    case AppConstants.TYPE_NUMBER_DECIMAL:
                        formSectionController.addElement(new EditTextController(context, q.getId(), (IS_TRANSLATION) ? q.getTranslation() : q.getCaption(), storedValue, true, InputType.TYPE_NUMBER_FLAG_DECIMAL, !isEnabled, q.getHelpText()));
                        //getValue(q);

                        break;

                    case AppConstants.TYPE_SELECTABLE:
                        formSectionController.addElement(new SelectionController(context, q.getId(), (IS_TRANSLATION) ? q.getTranslation() : q.getCaption(), true, storedValue, q.formatQuestionOptions(), true, !isEnabled, q.getHelpText()));
                        //getValue(q);

                        break;

                    case AppConstants.TYPE_MULTI_SELECTABLE:
                        formSectionController.addElement(new CheckBoxController(context, q.getId(), (IS_TRANSLATION) ? q.getTranslation() : q.getCaption(), true, q.formatQuestionOptions(), true, !isEnabled));
                        //getValue(q, jsonObject);

                        break;

                    case AppConstants.TYPE_TIMEPICKER:
                        formSectionController.addElement(new TimePickerController(context, q.getId(), (IS_TRANSLATION) ? q.getTranslation() : q.getCaption()));
                        //getValue(q);

                        break;
                    case AppConstants.TYPE_DATEPICKER:
                        formSectionController.addElement(new DatePickerController(context, q.getId(), (IS_TRANSLATION) ? q.getTranslation() : q.getCaption()));
                        //getValue(q);

                        break;

                    case AppConstants.TYPE_MATH_FORMULA:
                        formSectionController.addElement(new EditTextController(context, q.getId(), (IS_TRANSLATION) ? q.getTranslation() : q.getCaption(), storedValue, true, InputType.TYPE_CLASS_TEXT, false));
                        //getValue(q);
                        applyCalculation(appDataManager.getDatabaseManager().calculationsDao().getCalculationById(q.getId()));

                        break;

                    case AppConstants.TYPE_LOGIC_FORMULA:
                        formSectionController.addElement(new EditTextController(context, q.getId(), (IS_TRANSLATION) ? q.getTranslation() : q.getCaption(), storedValue, true, InputType.TYPE_CLASS_TEXT, false));
                        //getValue(q);
                        break;


                    case AppConstants.TYPE_LOCATION:
                        formSectionController.addElement(new ButtonController(context, q.getId(), (IS_TRANSLATION) ? q.getTranslation() : q.getCaption(), new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {

                                Log.i(TAG, "^^^^^^^^^^ LOCATION CHANGED ^^^^^^^^^^^^");

                                Log.i(TAG, "lat:" + location.getLatitude() + " lon:" + location.getLongitude());

                                getModel().setValue(q.getId(), location.getLatitude() + ", " + location.getLongitude());


                            }

                            @Override
                            public void onStatusChanged(String s, int i, Bundle bundle) {

                            }

                            @Override
                            public void onProviderEnabled(String s) {
                                Log.i(TAG, "^^^^^^^^^^ PROVIDER ENABLED ^^^^^^^^^^^^");

                            }

                            @Override
                            public void onProviderDisabled(String s) {
                                Log.i(TAG, "^^^^^^^^^^ PROVIDER DISABLED ^^^^^^^^^^^^");


                            }
                        }, !isEnabled));
                        //getValue(q);
                        break;


                    case AppConstants.TYPE_PHOTO:
                        formSectionController.addElement(new PhotoButtonController(context, q.getId(), (IS_TRANSLATION) ? q.getTranslation() : q.getCaption(), (View.OnClickListener) v -> {

                            try {

                                startCameraIntent(q.getId());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }, !isEnabled));
                        getValue(q);
                        break;

                }

            }

            setUpSkipLogics(q);

        }

    }

    String getValue(Question q) {

        String defVal = "";
        try {
            defVal = ALL_ANSWERS_JSON_OBJECT.get(q.getId()).toString();
            getModel().setValue(q.getId(), defVal);
            return defVal;

        } catch (JSONException e) {

            Log.i("FORM FRAGMENT", "####### NO STORED VALUE EXCEPTION ##########" + e.getMessage());

            return defVal;
        }

    }

    void setUpSkipLogics(Question q) {
        skipLogicsDao.getAllByQuestionId(q.getId()).observe(this,
                skipLogics -> {

        if (skipLogics != null && skipLogics.size() > 0) {

            final String caption = q.getCaption();

            getModel().addPropertyChangeListener(q.getId(), event -> {

                AppLogger.d("PROPERTY CHANGE FOR QUESTION " + caption + "Value was: " + event.getOldValue() + ", now: " + event.getNewValue());

                for (SkipLogic sl : skipLogics) {


                    try {
                        if (ArithmeticUtils.compareBooleanValues(engine, sl, String.valueOf(event.getNewValue()))) {


                            if (sl.getActionToBeTaken().equalsIgnoreCase(AppConstants.HIDE))
                                getFormController().getElement(sl.getQuestionShowHide()).getView().setVisibility(View.GONE);

                            else
                                getFormController().getElement(sl.getQuestionShowHide()).getView().setVisibility(View.VISIBLE);

                        } else {

                            if (sl.getActionToBeTaken().equalsIgnoreCase(AppConstants.HIDE))
                                getFormController().getElement(sl.getQuestionShowHide()).getView().setVisibility(View.VISIBLE);
                            else
                                getFormController().getElement(sl.getQuestionShowHide()).getView().setVisibility(View.GONE);
                        }


                    } catch (Exception ignored) {
                    }


                }
            });


        }
                });
    }

    void initiateSkipLogicsAndHideViews(List<Question> questions, final MyFormController formController) {

        AppLogger.d(TAG + " QUESTIONS SIZE IS " + questions.size());

        for (Question q : questions) {

            skipLogicsDao.getAllByQuestionId(q.getId()).observe(this, skipLogics -> {

                if (skipLogics != null && skipLogics.size() > 0) {


                    final String caption = q.getCaption();

                    Log.i(TAG, "Size of Skip Logic is " + skipLogics.size());


                    for (final SkipLogic sl : skipLogics) {


                        try {

                            if (ArithmeticUtils.compareBooleanValues(engine, sl, formController.getModel().getValue(sl.getQuestionId()).toString())) {

                                Log.i(TAG, "COMPARING VALUES EVALUATED TO " + true);

                                if (sl.getActionToBeTaken().equalsIgnoreCase(AppConstants.HIDE)) {

                                    formController.getElement(sl.getQuestionShowHide()).getView().animate().alpha(0f).setDuration(200).setListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animator) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animator) {
                                            formController.getElement(sl.getQuestionShowHide()).getView().setVisibility(View.GONE);
                                            formController.getElement(sl.getQuestionShowHide()).getView().setAlpha(1);

                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animator) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animator) {

                                        }
                                    })
                                            .start();


                                } else {
                                    formController.getElement(sl.getQuestionShowHide()).getView().animate().alpha(1f).setDuration(500).setListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animator) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animator) {
                                            formController.getElement(sl.getQuestionShowHide()).getView().setVisibility(View.VISIBLE);
                                            formController.getElement(sl.getQuestionShowHide()).getView().clearAnimation();

                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animator) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animator) {

                                        }
                                    })
                                            .start();

                                }

                            } else {

                                Log.i(TAG, "COMPARING VALUES EVALUATED TO " + false);

                                if (sl.getActionToBeTaken().equalsIgnoreCase(AppConstants.HIDE)) {

                                    formController.getElement(sl.getQuestionShowHide()).getView().animate().alpha(1f).setDuration(500)
                                            .setListener(new Animator.AnimatorListener() {
                                                @Override
                                                public void onAnimationStart(Animator animator) {

                                                }

                                                @Override
                                                public void onAnimationEnd(Animator animator) {
                                                    formController.getElement(sl.getQuestionShowHide()).getView().setVisibility(View.VISIBLE);
                                                    formController.getElement(sl.getQuestionShowHide()).getView().clearAnimation();

                                                }

                                                @Override
                                                public void onAnimationCancel(Animator animator) {

                                                }

                                                @Override
                                                public void onAnimationRepeat(Animator animator) {

                                                }
                                            })
                                            .start();
                                } else {
                                    formController.getElement(sl.getQuestionShowHide()).getView().animate().alpha(0f).setDuration(500).setListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animator) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animator) {
                                            formController.getElement(sl.getQuestionShowHide()).getView().setVisibility(View.GONE);
                                            formController.getElement(sl.getQuestionShowHide()).getView().setAlpha(1);

                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animator) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animator) {

                                        }
                                    })
                                            .start();

                                }
                            }


                        } catch (Exception ignored) {
                            ignored.printStackTrace();
                            Log.i(TAG, "INITIALIZING SKIP LOGIC " + ignored.getMessage());

                        }


                    }
                }


            });

        }
    }

    void applyCalculation(final Calculation calculation) {

        if (calculation.getQuestion4() != null && !calculation.getQuestion4().equals("null")) {

            System.out.println("***************************** CALCULATION WITH 4 DATA VALUES");

            addPropertyChangeListener(calculation, calculation.getQuestion1());
            addPropertyChangeListener(calculation, calculation.getQuestion2());
            addPropertyChangeListener(calculation, calculation.getQuestion3());
            addPropertyChangeListener(calculation, calculation.getQuestion4());


        } else if (calculation.getQuestion3() != null && !calculation.getQuestion3().equals("null")) {

            System.out.println("***************************** CALCULATION WITH 3 DATA VALUES");

            addPropertyChangeListener(calculation, calculation.getQuestion1());
            addPropertyChangeListener(calculation, calculation.getQuestion2());
            addPropertyChangeListener(calculation, calculation.getQuestion3());


        } else if (calculation.getQuestion2() != null && !calculation.getQuestion2().equals("null")) {

            System.out.println("***************************** CALCULATION WITH 2 DATA VALUES");

            addPropertyChangeListener(calculation, calculation.getQuestion1());
            addPropertyChangeListener(calculation, calculation.getQuestion2());

        }


    }

    void addPropertyChangeListener(final Calculation calculation, String id) {


            getModel().addPropertyChangeListener(id, propertyChangeEvent -> {

                String equation = getModel().getValue(calculation.getQuestion1()) + calculation.getOperator1()
                        + getModel().getValue(calculation.getQuestion2()) + calculation.getOperator2()
                        + getModel().getValue(calculation.getQuestion3()) + calculation.getOperator3()
                        + getModel().getValue(calculation.getQuestion4());

                equation = equation.replace("null", "").replace(",", "");

                System.out.println("####### PROPERTY CHANGE LISTENER FIRED ------ NO COMPLEX CALC");
                System.out.println("EQUATION IS " + equation);

                String newValue = "0.00";
                try {

                    newValue = ArithmeticUtils.calculateAndFormatDouble(engine, equation);

                    getModel().setValue(calculation.getResultQuestion(), newValue);

                } catch (ScriptException e) {
                    e.printStackTrace();

                    getModel().setValue(calculation.getResultQuestion(), newValue);

                }

                System.out.println("####### NEW VALUE IS " + newValue);

            });



    }

    public List<Question> getQuestions() {

        return questions;
    }


    @Override
    public void openNextActivity() {

    }

    @Override
    public void openLoginActivityOnTokenExpire() {

    }

    @Override
    public void onToggleFullScreenClicked(Boolean hideNavBar) {

    }
}