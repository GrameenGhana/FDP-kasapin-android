package org.grameen.fdp.kasapin.ui.addPlotMonitoring;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Monitoring;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.data.db.entity.SkipLogic;
import org.grameen.fdp.kasapin.parser.LogicFormulaParser;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.form.fragment.DynamicFormFragment;
import org.grameen.fdp.kasapin.ui.landing.LandingActivity;
import org.grameen.fdp.kasapin.ui.plotMonitoringActivity.PlotMonitoringActivity;
import org.grameen.fdp.kasapin.utilities.ActivityUtils;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.ComputationUtils;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;


public class AddPlotMonitoringActivity extends BaseActivity implements AddPlotMonitoringContract.View, FdpCallbacks.AnItemSelectedListener {

    @Inject
    AddPlotMonitoringPresenter mPresenter;

    RealFarmer FARMER;
    Plot PLOT;
    DynamicFormFragment dynamicFormFragment;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
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

    Iterator i1;
    String tmp_key;
    String startYearLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_new_plot_monitoring);
        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));

        mPresenter.takeView(this);

        FARMER = new Gson().fromJson(getIntent().getStringExtra("farmer"), RealFarmer.class);
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
            tmp_key = (String) i1.next();
            if (tmp_key.toLowerCase().contains("lime")) {
                try {
                    limeNeeded.setText(PLOT_AO_ANSWERS_JSON.getString(tmp_key));
                    limeNeeded.setTextColor(ContextCompat.getColor(this, R.color.cpb_red));
                    break;
                } catch (JSONException ignored) { }
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


        if(IS_NEW_MONITORING){
            setToolbar(getStringResources(R.string.add_plot_monitoring));
            MONITORING_ANSWERS_JSON = new JSONObject();
            MONITORING = new Monitoring();
            MONITORING.setExternalId(String.valueOf(System.currentTimeMillis()));
            MONITORING.setName("Monitoring " + MONITORING_POSITION);
            MONITORING.setPlotExternalId(PLOT.getExternalId());
            MONITORING.setYear(SELECTED_YEAR);


            mPresenter.getMonitoringQuestions();


        }else{

            setToolbar(getStringResources(R.string.edit_plot_monitoring) + " " + MONITORING.getName());
            try {
                MONITORING_ANSWERS_JSON = MONITORING.getMonitoringAOJsonData();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            mPresenter.getMonitoringQuestions();


        }











    }


    @Override
    public void setAreaUnits(String unit) {

        landSize.setText(PLOT.getArea() + " " + unit);
    }

    @Override
    public void setProductionUnit(String unit) {
        estimatedProductionSize.setText(PLOT.getEstimatedProductionSize() + " " + unit);
    }


    @Override
    public void loadDynamicFragmentAndViews(FormAndQuestions monitoringPlotInfoQuestions, FormAndQuestions aoMonitoringQuestions) {

        AppLogger.e(TAG, "AO MONITORING QUESTIONS >>> " + getGson().toJson(aoMonitoringQuestions.questions));

        if(IS_NEW_MONITORING)
        dynamicFormFragment = DynamicFormFragment.newInstance(monitoringPlotInfoQuestions, false, null, false, null);

        else{
            FormAnswerData formAnswerData = new FormAnswerData();
            formAnswerData.setFarmerCode(PLOT.getFarmerCode());
            formAnswerData.setFormId(monitoringPlotInfoQuestions.getForm().getId());
            formAnswerData.setData(MONITORING_ANSWERS_JSON.toString());

            dynamicFormFragment = DynamicFormFragment.newInstance(monitoringPlotInfoQuestions, true, PLOT.getFarmerCode(), false, formAnswerData);
        }

        ActivityUtils.loadDynamicView(getSupportFragmentManager(), dynamicFormFragment, monitoringPlotInfoQuestions.getForm().getFormNameC());


        new Handler().postDelayed(() -> {

            for(Question q : aoMonitoringQuestions.getQuestions()){
                addViewsDynamically(q);

                if(!IS_NEW_MONITORING)
                    setUpPropertyChangeListeners(q, ComputationUtils.getValue(q.getLabelC(), MONITORING_ANSWERS_JSON));
            }

            monitoringAOLayout.animate().alpha(1f).setDuration(200).start();

            Question monitoringObservationByQuestion = getAppDataManager().getDatabaseManager().questionDao().get("monitoring_plot_observationby_");
            if(monitoringObservationByQuestion != null)
                dynamicFormFragment.getModel().addPropertyChangeListener(monitoringObservationByQuestion.getLabelC(), evt -> {
                    String newValue = (String) evt.getNewValue();

                    if(newValue.equalsIgnoreCase("manager"))
                        for(int i = 0; i < COMPETENCE_VIEWS.size(); i++)
                            COMPETENCE_VIEWS.get(i).setEnabled(true);


                  else
                        for(int i = 0; i < COMPETENCE_VIEWS.size(); i++)
                            COMPETENCE_VIEWS.get(i).setEnabled(false);
                });


        }, 500);




    }

    @Override
    public void addViewsDynamically(Question q) {

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        labelParams.weight = 4;
        labelParams.gravity = Gravity.CENTER;

        View labelView = getLabelView(q);
        linearLayout.addView(labelView, labelParams);

        ALL_VIEWS_LIST.add(labelView);


        LinearLayout.LayoutParams aoParam = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        aoParam.weight = 2;
        aoParam.gravity = Gravity.CENTER;
        aoParam.leftMargin = 10;
        aoParam.rightMargin = 10;

        View AOView = getAOView(q);
        linearLayout.addView(AOView, aoParam);
        ALL_VIEWS_LIST.add(AOView);

        String relatedQuestions[] = q.splitRelatedQuestions();

        if (relatedQuestions != null) {

            String competenceName = relatedQuestions[0];
            String reasonForFailureName = relatedQuestions[1];


            LinearLayout.LayoutParams competenceParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            competenceParams.weight = 2;
            competenceParams.gravity = Gravity.CENTER;
            competenceParams.leftMargin = 10;
            competenceParams.rightMargin = 10;

            View competenceView = getCompetenceView(getAppDataManager().getDatabaseManager().questionDao().get((competenceName)));
            linearLayout.addView(competenceView, competenceParams);

            ALL_VIEWS_LIST.add(competenceView);
            COMPETENCE_VIEWS.add(competenceView);



            LinearLayout.LayoutParams reasonForFailureParam = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            reasonForFailureParam.weight = 3;
            reasonForFailureParam.gravity = Gravity.CENTER;
            reasonForFailureParam.leftMargin = 10;
            reasonForFailureParam.rightMargin = 10;

            View failureView = getReasonForFailureView(getAppDataManager().getDatabaseManager().questionDao().get((reasonForFailureName)));
            linearLayout.addView(failureView, reasonForFailureParam);
            ALL_VIEWS_LIST.add(failureView);


            monitoringAOLayout.addView(linearLayout);


        }

    }

    @Override
    public View getLabelView(Question q) {
        View view;
        TextView textView = new TextView(this);
        textView.setText(q.getCaptionC());
        textView.setTextSize(12);
        textView.setTag(q.getId());
        textView.setPadding(10, 10, 10, 10);
        view = textView;
        return view;
    }

    @Override
    public View getAOView(Question q) {

        final View view;

        if(q.getTypeC().equalsIgnoreCase(AppConstants.TYPE_NUMBER)){
            EditText editText = new EditText(this);
            editText.setHint(ComputationUtils.getValue(q.getLabelC(), MONITORING_ANSWERS_JSON));
            editText.setTextSize(12);
            editText.setTag(q.getLabelC());
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setPadding(10, 10, 10, 10);
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
                        if(MONITORING_ANSWERS_JSON.has(q.getLabelC())) {
                            MONITORING_ANSWERS_JSON.remove(q.getLabelC());
                            MONITORING_ANSWERS_JSON.put(q.getLabelC(), s.toString());
                        }else  MONITORING_ANSWERS_JSON.put(q.getLabelC(), s.toString());

                        setUpPropertyChangeListeners(q, s.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
            view = editText;

        }else {

            final List<String>items = q.formatQuestionOptions();
            final Spinner spinner = new Spinner(this);
            spinner.setPrompt(q.getDefaultValueC());
            spinner.setTag(q.getLabelC());

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
                            MONITORING_ANSWERS_JSON.put(q.getLabelC(), parent.getSelectedItem().toString());

                        } else
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
            view = spinner;
        }

        return view;
    }


    @Override
    public View getCompetenceView(Question q) {
        View view;

        final List<String>items = q.formatQuestionOptions();

        final Spinner spinner = new Spinner(this);
        spinner.setPrompt(q.getDefaultValueC());
        spinner.setTag(q.getLabelC());

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
                return super.getCount();
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
                            MONITORING_ANSWERS_JSON.put(q.getLabelC(), parent.getSelectedItem().toString());

                        } else
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

        view = spinner;

        return view;
    }

    @Override
    public View getReasonForFailureView(Question q) {
        View view;
        final List<String>items = q.formatQuestionOptions();

        final Spinner spinner = new Spinner(this);
        spinner.setPrompt(q.getDefaultValueC());
        spinner.setTag(q.getLabelC());

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
                return super.getCount();
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
                            MONITORING_ANSWERS_JSON.put(q.getLabelC(), parent.getSelectedItem().toString());

                        } else
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

        view = spinner;
        return view;
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
        }catch(Exception ignored){
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

        if(defValue != null)
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
                        if (ComputationUtils.compareValues(sl, value, scriptEngine)) {

                            if (sl.shouldHide()) {
                                for(int i = 0; i < ALL_VIEWS_LIST.size(); i++){
                                    if(ALL_VIEWS_LIST.get(i).getTag().equals(sl.getComparingQuestion())) {
                                        ALL_VIEWS_LIST.get(i).setVisibility(View.INVISIBLE);
                                        ALL_VIEWS_LIST.get(i).setEnabled(false);
                                        break;
                                    }
                                }
                            }else{
                                for(int i = 0; i < ALL_VIEWS_LIST.size(); i++){

                                    if(ALL_VIEWS_LIST.get(i).getTag().equals(sl.getComparingQuestion())) {
                                        ALL_VIEWS_LIST.get(i).setVisibility(View.VISIBLE);
                                        ALL_VIEWS_LIST.get(i).setEnabled(true);

                                        break;
                                    }
                                }

                            }
                        } else
                            for(int i = 0; i < ALL_VIEWS_LIST.size(); i++){

                                if(ALL_VIEWS_LIST.get(i).getTag().equals(sl.getComparingQuestion())) {
                                    ALL_VIEWS_LIST.get(i).setVisibility(View.VISIBLE);
                                    ALL_VIEWS_LIST.get(i).setEnabled(true);

                                    break;
                                }
                        }
                    } catch (Exception ignored) {
                    }



            }
        }
    }


    @OnClick(R.id.saveButton)
    void saveData(){




        //Merge AO/AOR values generated in diagnostic into one JSON. This is needed to get values from a single JSON to parse the formulas instead of having 2 separate JSONs
        //where we obtain values from
        //Start year (start_year_COUNTRY) key:value is not added to the merged json because when the Logic formula parser is iterating through the JSONObject and replacing
        //the question labels with their values, it might conflict with (plot_intervention_start_year_COUNTRY).
        //start_year_COUNTRY value is not needed here. What we need is plot_intervention_start_year_COUNTRY even though they might be the same value
        //Eg.When iterating through the JSONObject, start_year_COUNTRY comes before plot_intervention_start_year_COUNTRY. If the value for start_year_COUNTRY = 3
        // in the formula string, plot_intervention_start_year_COUNTRY becomes plot_intervention_3 because
        //start_year_COUNTRY in plot_intervention_start_year_COUNTRY was replaced by 3 and that's not what we want.



        i1 = PLOT_AO_ANSWERS_JSON.keys();

        while (i1.hasNext()) {
            tmp_key = (String) i1.next();
            try {
                if(MONITORING_ANSWERS_JSON.has(tmp_key))
                    MONITORING_ANSWERS_JSON.remove(tmp_key);

                if(!tmp_key.equals(startYearLabel))
                MONITORING_ANSWERS_JSON.put(tmp_key, PLOT_AO_ANSWERS_JSON.get(tmp_key));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }




        for (Question q : dynamicFormFragment.getFormAndQuestions().getQuestions())
            try {
                if (MONITORING_ANSWERS_JSON.has(q.getLabelC()))
                    MONITORING_ANSWERS_JSON.remove(q.getLabelC());

                if(dynamicFormFragment.getAnswersData().has(q.getLabelC()))
                MONITORING_ANSWERS_JSON.put(q.getLabelC(), dynamicFormFragment.getAnswersData().get(q.getLabelC()));

            } catch (JSONException ignore) {}

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


        System.out.println("#########################################################################################################");
        AppLogger.e(TAG, "MERGED JSON DATA >>>> " + MONITORING_ANSWERS_JSON);
        System.out.println("--------------------------------------------------------------------------------------------------------");


        //Get monitoring results question and apply logic
        FormAndQuestions aoMonitoringResultsFormAndQuestions = getAppDataManager().getDatabaseManager().formAndQuestionsDao().maybeGetFormAndQuestionsByName(AppConstants.AO_MONITORING_RESULT)
                .blockingGet();

            if(aoMonitoringResultsFormAndQuestions != null && aoMonitoringResultsFormAndQuestions.getQuestions().size() > 0){
                LogicFormulaParser logicFormulaParser =  LogicFormulaParser.getInstance();
                logicFormulaParser.setJsonObject(MONITORING_ANSWERS_JSON);
                AppLogger.e(TAG, "LOOPING THROUGH  " + aoMonitoringResultsFormAndQuestions.getForm().getFormNameC() + " QUESTIONS AND PARSING THEIR FORMULAS");
                System.out.println();

                for(Question q : aoMonitoringResultsFormAndQuestions.getQuestions()) {

                    AppLogger.e(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    AppLogger.e(TAG, "Applying parser to evaluate formula of question " + q.getLabelC());
                    AppLogger.e(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");


                    String value;
                    if(q.getTypeC().equalsIgnoreCase(AppConstants.FORMULA_TYPE_COMPLEX_FORMULA))
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
        System.out.println("##########################################  LOOP END   ###############################################################");



            //Remove Plot AO and AOR values from MONITORING JSON.

            i1 = PLOT_AO_ANSWERS_JSON.keys();
            while (i1.hasNext()) {
                tmp_key = (String) i1.next();
                if (MONITORING_ANSWERS_JSON.has(tmp_key))
                    MONITORING_ANSWERS_JSON.remove(tmp_key);
            }


         MONITORING.setJson(MONITORING_ANSWERS_JSON.toString());
        mPresenter.saveMonitoringData(MONITORING, FARMER);





    }


    @Override
    public void onBackPressed() {
        showDialog(true, getStringResources(R.string.save_data), getStringResources(R.string.save_data_explanation),
                (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    saveData();
                }
                , getStringResources(R.string.yes),

                (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    finish();
                }, getStringResources(R.string.no), 0);
    }
}