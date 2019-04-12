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
import org.grameen.fdp.kasapin.data.db.entity.LaborDaysLaborCost;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.data.db.entity.Recommendation;
import org.grameen.fdp.kasapin.data.db.entity.SkipLogic;
import org.grameen.fdp.kasapin.data.db.entity.SuppliesCost;
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
    @BindView(R.id.fdpStatus)
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
    List<Data> TABLE_DATA_LIST ;
    List<String> TOTAL_LABOR_COST;
    List<String> TOTAL_LABOR_DAYS ;
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

    String startYearId = "nil";
    Question CSSV_QUESTION;
    Question PLOT_SIZE_QUESTION;
    String CSSV_VALUE = "--";
    Boolean DID_LABOUR = false;
    String LABOUR_TYPE;
    Boolean shouldHideStartYear = null;
    String START_YEAR_LABEL;
    int  COUNTER = 0;


    MyTableViewAdapter myTableViewAdapter;
    private JSONObject PLOT_ANSWERS_JSON_OBJECT = new JSONObject();
    JSONObject PLOT_SIZES_IN_HA;
    boolean isTranslation;
    static final int MAX_YEARS = 7;

    ScriptEngine engine;

    RealFarmer farmer;

    MathFormulaParser mathFormulaParser;

    Recommendation GAPS_RECOMENDATION_FOR_START_YEAR;
    Recommendation PLOT_RECOMMENDATION;




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

        startYearId = getAppDataManager().getDatabaseManager().questionDao().getLabel("start_year_").blockingGet();
        CSSV_QUESTION = getAppDataManager().getDatabaseManager().questionDao().get("ao_disease_").blockingGet();

        currency.setText(getAppDataManager().getStringValue("currency"));

        mathFormulaParser = MathFormulaParser.getInstance();


        if (farmer != null)
            setUpViews();
        else {
            finish();
            showMessage(getStringResources(R.string.error_has_occurred));
        }



        if (BuildConfig.DEBUG)
            issuePrint();


        if(getAppDataManager().isMonitoring()){
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
            intent.putExtra("multiplier", PLOT_SIZES_IN_HA.toString());

            startActivity(intent);

        });


        tableView.setSaveEnabled(true);


        if (farmer.getHasSubmitted().equalsIgnoreCase(AppConstants.YES) && farmer.getSyncStatus() == 1) {
            submitAgreement.setVisibility(View.GONE);
            findViewById(R.id.save).setVisibility(View.GONE);
        }



        submitAgreement.setOnClickListener(v->{

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
                                    (d, w)-> {
                                            d.dismiss();

                                            //Todo update farmer data, set agreement submitted to true

                                    }, getStringResources(R.string.ok), null, "", 0);

                        }


                    } else {


                        showDialog(true, getStringResources(R.string.fdp_status_incomplete), getStringResources(R.string.fill_out_fdp_status) + farmer.getFarmerName(),
                                (d, w)->d.dismiss(), getStringResources(R.string.ok), (d, w1)->{

                                        d.dismiss();
                                        moveToFdpStatusActivity();

                                }, getStringResources(R.string.go_to_fdp_status_form), 0);

                    }


                } else if (farmer.getHasSubmitted().equalsIgnoreCase(AppConstants.YES)) {

                    showDialog(false, getStringResources(R.string.status_submitted), getStringResources(R.string.no_more_mods) + farmer.getFarmerName()
                                    + getStringResources(R.string.apostrophe_s) + getStringResources(R.string.data),
                            (d, w) ->{
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
    public void moveToFdpStatusActivity(){

        Intent intent = new Intent(this, FDPStatusActivity.class);
        intent.putExtra("farmer", getGson().toJson(farmer));
        startActivity(intent);
    }


    @Override
    public void issuePrint(){
        findViewById(R.id.print).setVisibility(View.VISIBLE);

        findViewById(R.id.print).setOnClickListener(v -> {

            findViewById(R.id.bottom_buttons).setVisibility(View.GONE);
            findViewById(R.id.fdpStatus).setVisibility(View.GONE);
            findViewById(R.id.currency_layout).setVisibility(View.GONE);


            showLoading( "Initializing print", "Please wait...", false, 0, false);


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


        final Question labourQuestion = getAppDataManager().getDatabaseManager().questionDao().get("labour").blockingGet();
        final Question labourTypeQuestion = getAppDataManager().getDatabaseManager().questionDao().get("labour_type").blockingGet();




                try {
                    String val = VALUES_JSON_OBJECT.getString(labourQuestion.getCaptionC());
                    if (val.equalsIgnoreCase("Yes"))
                        DID_LABOUR = true;
                    LABOUR_TYPE = VALUES_JSON_OBJECT.getString(labourTypeQuestion.getCaptionC());
                    // CustomToast.makeToast(this, "Labour? " + val + " LABOUR TYPE = " + LABOUR_TYPE, Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }




        AppLogger.d("P & L ACTIVITY", "MAIN JSON OBJECT ITERATION COMPLETE. DATA IS \n" + VALUES_JSON_OBJECT);



                START_YEAR_LABEL = getAppDataManager().getDatabaseManager().questionDao().getLabel("start_year_").blockingGet();
                PLOT_SIZE_QUESTION = getAppDataManager().getDatabaseManager().questionDao().get("plot_area_ha_").blockingGet();



        Completable.fromAction(this::populateTableData).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();




    }





    @Override
    public boolean checkIfFarmerFdpStatusFormFilled(String code) {

        return false;
    }






    @Override
    public void populateTableData(){
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
        MAINTENANCE_COST_STRING_BUILDERS = new ArrayList<>()  ;
        LABOR_DAYS_STRING_BUILDERS = new ArrayList<>();
        LABOR_COST_STRING_BUILDERS = new ArrayList<>();

        for(int i = 0; i <= MAX_YEARS; i++){

            GROSS_COCOA_STRING_BUILDERS.add(new StringBuilder());
            MAINTENANCE_COST_STRING_BUILDERS.add(new StringBuilder());
            LABOR_DAYS_STRING_BUILDERS.add(new StringBuilder());
            LABOR_COST_STRING_BUILDERS.add(new StringBuilder());
        }

        PLOT_SIZES_IN_HA = new JSONObject();


        if (farmer != null) {
            realPlotList = getAppDataManager().getDatabaseManager().plotsDao().getFarmersPlots(farmer.getCode()).blockingGet();


              COUNTER = 0;

            for (Plot PLOT : realPlotList) {

                if(START_YEAR_LABEL != null){

                    try {
                        PLOT.setStartYear(Integer.valueOf(PLOT.getAOJsonData().getString(START_YEAR_LABEL)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                shouldHideStartYear = false;

                AppLogger.d("P & L ACTIVITY", "START YEAR IS " + PLOT.getStartYear());

                try {
                    PLOT_ANSWERS_JSON_OBJECT = PLOT.getAOJsonData();
                } catch (Exception e) {
                    e.printStackTrace();

                    PLOT_ANSWERS_JSON_OBJECT = new JSONObject();
                }






                String plotSizeInHaValue = "0.0";
                try {
                    //Todo complex calculation Application

                    LogicFormulaParser logicFormulaParser = LogicFormulaParser.getInstance();
                    logicFormulaParser.setJsonObject(PLOT.getAOJsonData());
                    plotSizeInHaValue = logicFormulaParser.evaluate(PLOT_SIZE_QUESTION.getFormulaC());

                } catch (Exception e) {
                    e.printStackTrace();
                }


                AppLogger.i(TAG, "^^^^^^^^^^^^^^   PLOT SIZE IN HA VALUE IS ^^^^^^^^^^^^^^^^^  " + plotSizeInHaValue);


                try {
                    PLOT_SIZES_IN_HA.put(PLOT.getExternalId(), plotSizeInHaValue);
                } catch (JSONException e) {
                    e.printStackTrace();
                }





                  GAPS_RECOMENDATION_FOR_START_YEAR = getAppDataManager().getDatabaseManager().recommendationsDao().get(PLOT.getGapsId()).blockingGet();
                  PLOT_RECOMMENDATION = getAppDataManager().getDatabaseManager().recommendationsDao().get(PLOT.getRecommendationId()).blockingGet();


                if (PLOT.getStartYear() > 0) {
                    loadDataForYear(PLOT, PLOT.getStartYear());

                } else if (PLOT.getStartYear() < 0) {
                    loadDataForInterventionMadeYear(PLOT, PLOT.getStartYear());
                }

                COUNTER++;


            }// For Loop ends



            mathFormulaParser.setJsonObject(VALUES_JSON_OBJECT);



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

            if(aggregateResultsFormId != null){


                List<Question> AGGREGATE_ECO_RESULTS_QUESTIONS = getAppDataManager().getDatabaseManager()
                        .questionDao().getQuestionsByForm(aggregateResultsFormId).blockingGet();



                if (AGGREGATE_ECO_RESULTS_QUESTIONS != null) {

                    for(Question q : AGGREGATE_ECO_RESULTS_QUESTIONS)

                        if (q.getLabelC().startsWith("net_income_cocoa_")) {

                            TABLE_DATA_LIST.add(new Data(q.getCaptionC(), TOTAL_GROSS_INCOME_FROM_COCOA, TAG_RESULTS));



                        }else  if (q.getLabelC().startsWith("net_income_other_crops_")) {

                        String value = mathFormulaParser.evaluate((q.getFormulaC()));
                        calculationsList = new ArrayList<>();

                        for (int i = 0; i < MAX_YEARS + 1; i++) {
                            calculationsList.add(value);
                        }

                        TOTAL_NET_INCOME_FROM_OTHER_CROPS = calculationsList;
                        TABLE_DATA_LIST.add(new Data(q.getCaptionC(), calculationsList, TAG_RESULTS));


                    } else if (q.getLabelC().startsWith("farming_income_")) {

                        calculationsList = new ArrayList<>();

                        for (int i = 0; i < MAX_YEARS + 1; i++) {
                            calculationsList.add(mathFormulaParser.evaluate((TOTAL_GROSS_INCOME_FROM_COCOA.get(i) + "+" + TOTAL_NET_INCOME_FROM_OTHER_CROPS.get(i))));
                        }

                        TABLE_DATA_LIST.add(new Data(q.getCaptionC(), calculationsList, TAG_RESULTS));
                        TOTAL_FARMING_INCOME = calculationsList;


                    } else if (q.getLabelC().startsWith("net_income_other_sources_")) {

                        String value = mathFormulaParser.evaluate((q.getFormulaC()));
                        calculationsList = new ArrayList<>();

                        for (int i = 0; i < MAX_YEARS + 1; i++) {
                            calculationsList.add(value);
                        }

                        TOTAL_NET_INCOME_FROM_OTHER_SOURCES = calculationsList;
                        TABLE_DATA_LIST.add(new Data(q.getCaptionC(), calculationsList, TAG_RESULTS));


                    } else if (q.getLabelC().startsWith("total_income_")) {
                        calculationsList = new ArrayList<>();

                        for (int i = 0; i < MAX_YEARS + 1; i++) {
                            calculationsList.add(mathFormulaParser.evaluate((TOTAL_FARMING_INCOME.get(i) + "+" + TOTAL_NET_INCOME_FROM_OTHER_SOURCES.get(i))));
                        }

                        TABLE_DATA_LIST.add(new Data(q.getCaptionC(), calculationsList, TAG_RESULTS));

                        TOTAL_INCOME = calculationsList;


                    } else if (q.getLabelC().startsWith("total_family_expenses_")) {

                        String value = mathFormulaParser.evaluate((q.getFormulaC()));
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


                    }
                        else if (q.getLabelC().startsWith("fdp_invest_")) {

                            calculationsList = new ArrayList<>();

                            for (int i = 0; i < MAX_YEARS + 1; i++) {
                                calculationsList.add(mathFormulaParser.evaluate((TOTAL_MAINTENANCE_COST.get(i) + "+" + TOTAL_LABOR_COST.get(i))));
                            }


                            TABLE_DATA_LIST.add(new Data(q.getCaptionC(), calculationsList, TAG_RESULTS));


                            TOTAL_INVESTMENT_IN_FDP = calculationsList;

                        }else if (q.getLabelC().startsWith("p&l_")) {
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

            TABLE_DATA_LIST.add(new Data("", null, TAG_OTHER_TEXT_VIEW));
            TABLE_DATA_LIST.add(new Data("", null, TAG_OTHER_TEXT_VIEW));





            Question hhs = getAppDataManager().getDatabaseManager().questionDao().get("hh_savings_").blockingGet();
            Question pI = getAppDataManager().getDatabaseManager().questionDao().get("hh_planed_investment_").blockingGet();

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
                    tableView.setDataAdapter(myTableViewAdapter);


                    myTableViewAdapter.setItemSelectedListener((view, i, id, item) -> {

                        AppLogger.i(TAG, "Spinner item selected with tag " + view.getTag());


                        /**Position of plot selected from the list is saved as a tag to the start year view for you to know which plot has its start year changed
                         ***/

                        Plot plot = realPlotList.get(Integer.valueOf(view.getTag().toString()));

                        try{

                            if(plot.getAOJsonData().has(START_YEAR_LABEL))
                                plot.getAOJsonData().remove(START_YEAR_LABEL);
                                plot.getAOJsonData().put(START_YEAR_LABEL, String.valueOf(i + 1));

                                plot.setAnswersData(plot.getAOJsonData().toString());

                                mPresenter.updatePlotData(plot, true);

                        }catch (Exception e){
                            e.printStackTrace();
                            showMessage("An error occurred changing start year");
                        }
                    });


                    myTableViewAdapter.setClickistener(view -> {
                        AppLogger.i(TAG, "Button item clicked with tag " + view.getTag());

                        try{
                            Plot plot = realPlotList.get(Integer.valueOf(view.getTag().toString()));


                            Recommendation PLOT_REC = getAppDataManager().getDatabaseManager().recommendationsDao().get(plot.getRecommendationId()).blockingGet();
                            Recommendation GAPS_RECOMMENDATION_FOR_START_YEAR;


                            if (PLOT_REC != null) {


                                if (PLOT_REC.getLabel().equalsIgnoreCase("Replant") || PLOT_REC.getLabel().equalsIgnoreCase("Replant + Extra soil")) {

                                    GAPS_RECOMMENDATION_FOR_START_YEAR =  getAppDataManager().getDatabaseManager().recommendationsDao()
                                            .getLabel("Minimal GAPs").blockingGet();

                                } else if (PLOT_REC.getLabel().equalsIgnoreCase("Grafting") || PLOT_REC.getLabel().equalsIgnoreCase("Grafting + Extra soil")) {

                                    GAPS_RECOMMENDATION_FOR_START_YEAR = getAppDataManager().getDatabaseManager().recommendationsDao()
                                            .getLabel("Modest GAPs").blockingGet();


                                } else {

                                    GAPS_RECOMMENDATION_FOR_START_YEAR = getAppDataManager().getDatabaseManager().recommendationsDao()
                                            .getLabel("Maintenance (GAPs)").blockingGet();

                                }

                                plot.setGapsId(GAPS_RECOMMENDATION_FOR_START_YEAR.getId());



                                    mPresenter.updatePlotData(plot, true);
                            }


                            if(getAppDataManager().getDatabaseManager().plotsDao().insertOne(plot) > 0) {
                                showLoading(getStringResources(R.string.updating_table_data), getStringResources(R.string.please_wait), true, 0,
                                        false);

                                Completable.fromAction(this::populateTableData).subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe();
                            }

                        }catch (Exception e){
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
                    .subscribe();

    }


    void loadDataForYear(Plot PLOT, int CONTROLLING_YEAR) {

        System.out.println("################################ \n CALCULATING INCOME \n ###################################");



        AppLogger.i(TAG, "loadDataForYear " + CONTROLLING_YEAR);
        int TEMP = CONTROLLING_YEAR;
        int TEMP2 = CONTROLLING_YEAR;

        AppLogger.i(TAG, "TEMP = " + TEMP);
        AppLogger.i(TAG, "TEMP 2 =  " + TEMP2);




         plotIncomes = new ArrayList<>();

        try {
            mathFormulaParser.setJsonObject(PLOT.getAOJsonData());
        } catch (JSONException e) {
            e.printStackTrace();
            mathFormulaParser.setJsonObject(new JSONObject());
        }

        for(int i = 0; i < CONTROLLING_YEAR; i++){
            //Todo get Recommendation calculation, get formula and apply here

            AppLogger.i(TAG, "\nYEAR " + i);
            Calculation calculation = getAppDataManager().getDatabaseManager().calculationsDao().getByRecommendationYear(GAPS_RECOMENDATION_FOR_START_YEAR.getId(), 0).blockingGet();

            if(calculation != null) {
                mathFormulaParser.setMathFormula(calculation.getFormula());
                plotIncomes.add(mathFormulaParser.evaluate());
            }else
                plotIncomes.add("0");

            GROSS_COCOA_STRING_BUILDERS.get(i).append(plotIncomes.get(i)).append("+");


        }

        int temp = 0;


        for (int i = CONTROLLING_YEAR; i <= MAX_YEARS; i++) {

            temp++;

                Calculation calculation = getAppDataManager().getDatabaseManager().calculationsDao().getByRecommendationYear(PLOT_RECOMMENDATION.getId(), temp).blockingGet();


                if(calculation != null){
                    AppLogger.i(TAG, "\nYEAR " + i +  " >> " + calculation.getFormula());
                    mathFormulaParser.setMathFormula(calculation.getFormula());
                    plotIncomes.add(mathFormulaParser.evaluate());
                }else
                    plotIncomes.add("0");

            GROSS_COCOA_STRING_BUILDERS.get(i).append(plotIncomes.get(i)).append("+");


            if(TEMP == MAX_YEARS) break;

            TEMP += 1;

            AppLogger.i(TAG, "^^^^^^^^^^^^^^    TEMP IS NOW " + TEMP);


        }


        String name =  PLOT_RECOMMENDATION.getLabel();


        TABLE_DATA_LIST.add(new Data(PLOT.getName() + "\n" + name, null, TAG_TITLE_TEXT_VIEW));

        Question plotIncomeQuestion = getAppDataManager().getDatabaseManager().questionDao().get("plot_income_").blockingGet();

        TABLE_DATA_LIST.add(new Data(plotIncomeQuestion.getLabelC(), plotIncomes, TAG_OTHER_TEXT_VIEW));

        //Todo maintenance cost, labor cost and labor days * plot size in ha


        //This is also known as Supplies cost. The selected year is always with   the (GAPS) recommendation obtained in conjunction with the plot recommendation attained
        maintenanceCostList = new ArrayList<>();
        labourCostList = new ArrayList<>();
        labourDaysList = new ArrayList<>();
        pandlist = new ArrayList<>();





        System.out.println("################################ \n CALCULATING MAINTENANCE COST \n ###################################");

        LaborDaysLaborCost lls1;
        SuppliesCost supplies;

        System.out.println("################################ \n CALCULATING LABOUR DAYS AND COST FOR YEAR 0 to SELECTED START YEAR (1 = DEFAULT) \n ###################################");


        for(int i = 0; i < CONTROLLING_YEAR; i++){


            if(i == 0){
                AppLogger.i(TAG, "\nYEAR " + i);
                maintenanceCostList.add("0.0");
                MAINTENANCE_COST_STRING_BUILDERS.get(i).append(maintenanceCostList.get(i)).append("+");

            }else {

                //supplies = databaseHelper.getTotalSuppliesCostByYear("1", GAPS_RECOMENDATION_FOR_START_YEAR.getId());

               // maintenanceCostList.add(applyCalculation("(" + supplies.getSuppliesCost() + ") * " + plotSizeInHaValue));
                maintenanceCostList.add("0.0");

                MAINTENANCE_COST_STRING_BUILDERS.get(i).append(  maintenanceCostList.get(i)).append("+");
            }


            if (DID_LABOUR) {

               /* if (LABOUR_TYPE.equalsIgnoreCase("full"))
                    lls1 = databaseHelper.getTotalLaborDaysLaborCostByYear("1", GAPS_RECOMENDATION_FOR_START_YEAR.getId());
                else
                    lls1 = databaseHelper.getTotalSeasonalLaborDaysLaborCostByYear("1", GAPS_RECOMENDATION_FOR_START_YEAR.getId(), "true");



                labourCostList.add(applyCalculation( "(" + lls1.getLaborCost() + ") * " + plotSizeInHaValue));
                labourDaysList.add(applyCalculation( "(" + lls1.getLaborDays() + ") * " + plotSizeInHaValue));
                */

                labourCostList.add("0.0");
                labourDaysList.add("0.0");


            } else {
                labourCostList.add("0.0");
                labourDaysList.add("0.0");

            }

            LABOR_COST_STRING_BUILDERS.get(i).append(labourCostList.get(i)).append("+");
            LABOR_DAYS_STRING_BUILDERS.get(i).append(labourDaysList.get(i)).append("+");

        }




        System.out.println("################################ \n CALCULATING LABOUR DAYS AND COST \n ###################################");


        for(int i = 1; i < MAX_YEARS + 1; i++) {
            /*
            supplies = databaseHelper.getTotalSuppliesCostByYear(String.valueOf(i), PLOT_RECOMMENDATION.getId());
            maintenanceCostList.add(applyCalculation("(" + supplies.getSuppliesCost() + ") * " + plotSizeInHaValue));

            if (DID_LABOUR) {

                if (LABOUR_TYPE.equalsIgnoreCase("full"))
                    lls1 = databaseHelper.getTotalLaborDaysLaborCostByYear(String.valueOf(i), PLOT_RECOMMENDATION.getId());
                else
                    lls1 = databaseHelper.getTotalSeasonalLaborDaysLaborCostByYear(String.valueOf(i), PLOT_RECOMMENDATION.getId(), "true");

                labourCostList.add(applyCalculation( "(" + lls1.getLaborCost() + ") * " + plotSizeInHaValue));
                labourDaysList.add(applyCalculation( "(" + lls1.getLaborDays() + ") * " + plotSizeInHaValue));
            } else {
                labourCostList.add("0.0");
                labourDaysList.add("0.0");

            }*/
            labourCostList.add("0.0");
            labourDaysList.add("0.0");






            MAINTENANCE_COST_STRING_BUILDERS.get(TEMP2).append(maintenanceCostList.get(TEMP2)).append("+");
            LABOR_COST_STRING_BUILDERS.get(TEMP2).append(labourCostList.get(TEMP2)).append("+");
            LABOR_DAYS_STRING_BUILDERS.get(TEMP2).append(labourDaysList.get(TEMP2)).append("+");


            if(TEMP2 == MAX_YEARS) break;

            TEMP2 += 1;
        }




        AppLogger.i(TAG, "AFTER : TEMP = " + TEMP);
        AppLogger.i(TAG, "AFTER : TEMP 2 =  " + TEMP2);




        computePlotResults();





        showOrHideStartYear( PLOT.getStartYear() - 1);


        Question plotRenovatedCorrectlyQuestion =getAppDataManager().getDatabaseManager().questionDao().get("plot_renovated_correctly_").blockingGet();

        if (plotRenovatedCorrectlyQuestion != null) {

            if (!ComputationUtils.getValue(plotRenovatedCorrectlyQuestion.getLabelC(), PLOT_ANSWERS_JSON_OBJECT).equalsIgnoreCase("yes"))




                if ((PLOT_RECOMMENDATION.getLabel().equalsIgnoreCase("Grafting")) ||
                        PLOT_RECOMMENDATION.getLabel().equalsIgnoreCase("Thinning Out") ||
                        PLOT_RECOMMENDATION.getLabel().equalsIgnoreCase("Filling In")) {

                    getAppDataManager().setStringValue(PLOT.getExternalId(), PLOT_RECOMMENDATION.getLabel());


                    TABLE_DATA_LIST.add(new Data(PLOT.getExternalId() + "_Replant", null, BUTTON_VIEW));


                } else if (PLOT_RECOMMENDATION.getLabel().equalsIgnoreCase("Replant")) {


                    if (getAppDataManager().getStringValue(PLOT.getExternalId()).equalsIgnoreCase("thinning out"))

                        TABLE_DATA_LIST.add(new Data(PLOT.getExternalId() + "_Thinning out", null, BUTTON_VIEW));

                    else if (getAppDataManager().getStringValue(PLOT.getExternalId()).equalsIgnoreCase("filling in"))

                        TABLE_DATA_LIST.add(new Data(PLOT.getExternalId() + "_Filling in", null, BUTTON_VIEW));

                    else if (getAppDataManager().getStringValue(PLOT.getExternalId()).equalsIgnoreCase("grafting"))

                        TABLE_DATA_LIST.add(new Data(PLOT.getExternalId() + "_Grafting", null, BUTTON_VIEW));


                } else if (PLOT_RECOMMENDATION.getLabel().equalsIgnoreCase("Filling in + Extra Soil")
                        || PLOT_RECOMMENDATION.getLabel().equalsIgnoreCase("Thinning out + Extra Soil")
                        || PLOT_RECOMMENDATION.getLabel().equalsIgnoreCase("Grafting + Extra Soil")) {

                    getAppDataManager().setStringValue(PLOT.getExternalId(), PLOT_RECOMMENDATION.getLabel());
                    TABLE_DATA_LIST.add(new Data(PLOT.getExternalId() + "_Replant + Extra Soil", null, BUTTON_VIEW));


                } else if (PLOT_RECOMMENDATION.getLabel().equalsIgnoreCase("Replant + Extra Soil")) {


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


    void loadDataForInterventionMadeYear(Plot PLOT, int CONTROLLING_YEAR) {

        AppLogger.i(TAG, "loadDataForYear " + CONTROLLING_YEAR);


        int TEMP = CONTROLLING_YEAR * -1;
        int TEMP2 = CONTROLLING_YEAR * -1;


        AppLogger.i(TAG, "TEMP = " + TEMP);
        //AppLogger.i(TAG, "TEMP2 =  " + TEMP2);


        plotIncomes = new ArrayList<>();

        try {
            mathFormulaParser.setJsonObject(PLOT.getAOJsonData());
        } catch (JSONException e) {
            e.printStackTrace();
        }




            //Todo get Recommendation calculation, get formula and apply here

            AppLogger.i(TAG, "\nYEAR " + 0);
            Calculation calculation = getAppDataManager().getDatabaseManager().calculationsDao().getByRecommendationYear(GAPS_RECOMENDATION_FOR_START_YEAR.getId(), 0).blockingGet();

            if(calculation != null) {
                mathFormulaParser.setMathFormula(calculation.getFormula());
                plotIncomes.add(mathFormulaParser.evaluate());
            }else
                plotIncomes.add("0");

        GROSS_COCOA_STRING_BUILDERS.get(0).append(plotIncomes.get(0)).append("+");






        for (int i = 1; i <= MAX_YEARS; i++) {
            TEMP += 1;

            if(TEMP >= MAX_YEARS){
                calculation = getAppDataManager().getDatabaseManager().calculationsDao().getByRecommendationYear(PLOT_RECOMMENDATION.getId(), 7).blockingGet();


                if (calculation != null) {
                    AppLogger.i(TAG, "\nYEAR " + 7 + " >> " + calculation.getFormula());
                    mathFormulaParser.setMathFormula(calculation.getFormula());
                    plotIncomes.add(mathFormulaParser.evaluate());
                } else
                    plotIncomes.add("0");



            }else {
                calculation = getAppDataManager().getDatabaseManager().calculationsDao().getByRecommendationYear(PLOT_RECOMMENDATION.getId(), TEMP).blockingGet();


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






        String name =  PLOT_RECOMMENDATION.getLabel();


        TABLE_DATA_LIST.add(new Data(PLOT.getName() + "\n" + name, null, TAG_TITLE_TEXT_VIEW));

        Question plotIncomeQuestion = getAppDataManager().getDatabaseManager().questionDao().get("plot_income_").blockingGet();

        TABLE_DATA_LIST.add(new Data(plotIncomeQuestion.getLabelC(), plotIncomes, TAG_OTHER_TEXT_VIEW));




        ////////

        //This is also known as Supplies cost. The selected year is always with   the (GAPS) recommendation obtained in conjunction with the plot recommendation attained
        maintenanceCostList = new ArrayList<>();
        labourCostList = new ArrayList<>();
        labourDaysList = new ArrayList<>();
        pandlist = new ArrayList<>();

        LaborDaysLaborCost lls1;
        SuppliesCost supplies;


        TEMP = 0;


        System.out.println();
        System.out.println("**********************************************");
        System.out.println("MAINTENANCE COST AND LABOUR COST CALCULATIONS");
        System.out.println("**********************************************");
        System.out.println();


        AppLogger.i(TAG, "\nYEAR " + 0);
        maintenanceCostList.add("0.0");
        MAINTENANCE_COST_STRING_BUILDERS.get(0).append(maintenanceCostList.get(0)).append("+");


        if (DID_LABOUR) {
            labourCostList.add("0.0");
            labourDaysList.add("0.0");
        } else {
            labourCostList.add("0.0");
            labourDaysList.add("0.0");

        }

        LABOR_COST_STRING_BUILDERS.get(0).append(labourCostList.get(0)).append("+");
        LABOR_DAYS_STRING_BUILDERS.get(0).append(labourDaysList.get(0)).append("+");


        for (int i = 1; i <= MAX_YEARS; i++) {

            System.out.println("**********************************************");
            System.out.println("TEMP 2 = " + TEMP2 + " and i = " + i);
            System.out.println("**********************************************");

            TEMP = TEMP2 + i;

            if (TEMP <= MAX_YEARS) {

                /*supplies = databaseHelper.getTotalSuppliesCostByYear(String.valueOf(TEMP), PLOT_RECOMMENDATION.getId());
                maintenanceCostList.add(applyCalculation("(" + supplies.getSuppliesCost() + ") * " + plotSizeInHaValue));
               */

                maintenanceCostList.add("0.0");
                MAINTENANCE_COST_STRING_BUILDERS.get(i).append(maintenanceCostList.get(i)).append("+");

                if (DID_LABOUR) {
/*
                    if (LABOUR_TYPE.equalsIgnoreCase("full"))
                        lls1 = databaseHelper.getTotalLaborDaysLaborCostByYear(String.valueOf(TEMP), PLOT_RECOMMENDATION.getId());
                    else
                        lls1 = databaseHelper.getTotalSeasonalLaborDaysLaborCostByYear(String.valueOf(TEMP), PLOT_RECOMMENDATION.getId(), "true");

                    labourCostList.add(applyCalculation("(" + lls1.getLaborCost() + ") * " + plotSizeInHaValue));
                    labourDaysList.add(applyCalculation("(" + lls1.getLaborDays() + ") * " + plotSizeInHaValue));*/

                    labourCostList.add("0.0");
                    labourDaysList.add("0.0");
                } else {
                    labourCostList.add("0.0");
                    labourDaysList.add("0.0");
                }

                LABOR_COST_STRING_BUILDERS.get(i).append(labourCostList.get(i)).append("+");
                LABOR_DAYS_STRING_BUILDERS.get(i).append(labourDaysList.get(i)).append("+");

            } else {


                //supplies = databaseHelper.getTotalSuppliesCostByYear("7", PLOT_RECOMMENDATION.getId());

                //maintenanceCostList.add(applyCalculation("(" + supplies.getSuppliesCost() + ") * " + plotSizeInHaValue));
                maintenanceCostList.add("0.0");

                if (DID_LABOUR) {
                   /* if (LABOUR_TYPE.equalsIgnoreCase("full"))
                        lls1 = databaseHelper.getTotalLaborDaysLaborCostByYear("7", PLOT_RECOMMENDATION.getId());
                    else
                        lls1 = databaseHelper.getTotalSeasonalLaborDaysLaborCostByYear("7", PLOT_RECOMMENDATION.getId(), "true");

                    labourCostList.add(applyCalculation("(" + lls1.getLaborCost() + ") * " + plotSizeInHaValue));
                    labourDaysList.add(applyCalculation("(" + lls1.getLaborDays() + ") * " + plotSizeInHaValue));*/

                    labourCostList.add("0.0");
                    labourDaysList.add("0.0");
                } else {
                    labourCostList.add("0.0");
                    labourDaysList.add("0.0");
                }

                MAINTENANCE_COST_STRING_BUILDERS.get(i).append(maintenanceCostList.get(i)).append("+");
                LABOR_COST_STRING_BUILDERS.get(i).append(labourCostList.get(i)).append("+");
                LABOR_DAYS_STRING_BUILDERS.get(i).append(labourDaysList.get(i)).append("+");


            }

        }

        computePlotResults();






        TABLE_DATA_LIST.add(new Data("", null, TAG_OTHER_TEXT_VIEW));

    }


    void showOrHideStartYear(int year){

        AppLogger.i(TAG, "\n\n$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$\n\n");
        Boolean value  = false;



        CSSV_VALUE = "--";

        if (PLOT_ANSWERS_JSON_OBJECT != null) {

                CSSV_VALUE = ComputationUtils.getValue(CSSV_QUESTION.getLabelC(), PLOT_ANSWERS_JSON_OBJECT);

        }


        List<SkipLogic> skipLogics = getAppDataManager().getDatabaseManager().skipLogicsDao().getAllByQuestionId(CSSV_QUESTION.getId()).blockingGet();

        if (skipLogics != null && skipLogics.size() > 0) {

            for (SkipLogic sl : skipLogics) {

                try {

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


    void computePlotResults(){


        for(int i = 0; i < MAX_YEARS + 1; i++){

            pandlist.add(mathFormulaParser.evaluate(plotIncomes.get(i) + "-" + "(" + maintenanceCostList.get(i) + "+" + labourCostList.get(i) + ")"));
        }



        Question plotCostQuestion = getAppDataManager().getDatabaseManager().questionDao().get("plot_cost_").blockingGet();
        TABLE_DATA_LIST.add(new Data(plotCostQuestion.getCaptionC(), maintenanceCostList, TAG_OTHER_TEXT_VIEW));


        Question laborDaysQuestion = getAppDataManager().getDatabaseManager().questionDao().get("plot_labor_days_").blockingGet();
        TABLE_DATA_LIST.add(new Data(laborDaysQuestion.getCaptionC(), labourDaysList, TAG_OTHER_TEXT_VIEW));


        Question labourCostQuestion = getAppDataManager().getDatabaseManager().questionDao().get("plot_labor_cost_").blockingGet();
        TABLE_DATA_LIST.add(new Data(labourCostQuestion.getCaptionC(), labourCostList, TAG_OTHER_TEXT_VIEW));

        Question profitLossQuestion = getAppDataManager().getDatabaseManager().questionDao().get("p&l_").blockingGet();
        TABLE_DATA_LIST.add(new Data(profitLossQuestion.getCaptionC(), pandlist, TAG_OTHER_TEXT_VIEW));






        Integer plotResultsFormId = getAppDataManager().getDatabaseManager().formsDao().getId("Plot results").blockingGet();
        if(plotResultsFormId != null) {

            List<Question> plotResultsQuestions =getAppDataManager().getDatabaseManager().questionDao().getQuestionsByForm(plotResultsFormId).blockingGet();
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





}