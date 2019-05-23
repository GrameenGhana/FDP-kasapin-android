package org.grameen.fdp.kasapin.ui.AddEditFarmerPlot;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.data.db.entity.Recommendation;
import org.grameen.fdp.kasapin.parser.LogicFormulaParser;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.form.fragment.DynamicPlotFormFragment;
import org.grameen.fdp.kasapin.ui.map.MapActivity;
import org.grameen.fdp.kasapin.ui.plotDetails.PlotDetailsActivity;
import org.grameen.fdp.kasapin.utilities.ActivityUtils;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.ComputationUtils;
import org.grameen.fdp.kasapin.utilities.TimeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A login screen that offers login via email/password.
 */
public class AddEditFarmerPlotActivity extends BaseActivity implements AddEditFarmerPlotContract.View {

    @Inject
    AddEditFarmerPlotPresenter mPresenter;
    @BindView(R.id.plotNameEdittext)
    EditText plotNameEdittext;
    @BindView(R.id.plotSizeEdittext)
    EditText plotSizeEdittext;
    @BindView(R.id.estimatedProductionEdittext)
    EditText estimatedProductionEdittext;
    @BindView(R.id.phEdittext)
    EditText phEdittext;
    @BindView(R.id.content)
    CardView content;
    @BindView(R.id.back)
    Button back;
    @BindView(R.id.saveButton)
    Button saveButton;
    boolean isEditMode = false;

    Plot PLOT;
    String FARMER_CODE;
    RealFarmer FARMER;

