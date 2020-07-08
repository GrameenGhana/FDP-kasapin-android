package org.grameen.fdp.kasapin.ui.plotReview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.model.HistoricalTableViewData;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.base.model.PlotMonitoringTableData;
import org.grameen.fdp.kasapin.ui.main.MainActivity;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.ComputationUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.codecrafters.tableview.TableView;

public class PlotReviewActivity extends BaseActivity implements PlotReviewContract.View {
    @Inject
    PlotReviewPresenter mPresenter;
    @BindView(R.id.plotName)
    TextView plotName;
    @BindView(R.id.name)
    TextView farmerName;
    @BindView(R.id.code)
    TextView farmerCode;
    @BindView(R.id.nameLayout)
    LinearLayout nameLayout;
    @BindView(R.id.title_view)
    TextView titleView;
    @BindView(R.id.currencyLayout)
    RelativeLayout currencyLayout;
    @BindView(R.id.general_ao_tableView)
    TableView<HistoricalTableViewData> generalAoTableView;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.noData)
    TextView noData;
    @BindView(R.id.ll5)
    LinearLayout ll5;
    @BindView(R.id.ll1)
    LinearLayout ll1;
    @BindView(R.id.back)
    Button back;
    @BindView(R.id.sync)
    Button sync;
    @BindView(R.id.bottom_buttons)
    LinearLayout bottomButtons;
    ReviewTablePagerAdapter plotMonitoringTablePagerAdapter;
    List<PlotMonitoringTableData> plotMonitoringTableDataList;
    Farmer FARMER;
    List<Question> ALL_PLOT_DATA_QUESTIONS = new ArrayList<>();
    List<Plot> PLOTS_LIST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toggleFullScreen(false, getWindow());
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_plot_review);
        getActivityComponent().inject(this);
        mPresenter.takeView(this);
        setUnBinder(ButterKnife.bind(this));
        FARMER = getGson().fromJson(getIntent().getStringExtra("farmer"), Farmer.class);
        getAppDataManager().setBooleanValue("refreshViewPager", false);
        if (FARMER != null) {
            mPresenter.getAllPlotQuestions();
        }
    }

    @Override
    public void setPlotQuestions(List<Question> questions) {
        ALL_PLOT_DATA_QUESTIONS = questions;
        PLOTS_LIST = getAppDataManager().getDatabaseManager().plotsDao().getFarmersPlots(FARMER.getCode()).blockingGet();
        AppLogger.e(TAG, getGson().toJson(PLOTS_LIST));

        if (ALL_PLOT_DATA_QUESTIONS != null && PLOTS_LIST != null && PLOTS_LIST.size() > 0)
            setUpViewPager();
    }

    @Override
    public void setUpViewPager() {
        viewPager = findViewById(R.id.view_pager);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(0, 0, 130, 0);
        viewPager.setPageMargin(60);
        farmerName = findViewById(R.id.name);
        farmerCode = findViewById(R.id.code);
        plotName = findViewById(R.id.plotName);
        farmerName.setText(FARMER.getFarmerName());
        farmerCode.setText(FARMER.getCode());
        plotMonitoringTableDataList = new ArrayList<>();
        updateTableData();

        plotMonitoringTablePagerAdapter = new ReviewTablePagerAdapter(this, plotMonitoringTableDataList);
        if (plotMonitoringTableDataList.size() > 0) {
            viewPager.setAdapter(plotMonitoringTablePagerAdapter);
            findViewById(R.id.noData).setVisibility(View.GONE);
        } else findViewById(R.id.noData).setVisibility(View.VISIBLE);
        onBackClicked();
    }

    void updateTableData() {
        if (PLOTS_LIST != null && PLOTS_LIST.size() > 0) {
            findViewById(R.id.noData).setVisibility(View.GONE);
            for (Plot plot : PLOTS_LIST) {
                List<HistoricalTableViewData> historicalTableViewDataList = new ArrayList<>();
                JSONObject jsonObject;
                try {
                    jsonObject = plot.getAOJsonData();
                } catch (JSONException e) {
                    e.printStackTrace();
                    jsonObject = new JSONObject();
                }

                Question plotSizeQuestion = getAppDataManager().getDatabaseManager().questionDao().get("plot_area_");
                if (plotSizeQuestion != null)
                    historicalTableViewDataList.add(new HistoricalTableViewData(plotSizeQuestion.getCaptionC(), plot.getArea(), "", "", null));

                Question estProdQuestion = getAppDataManager().getDatabaseManager().questionDao().get("plot_estimate_production");
                if (estProdQuestion != null) {
                    if (jsonObject.has(estProdQuestion.getLabelC()))
                        jsonObject.remove(estProdQuestion.getLabelC());
                    try {
                        jsonObject.put(estProdQuestion.getLabelC(), plot.getEstimatedProductionSize());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Question startYearQuestion = getAppDataManager().getDatabaseManager().questionDao().get("plot_intervention_start_year_");
                if (startYearQuestion != null) {
                    if (jsonObject.has(startYearQuestion.getLabelC()))
                        jsonObject.remove(startYearQuestion.getLabelC());
                    try {
                        jsonObject.put(startYearQuestion.getLabelC(), plot.getStartYear());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Question plotRec = getAppDataManager().getDatabaseManager().questionDao().get("plot_recommendation");
                if (plotRec != null) {
                    String recName;
                    try {
                        recName = getAppDataManager().getDatabaseManager().recommendationsDao().getByRecommendationName(plot.getRecommendationId()).blockingGet();
                    } catch (Exception e) {
                        e.printStackTrace();
                        recName = "--";
                    }
                    historicalTableViewDataList.add(new HistoricalTableViewData(plotRec.getCaptionC(), recName, "", "", null));
                }

                for (Question q : ALL_PLOT_DATA_QUESTIONS) {
                    if (!q.shouldHide())
                        historicalTableViewDataList.add(new HistoricalTableViewData(q.getCaptionC(), ComputationUtils.getDataValue(q, jsonObject), "", "", null));
                }

                PlotMonitoringTableData p = new PlotMonitoringTableData(plot.getName(), historicalTableViewDataList);
                plotMonitoringTableDataList.add(p);
            }
        } else
            findViewById(R.id.noData).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        mPresenter.dropView();
        super.onDestroy();
    }

    @Override
    public void openMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void openLoginActivityOnTokenExpire() {
    }
}