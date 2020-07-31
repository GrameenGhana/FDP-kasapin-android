package org.grameen.fdp.kasapin.ui.detailedYearMonthlyView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.RecommendationActivity;
import org.grameen.fdp.kasapin.data.db.model.HistoricalTableViewData;
import org.grameen.fdp.kasapin.parser.MathFormulaParser;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.base.model.TableData;
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
    TableView<TableData> tableView;
    Farmer farmer;
    DetailedYearTableViewAdapter myTableViewAdapter;
    Boolean DID_LABOUR = false;
    String LABOUR_TYPE;
    String TAG = "DETAILED ACTIVITY";
    String CURRENT_SIZE_IN_HA = "1";
    int year;
    JSONObject PLOT_SIZES_IN_HA = new JSONObject();
    List<Plot> plotList;
    List<TableData> TABLE_DATA_LIST = new ArrayList<>();
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
        farmer = new Gson().fromJson(getIntent().getStringExtra("farmer"), Farmer.class);
        year = getIntent().getIntExtra("year", -1);

        DID_LABOUR = getIntent().getBooleanExtra("labour", false);
        LABOUR_TYPE = getIntent().getStringExtra("labourType");

        try {
            PLOT_SIZES_IN_HA = new JSONObject(getIntent().getStringExtra("multiplier"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        toolbar = setToolbar(getString(R.string.year) + " " + year);

        if (farmer != null) {
            farmerName.setText(farmer.getFarmerName());
            farmerCode.setText(farmer.getCode());
            mPresenter.getPlotsData(farmer.getCode());
        }
        onBackClicked();
    }

    @Override
    public void setPlotsData(List<Plot> plotsData) {
        plotList = plotsData;
        setData();
    }

    @Override
    public void setData() {
        tableView.setColumnCount(12);
        String[] TABLE_HEADERS = {getString(R.string.jan), getString(R.string.feb),
                getString(R.string.mar), getString(R.string.apr), getString(R.string.may), getString(R.string.jun),
                getString(R.string.jul), getString(R.string.aug), getString(R.string.sep), getString(R.string.oct),
                getString(R.string.nov), getString(R.string.dec)};

        tableView.setHeaderAdapter(new DetailedYearTableHeaderAdapter(this, TABLE_HEADERS));
        for (Plot plot : plotList) {
            try {
                CURRENT_SIZE_IN_HA = PLOT_SIZES_IN_HA.getString(plot.getExternalId());
            } catch (JSONException e) {
                e.printStackTrace();
                CURRENT_SIZE_IN_HA = "1";
            }

            int gapsId = plot.getGapsId();
            int recommendationId = plot.getRecommendationId();
            int plotYear = plot.getStartYear();

            if (plotYear >= 1) {
                if (plotYear == 1)
                    getActivitiesSuppliesAndCosts(recommendationId, plot.getName(), year);
                else {
                    if (year < plotYear)
                        getActivitiesSuppliesAndCosts(gapsId, plot.getName(), 1);
                    else
                        getActivitiesSuppliesAndCosts(recommendationId, plot.getName(), (year - plotYear) + 1);
                }
            } else
                getActivitiesSuppliesAndCosts(recommendationId, plot.getName(), Math.min(year + Math.abs(plotYear), 7));
        }
        myTableViewAdapter = new DetailedYearTableViewAdapter(this, TABLE_DATA_LIST, tableView);
        tableView.setDataAdapter(myTableViewAdapter);
    }

    HistoricalTableViewData getMonthlyData(int id, String month, int year) {
        List<RecommendationActivity> recommendationsPlusActivities;
        HistoricalTableViewData data = new HistoricalTableViewData("", "", "");

        try {
            recommendationsPlusActivities = getAppDataManager().getDatabaseManager().recommendationPlusActivitiesDao().getAllByRecommendation(id, month, String.valueOf(year)).blockingGet();
            StringBuilder activities = new StringBuilder();
            StringBuilder labourCost = new StringBuilder();
            StringBuilder suppliesCost = new StringBuilder();

            if (recommendationsPlusActivities != null)
                for (int i = 0; i < recommendationsPlusActivities.size(); i++) {
                    RecommendationActivity ra = recommendationsPlusActivities.get(i);
                    try {
                        if (ra.getActivityTranslation() != null && !ra.getActivityTranslation().equals("null"))
                            if (!activities.toString().toLowerCase().contains(ra.getActivityTranslation().toLowerCase()))
                                activities.append(CommonUtils.toCamelCase(ra.getActivityTranslation())).append(", ");

                        suppliesCost.append(ra.getSuppliesCost()).append("+");

                        try {
                            if (DID_LABOUR) {
                                if (LABOUR_TYPE.equalsIgnoreCase("seasonal")) {
                                    if (ra.getSeasonal() == 1)
                                        labourCost.append(ra.getLaborCost()).append("+");
                                    else
                                        labourCost.append("0").append("+");
                                } else {
                                    labourCost.append(ra.getLaborCost()).append("+");
                                }
                            } else
                                labourCost.append("0").append("+");
                        } catch (Exception ignored) {
                            labourCost.append("0").append("+");
                        }
                    } catch (Exception ignored) {
                    }
                }

            suppliesCost.append("0.0");
            labourCost.append("0.0");
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
            suppliesCost.add(data.getValueAtColumn1());
            if (DID_LABOUR)
                labourCost.add(data.getValueAtColumn2());
        }

        TABLE_DATA_LIST.add(new TableData(plotName, null, TAG_TITLE_TEXT_VIEW));
        TABLE_DATA_LIST.add(new TableData(getString(R.string.activities), activities, TAG_OTHER_TEXT_VIEW));
        TABLE_DATA_LIST.add(new TableData(getString(R.string.supplies), suppliesCost, TAG_OTHER_TEXT_VIEW));

        if (DID_LABOUR)
            TABLE_DATA_LIST.add(new TableData(getString(R.string.labour), labourCost, TAG_OTHER_TEXT_VIEW));
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