package org.grameen.fdp.kasapin.ui.plotMonitoringActivity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.Monitoring;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.data.db.model.HistoricalTableViewData;
import org.grameen.fdp.kasapin.ui.addPlotMonitoring.AddPlotMonitoringActivity;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.base.model.PlotMonitoringTableData;
import org.grameen.fdp.kasapin.ui.plotReview.HistoricalTableHeaderAdapter;
import org.grameen.fdp.kasapin.ui.plotReview.PlotMonitoringTablePagerAdapter;
import org.grameen.fdp.kasapin.ui.plotReview.PlotMonitoringTableViewAdapter;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.ComputationUtils;
import org.grameen.fdp.kasapin.utilities.NetworkUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import io.reactivex.functions.Action;


public class PlotMonitoringActivity extends BaseActivity implements PlotMonitoringContract.View {

    @Inject
    PlotMonitoringPresenter mPresenter;
    @BindView(R.id.plotName)
    TextView plotName;
    @BindView(R.id.name)
    TextView farmerName;
    @BindView(R.id.code)
    TextView farmerCode;

    @BindView(R.id.title_view)
    TextView titleView;
    @BindView(R.id.noData)
    TextView noDataTextView;
    @BindView(R.id.currencyLayout)
    RelativeLayout currencyLayout;
    @BindView(R.id.general_ao_tableView)
    TableView generalAoTableView;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.edit_monitoring)
    Button editMonitoring;
    @BindView(R.id.add_monitoring)
    Button addMonitoring;

    @BindView(R.id.sync)
    Button sync;


    RealFarmer FARMER;
    Plot PLOT;

    JSONObject AO_JSON_OBJECT;
    PlotMonitoringTablePagerAdapter plotMonitoringTablePagerAdapter;

    List<Question> AO_QUESTIONS;
    List<Question> MONITORING_AO_QUESTIONS;
    List<PlotMonitoringTableData> plotMonitoringTableDataList;

    int MONITORING_POSITION = 0;

    private List<Monitoring> monitoringList;
    int SELECTED_YEAR;
    public static boolean newMonitoringAdded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot_monitoring_view);
        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));

        mPresenter.takeView(this);

        FARMER = new Gson().fromJson(getIntent().getStringExtra("farmer"), RealFarmer.class);
        PLOT = new Gson().fromJson(getIntent().getStringExtra("plot"), Plot.class);
        SELECTED_YEAR = getIntent().getIntExtra("selectedYear", 1);

        setUpViews();
        onBackClicked();



    }


    @Override
    public void setUpViews() {
        //setToolbar(getStringResources(R.string.plot_monitoring));


        viewPager.setClipToPadding(false);
        viewPager.setPadding(100, 0, 100, 0);
        viewPager.setPageMargin(40);


        farmerName.setText(FARMER.getFarmerName());
        farmerCode.setText(FARMER.getCode());

        String name_of_plot = PLOT.getName() + " Year " + SELECTED_YEAR;
        plotName.setText(name_of_plot);


        if (PLOT.getAnswersData() != null && !PLOT.getAnswersData().contains("--") && PLOT.getRecommendationId() > 0)
            mPresenter.getAOQuestions();
        else
            toggleNoDataPlaceholder(true, "No data!\n\nPlease ensure that all Adoption Observations were completed for " + PLOT.getName());


    }


    @Override
    public void setupAdoptionObservationsTableView(FormAndQuestions aoFormQuestions, FormAndQuestions monitoringAOFormAndQuestions) {

        AO_QUESTIONS = aoFormQuestions.getQuestions();
        MONITORING_AO_QUESTIONS = monitoringAOFormAndQuestions.getQuestions();


        try {
            AO_JSON_OBJECT = PLOT.getAOJsonData();
        } catch (JSONException e) {
            e.printStackTrace();
            AO_JSON_OBJECT = new JSONObject();
        }


        String recommendationName = getAppDataManager().getDatabaseManager().recommendationsDao().getByRecommendationName(PLOT.getRecommendationId()).blockingGet();

        //Set the table headers for AO table view
        String[] TABLE_HEADERS = {recommendationName, getStringResources(R.string.diagnostic)};


        //Set general ao table column model
        TableColumnWeightModel columnModel = new TableColumnWeightModel(2);
        columnModel.setColumnWeight(0, 4);
        columnModel.setColumnWeight(1, 1);
        generalAoTableView.setColumnModel(columnModel);

        //Set the  header adapter for the table
        HistoricalTableHeaderAdapter headerAdapter = new HistoricalTableHeaderAdapter(PlotMonitoringActivity.this, TABLE_HEADERS);
        generalAoTableView.setHeaderAdapter(headerAdapter);


        //Set the table view data
        List<HistoricalTableViewData> ADOPTION_OBSERVATIONS_TABLE_VIEW_DATA = new ArrayList<>();
        for(Question q : AO_QUESTIONS){
            //Todo get results
            ADOPTION_OBSERVATIONS_TABLE_VIEW_DATA.add(new HistoricalTableViewData(q.getCaptionC(), ComputationUtils.getValue(q.getLabelC(), AO_JSON_OBJECT), "--", "--", null));
        }


        PlotMonitoringTableViewAdapter plotMonitoringTableViewAdapter = new PlotMonitoringTableViewAdapter(this, ADOPTION_OBSERVATIONS_TABLE_VIEW_DATA, generalAoTableView);
        generalAoTableView.setDataAdapter(plotMonitoringTableViewAdapter);



        mPresenter.getMonitoringForSelectedYear(PLOT, SELECTED_YEAR);

    }


    @Override
    public void updateTableData(List<Monitoring> _monitoringList) {
        plotMonitoringTableDataList = new ArrayList<>();
        monitoringList = _monitoringList;



        for(Monitoring monitoring : monitoringList) {
            List<HistoricalTableViewData> historicalTableViewDataList = new ArrayList<>();

            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(monitoring.getJson());
            } catch (JSONException e) {
                e.printStackTrace();
                jsonObject = new JSONObject();
            }

            for (Question q : MONITORING_AO_QUESTIONS) {
                String[] questionIds = q.splitRelatedQuestions();
                if (questionIds != null) {
                    Question competenceQuestion = getAppDataManager().getDatabaseManager().questionDao().get(questionIds[0]);
                    Question failureQuestion = getAppDataManager().getDatabaseManager().questionDao().get(questionIds[1]);

                    //Todo get results
                    if(competenceQuestion != null && failureQuestion != null)
                    historicalTableViewDataList.add(new HistoricalTableViewData("", ComputationUtils.getDataValue(q, jsonObject), ComputationUtils.getDataValue(competenceQuestion, jsonObject), ComputationUtils.getDataValue(failureQuestion, jsonObject), null));
                    else
                        historicalTableViewDataList.add(new HistoricalTableViewData("", ComputationUtils.getDataValue(q, jsonObject), "--", "--", null));

                }else
                    historicalTableViewDataList.add(new HistoricalTableViewData("", ComputationUtils.getDataValue(q, jsonObject), "--", "--", null));

            }

            Question monitoringPlotDate = getAppDataManager().getDatabaseManager().questionDao().get("monitoring_plot_date_");
            if(monitoringPlotDate != null) {

                String dateValue = ComputationUtils.getValue(monitoringPlotDate.getLabelC(), jsonObject);


                try {
                    if (dateValue.length() > 20) {

                        historicalTableViewDataList.add(new HistoricalTableViewData("", "", getStringResources(R.string.date), dateValue.substring(0, 19), AppConstants.TAG_RESULTS));
                    } else
                        historicalTableViewDataList.add(new HistoricalTableViewData("", "", getStringResources(R.string.date), dateValue, AppConstants.TAG_RESULTS));

                } catch (Exception e) {
                    historicalTableViewDataList.add(new HistoricalTableViewData("", "", getStringResources(R.string.date), dateValue.substring(0, 19), AppConstants.TAG_RESULTS));
                    e.printStackTrace();
                }
            }

            //Get plot assessment questions and add to the table data
            FormAndQuestions monitoringPlotResultsFormAndQuestions = getAppDataManager().getDatabaseManager()
                    .formAndQuestionsDao()
                    .getFormAndQuestionsByName(AppConstants.AO_MONITORING_RESULT)
                    .blockingGet();

            if (monitoringPlotResultsFormAndQuestions != null && monitoringPlotResultsFormAndQuestions.getQuestions().size() > 0)
                for (Question q : monitoringPlotResultsFormAndQuestions.getQuestions())
                    historicalTableViewDataList.add(new HistoricalTableViewData("", "", q.getCaptionC(), ComputationUtils.getValue(q.getLabelC(), jsonObject), AppConstants.TAG_RESULTS));


            PlotMonitoringTableData p = new PlotMonitoringTableData(monitoring.getName(), historicalTableViewDataList);

            plotMonitoringTableDataList.add(p);
        }


        plotMonitoringTablePagerAdapter = new PlotMonitoringTablePagerAdapter(this, plotMonitoringTableDataList);
        if (plotMonitoringTableDataList.size() > 0) {
            viewPager.setAdapter(plotMonitoringTablePagerAdapter);
            titleView.setText(plotMonitoringTableDataList.get(0).getTitle());

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    AppLogger.e(TAG, "ON PAGE SELECTED >> " + position + " >>> TITLE = " + plotMonitoringTableDataList.get(position).getTitle());
                    MONITORING_POSITION = position;
                    titleView.setText(plotMonitoringTableDataList.get(position).getTitle());
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            toggleNoDataPlaceholder(false, null);


            AppLogger.e(TAG, "****************** NEW MONITORING ADDED? >>>> " + newMonitoringAdded);

            if(newMonitoringAdded)
            new Handler().postDelayed(() -> {
                viewPager.setCurrentItem(plotMonitoringTablePagerAdapter.getCount() - 1, true);
                newMonitoringAdded = false;
            }, 1000);

        } else
            toggleNoDataPlaceholder(true, getString(R.string.no_monitoring_data));




    }



    void toggleNoDataPlaceholder(boolean shouldShow, String noDataText) {
        noDataTextView.setVisibility((shouldShow) ? View.VISIBLE : View.GONE);
        sync.setEnabled(!shouldShow);
        //addMonitoring.setVisibility((shouldShow) ? View.VISIBLE : View.GONE);

        editMonitoring.setVisibility((shouldShow) ? View.GONE : View.VISIBLE);
        noDataTextView.setText(noDataText);

    }


    @Override
    protected void onDestroy() {
        mPresenter.dropView();
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();

        try {
            Action action = () -> {
                if(getAppDataManager().getBooleanValue("refreshViewPager")){
                    mPresenter.getMonitoringForSelectedYear(PLOT, SELECTED_YEAR);
                    getAppDataManager().setBooleanValue("refreshViewPager", false);
                }
            };
            action.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void openNextActivity() {


    }


    @OnClick({R.id.edit_monitoring, R.id.add_monitoring})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.sync:
                if (FARMER.getSyncStatus() == AppConstants.SYNC_OK) {
                    showMessage(R.string.no_new_data);
                    return;
                }

                if (NetworkUtils.isNetworkConnected(this))
                    mPresenter.syncFarmerData(FARMER, true);
                else
                    showMessage(R.string.no_internet_connection_available);

                break;

            case R.id.edit_monitoring:

         intent = new Intent(PlotMonitoringActivity.this, AddPlotMonitoringActivity.class);
                intent.putExtra("plot", new Gson().toJson(PLOT));
                intent.putExtra("farmer", new Gson().toJson(FARMER));
                intent.putExtra("year", SELECTED_YEAR);
                intent.putExtra("monitoringPosition", monitoringList.size() + 1);
                intent.putExtra("monitoring", getGson().toJson(monitoringList.get(MONITORING_POSITION)));
                startActivity(intent);


                break;

            case R.id.add_monitoring:
                 intent = new Intent(PlotMonitoringActivity.this, AddPlotMonitoringActivity.class);
                intent.putExtra("plot", new Gson().toJson(PLOT));
                intent.putExtra("year", SELECTED_YEAR);
                intent.putExtra("farmer", new Gson().toJson(FARMER));
                intent.putExtra("monitoringPosition", monitoringList.size() + 1);
                startActivity(intent);

                break;
        }
    }
}