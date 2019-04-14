package org.grameen.fdp.kasapin.ui.detailedYearMonthlyView;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.grameen.fdp.kasapin.BuildConfig;
import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.data.db.entity.RecommendationActivity;
import org.grameen.fdp.kasapin.data.db.model.HistoricalTableViewData;
import org.grameen.fdp.kasapin.parser.MathFormulaParser;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.base.model.Data;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.CommonUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.codecrafters.tableview.TableView;

import static org.grameen.fdp.kasapin.utilities.AppConstants.TAG_OTHER_TEXT_VIEW;
import static org.grameen.fdp.kasapin.utilities.AppConstants.TAG_TITLE_TEXT_VIEW;

/**
 * A login screen that offers login via email/password.
 */
public class DetailedMonthActivity extends BaseActivity implements DetailedMonthContract.View {

    @Inject
    DetailedMonthPresenter mPresenter;




    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.print)
    ImageView print;
    @BindView(R.id.name)
    TextView farmerName;
    @BindView(R.id.code)
    TextView farmerCode;
    @BindView(R.id.currency)
    TextView currency;
    @BindView(R.id.tableView)
    TableView tableView;
    @BindView(R.id.bottom_buttons)
    LinearLayout bottomButtons;


    String TAG = "DETAILED ACTIVITY";
    RealFarmer farmer;

    DetailedYearTableViewAdapter myTableViewAdapter;
    Boolean DID_LABOUR = false;
    String LABOUR_TYPE;
    int year;
    JSONObject PLOT_SIZES_IN_HA = new JSONObject();

    List<Plot> plotList;
    List<Data> TABLE_DATA_LIST = new ArrayList<>();

    String CURRENT_SIZE_IN_HA = "1";

    ScriptEngine engine;

    MathFormulaParser mathFormulaParser;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_monthly_view);
        ButterKnife.bind(this);



        getActivityComponent().inject(this);
        mPresenter.takeView(this);

        mathFormulaParser = MathFormulaParser.getInstance();

        engine = new ScriptEngineManager().getEngineByName("rhino");

        farmer = new Gson().fromJson(getIntent().getStringExtra("farmer"), RealFarmer.class);
        year = getIntent().getIntExtra("year", -1);

        DID_LABOUR = getIntent().getBooleanExtra("labour", false);
        LABOUR_TYPE = getIntent().getStringExtra("labourType");

        try {
            PLOT_SIZES_IN_HA = new JSONObject(getIntent().getStringExtra("multiplier"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


         toolbar = setToolbar(getStringResources(R.string.year) + " " + year);

        AppLogger.i(TAG, "^^^^^^^^ LABOUR? " + DID_LABOUR + " TYPE = " + LABOUR_TYPE);



        if (farmer != null) {

            farmerName.setText(farmer.getFarmerName());
            farmerCode.setText(farmer.getCode());
            mPresenter.getPlotsData(farmer.getCode());
        }


        if (BuildConfig.DEBUG){


            findViewById(R.id.print).setVisibility(View.VISIBLE);

            findViewById(R.id.print).setOnClickListener(v -> {

                findViewById(R.id.bottom_buttons).setVisibility(View.GONE);
                findViewById(R.id.currency_layout).setVisibility(View.GONE);
                findViewById(R.id.print).setVisibility(View.GONE);


                showLoading( "Initializing", "Please wait...", false, 0, false);


                new Handler().postDelayed(() -> {

                    String fileLocation = captureScreenshot(findViewById(R.id.main_layout), "monthly_activities");

                    hideLoading();

                   /* if (fileLocation != null) {

                        Intent intent = new Intent(this, PrintingActivity.class);
                        intent.putExtra("file_location", fileLocation);
                        startActivity(intent);

                        findViewById(R.id.bottom_buttons).setVisibility(View.VISIBLE);
                        findViewById(R.id.currency_layout).setVisibility(View.VISIBLE);
                        findViewById(R.id.print).setVisibility(View.VISIBLE);


                    } else
                        showMessage("Error starting the printer service!")
                        ;*/


                    showMessage("File location is " + fileLocation);


                }, 2000);

            });


        }

        onBackClicked();


    }


    @Override
    public void setPlotsData(List<Plot> plotsData) {

        plotList = plotsData;

        setData();



    }

    @Override
    public void setData(){

            tableView.setColumnCount(12);
            String[] TABLE_HEADERS = {getStringResources(R.string.jan), getStringResources(R.string.feb),
                    getStringResources(R.string.mar), getStringResources(R.string.apr), getStringResources(R.string.may), getStringResources(R.string.jun),
                    getStringResources(R.string.jul),getStringResources(R.string.aug), getStringResources(R.string.sep), getStringResources(R.string.oct),
                    getStringResources(R.string.nov), getStringResources(R.string.dec)};




            tableView.setHeaderAdapter(new DetailedYearTableHearderAdapter(this, TABLE_HEADERS));
            for (Plot plot : plotList) {

                try {
                    CURRENT_SIZE_IN_HA = PLOT_SIZES_IN_HA.getString(plot.getExternalId());
                } catch (JSONException e) {
                    e.printStackTrace();
                    CURRENT_SIZE_IN_HA = "1";

                }



                int gapsId =  plot.getGapsId();
                int recommendationId = plot.getRecommendationId();


                int plotYear = plot.getStartYear();
                System.out.println();
                System.out.println("#####################");
                AppLogger.e(TAG, "PLOT YEAR IS " + plotYear);
                AppLogger.e(TAG, "GAPS ID IS " + gapsId);
                AppLogger.e(TAG, "RECOMMENDATION ID IS " + recommendationId);
                System.out.println("#####################");
                System.out.println();


                if (plotYear >= 1) {

                    if (plotYear == 1)
                        getActivitiesSuppliesAndCosts(recommendationId, plot.getName(), year);
                    else {


                        if (year < plotYear)
                            getActivitiesSuppliesAndCosts(gapsId, plot.getName(), 1);
                        else
                            getActivitiesSuppliesAndCosts(recommendationId, plot.getName(), (year - plotYear) + 1);

                    }

                } else {

                    if (year + Math.abs(plotYear) > 7) {

                        getActivitiesSuppliesAndCosts(recommendationId, plot.getName(), 7);

                    } else {

                        getActivitiesSuppliesAndCosts(recommendationId, plot.getName(), year + Math.abs(plotYear));
                    }


                }
            }

            myTableViewAdapter = new DetailedYearTableViewAdapter(this, TABLE_DATA_LIST, tableView);
            tableView.setDataAdapter(myTableViewAdapter);

}




    HistoricalTableViewData getMonthlyData(int id, String month, int year) {

        List<RecommendationActivity> recommendationsPlusActivities;
        List<RecommendationActivity> recommendationsPlusActivities2;

        HistoricalTableViewData data = new HistoricalTableViewData("", "", "");
        try {

           /*

           if(DID_LABOUR)
                if(LABOUR_TYPE.equalsIgnoreCase("seasonal"))
                    recommendationsPlusActivities = databaseHelper.getSeasonalRecommendationPlusAcivityByRecommendationIdMonthAndYear(id, month, year + "", "true");
                else
                    recommendationsPlusActivities = databaseHelper.getRecommendationPlusAcivityByRecommendationIdMonthAndYear(id, month, year + "");
                else

           */

            recommendationsPlusActivities = getAppDataManager().getDatabaseManager().recommendationPlusActivitiesDao().getAllByRecommendation(id, month, String.valueOf(year)).blockingGet();

            recommendationsPlusActivities2 = getAppDataManager().getDatabaseManager().recommendationPlusActivitiesDao().getAllByRecommendation(id, month, String.valueOf(year), "1").blockingGet();


            StringBuilder activities = new StringBuilder() ;
            StringBuilder labourCost = new StringBuilder() ;
            StringBuilder suppliesCost = new StringBuilder();


            if(recommendationsPlusActivities != null)
            for (int i = 0; i < recommendationsPlusActivities.size(); i++) {

                RecommendationActivity ra = recommendationsPlusActivities.get(i);

                try {

                    if(ra.getActivityTranslation() != null && !ra.getActivityTranslation().equals("null"))
                        if (!activities.toString().toLowerCase().contains(ra.getActivityTranslation().toLowerCase()))
                            activities.append(CommonUtils.toCamelCase(ra.getActivityTranslation())).append(", ");

                    //suppliesCost.append(ra.getSuppliesCost()).append("+");
                    suppliesCost.append("0").append("+");


                    try {

                        if (DID_LABOUR)
                            if (LABOUR_TYPE.equalsIgnoreCase("seasonal")) {
                                //labourCost.append(recommendationsPlusActivities2.get(i).getLaborCost()).append("+");
                                labourCost.append("0").append("+");

                            }
                            else {
                                labourCost.append("0").append("+");
                                //labourCost.append(ra.getLaborCost()).append("+");


                            }
                        else
                            labourCost.append("0").append("+");

                    } catch (Exception ignored) {
                        labourCost.append("0").append("+");
                    }

                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }



            suppliesCost.append("0.0");
            labourCost.append("0.0");

            AppLogger.e(TAG, "CURRENT SIZE IN HA = " + CURRENT_SIZE_IN_HA);
            AppLogger.e(TAG, "Appended Activities are = " + activities.toString());
            AppLogger.e(TAG, "Appended Supplies cost are = " + suppliesCost.toString());
            AppLogger.e(TAG, "Appended labor cost are = " + labourCost.toString());


            data = new HistoricalTableViewData(activities.toString(),
                    mathFormulaParser.evaluate("(" + suppliesCost.toString() + ") * " + CURRENT_SIZE_IN_HA),
                    mathFormulaParser.evaluate("(" + labourCost.toString() + ") * " + CURRENT_SIZE_IN_HA));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;

    }


    void getActivitiesSuppliesAndCosts(int recommendationId, String plotName, int year) {


        List<HistoricalTableViewData> dataList = new ArrayList<>();


        dataList.add(getMonthlyData(recommendationId, "Jan", year));
        dataList.add(getMonthlyData(recommendationId, "Feb", year));
        dataList.add(getMonthlyData(recommendationId, "Mar", year));
        dataList.add(getMonthlyData(recommendationId, "Apr", year));
        dataList.add(getMonthlyData(recommendationId, "May", year));
        dataList.add(getMonthlyData(recommendationId, "Jun", year));
        dataList.add(getMonthlyData(recommendationId, "Jul", year));
        dataList.add(getMonthlyData(recommendationId, "Aug", year));
        dataList.add(getMonthlyData(recommendationId, "Sep", year));
        dataList.add(getMonthlyData(recommendationId, "Oct", year));
        dataList.add(getMonthlyData(recommendationId, "Nov", year));
        dataList.add(getMonthlyData(recommendationId, "Dec", year));


        List<String> activities = new ArrayList<>();
        List<String> labourCost = new ArrayList<>();
        List<String> suppliesCost = new ArrayList<>();

        for (HistoricalTableViewData data : dataList) {

            activities.add(data.getLabel());
            suppliesCost.add(data.getV1());

            if (DID_LABOUR)
                labourCost.add(data.getV2());


        }


        TABLE_DATA_LIST.add(new Data(plotName, null, TAG_TITLE_TEXT_VIEW));
        // TABLE_DATA_LIST.add(new Data("", null, TAG_OTHER_TEXT_VIEW));


        TABLE_DATA_LIST.add(new Data(getStringResources(R.string.activities), activities, TAG_OTHER_TEXT_VIEW));
        TABLE_DATA_LIST.add(new Data(getStringResources(R.string.supplies), suppliesCost, TAG_OTHER_TEXT_VIEW));

        if (DID_LABOUR)
            TABLE_DATA_LIST.add(new Data(getStringResources(R.string.labour), labourCost, TAG_OTHER_TEXT_VIEW));


    }



    @Override
    protected void onDestroy() {
        mPresenter.dropView();
        super.onDestroy();
    }


    @Override
    public void openLoginActivityOnTokenExpire() {


    }


}