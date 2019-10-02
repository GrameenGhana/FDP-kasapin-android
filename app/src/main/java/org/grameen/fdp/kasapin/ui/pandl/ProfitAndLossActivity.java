package org.grameen.fdp.kasapin.ui.pandl;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.grameen.fdp.kasapin.BuildConfig;
import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Calculation;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.data.db.entity.Recommendation;
import org.grameen.fdp.kasapin.data.db.entity.RecommendationActivity;
import org.grameen.fdp.kasapin.data.db.entity.SkipLogic;
import org.grameen.fdp.kasapin.parser.LogicFormulaParser;
import org.grameen.fdp.kasapin.parser.MathFormulaParser;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.base.model.Data;
import org.grameen.fdp.kasapin.ui.detailedYearMonthlyView.DetailedMonthActivity;
import org.grameen.fdp.kasapin.ui.fdpStatus.FDPStatusActivity;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.ComputationUtils;
import org.grameen.fdp.kasapin.utilities.NetworkUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.codecrafters.tableview.TableView;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

import static org.grameen.fdp.kasapin.utilities.AppConstants.BUTTON_VIEW;
import static org.grameen.fdp.kasapin.utilities.AppConstants.TAG_OTHER_TEXT_VIEW;
import static org.grameen.fdp.kasapin.utilities.AppConstants.TAG_RESULTS;
import static org.grameen.fdp.kasapin.utilities.AppConstants.TAG_TITLE_TEXT_VIEW;
import static org.grameen.fdp.kasapin.utilities.AppConstants.TAG_VIEW;
import static org.grameen.fdp.kasapin.utilities.AppConstants.TYPE_TEXT;

/**
 * A login screen that offers login via email/password.
 */
public class ProfitAndLossActivity extends BaseActivity implements ProfitAndLossContract.View {

    static final int MAX_YEARS = 7;
    @Inject
    ProfitAndLossPresenter mPresenter;
    @BindView(R.id.name)
    TextView farmerName;
    @BindView(R.id.code)
    TextView farmerCode;
    @BindView(R.id.nameLayout)
    LinearLayout nameLayout;
    @BindView(R.id.currency)
    TextView currency;
    @BindView(R.id.currency_layout)
    LinearLayout currencyLayout;
    @BindView(R.id.tableView)
    TableView tableView;
    @BindView(R.id.print)
    ImageView print;
    @BindView(R.id.fdp_status)

