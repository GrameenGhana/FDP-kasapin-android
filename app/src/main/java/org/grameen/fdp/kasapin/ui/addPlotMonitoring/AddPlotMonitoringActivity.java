package org.grameen.fdp.kasapin.ui.addPlotMonitoring;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Monitoring;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.SkipLogic;
import org.grameen.fdp.kasapin.parser.LogicFormulaParser;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.form.InputValidator;
import org.grameen.fdp.kasapin.ui.form.ValidationError;
import org.grameen.fdp.kasapin.ui.form.fragment.DynamicFormFragment;
import org.grameen.fdp.kasapin.ui.plotMonitoringActivity.PlotMonitoringActivity;
import org.grameen.fdp.kasapin.utilities.ActivityUtils;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.ComputationUtils;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;
import org.grameen.fdp.kasapin.utilities.TimeUtils;
import org.grameen.fdp.kasapin.utilities.Validator;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

public class AddPlotMonitoringActivity extends BaseActivity implements AddPlotMonitoringContract.View, FdpCallbacks.AnItemSelectedListener {
    @Inject
    AddPlotMonitoringPresenter mPresenter;
    Farmer FARMER;
    Plot PLOT;
    DynamicFormFragment dynamicFormFragment;
    @BindView(R.id.plotName)
    TextView plotName;
    @BindView(R.id.landSize)
    TextView landSize;
    @BindView(R.id.estimatedProductionSize)
    TextView estimatedProductionSize;
    @BindView(R.id.recommended_intervention)
    TextView recommendedIntervention;
    @BindView(R.id.ph)
    TextView ph;
    @BindView(R.id.lime_needed)
    TextView limeNeeded;
    @BindView(R.id.lime_needed_caption)
    TextView limeNeededCaptionTextView;
    @BindView(R.id.soil_ph_caption)
    TextView soilPhCaptionTextView;
    @BindView(R.id.monitoringAOLayout)
    ViewGroup monitoringAOLayout;
    Question recommendationQuestion;
    List<View> ALL_VIEWS_LIST = new ArrayList<>();
    boolean IS_NEW_MONITORING;
    int MONITORING_POSITION;
    String SELECTED_YEAR = "";
    List<View> COMPETENCE_VIEWS = new ArrayList<>();
    JSONObject MONITORING_ANSWERS_JSON;
    JSONObject PLOT_AO_ANSWERS_JSON;
    Monitoring MONITORING;
    ScriptEngine scriptEngine;
    Iterator<String> i1;
    String tmp_key;
    String startYearLabel;
    int counter = 0;
    Validator validator = new Validator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_plot_monitoring);
        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.takeView(this);
        FARMER = new Gson().fromJson(getIntent().getStringExtra("farmer"), Farmer.class);
        PLOT = new Gson().fromJson(getIntent().getStringExtra("plot"), Plot.class);
        MONITORING_POSITION = getIntent().getIntExtra("monitoringPosition", 1);
        MONITORING = getGson().fromJson(getIntent().getStringExtra("monitoring"), Monitoring.class);
        SELECTED_YEAR = String.valueOf(getIntent().getIntExtra("year", -1));
        scriptEngine = new ScriptEngineManager().getEngineByName("rhino");
        IS_NEW_MONITORING = MONITORING == null;
        startYearLabel = getAppDataManager().getDatabaseManager().questionDao().getLabel("start_year_").blockingGet();
        setUpViews();
        onBackClicked();
    }

    @Override
    public void setUpViews() {
        monitoringAOLayout.setAlpha(0f);
        try {
            PLOT_AO_ANSWERS_JSON = PLOT.getAOJsonData();
        } catch (JSONException e) {
            e.printStackTrace();
            PLOT_AO_ANSWERS_JSON = new JSONObject();
        }

        plotName.setText(PLOT.getName());
        ph.setText(PLOT.getPh());
        i1 = PLOT_AO_ANSWERS_JSON.keys();
        while (i1.hasNext()) {
            tmp_key = i1.next();
            if (tmp_key.toLowerCase().contains("lime")) {
                try {
                    limeNeeded.setText(PLOT_AO_ANSWERS_JSON.getString(tmp_key));
                    limeNeeded.setTextColor(ContextCompat.getColor(this, R.color.cpb_red));
                    break;
                } catch (JSONException ignored) {
                }
            }
        }

        mPresenter.getAreaUnits(PLOT.getFarmerCode());
        //SetCaptionLabels
        Completable.fromAction(() -> {
            String estimatedProductionSizeCaption = getAppDataManager().getDatabaseManager().questionDao().getCaption("plot_estimate_production_");
            if (!TextUtils.isEmpty(estimatedProductionSizeCaption))
                estimatedProductionSize.setText(estimatedProductionSizeCaption);
            recommendationQuestion = getAppDataManager().getDatabaseManager().questionDao().get("plot_recommendation_");
            if (!TextUtils.isEmpty(recommendationQuestion.getCaptionC()))
                recommendedIntervention.setText(recommendationQuestion.getCaptionC());
            String soilPhCaption = getAppDataManager().getDatabaseManager().questionDao().getCaption("plot_ph_");
            if (!TextUtils.isEmpty(soilPhCaption))
                soilPhCaptionTextView.setText(soilPhCaption);
            String limeNeededCaption = getAppDataManager().getDatabaseManager().questionDao().getCaption("lime_");
            if (!TextUtils.isEmpty(limeNeededCaption))
                limeNeededCaptionTextView.setText(limeNeededCaption);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable ignored) {
                    }
                });
        if (PLOT.getRecommendationId() > 0) {
            getAppDataManager().getCompositeDisposable().add(getAppDataManager().getDatabaseManager().recommendationsDao().getByRecommendationId(PLOT.getRecommendationId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(s -> recommendedIntervention.setText(s), throwable -> showMessage("Couldn't load plot's recommendation")));
        }
        if (IS_NEW_MONITORING) {
            setToolbar(getString(R.string.add_plot_monitoring));
            MONITORING_ANSWERS_JSON = new JSONObject();
            MONITORING = new Monitoring();
            MONITORING.setExternalId(String.valueOf(System.currentTimeMillis()));
            MONITORING.setName("Monitoring " + MONITORING_POSITION);
            MONITORING.setPlotExternalId(PLOT.getExternalId());
            MONITORING.setYear(SELECTED_YEAR);

        } else {
            setToolbar(getString(R.string.edit_plot_monitoring) + " " + MONITORING.getName());
            try {
                MONITORING_ANSWERS_JSON = MONITORING.getMonitoringAOJsonData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mPresenter.getMonitoringQuestions();
    }

    @Override
    public void setAreaUnits(String unit) {
        landSize.setText(String.format("%s %s", PLOT.getArea(), unit));
    }

    @Override
    public void setProductionUnit(String unit) {
        estimatedProductionSize.setText(String.format("%s %s", PLOT.getEstimatedProductionSize(), unit));
    }


    @Override
    public void loadDynamicFragmentAndViews(FormAndQuestions monitoringPlotInfoQuestions, FormAndQuestions aoMonitoringQuestions) {
        FormAnswerData formAnswerData = new FormAnswerData();
        formAnswerData.setFarmerCode(PLOT.getFarmerCode());
        formAnswerData.setFormId(monitoringPlotInfoQuestions.getForm().getId());

        if (IS_NEW_MONITORING) {
            formAnswerData.setCreatedAt(TimeUtils.getCurrentDateTime());
            formAnswerData.setData(new JSONObject().toString());
            dynamicFormFragment = DynamicFormFragment.newInstance(monitoringPlotInfoQuestions, false, PLOT.getFarmerCode(), false, formAnswerData);
        } else {
            formAnswerData.setData(MONITORING_ANSWERS_JSON.toString());
            dynamicFormFragment = DynamicFormFragment.newInstance(monitoringPlotInfoQuestions, true, PLOT.getFarmerCode(), false, formAnswerData);
        }
        ActivityUtils.loadDynamicView(getSupportFragmentManager(), dynamicFormFragment, monitoringPlotInfoQuestions.getForm().getFormNameC());

        new Handler().postDelayed(() -> {
            for (Question q : aoMonitoringQuestions.getQuestions()) {
                addViewsDynamically(q);
                if (!IS_NEW_MONITORING)
                    setUpPropertyChangeListeners(q, ComputationUtils.getDataValue(q, MONITORING_ANSWERS_JSON));
            }
            monitoringAOLayout.animate().alpha(1f).setDuration(200).start();
            Question monitoringObservationByQuestion = getAppDataManager().getDatabaseManager().questionDao().get("monitoring_plot_observationby_");
            if (monitoringObservationByQuestion != null)
                dynamicFormFragment.getModel().addPropertyChangeListener(monitoringObservationByQuestion.getLabelC(), evt -> {
                    String newValue = (String) evt.getNewValue();
                    if (newValue.equalsIgnoreCase("manager"))
                        for (int i = 0; i < COMPETENCE_VIEWS.size(); i++)
                            COMPETENCE_VIEWS.get(i).setEnabled(true);
                    else
                        for (int i = 0; i < COMPETENCE_VIEWS.size(); i++)
                            COMPETENCE_VIEWS.get(i).setEnabled(false);
                });
        }, 500);
    }

    @Override
    public void addViewsDynamically(Question q) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setBackgroundColor((counter % 2 == 0)
                ? ContextCompat.getColor(this, R.color.light_grey)
                : ContextCompat.getColor(this, R.color.white));
        counter += 1;

        LinearLayout.LayoutParams labelParams = getLayoutParameters();
        labelParams.weight = 4;

        View labelView = getLabelView(q);
        linearLayout.addView(labelView, labelParams);
        ALL_VIEWS_LIST.add(labelView);

        LinearLayout.LayoutParams aoParam = getLayoutParameters();
        aoParam.weight = 2;

        View AOView = getAOView(q);
        linearLayout.addView(AOView, aoParam);
        ALL_VIEWS_LIST.add(AOView);

        String[] relatedQuestions = q.splitRelatedQuestions();
        if (relatedQuestions != null) {
            String competenceName = relatedQuestions[0];
            String reasonForFailureName = relatedQuestions[1];
            LinearLayout.LayoutParams competenceParams = getLayoutParameters();
            competenceParams.weight = 2;

            Question competenceQuestion = getAppDataManager().getDatabaseManager().questionDao().get(competenceName);
            View competenceView = getCompetenceView(competenceQuestion);
            linearLayout.addView(competenceView, competenceParams);

            ALL_VIEWS_LIST.add(competenceView);
            LinearLayout.LayoutParams reasonForFailureParam = getLayoutParameters();
            reasonForFailureParam.weight = 3;

            Question reasonForFailureQuestion = getAppDataManager().getDatabaseManager().questionDao().get(reasonForFailureName);
            View failureView = getReasonForFailureView(reasonForFailureQuestion);
            linearLayout.addView(failureView, reasonForFailureParam);
            ALL_VIEWS_LIST.add(failureView);
            monitoringAOLayout.addView(linearLayout);
        }
        validator.addValidation(q);
    }

    LinearLayout.LayoutParams getLayoutParameters() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        params.leftMargin = 10;
        params.rightMargin = 10;
        params.topMargin = 10;
        params.bottomMargin = 10;
        return params;
    }

    @Override
    public View getLabelView(Question q) {
        View view;
        TextView textView = new TextView(this);
        textView.setText(q.getCaptionC());
        textView.setTextSize(13);
        textView.setTag(null);
        textView.setPadding(10, 10, 10, 10);
        view = textView;
        return view;
    }

    @Override
    public View getAOView(Question q) {
        if (q.getTypeC().equalsIgnoreCase(AppConstants.TYPE_NUMBER)) {
            View layout = LayoutInflater.from(this).inflate(R.layout.layout_edittext, null, false);
            layout.setTag(q);
            EditText editText = layout.findViewById(R.id.cell_data);
            editText.setHint(ComputationUtils.getDataValue(q, MONITORING_ANSWERS_JSON));
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setTag(q);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (MONITORING_ANSWERS_JSON.has(q.getLabelC())) {
                            MONITORING_ANSWERS_JSON.remove(q.getLabelC());
                        }
                        MONITORING_ANSWERS_JSON.put(q.getLabelC(), s.toString());
                        setUpPropertyChangeListeners(q, s.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            return layout;
        } else {
            View layout = LayoutInflater.from(this).inflate(R.layout.layout_spinner, null, false);
            layout.setTag(q);
            setSpinnerAdapter(layout.findViewById(R.id.cell_data), q);
            return layout;
        }
    }

    @Override
    public View getCompetenceView(Question q) {
        View layout = LayoutInflater.from(this).inflate(R.layout.layout_spinner, null, false);
        layout.setTag(q);
        Spinner spinner = layout.findViewById(R.id.cell_data);
        setSpinnerAdapter(spinner, q);
        COMPETENCE_VIEWS.add(spinner);
        return layout;
    }

    @Override
    public View getReasonForFailureView(Question q) {
        View layout = LayoutInflater.from(this).inflate(R.layout.layout_spinner, null, false);
        layout.setTag(q);
        setSpinnerAdapter(layout.findViewById(R.id.cell_data), q);
        return layout;
    }

    void setSpinnerAdapter(Spinner spinner, Question q) {
        spinner.setPrompt(q.getDefaultValueC());
        final List<String> items = q.formatQuestionOptions();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(AddPlotMonitoringActivity.this, android.R.layout.simple_spinner_item, items) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    TextView itemView = (view.findViewById(android.R.id.text1));
                    itemView.setText("");
                    itemView.setHint(getItem(getCount()));
                }
                return view;
            }

            @Override
            public int getCount() {
                return super.getCount(); // don't display last item (it's used for the prompt)
            }
        };
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                try {
                    if (MONITORING_ANSWERS_JSON.has(q.getLabelC())) {
                        MONITORING_ANSWERS_JSON.remove(q.getLabelC());
                    }
                    MONITORING_ANSWERS_JSON.put(q.getLabelC(), parent.getSelectedItem().toString());
                    setUpPropertyChangeListeners(q, parent.getSelectedItem().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        refresh(spinner, ComputationUtils.getDataValue(q, MONITORING_ANSWERS_JSON), items);
    }

    @Override
    protected void onDestroy() {
        mPresenter.dropView();
        super.onDestroy();
    }

    @Override
    public void openNextActivity() {
        int year = Integer.parseInt(SELECTED_YEAR);
        int oldYear = 1;
        try {
            oldYear = Integer.parseInt(getAppDataManager().getStringValue(PLOT.getFarmerCode()));
        } catch (Exception ignored) {
        }
        if (year >= oldYear)
            getAppDataManager().setStringValue(PLOT.getFarmerCode(), String.valueOf(year));
        //Todo go to plot activity, reload viewpager
        PlotMonitoringActivity.newMonitoringAdded = IS_NEW_MONITORING;
        getAppDataManager().setBooleanValue("refreshViewPager", true);
        finish();
    }

    @Override
    public void onItemSelected(String item) {
    }

    @Override
    public void refresh(Spinner spinner, @Nullable String defValue, List<String> items) {
        int selectionIndex = items.size() - 1;    // index of last item shows the 'prompt'
        if (defValue != null)
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).equals(defValue)) {
                    selectionIndex = i;
                    break;
                }
            }
        spinner.setSelection(selectionIndex);
    }

    public void setUpPropertyChangeListeners(Question question, String value) {
        List<SkipLogic> skipLogics = getAppDataManager().getDatabaseManager().skipLogicsDao().getMaybeSkipLogicByQuestionId(question.getId()).blockingGet();
        if (skipLogics != null && skipLogics.size() > 0) {
            for (SkipLogic sl : skipLogics) {
                String[] values = sl.getFormula().replace("\"", "").split(" ");
                sl.setComparingQuestion(values[0]);
                sl.setLogicalOperator(values[1]);
                sl.setAnswerValue(values[2]);
                try {
                    if (ComputationUtils.compareSkipLogicValues(sl, value, scriptEngine)) {
                        if (sl.shouldHide()) {
                            for (int i = 0; i < ALL_VIEWS_LIST.size(); i++) {
                                if (ALL_VIEWS_LIST.get(i).getTag() != null) {
                                    Question q = (Question) ALL_VIEWS_LIST.get(i).getTag();
                                    if (q.getLabelC().equals(sl.getComparingQuestion())) {
                                        ALL_VIEWS_LIST.get(i).setVisibility(View.INVISIBLE);
                                        ALL_VIEWS_LIST.get(i).setEnabled(false);
                                        break;
                                    }
                                }
                            }
                        } else {
                            for (int i = 0; i < ALL_VIEWS_LIST.size(); i++) {
                                if (ALL_VIEWS_LIST.get(i).getTag() != null) {
                                    Question q = (Question) ALL_VIEWS_LIST.get(i).getTag();
                                    if (q.getLabelC().equals(sl.getComparingQuestion())) {
                                        ALL_VIEWS_LIST.get(i).setVisibility(View.VISIBLE);
                                        ALL_VIEWS_LIST.get(i).setEnabled(true);
                                        break;
                                    }
                                }
                            }
                        }
                    } else
                        for (int i = 0; i < ALL_VIEWS_LIST.size(); i++) {
                            if (ALL_VIEWS_LIST.get(i).getTag() != null) {
                                Question q = (Question) ALL_VIEWS_LIST.get(i).getTag();
                                if (q.getLabelC().equals(sl.getComparingQuestion())) {
                                    ALL_VIEWS_LIST.get(i).setVisibility(View.VISIBLE);
                                    ALL_VIEWS_LIST.get(i).setEnabled(true);
                                    break;
                                }
                            }
                        }
                } catch (Exception ignored) {
                }
            }
        }
    }

    @OnClick(R.id.saveButton)
    void saveData() {
        if (!dynamicFormFragment.getFormController().isValidInput()) {
            dynamicFormFragment.getFormController().resetValidationErrors();
            dynamicFormFragment.getFormController().showValidationErrors();
            return;
        }
        if (!validate())
            return;

        /* Merge AO/AOR values generated in diagnostic into one JSON. This is needed to get values from a single JSON to parse the formulas instead of having 2 separate JSONs
         * where we obtain values from
         * Start year (start_year_COUNTRY) key:value is not added to the merged json because when the Logic formula parser is iterating through the JSONObject and replacing
         * the question labels with their values, it might conflict with (plot_intervention_start_year_COUNTRY).
         * start_year_COUNTRY value is not needed here. What we need is plot_intervention_start_year_COUNTRY even though they might be the same value
         * Eg.When iterating through the JSONObject, start_year_COUNTRY comes before plot_intervention_start_year_COUNTRY. If the value for start_year_COUNTRY = 3
         * in the formula string, plot_intervention_start_year_COUNTRY becomes plot_intervention_3 because
         * start_year_COUNTRY in plot_intervention_start_year_COUNTRY was replaced by 3 and that's not what we want.
         */

        i1 = PLOT_AO_ANSWERS_JSON.keys();
        while (i1.hasNext()) {
            tmp_key = i1.next();
            try {
                if (MONITORING_ANSWERS_JSON.has(tmp_key))
                    MONITORING_ANSWERS_JSON.remove(tmp_key);
                if (!tmp_key.equals(startYearLabel))
                    MONITORING_ANSWERS_JSON.put(tmp_key, PLOT_AO_ANSWERS_JSON.get(tmp_key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        for (Question q : dynamicFormFragment.getFormAndQuestions().getQuestions())
            try {
                if (MONITORING_ANSWERS_JSON.has(q.getLabelC()))
                    MONITORING_ANSWERS_JSON.remove(q.getLabelC());
                if (dynamicFormFragment.getDataJson().has(q.getLabelC()))
                    MONITORING_ANSWERS_JSON.put(q.getLabelC(), dynamicFormFragment.getDataJson().get(q.getLabelC()));
            } catch (JSONException ignore) {
            }

        Question monitoringYearQuestion = getAppDataManager().getDatabaseManager().questionDao().get("monitoring_year_");
        try {
            if (MONITORING_ANSWERS_JSON.has(monitoringYearQuestion.getLabelC()))
                MONITORING_ANSWERS_JSON.remove(monitoringYearQuestion.getLabelC());
            MONITORING_ANSWERS_JSON.put(monitoringYearQuestion.getLabelC(), SELECTED_YEAR);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (MONITORING_ANSWERS_JSON.has(recommendationQuestion.getLabelC()))
                MONITORING_ANSWERS_JSON.remove(recommendationQuestion.getLabelC());
            MONITORING_ANSWERS_JSON.put(recommendationQuestion.getLabelC(), PLOT.getRecommendationId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Get monitoring results question and apply logic
        FormAndQuestions aoMonitoringResultsFormAndQuestions = getAppDataManager().getDatabaseManager().formAndQuestionsDao().maybeGetFormAndQuestionsByName(AppConstants.AO_MONITORING_RESULT)
                .blockingGet();

        if (aoMonitoringResultsFormAndQuestions != null && aoMonitoringResultsFormAndQuestions.getQuestions().size() > 0) {
            LogicFormulaParser logicFormulaParser = LogicFormulaParser.getInstance();
            logicFormulaParser.setJsonObject(MONITORING_ANSWERS_JSON);
            AppLogger.e(TAG, "LOOPING THROUGH  " + aoMonitoringResultsFormAndQuestions.getForm().getFormNameC() + " QUESTIONS AND PARSING THEIR FORMULAS");
            System.out.println();

            for (Question q : aoMonitoringResultsFormAndQuestions.getQuestions()) {
                String value;
                if (q.getTypeC().equalsIgnoreCase(AppConstants.FORMULA_TYPE_COMPLEX_FORMULA))
                    value = logicFormulaParser.evaluateComplexFormula(q.getFormulaC());
                else
                    value = logicFormulaParser.evaluate(q.getFormulaC());
                try {
                    if (MONITORING_ANSWERS_JSON.has(q.getLabelC()))
                        MONITORING_ANSWERS_JSON.remove(q.getLabelC());
                    MONITORING_ANSWERS_JSON.put(q.getLabelC(), value);
                    logicFormulaParser.setJsonObject(MONITORING_ANSWERS_JSON);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("--------------------------------------------------------clear------------------------------------------------\n\n");
            }
        }
        MONITORING.setJson(MONITORING_ANSWERS_JSON.toString());
        mPresenter.saveMonitoringData(MONITORING, FARMER);
    }

    @Override
    public void onBackPressed() {
        showDialog(true, getString(R.string.save_data), getString(R.string.save_data_explanation),
                (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    saveData();
                }
                , getString(R.string.yes),
                (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    finish();
                }, getString(R.string.no), 0);
    }

    boolean validate() {
        List<ValidationError> errors = new ArrayList<>();
        for (View view : ALL_VIEWS_LIST) {
            if (view != null && view.getTag() != null) {
                Question q = (Question) view.getTag();
                HashSet<InputValidator> set = validator.getValidators(q.getLabelC());
                if (set != null) {
                    ValidationError error;
                    for (InputValidator inputVal : set) {
                        error = inputVal.validate(ComputationUtils.getDataValue(q, MONITORING_ANSWERS_JSON), q.getLabelC(), q.getLabelC());
                        if (error != null) {
                            errors.add(error);
                            try {
                                if (view.findViewById(R.id.cell_data) instanceof EditText) {
                                    EditText edittext = view.findViewById(R.id.cell_data);
                                    edittext.setError(error.getMessage(getResources()));
                                } else if (view.findViewById(R.id.cell_data) instanceof Spinner) {
                                    TextView errorTextView = view.findViewById(R.id.info);
                                    errorTextView.setText((error.getMessage(getResources())));
                                    errorTextView.setVisibility(View.VISIBLE);
                                }
                            } catch (Exception ignore) {
                            }
                        }
                    }
                }
            }
        }
        AppLogger.e(TAG, "ERRORS SIZE IS " + errors.size());
        return errors.isEmpty();
    }
}