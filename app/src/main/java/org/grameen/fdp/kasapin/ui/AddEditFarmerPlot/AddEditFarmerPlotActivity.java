package org.grameen.fdp.kasapin.ui.AddEditFarmerPlot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.Recommendation;
import org.grameen.fdp.kasapin.parser.LogicFormulaParser;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.form.fragment.DynamicPlotFormFragment;
import org.grameen.fdp.kasapin.ui.gpsPicker.MapActivity;
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
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddEditFarmerPlotActivity extends BaseActivity implements AddEditFarmerPlotContract.View {
    @Inject
    AddEditFarmerPlotPresenter mPresenter;
    @BindView(R.id.plotNameEdittext)
    EditText plotNameEditText;
    @BindView(R.id.plotSizeEdittext)
    EditText plotSizeEditText;
    @BindView(R.id.estimatedProductionEdittext)
    EditText estimatedProductionEditText;
    @BindView(R.id.phEdittext)
    EditText phEditText;
    @BindView(R.id.saveButton)
    Button saveButton;
    boolean isEditMode = false;
    Plot PLOT;
    String FARMER_CODE;
    Farmer FARMER;
    DynamicPlotFormFragment dynamicPlotFormFragment;
    @BindView(R.id.plot_name_text)
    TextView plotNameText;
    @BindView(R.id.plot_size_text)
    TextView plotSizeText;
    @BindView(R.id.plot_est_prod_text)
    TextView plotEstProdText;
    @BindView(R.id.plot_ph_text)
    TextView plotPhText;
    Question soilPhQuestion;
    Question estProductionQuestion;
    Question plotAreaQuestion;
    Question plotNameQuestion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_a_plot);
        setUnBinder(ButterKnife.bind(this));
        getActivityComponent().inject(this);
        mPresenter.takeView(this);
        mAppDataManager = mPresenter.getAppDataManager();

        FARMER = getGson().fromJson(getIntent().getStringExtra("farmer"), Farmer.class);

        if (FARMER != null)
            FARMER_CODE = FARMER.getCode();
        plotNameQuestion = getAppDataManager().getDatabaseManager().questionDao().get("plot_name_");
        soilPhQuestion = getAppDataManager().getDatabaseManager().questionDao().get("plot_ph_");
        estProductionQuestion = getAppDataManager().getDatabaseManager().questionDao().get("plot_estimate_production_");
        plotAreaQuestion = getAppDataManager().getDatabaseManager().questionDao().get("plot_area_");

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
        PLOT_FORM_AND_QUESTIONS = formAndQuestionsList;
        dynamicPlotFormFragment = DynamicPlotFormFragment.newInstance(isEditMode, FARMER_CODE,
                getAppDataManager().isMonitoring(), PLOT.getAnswersData());
        ActivityUtils.loadDynamicView(getSupportFragmentManager(), dynamicPlotFormFragment, FARMER_CODE);
    }


    void setupViews() {
        Completable.fromAction(() -> {
            if (estProductionQuestion != null)
                plotEstProdText.setText(estProductionQuestion.getCaptionC());

            if (soilPhQuestion != null)
                plotPhText.setText(soilPhQuestion.getCaptionC());

            if (plotNameQuestion != null)
                plotNameText.setText(plotNameQuestion.getCaptionC());

            if (plotAreaQuestion != null)
                plotSizeText.setText(plotAreaQuestion.getCaptionC());

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        //Edit Plot
        saveButton.setEnabled(false);
        plotNameEditText.addTextChangedListener(new TextWatcher() {
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
            setToolbar(getString(R.string.edit_plot));
            PLOT = getGson().fromJson(getIntent().getStringExtra("plot"), Plot.class);
            FARMER_CODE = PLOT.getFarmerCode();
            plotNameEditText.setText(PLOT.getName());
            plotSizeEditText.setText(PLOT.getArea());
            estimatedProductionEditText.setText(PLOT.getEstimatedProductionSize());
            phEditText.setText(PLOT.getPh());
        } else {
            PLOT = new Plot();
            PLOT.setExternalId(FARMER_CODE + String.valueOf(System.currentTimeMillis()));
            PLOT.setFarmerCode(FARMER_CODE);
            PLOT.setAnswersData(new JSONObject().toString());
            PLOT.setCreatedAt(TimeUtils.getCurrentDateTime());
            String name = "Plot " + (getIntent().getIntExtra("plotSize", 0) + 1);
            plotNameEditText.setText(name);
            PLOT.setName(name);
            //New Plot
            setToolbar(getString(R.string.add_new_plot));
        }
        mPresenter.getPlotQuestions();
        saveButton.setOnClickListener(v -> savePlotData(null));
    }

    void savePlotData(String flag) {
        if (validateFields()) {
            showLoading();
            JSONObject jsonObject = dynamicPlotFormFragment.getAnswersData();
            try {
                if (jsonObject.has(soilPhQuestion.getLabelC()))
                    jsonObject.remove(soilPhQuestion.getLabelC());
                jsonObject.put(soilPhQuestion.getLabelC(), phEditText.getText().toString());

                if (jsonObject.has(estProductionQuestion.getLabelC()))
                    jsonObject.remove(estProductionQuestion.getLabelC());
                jsonObject.put(estProductionQuestion.getLabelC(), estimatedProductionEditText.getText().toString());

                if (jsonObject.has(plotAreaQuestion.getLabelC()))
                    jsonObject.remove(plotAreaQuestion.getLabelC());
                jsonObject.put(plotAreaQuestion.getLabelC(), plotSizeEditText.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            AppLogger.e(TAG, jsonObject);
            LogicFormulaParser logicFormulaParser = LogicFormulaParser.getInstance();
            logicFormulaParser.setJsonObject(jsonObject);

            //Compute AOR and AI question values here, put values into the json
            for (FormAndQuestions formAndQuestions : PLOT_FORM_AND_QUESTIONS) {
                if (formAndQuestions.getForm().getFormNameC().equalsIgnoreCase(AppConstants.ADOPTION_OBSERVATION_RESULTS)
                        || formAndQuestions.getForm().getFormNameC().equalsIgnoreCase(AppConstants.ADDITIONAL_INTERVENTION)) {
                    for (Question question : formAndQuestions.getQuestions()) {
                        if (question.getFormulaC() != null && !question.getFormulaC().equalsIgnoreCase("null")) {
                            try {
                                String value = logicFormulaParser.evaluate(question.getFormulaC());
                                if (jsonObject.has(question.getLabelC()))
                                    jsonObject.remove(question.getLabelC());
                                jsonObject.put(question.getLabelC(), value);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            //Todo check if farm size corresponds
            //Todo check if checkIfFarmProductionCorresponds
            //Todo checkIfPlotWasRenovatedRecently

            int year = 1;
            Question PLOT_RENOVATED_CORRECTLY_QUESTION = getAppDataManager().getDatabaseManager().questionDao().get("plot_renovated_correctly_");
            Question PLOT_RENOVATION_MADE_YEARS = getAppDataManager().getDatabaseManager().questionDao().get("plot_renovated_made_");
            Question PLOT_RENOVATION_INTERVENTION_QUESTION = getAppDataManager().getDatabaseManager().questionDao().get("plot_renovated_intervention_");
            Recommendation GAPS_RECOMMENDATION_FOR_START_YEAR = null;
            if (PLOT_RENOVATED_CORRECTLY_QUESTION != null && PLOT_RENOVATION_MADE_YEARS != null) {
                    try {
                        String answerValue = jsonObject.getString(PLOT_RENOVATED_CORRECTLY_QUESTION.getLabelC()).trim();

                        if (answerValue.equalsIgnoreCase("yes")) {
                            year = Integer.parseInt(jsonObject.getString(PLOT_RENOVATION_MADE_YEARS.getLabelC()));

                             String recommendationName = jsonObject.getString(PLOT_RENOVATION_INTERVENTION_QUESTION.getLabelC());

                            if (recommendationName.equalsIgnoreCase("replanting"))
                                GAPS_RECOMMENDATION_FOR_START_YEAR = getAppDataManager().getDatabaseManager().recommendationsDao()
                                        .getByRecommendationName("Replant").blockingGet();
                            else if (recommendationName.equalsIgnoreCase("grafting"))
                                GAPS_RECOMMENDATION_FOR_START_YEAR = getAppDataManager().getDatabaseManager().recommendationsDao()
                                        .getByRecommendationName("Grafting").blockingGet();
                            if (GAPS_RECOMMENDATION_FOR_START_YEAR != null) {
                                PLOT.setRecommendationId(GAPS_RECOMMENDATION_FOR_START_YEAR.getId());
                                PLOT.setGapsId(GAPS_RECOMMENDATION_FOR_START_YEAR.getId());
                                year = year * -1;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        year = 1;
                }
            }

            try {
                String plotInterventionStartYearLabel = getAppDataManager().getDatabaseManager().questionDao().getLabel("plot_intervention_start_year_").blockingGet("null");

                if (jsonObject.has(plotInterventionStartYearLabel))
                    jsonObject.remove(plotInterventionStartYearLabel);
                jsonObject.put(plotInterventionStartYearLabel, year);
                String startYearLabel = getAppDataManager().getDatabaseManager().questionDao().getLabel("start_year_").blockingGet("null");
                if (jsonObject.has(startYearLabel))
                    jsonObject.remove(startYearLabel);
                jsonObject.put(startYearLabel, year);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            PLOT.setStartYear(year);
            PLOT.setAnswersData(jsonObject.toString());
            PLOT.setPh(phEditText.getText().toString());
            PLOT.setName(plotNameEditText.getText().toString());
            PLOT.setEstimatedProductionSize(estimatedProductionEditText.getText().toString());
            PLOT.setLastVisitDate(TimeUtils.getCurrentDateTime());
            PLOT.setUpdatedAt(TimeUtils.getCurrentDateTime());
            PLOT.setArea(plotSizeEditText.getText().toString());

            mPresenter.saveData(PLOT, flag);
        }
    }

    private boolean validateFields() {
        boolean isValid = true;
        if (plotNameEditText.getText().toString().trim().isEmpty()) {
            plotNameEditText.setError(plotNameQuestion.getErrorMessage());
            isValid = false;
        } else
            plotNameEditText.setError(null);

        if (estProductionQuestion.isRequired() && estimatedProductionEditText.getText().toString().trim().isEmpty()) {
            estimatedProductionEditText.setError(estProductionQuestion.getErrorMessage());
            isValid = false;
        } else
            estimatedProductionEditText.setError(null);

        if (plotAreaQuestion.isRequired() && plotSizeEditText.getText().toString().trim().isEmpty()) {
            plotSizeEditText.setError(plotAreaQuestion.getErrorMessage());
            isValid = false;
        } else
            plotSizeEditText.setError(null);
        if (soilPhQuestion.isRequired() && phEditText.getText().toString().trim().isEmpty()) {
            phEditText.setError(soilPhQuestion.getErrorMessage());
            isValid = false;
        } else
            phEditText.setError(null);

        if (!dynamicPlotFormFragment.getFormController().isValidInput()) {
            dynamicPlotFormFragment.getFormController().resetValidationErrors();
            dynamicPlotFormFragment.getFormController().showValidationErrors();
            isValid = false;
        }

        return isValid;
    }

    @OnClick(R.id.plot_area_calculation)
    void onPlotAreaCalculationClicked() {
        //Todo go to Map Activity
        if (!TextUtils.isEmpty(plotNameEditText.getText().toString()))
            savePlotData("gpsPicker");
        else
            showMessage(R.string.provide_plot_name);
    }

    @Override
    public void showPlotDetailsActivity(Plot plot) {
        final Intent intent = new Intent(this, PlotDetailsActivity.class);
        intent.putExtra("plot", new Gson().toJson(plot));
        new Handler().postDelayed(() -> {
            startActivity(intent);
            finish();
        }, 500);
    }

    @Override
    public void moveToMapActivity(Plot plot) {
        final Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("plotExternalId", plot.getExternalId());
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}