    DynamicPlotFormFragment dynamicPlotFormFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_a_plot);
        setUnBinder(ButterKnife.bind(this));

        getActivityComponent().inject(this);
        mPresenter.takeView(this);
        mAppDataManager = mPresenter.getAppDataManager();


        FARMER = getGson().fromJson(getIntent().getStringExtra("farmer"), RealFarmer.class);

        if (FARMER != null)
            FARMER_CODE = FARMER.getCode();


        setupViews();

    }


    @Override
    protected void onDestroy() {
        if (mPresenter != null)
            mPresenter.dropView();
        super.onDestroy();
    }


    @Override
    public void openNextActivity() {


    }

    @Override
    public void openLoginActivityOnTokenExpire() {

    }


    @Override
    public void showForm(List<FormAndQuestions> formAndQuestionsList) {

        AppLogger.e(TAG, "Plot Questions list size is " + formAndQuestionsList.size());

        PLOT_FORM_AND_QUESTIONS = formAndQuestionsList;

        dynamicPlotFormFragment = DynamicPlotFormFragment.newInstance(isEditMode, FARMER_CODE,
                getAppDataManager().isMonitoring(), PLOT.getAnswersData());

        ActivityUtils.loadDynamicView(getSupportFragmentManager(), dynamicPlotFormFragment, FARMER_CODE);

    }


    void setupViews() {

        //Edit Plot
        saveButton.setEnabled(false);
        plotNameEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.length() < 2) {
                    saveButton.setEnabled(false);
                    showMessage(R.string.enter_valid_plot_name);

                } else saveButton.setEnabled(true);
            }
        });


        if (getIntent().getStringExtra("flag") != null && getIntent().getStringExtra("flag").equals("edit")) {

            isEditMode = true;
            setToolbar(getStringResources(R.string.edit_plot));

            PLOT = getGson().fromJson(getIntent().getStringExtra("plot"), Plot.class);
            FARMER_CODE = PLOT.getFarmerCode();


            plotNameEdittext.setText(PLOT.getName());
            plotSizeEdittext.setText(PLOT.getArea());
            estimatedProductionEdittext.setText(PLOT.getEstimatedProductionSize());
            phEdittext.setText(PLOT.getPh());


        } else {

            PLOT = new Plot();
            PLOT.setExternalId(String.valueOf(System.currentTimeMillis()));
            PLOT.setFarmerCode(FARMER_CODE);
            PLOT.setAnswersData(new JSONObject().toString());

            String name = "Plot " + (getIntent().getIntExtra("plotSize", 0) + 1);

            plotNameEdittext.setText(name);
            PLOT.setName(name);


            //New Plot
            setToolbar(getStringResources(R.string.add_new_plot));
        }

        AppLogger.e(TAG, "Farmer Code is " + FARMER_CODE);


        mPresenter.getPlotQuestions();
        saveButton.setOnClickListener(v -> savePlotData(null));

    }


    void savePlotData(String flag) {

        showLoading();

        JSONObject jsonObject = dynamicPlotFormFragment.getAnswersData();


        String soilPhLabel = getAppDataManager().getDatabaseManager().questionDao().getLabel("plot_ph_").blockingGet();
        String estProductionLabel = getAppDataManager().getDatabaseManager().questionDao().getLabel("plot_estimate_production_").blockingGet();
        String plotArea = getAppDataManager().getDatabaseManager().questionDao().getLabel("plot_area_").blockingGet();


        try {
            if (jsonObject.has(soilPhLabel))
                jsonObject.remove(soilPhLabel);

            if (jsonObject.has(estProductionLabel))
                jsonObject.remove(estProductionLabel);

            if (jsonObject.has(plotArea))
                jsonObject.remove(plotArea);

            jsonObject.put(soilPhLabel, phEdittext.getText().toString());
            jsonObject.put(estProductionLabel, estimatedProductionEdittext.getText().toString());
            jsonObject.put(plotArea, plotSizeEdittext.getText().toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }


        LogicFormulaParser logicFormulaParser = LogicFormulaParser.getInstance();
        logicFormulaParser.setJsonObject(jsonObject);


        //Calculate AOR and AI question values here, put values into the json
        for (FormAndQuestions formAndQuestions : PLOT_FORM_AND_QUESTIONS) {

            if (formAndQuestions.getForm().getFormNameC().equalsIgnoreCase(AppConstants.ADOPTION_OBSERVATION_RESULTS)
                    || formAndQuestions.getForm().getFormNameC().equalsIgnoreCase(AppConstants.ADDITIONAL_INTERVENTION)) {


                for (Question question : formAndQuestions.getQuestions()) {
                    if (question.getFormulaC() != null && !question.getFormulaC().equalsIgnoreCase("null")) {


                        try {

                            AppLogger.e(TAG, "---------------------------------------------------------------------------");
                            AppLogger.e(TAG, "---------------------------------------------------------------------------");

                            AppLogger.e(TAG, "Question name is ***** " + question.getLabelC() + " *****");

                            String value = logicFormulaParser.evaluate(question.getFormulaC());

                            if (jsonObject.has(question.getLabelC()))
                                jsonObject.remove(question.getLabelC());

                            jsonObject.put(question.getLabelC(), value);

                            AppLogger.e(TAG, "Added " + value + " to json for " + question.getLabelC());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                //AOR_AI_QUESTIONS.addAll(formAndQuestions.getQuestions());
            }


            /*else if(formAndQuestions.getForm().getFormNameC().equalsIgnoreCase(AppConstants.PLOT_INFORMATION)){

                for(Question question : formAndQuestions.getQuestions()){
                    if(question.getTypeC().equalsIgnoreCase(AppConstants.FORMULA_TYPE_COMPLEX_FORMULA) && !question.getFormulaC().equalsIgnoreCase("null")){


                        AppLogger.e(TAG, "---------------------------------------------------------------------------");
                        AppLogger.e(TAG, "---------------------------------------------------------------------------");

                        AppLogger.e(TAG, "Question name is ***** " + question.getLabelC() + " *****");

                        try {

                            String value = logicFormulaParser.evaluate(question.getFormulaC());

                            if(jsonObject.has(question.getLabelC()))
                                jsonObject.remove(question.getLabelC());
                            jsonObject.put(question.getLabelC(), value);
                        }catch(Exception e){e.printStackTrace();
                        }
                    }
                }
            }*/
        }

        AppLogger.i(TAG, "-----------------------  FINAL JSON VALUE   ------------------------");
        AppLogger.i(TAG, jsonObject.toString());


        //Todo check if farm size corresponds
        //Todo check if checkIfFarmProductionCorresponds
        //Todo checkIfPlotWasRenovatedRecently

        int year = 1;
        Question PLOT_RENOVATED_CORRECTLY_QUESTION = getAppDataManager().getDatabaseManager().questionDao().get("plot_renovated_").blockingGet();
        Question PLOT_RENOVATION_MADE = getAppDataManager().getDatabaseManager().questionDao().get("plot_renovated_made_").blockingGet();
        Question PLOT_RENOVATION_INTERVENTION_QUESTION = getAppDataManager().getDatabaseManager().questionDao().get("plot_renovated_intervention_").blockingGet();
        Recommendation GAPS_RECOMENDATION_FOR_START_YEAR = null;


        PLOT.setRecommendationId(-1);



        if(PLOT_RENOVATED_CORRECTLY_QUESTION != null && PLOT_RENOVATION_MADE != null) {

            if (jsonObject.has(PLOT_RENOVATED_CORRECTLY_QUESTION.getLabelC())) {
                try {
                    if (ComputationUtils.getValue(PLOT_RENOVATED_CORRECTLY_QUESTION.getLabelC(), jsonObject).equalsIgnoreCase("yes")) {

                        AppLogger.e(TAG, "PLOT RENOVATED CORRECTLY? >>>> YES");
                        year = Integer.parseInt(jsonObject.getString(PLOT_RENOVATION_MADE.getLabelC()));


                        AppLogger.e(TAG, "START YEAR >>>> " + year);


                        String recommendationName = jsonObject.getString(PLOT_RENOVATION_INTERVENTION_QUESTION.getLabelC());

                        if (recommendationName.equalsIgnoreCase("replanting"))
                            GAPS_RECOMENDATION_FOR_START_YEAR = getAppDataManager().getDatabaseManager().recommendationsDao()
                                    .getLabel("Replant").blockingGet();


                        else if (recommendationName.equalsIgnoreCase("grafting"))
                            GAPS_RECOMENDATION_FOR_START_YEAR = getAppDataManager().getDatabaseManager().recommendationsDao()
                                    .getLabel("Grafting").blockingGet();

                        if (GAPS_RECOMENDATION_FOR_START_YEAR != null) {

                            AppLogger.e(TAG, "RECOMMENDATION MADE  >>>> " + GAPS_RECOMENDATION_FOR_START_YEAR.getLabel());


                            PLOT.setRecommendationId(GAPS_RECOMENDATION_FOR_START_YEAR.getId());
                            PLOT.setGapsId(GAPS_RECOMENDATION_FOR_START_YEAR.getId());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }




        PLOT.setStartYear(year);
        PLOT.setAnswersData(jsonObject.toString());
        PLOT.setPh(phEdittext.getText().toString());
        PLOT.setName(plotNameEdittext.getText().toString());
        PLOT.setEstimatedProductionSize(estimatedProductionEdittext.getText().toString());
        PLOT.setLastVisitDate(TimeUtils.getCurrentDateTime());
        PLOT.setArea(plotSizeEdittext.getText().toString());
        mPresenter.saveData(PLOT, flag);
    }


    @OnClick(R.id.plot_area_calculation)
    void openPlotAreaCalculationActivity() {

        //Todo go to Map Activity
        if (!plotNameEdittext.getText().toString().isEmpty() || !plotNameEdittext.getText().toString().equals(""))
            savePlotData("MAP");
        else
            showMessage(R.string.provide_plot_name);
    }


    @Override
    public void showPlotDetailsActivity(Plot plot) {

        final Intent intent = new Intent(this, PlotDetailsActivity.class);
        intent.putExtra("plot", new Gson().toJson(PLOT));
        new Handler().postDelayed(() -> {
            startActivity(intent);
            finish();
        }, 500);

    }

    @Override
    public void moveToMapActivity(Plot plot) {

        final Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("plot", new Gson().toJson(PLOT));
        new Handler().postDelayed(() -> {
            startActivity(intent);
            finish();
        }, 500);


    }


    @Override
    public void onBackPressed() {
        finish();
    }
}