    TextView fdpStatus;
    @BindView(R.id.back)
    Button back;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.submitAgreement)
    Button submitAgreement;
    @BindView(R.id.bottom_buttons)
    LinearLayout bottomButtons;
    @BindView(R.id.main_layout)
    RelativeLayout mainLayout;
    JSONObject VALUES_JSON_OBJECT;
    List<Data> TABLE_DATA_LIST;
    List<String> TOTAL_LABOR_COST;
    List<String> TOTAL_LABOR_DAYS;
    List<String> TOTAL_MAINTENANCE_COST;
    List<String> TOTAL_GROSS_INCOME_FROM_COCOA;
    List<String> TOTAL_NET_INCOME_FROM_OTHER_CROPS;
    List<String> TOTAL_FARMING_INCOME;
    List<String> TOTAL_NET_INCOME_FROM_OTHER_SOURCES;
    List<String> TOTAL_INCOME;
    List<String> NET_FAMILY_INCOME;
    List<String> calculationsList;
    List<Plot> realPlotList;
    String TOTAL_FAMILY_EXPENSES = "";
    List<String> TOTAL_INVESTMENT_IN_FDP;
    List<String> TOTAL_P_AND_L_LIST;
    List<StringBuilder> GROSS_COCOA_STRING_BUILDERS = new ArrayList<>();
    List<StringBuilder> MAINTENANCE_COST_STRING_BUILDERS = new ArrayList<>();
    List<StringBuilder> LABOR_DAYS_STRING_BUILDERS = new ArrayList<>();
    List<StringBuilder> LABOR_COST_STRING_BUILDERS = new ArrayList<>();
    List<String> plotIncomes = new ArrayList<>();
    List<String> maintenanceCostList = new ArrayList<>();
    List<String> labourCostList = new ArrayList<>();
    List<String> labourDaysList = new ArrayList<>();
    List<String> pandlist = new ArrayList<>();
    //String startYearId = "nil";
    Question CSSV_QUESTION;
    static Question START_YEAR_QUESTION;
    String START_YEAR_LABEL;
    Question PLOT_SIZE_QUESTION;
    Question PLOT_PROD_QUESTION;
    Question PLOT_INTERVENTION_START_YEAR;
    String FARM_AREA_UNITS_LABEL;
    String FARM_WEIGHT_UNITS_LABEL;
    String CSSV_VALUE = "--";
    Boolean DID_LABOUR = false;
    String LABOUR_TYPE;
    Boolean shouldHideStartYear = null;

    int COUNTER = 0;
    MyTableViewAdapter myTableViewAdapter;
    JSONObject PLOT_SIZES_IN_HA_JSON;
    boolean isTranslation;
    ScriptEngine engine;
    RealFarmer farmer;
    MathFormulaParser mathFormulaParser;
    LogicFormulaParser logicFormulaParser;
    Recommendation GAPS_RECOMENDATION_FOR_START_YEAR;
    Recommendation PLOT_RECOMMENDATION;
    String plotSizeInHaValue = "0.0";
    private JSONObject PLOT_ANSWERS_JSON_OBJECT = new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toggleFullScreen(false, getWindow());
        setContentView(R.layout.activity_profit_and_loss);

        getActivityComponent().inject(this);
        mPresenter.takeView(this);
        setUnBinder(ButterKnife.bind(this));
        engine = new ScriptEngineManager().getEngineByName("rhino");

        farmer = getGson().fromJson(getIntent().getStringExtra("farmer"), RealFarmer.class);

        START_YEAR_QUESTION = getAppDataManager().getDatabaseManager().questionDao().get("start_year_");
        START_YEAR_LABEL = START_YEAR_QUESTION.getLabelC();
        CSSV_QUESTION = getAppDataManager().getDatabaseManager().questionDao().get("ao_disease_");
        PLOT_INTERVENTION_START_YEAR = getAppDataManager().getDatabaseManager().questionDao().get("plot_intervention_start_year_");

        currency.setText(getAppDataManager().getStringValue("currency"));

        mathFormulaParser = MathFormulaParser.getInstance();
        logicFormulaParser = LogicFormulaParser.getInstance();

        if (farmer != null)
            setUpViews();
        else {
            finish();
            showMessage(getStringResources(R.string.error_has_occurred));
        }


        if (BuildConfig.DEBUG)
            issuePrint();


        if (getAppDataManager().isMonitoring()) {
            save.setVisibility(View.GONE);
            submitAgreement.setVisibility(View.GONE);
        }

    }

    @Override
    public void setUpViews() {
        farmerName.setText(farmer.getFarmerName());
        farmerCode.setText(farmer.getCode());

        tableView.setColumnCount(9);

        String[] TABLE_HEADERS = getResources().getStringArray(R.array.seven_years);

        MyTableHearderAdapter tableHearderAdapter = new MyTableHearderAdapter(this, TABLE_HEADERS);
        tableView.setHeaderAdapter(tableHearderAdapter);

        tableHearderAdapter.setHeaderClickListener(view -> {

            int position = Integer.parseInt(view.getTag().toString());
            AppLogger.i("P & L ACTIVITY", position + "");

            Intent intent = new Intent(this, DetailedMonthActivity.class);
            intent.putExtra("year", position - 1);
            intent.putExtra("farmer", new Gson().toJson(farmer));
            intent.putExtra("labour", DID_LABOUR);
            intent.putExtra("labourType", LABOUR_TYPE);
            intent.putExtra("multiplier", PLOT_SIZES_IN_HA_JSON.toString());
            startActivity(intent);

        });


        tableView.setSaveEnabled(true);


        if (farmer.getHasSubmitted().equalsIgnoreCase(AppConstants.YES) && farmer.getSyncStatus() == 1) {
            submitAgreement.setVisibility(View.GONE);
            findViewById(R.id.save).setVisibility(View.GONE);
        }


        submitAgreement.setOnClickListener(v -> {

            if (farmer.getHasSubmitted() == null || farmer.getHasSubmitted().equalsIgnoreCase(AppConstants.NO)) {

                if (checkIfFarmerFdpStatusFormFilled(farmer.getCode())) {


                    if (NetworkUtils.isNetworkConnected(ProfitAndLossActivity.this)) {

                        showDialog(true, getStringResources(R.string.caution), getStringResources(R.string.read_only_rational),
                                (dialog, which) -> {

                                    //Todo sync up, set agreement submitted


                                }, getStringResources(R.string.ok), (dialog, which) -> dialog.dismiss(), getStringResources(R.string.cancel), 0);


                    } else {

                        showDialog(false, getStringResources(R.string.status_submitted), getStringResources(R.string.no_internet_connection_available) + "\n" +
                                        getStringResources(R.string.you_can_still_make_edits) + farmer.getFarmerName() +
                                        getStringResources(R.string.apostrophe_s) + getStringResources(R.string.data_before_sync),
                                (d, w) -> {
                                    d.dismiss();

                                    //Todo update farmer data, set agreement submitted to true

                                }, getStringResources(R.string.ok), null, "", 0);

                    }


                } else {


                    showDialog(true, getStringResources(R.string.fdp_status_incomplete), getStringResources(R.string.fill_out_fdp_status) + farmer.getFarmerName(),
                            (d, w) -> d.dismiss(), getStringResources(R.string.ok), (d, w1) -> {

                                d.dismiss();
                                moveToFdpStatusActivity();

                            }, getStringResources(R.string.go_to_fdp_status_form), 0);

                }


            } else if (farmer.getHasSubmitted().equalsIgnoreCase(AppConstants.YES)) {

                showDialog(false, getStringResources(R.string.status_submitted), getStringResources(R.string.no_more_mods) + farmer.getFarmerName()
                                + getStringResources(R.string.apostrophe_s) + getStringResources(R.string.data),
                        (d, w) -> {
                            d.dismiss();
                            submitAgreement.setVisibility(View.GONE);

                        }, getStringResources(R.string.ok), null, "", 0);
            }
        });

        onBackClicked();

        showLoading(getStringResources(R.string.populating_data), getStringResources(R.string.please_wait), true, 0, false);

        mPresenter.getAllAnswers(farmer.getCode());


    }

    @Override
    protected void onDestroy() {
        mPresenter.dropView();
        super.onDestroy();
    }


    @Override
    public void openLoginActivityOnTokenExpire() {


    }


    @OnClick(R.id.fdp_status)
    @Override
    public void moveToFdpStatusActivity() {

        Intent intent = new Intent(this, FDPStatusActivity.class);
        intent.putExtra("farmer", getGson().toJson(farmer));
        startActivity(intent);
    }


    @Override
    public void issuePrint() {
        findViewById(R.id.print).setVisibility(View.VISIBLE);

        findViewById(R.id.print).setOnClickListener(v -> {

            findViewById(R.id.bottom_buttons).setVisibility(View.GONE);
            findViewById(R.id.fdpStatus).setVisibility(View.GONE);
            findViewById(R.id.currency_layout).setVisibility(View.GONE);


            showLoading("Initializing print", "Please wait...", false, 0, false);


            new Handler().postDelayed(() -> {
                String fileLocation = captureScreenshot(findViewById(R.id.main_layout), "pandl");
                hideLoading();

                if (fileLocation != null) {
                    showMessage("File saved!");

                       /* Intent intent = new Intent(this, PrintingActivity.class);
                        intent.putExtra("file_location", fileLocation);
                        startActivity(intent);

                        findViewById(R.id.bottom_buttons).setVisibility(View.VISIBLE);
                        findViewById(R.id.fdpStatus).setVisibility(View.VISIBLE);
                        findViewById(R.id.currency_layout).setVisibility(View.VISIBLE);*/


                } else
                    showMessage("Error starting the printer service!");


            }, 2000);


        });

    }


    @Override
    public void setAnswerData(JSONObject object) {

        VALUES_JSON_OBJECT = object;


        //Todo remove this value
        try {
            if (!VALUES_JSON_OBJECT.has("total_family_income_ghana"))
                VALUES_JSON_OBJECT.put("total_family_income_ghana", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AppLogger.e("P & L ACTIVITY", "ALL VALUES JSON IS " + VALUES_JSON_OBJECT);

        logicFormulaParser.setAllValuesJsonObject(VALUES_JSON_OBJECT);
        mathFormulaParser.setAllValuesJsonObject(VALUES_JSON_OBJECT);


        final Question labourQuestion = getAppDataManager().getDatabaseManager().questionDao().get("labour");
        final Question labourTypeQuestion = getAppDataManager().getDatabaseManager().questionDao().get("labour_type");


        AppLogger.i(TAG, "*********   GETTING VALUE FOR DID LABOR   ***********");
        try {
            String val = VALUES_JSON_OBJECT.getString(labourQuestion.getLabelC());

            DID_LABOUR = val.equalsIgnoreCase("Yes");

            AppLogger.e(TAG, "*********   DID LABOUR  == " + DID_LABOUR);


            LABOUR_TYPE = VALUES_JSON_OBJECT.getString(labourTypeQuestion.getLabelC());

            AppLogger.e(TAG, "*********  LABOUR TYPE  == " + LABOUR_TYPE);

        } catch (Exception e) {
            e.printStackTrace();
        }


        AppLogger.d("P & L ACTIVITY", "MAIN JSON OBJECT ITERATION COMPLETE. DATA IS \n" + VALUES_JSON_OBJECT);



        PLOT_SIZE_QUESTION = getAppDataManager().getDatabaseManager().questionDao().get("plot_area_ha_");
        PLOT_PROD_QUESTION = getAppDataManager().getDatabaseManager().questionDao().get("plot_production_kg_");
        FARM_AREA_UNITS_LABEL = getAppDataManager().getDatabaseManager().questionDao().getLabel("farm_area_units_").blockingGet();
        FARM_WEIGHT_UNITS_LABEL = getAppDataManager().getDatabaseManager().questionDao().getLabel("farm_weight_units_").blockingGet();


        Completable.fromAction(this::populateTableData).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        showMessage(R.string.error_has_occurred_loading_data);

                        hideLoading();

                    }
                });


    }


    @Override
    public boolean checkIfFarmerFdpStatusFormFilled(String code) {

        return false;
    }


    @Override
    public void populateTableData() {
        TABLE_DATA_LIST = new ArrayList<>();
        TOTAL_LABOR_COST = new ArrayList<>();
        TOTAL_LABOR_DAYS = new ArrayList<>();
        TOTAL_MAINTENANCE_COST = new ArrayList<>();
        TOTAL_GROSS_INCOME_FROM_COCOA = new ArrayList<>();
        TOTAL_NET_INCOME_FROM_OTHER_CROPS = new ArrayList<>();
        TOTAL_FARMING_INCOME = new ArrayList<>();
        TOTAL_NET_INCOME_FROM_OTHER_SOURCES = new ArrayList<>();
        TOTAL_INCOME = new ArrayList<>();
        NET_FAMILY_INCOME = new ArrayList<>();

        TOTAL_INVESTMENT_IN_FDP = new ArrayList<>();
        TOTAL_P_AND_L_LIST = new ArrayList<>();


        GROSS_COCOA_STRING_BUILDERS = new ArrayList<>();
        MAINTENANCE_COST_STRING_BUILDERS = new ArrayList<>();
        LABOR_DAYS_STRING_BUILDERS = new ArrayList<>();
        LABOR_COST_STRING_BUILDERS = new ArrayList<>();

        for (int i = 0; i <= MAX_YEARS; i++) {

            GROSS_COCOA_STRING_BUILDERS.add(new StringBuilder());
            MAINTENANCE_COST_STRING_BUILDERS.add(new StringBuilder());
            LABOR_DAYS_STRING_BUILDERS.add(new StringBuilder());
            LABOR_COST_STRING_BUILDERS.add(new StringBuilder());
        }

        PLOT_SIZES_IN_HA_JSON = new JSONObject();


        if (farmer != null) {
            realPlotList = getAppDataManager().getDatabaseManager().plotsDao().getFarmersPlots(farmer.getCode()).blockingGet();


            COUNTER = 0;

            for (Plot PLOT : realPlotList) {
                try {
                    PLOT_ANSWERS_JSON_OBJECT = PLOT.getAOJsonData();
                } catch (Exception e) {
                    e.printStackTrace();

                    PLOT_ANSWERS_JSON_OBJECT = new JSONObject();
                }


                if (START_YEAR_LABEL != null) {

                    AppLogger.e("P & L ACTIVITY", "START YEAR LABEL " + START_YEAR_LABEL);


                    try {
                        PLOT.setStartYear(Integer.valueOf(PLOT_ANSWERS_JSON_OBJECT.getString(START_YEAR_LABEL)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                shouldHideStartYear = false;

                AppLogger.e("P & L ACTIVITY", "START YEAR IS " + PLOT.getStartYear());


                logicFormulaParser.setJsonObject(PLOT_ANSWERS_JSON_OBJECT);


                try {
                    //Todo complex calculation Application
                    plotSizeInHaValue = logicFormulaParser.evaluate(PLOT_SIZE_QUESTION.getFormulaC());

                    PLOT_SIZES_IN_HA_JSON.put(PLOT.getExternalId(), plotSizeInHaValue);

                    if (PLOT_ANSWERS_JSON_OBJECT.has(PLOT_SIZE_QUESTION.getLabelC()))
                        PLOT_ANSWERS_JSON_OBJECT.remove(PLOT_SIZE_QUESTION.getLabelC());

                    PLOT_ANSWERS_JSON_OBJECT.put(PLOT_SIZE_QUESTION.getLabelC(), plotSizeInHaValue);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    PLOT_SIZES_IN_HA_JSON.put(PLOT.getExternalId(), plotSizeInHaValue);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                AppLogger.i(TAG, "^^^^^^^^^^^^^^   PLOT SIZE IN HA VALUE IS ^^^^^^^^^^^^^^^^^  " + plotSizeInHaValue);


                String plot_production_kg = "0.0";
                try {
                    //Todo complex calculation Application
                    plot_production_kg = logicFormulaParser.evaluate(PLOT_PROD_QUESTION.getFormulaC());

                    if (PLOT_ANSWERS_JSON_OBJECT.has(PLOT_PROD_QUESTION.getLabelC()))
                        PLOT_ANSWERS_JSON_OBJECT.remove(PLOT_PROD_QUESTION.getLabelC());

                    PLOT_ANSWERS_JSON_OBJECT.put(PLOT_PROD_QUESTION.getLabelC(), plot_production_kg);

                } catch (Exception e) {
                    e.printStackTrace();
                }


                AppLogger.i(TAG, "^^^^^^^^^^^^^^   PLOT PROD IN HA VALUE IS ^^^^^^^^^^^^^^^^^  " + plot_production_kg);


                logicFormulaParser.setJsonObject(PLOT_ANSWERS_JSON_OBJECT);
                mathFormulaParser.setJsonObject(PLOT_ANSWERS_JSON_OBJECT);


                GAPS_RECOMENDATION_FOR_START_YEAR = getAppDataManager().getDatabaseManager().recommendationsDao().get(PLOT.getGapsId()).blockingGet();
                PLOT_RECOMMENDATION = getAppDataManager().getDatabaseManager().recommendationsDao().get(PLOT.getRecommendationId()).blockingGet();


                if (PLOT.getStartYear() > 0) {
                    loadYearData(PLOT, PLOT.getStartYear());

                } else if (PLOT.getStartYear() < 0) {
                    loadYearDataForInterventionMade(PLOT, PLOT.getStartYear());
                }

                COUNTER++;


            }// For Loop ends


            for (int i = 0; i < MAX_YEARS + 1; i++) {
                TOTAL_MAINTENANCE_COST.add(mathFormulaParser.evaluate(MAINTENANCE_COST_STRING_BUILDERS.get(i).toString() + "0"));
                TOTAL_LABOR_DAYS.add(mathFormulaParser.evaluate(LABOR_DAYS_STRING_BUILDERS.get(i).toString() + "0"));
                TOTAL_LABOR_COST.add(mathFormulaParser.evaluate(LABOR_COST_STRING_BUILDERS.get(i).toString() + "0"));
                TOTAL_GROSS_INCOME_FROM_COCOA.add(mathFormulaParser.evaluate(GROSS_COCOA_STRING_BUILDERS.get(i).toString() + "0.0"));
            }

            if (DID_LABOUR) {


                //TABLE_DATA_LIST.add(new Data("Total Costs" + prefs.getString("labourType", ""), null, TAG_TITLE_TEXT_VIEW));

                TABLE_DATA_LIST.add(new Data(getStringResources(R.string.total_maintenance_cost), TOTAL_MAINTENANCE_COST, TAG_RESULTS));
                TABLE_DATA_LIST.add(new Data(getStringResources(R.string.total_labour_days), TOTAL_LABOR_DAYS, TAG_RESULTS));
                TABLE_DATA_LIST.add(new Data(getStringResources(R.string.total_labour_cost), TOTAL_LABOR_COST, TAG_RESULTS));

            }

            TABLE_DATA_LIST.add(new Data("", null, TAG_OTHER_TEXT_VIEW));
            TABLE_DATA_LIST.add(new Data("", null, TAG_OTHER_TEXT_VIEW));


            //Get All Aggregate results questions

            Integer aggregateResultsFormId = getAppDataManager().getDatabaseManager().formsDao().getId(AppConstants.AGGREGATE_ECONOMIC_RESULTS).blockingGet();

            if (aggregateResultsFormId != null) {

                List<Question> AGGREGATE_ECO_RESULTS_QUESTIONS = getAppDataManager().getDatabaseManager()
                        .questionDao().getQuestionsByForm(aggregateResultsFormId).blockingGet();


                if (AGGREGATE_ECO_RESULTS_QUESTIONS != null) {

                    for (Question q : AGGREGATE_ECO_RESULTS_QUESTIONS) {
                        mathFormulaParser.setMathFormula(q.getFormulaC());


                        if (q.getLabelC().startsWith("net_income_cocoa_")) {

                            TABLE_DATA_LIST.add(new Data(q.getCaptionC(), TOTAL_GROSS_INCOME_FROM_COCOA, TAG_RESULTS));


                        } else if (q.getLabelC().startsWith("net_income_other_crops_")) {

                            String value = mathFormulaParser.evaluate();
                            calculationsList = new ArrayList<>();

                            for (int i = 0; i < MAX_YEARS + 1; i++) {
                                calculationsList.add(value);
                            }

                            TOTAL_NET_INCOME_FROM_OTHER_CROPS = calculationsList;
                            TABLE_DATA_LIST.add(new Data(q.getCaptionC(), calculationsList, TAG_RESULTS));


                        } else if (q.getLabelC().startsWith("farming_income_")) {

                            calculationsList = new ArrayList<>();

                            for (int i = 0; i < MAX_YEARS + 1; i++)
                                calculationsList.add(mathFormulaParser.evaluate((TOTAL_GROSS_INCOME_FROM_COCOA.get(i) + "+" + TOTAL_NET_INCOME_FROM_OTHER_CROPS.get(i))));


                            TABLE_DATA_LIST.add(new Data(q.getCaptionC(), calculationsList, TAG_RESULTS));
                            TOTAL_FARMING_INCOME = calculationsList;


                        } else if (q.getLabelC().startsWith("net_income_other_sources_")) {

                            String value = mathFormulaParser.evaluate();

                            calculationsList = new ArrayList<>();

                            for (int i = 0; i < MAX_YEARS + 1; i++) {
                                calculationsList.add(value);
                            }

                            TOTAL_NET_INCOME_FROM_OTHER_SOURCES = calculationsList;
                            TABLE_DATA_LIST.add(new Data(q.getCaptionC(), calculationsList, TAG_RESULTS));


                        } else if (q.getLabelC().startsWith("total_income_")) {
                            calculationsList = new ArrayList<>();

                            for (int i = 0; i < MAX_YEARS + 1; i++)
                                calculationsList.add(mathFormulaParser.evaluate((TOTAL_FARMING_INCOME.get(i) + "+" + TOTAL_NET_INCOME_FROM_OTHER_SOURCES.get(i))));

                            TABLE_DATA_LIST.add(new Data(q.getCaptionC(), calculationsList, TAG_RESULTS));

                            TOTAL_INCOME = calculationsList;


                        } else if (q.getLabelC().startsWith("total_family_expenses_")) {

                            String value = mathFormulaParser.evaluate();
                            calculationsList = new ArrayList<>();

                            for (int i = 0; i < MAX_YEARS + 1; i++) {
                                calculationsList.add(value);
                            }

                            TOTAL_FAMILY_EXPENSES = value;

                            TABLE_DATA_LIST.add(new Data(q.getCaptionC(), calculationsList, TAG_RESULTS));


                        } else if (q.getLabelC().startsWith("net_family_income_")) {

                            calculationsList = new ArrayList<>();
                            for (int i = 0; i < MAX_YEARS + 1; i++) {
                                calculationsList.add(mathFormulaParser.evaluate((TOTAL_INCOME.get(i) + "-" + TOTAL_FAMILY_EXPENSES)));
                            }

                            TABLE_DATA_LIST.add(new Data(q.getCaptionC(), calculationsList, TAG_RESULTS));

                            NET_FAMILY_INCOME = calculationsList;


                        } else if (q.getLabelC().startsWith("fdp_invest_")) {

                            calculationsList = new ArrayList<>();

                            for (int i = 0; i < MAX_YEARS + 1; i++) {
                                calculationsList.add(mathFormulaParser.evaluate((TOTAL_MAINTENANCE_COST.get(i) + "+" + TOTAL_LABOR_COST.get(i))));
                            }


                            TABLE_DATA_LIST.add(new Data(q.getCaptionC(), calculationsList, TAG_RESULTS));


                            TOTAL_INVESTMENT_IN_FDP = calculationsList;

                        } else if (q.getLabelC().startsWith("p&l_")) {
                            calculationsList = new ArrayList<>();
                            try {

                                for (int i = 0; i < MAX_YEARS + 1; i++) {
                                    calculationsList.add(mathFormulaParser.evaluate((NET_FAMILY_INCOME.get(i) + "-" + TOTAL_INVESTMENT_IN_FDP.get(i))));
                                }

                            } catch (Exception ignored) {
                                calculationsList = new ArrayList<>();

                                for (int i = 0; i < MAX_YEARS + 1; i++) {
                                    calculationsList.add(mathFormulaParser.evaluate((TOTAL_INCOME.get(i) + "-" + TOTAL_INVESTMENT_IN_FDP.get(i))));
                                }


                            }

                            TABLE_DATA_LIST.add(new Data(q.getCaptionC(), calculationsList, TAG_RESULTS));
                            TOTAL_P_AND_L_LIST = calculationsList;

                        }
                    }

                }

            }

            TABLE_DATA_LIST.add(new Data("", null, TAG_OTHER_TEXT_VIEW));
            TABLE_DATA_LIST.add(new Data("", null, TAG_OTHER_TEXT_VIEW));


            Question hhs = getAppDataManager().getDatabaseManager().questionDao().get("hh_savings_");
            Question pI = getAppDataManager().getDatabaseManager().questionDao().get("hh_planed_investment_");

            try {

                TABLE_DATA_LIST.add(new Data(hhs.getCaptionC(), VALUES_JSON_OBJECT.get(hhs.getLabelC()).toString()));
            } catch (JSONException e) {
                e.printStackTrace();

                TABLE_DATA_LIST.add(new Data(hhs.getCaptionC(), "0.00"));
            }


            try {

                TABLE_DATA_LIST.add(new Data(pI.getCaptionC(), VALUES_JSON_OBJECT.get(pI.getLabelC()).toString()));

            } catch (JSONException e) {
                e.printStackTrace();

                TABLE_DATA_LIST.add(new Data(pI.getCaptionC(), "0.00"));

            }


            TABLE_DATA_LIST.add(new Data("", null, TAG_OTHER_TEXT_VIEW));
            TABLE_DATA_LIST.add(new Data("", null, TAG_OTHER_TEXT_VIEW));


            myTableViewAdapter = new MyTableViewAdapter(ProfitAndLossActivity.this, TABLE_DATA_LIST, tableView, getAppDataManager().getDatabaseManager());

            runOnUiThread(() -> tableView.setDataAdapter(myTableViewAdapter));

            myTableViewAdapter.setItemSelectedListener((view, i, id, item) -> {

                AppLogger.i(TAG, "Spinner item selected with tag " + view.getTag());


                //Position of plot selected from the list is saved as a tag to the start year view for you to know which plot has its start year changed


                try {

                    int position = Integer.valueOf(view.getTag().toString());


                    if (PLOT_ANSWERS_JSON_OBJECT.has(START_YEAR_LABEL))
                        PLOT_ANSWERS_JSON_OBJECT.remove(START_YEAR_LABEL);
                    PLOT_ANSWERS_JSON_OBJECT.put(START_YEAR_LABEL, String.valueOf(i + 1));

                    if (PLOT_ANSWERS_JSON_OBJECT.has(PLOT_INTERVENTION_START_YEAR.getLabelC()))
                        PLOT_ANSWERS_JSON_OBJECT.remove(PLOT_INTERVENTION_START_YEAR.getLabelC());
                    PLOT_ANSWERS_JSON_OBJECT.put(PLOT_INTERVENTION_START_YEAR.getLabelC(), String.valueOf(i + 1));

                    realPlotList.get(position).setStartYear(i + 1);
                    realPlotList.get(position).setAnswersData(PLOT_ANSWERS_JSON_OBJECT.toString());


                    System.out.println("-------------------------------------------------------------------------------");
                    AppLogger.e(TAG, "UPDATED JSON DATA IS " + getGson().toJson(realPlotList.get(position)));
                    System.out.println("-------------------------------------------------------------------------------");


                    mPresenter.updatePlotData(realPlotList.get(position), true);

                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage("An error occurred changing start year");
                }
            });


            myTableViewAdapter.setClickistener(view -> {
                AppLogger.i(TAG, "Button item clicked with tag " + view.getTag());

                try {

                    String plotExtId = view.getTag().toString().split("_")[0];
                    String recoToChangeToName = view.getTag().toString().split("_")[1];


                    Plot plot = null;

                    for(Plot p : realPlotList)
                        if(p.getExternalId().equals(plotExtId)) {
                            plot = p;
                            break;
                        }

                    if(plot != null) {
                        Recommendation NEW_PLOT_RECO = getAppDataManager().getDatabaseManager().recommendationsDao()
                                .getByRecommendationName(recoToChangeToName).blockingGet();
                        Recommendation GAPS_RECOMMENDATION_FOR_START_YEAR;


                        if (NEW_PLOT_RECO != null) {
                            AppLogger.e(TAG, "PLOT RECO IS >>>>>>>>>>>>>>>>>>>>> " + getGson().toJson(NEW_PLOT_RECO));

                            if (NEW_PLOT_RECO.getRecommendationName().equalsIgnoreCase("Replant") || NEW_PLOT_RECO.getRecommendationName().equalsIgnoreCase("Replant + Extra soil")) {

                                GAPS_RECOMMENDATION_FOR_START_YEAR = getAppDataManager().getDatabaseManager().recommendationsDao()
                                        .getByRecommendationName("Minimal GAPs").blockingGet();

                            } else if (NEW_PLOT_RECO.getRecommendationName().equalsIgnoreCase("Grafting") || NEW_PLOT_RECO.getRecommendationName().equalsIgnoreCase("Grafting + Extra soil")) {

                                GAPS_RECOMMENDATION_FOR_START_YEAR = getAppDataManager().getDatabaseManager().recommendationsDao()
                                        .getByRecommendationName("Modest GAPs").blockingGet();


                            } else {

                                GAPS_RECOMMENDATION_FOR_START_YEAR = getAppDataManager().getDatabaseManager().recommendationsDao()
                                        .getByRecommendationName("Maintenance (GAPs)").blockingGet();

                            }

                            plot.setGapsId(GAPS_RECOMMENDATION_FOR_START_YEAR.getId());
                            plot.setRecommendationId(NEW_PLOT_RECO.getId());


                            mPresenter.updatePlotData(plot, true);
                        }
/*

                        if (getAppDataManager().getDatabaseManager().plotsDao().insertOne(plot) > 0) {
                            showLoading(getStringResources(R.string.updating_table_data), getStringResources(R.string.please_wait), true, 0,
                                    false);

                            Completable.fromAction(this::populateTableData).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe();
                        }*/


                    } else
                        showMessage("An error occurred changing start year");

                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage("An error occurred changing start year");
                }
            });
            hideLoading();


        }
    }


    @Override
    public void reloadTableData() {
        showLoading(getStringResources(R.string.updating_table_data), getStringResources(R.string.please_wait), true, 0, false);

        Completable.fromAction(this::populateTableData).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                        showMessage(R.string.error_has_occurred_loading_data);

                    }
                });

    }


    void loadYearData(Plot PLOT, int CONTROLLING_YEAR) {

        System.out.println("################################ \n CALCULATING INCOME \n ###################################");


        AppLogger.i(TAG, "loadYearData " + CONTROLLING_YEAR);
        int TEMP = CONTROLLING_YEAR;
        int TEMP2 = CONTROLLING_YEAR;

        AppLogger.i(TAG, "TEMP = " + TEMP);
        AppLogger.i(TAG, "TEMP 2 =  " + TEMP2);


        plotIncomes = new ArrayList<>();


        for (int i = 0; i < CONTROLLING_YEAR; i++) {
            //Todo get Recommendation calculation, get formula and apply here

            AppLogger.e(TAG, "YEAR " + i);


            Calculation calculation = getAppDataManager().getDatabaseManager().calculationsDao().getByRecommendationYear(GAPS_RECOMENDATION_FOR_START_YEAR.getId(), 0);

            if (calculation != null) {
                mathFormulaParser.setMathFormula(calculation.getFormula());

                plotIncomes.add(mathFormulaParser.evaluate());
            } else
                plotIncomes.add("0");


            GROSS_COCOA_STRING_BUILDERS.get(i).append(plotIncomes.get(i)).append("+");


        }


        int temp = 0;
        for (int i = CONTROLLING_YEAR; i <= MAX_YEARS; i++) {

            temp++;

            Calculation calculation = getAppDataManager().getDatabaseManager().calculationsDao().getByRecommendationYear(PLOT_RECOMMENDATION.getId(), temp);


            if (calculation != null) {
                AppLogger.i(TAG, "\nYEAR " + i + " >> " + calculation.getFormula());
                mathFormulaParser.setMathFormula(calculation.getFormula());
                plotIncomes.add(mathFormulaParser.evaluate());
            } else
                plotIncomes.add("0");

            GROSS_COCOA_STRING_BUILDERS.get(i).append(plotIncomes.get(i)).append("+");


            if (TEMP == MAX_YEARS) break;

            TEMP += 1;

            AppLogger.i(TAG, "^^^^^^^^^^^^^^    TEMP IS NOW " + TEMP);
        }


        String name = PLOT_RECOMMENDATION.getLabel();


        TABLE_DATA_LIST.add(new Data(PLOT.getName() + "\n" + name, null, TAG_TITLE_TEXT_VIEW));

        Question plotIncomeQuestion = getAppDataManager().getDatabaseManager().questionDao().get("plot_income_");

        TABLE_DATA_LIST.add(new Data(plotIncomeQuestion.getCaptionC(), plotIncomes, TAG_OTHER_TEXT_VIEW));

        //Todo maintenance cost, labor cost and labor days * plot size in ha


        //This is also known as Supplies cost. The selected year is always with   the (GAPS) recommendation obtained in conjunction with the plot recommendation attained
        maintenanceCostList = new ArrayList<>();
        labourCostList = new ArrayList<>();
        labourDaysList = new ArrayList<>();
        pandlist = new ArrayList<>();


        System.out.println("################################ \n CALCULATING MAINTENANCE COST \n ###################################");


        List<RecommendationActivity> recommendationActivities;

        System.out.println("################################ \n CALCULATING LABOUR DAYS AND COST FOR YEAR 0 to SELECTED START YEAR (1 = DEFAULT) \n ###################################");


        for (int i = 0; i < CONTROLLING_YEAR; i++) {

            AppLogger.i(TAG, "YEAR " + i);

            if (i == 0) {
                Calculation calculation = getAppDataManager().getDatabaseManager().calculationsDao().getByRecommendationYearAndType(GAPS_RECOMENDATION_FOR_START_YEAR.getId(), 0, AppConstants.RECOMMENDATION_CALCULATION_TYPE_COST);
                if (calculation != null) {
                    AppLogger.i(TAG, "\nYEAR " + i + " >> " + calculation.getFormula());
                    mathFormulaParser.setMathFormula(calculation.getFormula());
                    maintenanceCostList.add(mathFormulaParser.evaluate());

                } else
                    maintenanceCostList.add("0");


                recommendationActivities = getAppDataManager().getDatabaseManager().recommendationPlusActivitiesDao()
                        .getAllByRecommendation(GAPS_RECOMENDATION_FOR_START_YEAR.getId(), "0").blockingGet();


            } else {
                recommendationActivities = getAppDataManager().getDatabaseManager().recommendationPlusActivitiesDao()
                        .getAllByRecommendation(GAPS_RECOMENDATION_FOR_START_YEAR.getId(), "1").blockingGet();

                maintenanceCostList.add(mathFormulaParser.evaluate(computeCost(recommendationActivities, AppConstants.SUPPLIES_COSTS, false) + "*" + plotSizeInHaValue));

            }


            MAINTENANCE_COST_STRING_BUILDERS.get(i).append(maintenanceCostList.get(i)).append("+");


            getSuppliesLaborCostLabourDaysValues(recommendationActivities);

            LABOR_COST_STRING_BUILDERS.get(i).append(labourCostList.get(i)).append("+");
            LABOR_DAYS_STRING_BUILDERS.get(i).append(labourDaysList.get(i)).append("+");

        }


        System.out.println("################################ \n CALCULATING LABOUR DAYS AND COST \n ###################################");


        for (int i = 1; i < MAX_YEARS + 1; i++) {

            recommendationActivities = getAppDataManager().getDatabaseManager().recommendationPlusActivitiesDao()
                    .getAllByRecommendation(PLOT_RECOMMENDATION.getId(), String.valueOf(i)).blockingGet();


            maintenanceCostList.add(mathFormulaParser.evaluate(computeCost(recommendationActivities, AppConstants.SUPPLIES_COSTS, false) + "*" + plotSizeInHaValue));
            MAINTENANCE_COST_STRING_BUILDERS.get(TEMP2).append(maintenanceCostList.get(TEMP2)).append("+");


            getSuppliesLaborCostLabourDaysValues(recommendationActivities);

            LABOR_COST_STRING_BUILDERS.get(TEMP2).append(labourCostList.get(TEMP2)).append("+");
            LABOR_DAYS_STRING_BUILDERS.get(TEMP2).append(labourDaysList.get(TEMP2)).append("+");


            if (TEMP2 == MAX_YEARS) break;

            TEMP2 += 1;
        }


        AppLogger.i(TAG, "AFTER : TEMP = " + TEMP);
        AppLogger.i(TAG, "AFTER : TEMP 2 =  " + TEMP2);


        computePlotResults();

        showOrHideStartYear(PLOT.getStartYear() - 1);

        Question plotRenovatedCorrectlyQuestion = getAppDataManager().getDatabaseManager().questionDao().get("plot_renovated_correctly_");

        if (plotRenovatedCorrectlyQuestion != null) {
            if (!ComputationUtils.getValue(plotRenovatedCorrectlyQuestion.getLabelC(), PLOT_ANSWERS_JSON_OBJECT).equalsIgnoreCase("yes"))


                if ((PLOT_RECOMMENDATION.getRecommendationName().equalsIgnoreCase("Grafting")) ||
                        PLOT_RECOMMENDATION.getRecommendationName().equalsIgnoreCase("Thinning Out") ||
                        PLOT_RECOMMENDATION.getRecommendationName().equalsIgnoreCase("Filling In")) {

                    getAppDataManager().setStringValue(PLOT.getExternalId(), PLOT_RECOMMENDATION.getRecommendationName());


                    TABLE_DATA_LIST.add(new Data(PLOT.getExternalId() + "_Replant", null, BUTTON_VIEW));


                } else if (PLOT_RECOMMENDATION.getRecommendationName().equalsIgnoreCase("Replant")) {


                    if (getAppDataManager().getStringValue(PLOT.getExternalId()).equalsIgnoreCase("thinning out"))

                        TABLE_DATA_LIST.add(new Data(PLOT.getExternalId() + "_Thinning out", null, BUTTON_VIEW));

                    else if (getAppDataManager().getStringValue(PLOT.getExternalId()).equalsIgnoreCase("filling in"))

                        TABLE_DATA_LIST.add(new Data(PLOT.getExternalId() + "_Filling in", null, BUTTON_VIEW));

                    else if (getAppDataManager().getStringValue(PLOT.getExternalId()).equalsIgnoreCase("grafting"))

                        TABLE_DATA_LIST.add(new Data(PLOT.getExternalId() + "_Grafting", null, BUTTON_VIEW));


                } else if (PLOT_RECOMMENDATION.getRecommendationName().equalsIgnoreCase("Filling in + Extra Soil")
                        || PLOT_RECOMMENDATION.getRecommendationName().equalsIgnoreCase("Thinning out + Extra Soil")
                        || PLOT_RECOMMENDATION.getRecommendationName().equalsIgnoreCase("Grafting + Extra Soil")) {

                    getAppDataManager().setStringValue(PLOT.getExternalId(), PLOT_RECOMMENDATION.getRecommendationName());
                    TABLE_DATA_LIST.add(new Data(PLOT.getExternalId() + "_Replant + Extra Soil", null, BUTTON_VIEW));


                } else if (PLOT_RECOMMENDATION.getRecommendationName().equalsIgnoreCase("Replant + Extra Soil")) {


                    if (getAppDataManager().getStringValue(PLOT.getExternalId()).equalsIgnoreCase("Thinning out + Extra Soil"))

                        TABLE_DATA_LIST.add(new Data(PLOT.getExternalId() + "_Thinning out + Extra Soil", null, BUTTON_VIEW));

                    else if (getAppDataManager().getStringValue(PLOT.getExternalId()).equalsIgnoreCase("Filling in + Extra Soil"))

                        TABLE_DATA_LIST.add(new Data(PLOT.getExternalId() + "_Filling in + Extra Soil", null, BUTTON_VIEW));
                    else if (getAppDataManager().getStringValue(PLOT.getExternalId()).equalsIgnoreCase("Grafting + Extra Soil"))

                        TABLE_DATA_LIST.add(new Data(PLOT.getExternalId() + "_Grafting + Extra Soil", null, BUTTON_VIEW));
                } //else
                   /* runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //CustomToast.makeToast(PandLActivity.this, "Missing answer to question \"Was this plot renovated correctly?\"", Toast.LENGTH_LONG).show();

                        }
                    });*/

        } /*else
            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                   // CustomToast.makeToast(PandLActivity.this, "Missing question \"Was this plot renovated correctly?\"", Toast.LENGTH_LONG).show();

                }
            });
*/

        TABLE_DATA_LIST.add(new Data("", null, TAG_OTHER_TEXT_VIEW));


    }


    void loadYearDataForInterventionMade(Plot PLOT, int CONTROLLING_YEAR) {

        AppLogger.i(TAG, "loadYearData " + CONTROLLING_YEAR);


        int TEMP = CONTROLLING_YEAR * -1;
        int TEMP2 = CONTROLLING_YEAR * -1;


        AppLogger.i(TAG, "TEMP = " + TEMP);
        //AppLogger.i(TAG, "TEMP2 =  " + TEMP2);


        plotIncomes = new ArrayList<>();

        try {
            mathFormulaParser.setJsonObject(PLOT_ANSWERS_JSON_OBJECT);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //Todo get Recommendation calculation, get formula and apply here

        AppLogger.i(TAG, "\nYEAR " + 0);
        Calculation calculation = getAppDataManager().getDatabaseManager().calculationsDao().getByRecommendationYear(GAPS_RECOMENDATION_FOR_START_YEAR.getId(), 0);

        if (calculation != null) {
            mathFormulaParser.setMathFormula(calculation.getFormula());
            plotIncomes.add(mathFormulaParser.evaluate());
        } else
            plotIncomes.add("0");

        GROSS_COCOA_STRING_BUILDERS.get(0).append(plotIncomes.get(0)).append("+");


        for (int i = 1; i <= MAX_YEARS; i++) {
            TEMP += 1;

            if (TEMP >= MAX_YEARS) {
                calculation = getAppDataManager().getDatabaseManager().calculationsDao().getByRecommendationYear(PLOT_RECOMMENDATION.getId(), 7);


                if (calculation != null) {
                    AppLogger.i(TAG, "\nYEAR " + 7 + " >> " + calculation.getFormula());
                    mathFormulaParser.setMathFormula(calculation.getFormula());
                    plotIncomes.add(mathFormulaParser.evaluate());
                } else
                    plotIncomes.add("0");


            } else {
                calculation = getAppDataManager().getDatabaseManager().calculationsDao().getByRecommendationYear(PLOT_RECOMMENDATION.getId(), TEMP);


                if (calculation != null) {
                    AppLogger.i(TAG, "\nYEAR " + i + " >> " + calculation.getFormula());
                    mathFormulaParser.setMathFormula(calculation.getFormula());
                    plotIncomes.add(mathFormulaParser.evaluate());
                } else
                    plotIncomes.add("0");

            }


            GROSS_COCOA_STRING_BUILDERS.get(i).append(plotIncomes.get(i)).append("+");
            AppLogger.i(TAG, "^^^^^^^^^^^^^^    TEMP IS NOW " + TEMP);

        }


        String name = PLOT_RECOMMENDATION.getLabel();


        TABLE_DATA_LIST.add(new Data(PLOT.getName() + "\n" + name, null, TAG_TITLE_TEXT_VIEW));

        Question plotIncomeQuestion = getAppDataManager().getDatabaseManager().questionDao().get("plot_income_");

        TABLE_DATA_LIST.add(new Data(plotIncomeQuestion.getLabelC(), plotIncomes, TAG_OTHER_TEXT_VIEW));


        ////////

        //This is also known as Supplies cost. The selected year is always with   the (GAPS) recommendation obtained in conjunction with the plot recommendation
        maintenanceCostList = new ArrayList<>();
        labourCostList = new ArrayList<>();
        labourDaysList = new ArrayList<>();
        pandlist = new ArrayList<>();


        TEMP = 0;


        List<RecommendationActivity> recommendationActivities;


        System.out.println();
        System.out.println("**********************************************");
        System.out.println("MAINTENANCE COST AND LABOUR COST CALCULATIONS");
        System.out.println("**********************************************");
        System.out.println();


        AppLogger.i(TAG, "\nYEAR " + 0);

        calculation = getAppDataManager().getDatabaseManager().calculationsDao().getByRecommendationYearAndType(PLOT_RECOMMENDATION.getId(), 0, AppConstants.RECOMMENDATION_CALCULATION_TYPE_COST);
        if (calculation != null) {
            mathFormulaParser.setMathFormula(calculation.getFormula());
            maintenanceCostList.add(mathFormulaParser.evaluate());

        } else
            maintenanceCostList.add("0");

        MAINTENANCE_COST_STRING_BUILDERS.get(0).append(maintenanceCostList.get(0)).append("+");


        recommendationActivities = getAppDataManager().getDatabaseManager().recommendationPlusActivitiesDao()
                .getAllByRecommendation(PLOT_RECOMMENDATION.getId(), "0").blockingGet();

        getSuppliesLaborCostLabourDaysValues(recommendationActivities);


        LABOR_COST_STRING_BUILDERS.get(0).append(labourCostList.get(0)).append("+");
        LABOR_DAYS_STRING_BUILDERS.get(0).append(labourDaysList.get(0)).append("+");


        for (int i = 1; i <= MAX_YEARS; i++) {

            System.out.println("**********************************************");
            System.out.println("TEMP 2 = " + TEMP2 + " and i = " + i);
            System.out.println("**********************************************");

            TEMP = TEMP2 + i;

            if (TEMP <= MAX_YEARS)

                recommendationActivities = getAppDataManager().getDatabaseManager().recommendationPlusActivitiesDao()
                        .getAllByRecommendation(PLOT_RECOMMENDATION.getId(), String.valueOf(TEMP)).blockingGet();
            else

                recommendationActivities = getAppDataManager().getDatabaseManager().recommendationPlusActivitiesDao()
                        .getAllByRecommendation(PLOT_RECOMMENDATION.getId(), "7").blockingGet();


            maintenanceCostList.add(mathFormulaParser.evaluate(computeCost(recommendationActivities, AppConstants.SUPPLIES_COSTS, false) + "*" + plotSizeInHaValue));
            MAINTENANCE_COST_STRING_BUILDERS.get(i).append(maintenanceCostList.get(i)).append("+");


            getSuppliesLaborCostLabourDaysValues(recommendationActivities);

            LABOR_COST_STRING_BUILDERS.get(i).append(labourCostList.get(i)).append("+");
            LABOR_DAYS_STRING_BUILDERS.get(i).append(labourDaysList.get(i)).append("+");


        }

        computePlotResults();


        TABLE_DATA_LIST.add(new Data("", null, TAG_OTHER_TEXT_VIEW));

    }


    void getSuppliesLaborCostLabourDaysValues(List<RecommendationActivity> recommendationActivities) {
        if (DID_LABOUR) {
            String laborCostValue;
            String laborDaysValue;

            if (LABOUR_TYPE.equalsIgnoreCase("full")) {
                laborCostValue = computeCost(recommendationActivities, AppConstants.LABOR_COSTS, false);
                laborDaysValue = computeCost(recommendationActivities, AppConstants.LABOR_DAYS, false);

            } else {
                laborCostValue = computeCost(recommendationActivities, AppConstants.LABOR_COSTS, true);
                laborDaysValue = computeCost(recommendationActivities, AppConstants.LABOR_DAYS, true);
            }


            labourCostList.add(mathFormulaParser.evaluate(laborCostValue + "*" + plotSizeInHaValue));
            labourDaysList.add(mathFormulaParser.evaluate(laborDaysValue + "*" + plotSizeInHaValue));


        } else {
            labourCostList.add("0.0");
            labourDaysList.add("0.0");
        }


    }


    void showOrHideStartYear(int year) {
        AppLogger.i(TAG, "\n\n$$$$$$$$$$$$$$$$$$$$$$$ CSSV  $$$$$$$$$$$$$$$$$$$$$$$$$$\n\n");
        Boolean value = false;

        CSSV_VALUE = "--";

        if (PLOT_ANSWERS_JSON_OBJECT != null) {
            CSSV_VALUE = ComputationUtils.getValue(CSSV_QUESTION.getLabelC(), PLOT_ANSWERS_JSON_OBJECT);
        }

        List<SkipLogic> skipLogics = getAppDataManager().getDatabaseManager().skipLogicsDao().getAllByQuestionId(START_YEAR_QUESTION.getId()).blockingGet();

        if (skipLogics != null && skipLogics.size() > 0) {

            for (SkipLogic sl : skipLogics) {
                try {

                    String[] values = sl.getFormula().replace("\"", "").split(" ");
                    sl.setComparingQuestion(values[0]);
                    sl.setLogicalOperator(values[1]);
                    sl.setAnswerValue(values[2]);

                    ComputationUtils computationUtils = ComputationUtils.newInstance(null);

                    if (computationUtils.compareValues(sl, CSSV_VALUE)) {
                        TABLE_DATA_LIST.add(new Data(COUNTER + "_" + year, null, TAG_VIEW));

                        break;
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }


    void computePlotResults() {

        for (int i = 0; i < MAX_YEARS + 1; i++) {

            pandlist.add(mathFormulaParser.evaluate(plotIncomes.get(i) + "-" + "(" + maintenanceCostList.get(i) + "+" + labourCostList.get(i) + ")"));
        }

        Question plotCostQuestion = getAppDataManager().getDatabaseManager().questionDao().get("plot_cost_");
        TABLE_DATA_LIST.add(new Data(plotCostQuestion.getCaptionC(), maintenanceCostList, TAG_OTHER_TEXT_VIEW));


        Question laborDaysQuestion = getAppDataManager().getDatabaseManager().questionDao().get("plot_labor_days_");
        TABLE_DATA_LIST.add(new Data(laborDaysQuestion.getCaptionC(), labourDaysList, TAG_OTHER_TEXT_VIEW));


        Question labourCostQuestion = getAppDataManager().getDatabaseManager().questionDao().get("plot_labor_cost_");
        TABLE_DATA_LIST.add(new Data(labourCostQuestion.getCaptionC(), labourCostList, TAG_OTHER_TEXT_VIEW));

        Question plotProfitLossQuestion = getAppDataManager().getDatabaseManager().questionDao().get("plot_p&l_");
        TABLE_DATA_LIST.add(new Data(plotProfitLossQuestion.getCaptionC(), pandlist, TAG_OTHER_TEXT_VIEW));


        Integer plotResultsFormId = getAppDataManager().getDatabaseManager().formsDao().getId("Plot results").blockingGet();
        if (plotResultsFormId != null) {

            List<Question> plotResultsQuestions = getAppDataManager().getDatabaseManager().questionDao().getQuestionsByForm(plotResultsFormId).blockingGet();
            if (plotResultsQuestions != null) {
                for (Question q : plotResultsQuestions) {
                    if (q.getTypeC().equalsIgnoreCase(TYPE_TEXT)) {

                        TABLE_DATA_LIST.add(new Data(q.getCaptionC(), ComputationUtils.getValue(q.getLabelC(), PLOT_ANSWERS_JSON_OBJECT)));

                       /* try {

                            SkipLogic skipLogic = getAppDataManager().getDatabaseManager().skipLogicsDao().getSkipLogicByQuestionId(q.getId());

                            if (skipLogic != null && !setupSkipLogicsAndHideViews(skipLogic))
                                TABLE_DATA_LIST.add(new Data(q.getCaptionC(), skipLogic.getAnswerValue()));
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }*/
                    }
                }
            }
        }
    }


    String computeCost(List<RecommendationActivity> recommendationActivities, String which, boolean seasonal) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("0").append("+");

        if (recommendationActivities == null)
            return "0";


        switch (which) {

            case AppConstants.LABOR_DAYS:

                for (RecommendationActivity recommendationActivity : recommendationActivities) {

                    if (seasonal) {
                        if (recommendationActivity.getSeasonal() == 1)
                            stringBuilder.append(recommendationActivity.getLaborDays()).append("+");
                    } else
                        stringBuilder.append(recommendationActivity.getLaborDays()).append("+");


                }

                stringBuilder.append("0");

                return mathFormulaParser.evaluate(stringBuilder.toString());

            case AppConstants.LABOR_COSTS:

                for (RecommendationActivity recommendationActivity : recommendationActivities) {
                    if (seasonal) {
                        if (recommendationActivity.getSeasonal() == 1)
                            stringBuilder.append(recommendationActivity.getLaborCost()).append("+");
                    } else
                        stringBuilder.append(recommendationActivity.getLaborCost()).append("+");
                }

                stringBuilder.append("0");

                return mathFormulaParser.evaluate(stringBuilder.toString());


            case AppConstants.SUPPLIES_COSTS:

                for (RecommendationActivity recommendationActivity : recommendationActivities) {
                    if (seasonal) {
                        if (recommendationActivity.getSeasonal() == 1)
                            stringBuilder.append(recommendationActivity.getSuppliesCost()).append("+");
                    } else
                        stringBuilder.append(recommendationActivity.getSuppliesCost()).append("+");
                }

                stringBuilder.append("0");

                return mathFormulaParser.evaluate(stringBuilder.toString());

            default:
                return "0";

        }
    }


}