package org.grameen.fdp.kasapin.ui.plotReview;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.data.db.model.HistoricalTableViewData;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.base.model.PlotMonitoringTableData;
import org.grameen.fdp.kasapin.ui.main.MainActivity;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.ComputationUtils;
import org.grameen.fdp.kasapin.utilities.CustomToast;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.codecrafters.tableview.TableView;

/**
 * A login screen that offers login via email/password.
 */
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
    TableView generalAoTableView;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.noData)
    TextView noData;
    @BindView(R.id.ll5)
    LinearLayout ll5;
    @BindView(R.id.ll1)
    LinearLayout ll1;
    @BindView(R.id.farmer_hire_labour_text)
    TextView farmerHireLabourText;
    @BindView(R.id.farmer_hire_labour_spinner)
    MaterialSpinner labourSpinner;
    @BindView(R.id.labour_type_text)
    TextView labourTypeText;
    @BindView(R.id.labour_type_spinner)
    MaterialSpinner labourTypeSpinner;
    @BindView(R.id.back)
    Button back;
    @BindView(R.id.sync)
    Button sync;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.bottom_buttons)
    LinearLayout bottomButtons;
    ReviewTablePagerAdapter plotMonitoringTablePagerAdapter;
    List<PlotMonitoringTableData> plotMonitoringTableDataList;
    RealFarmer FARMER;
    List<Question> ALL_PLOT_DATA_QUESTIONS = new ArrayList<>();
    List<Plot> PLOTS_LIST;

    FormAnswerData farmingEcoProfileFormAnswerData = null;
    JSONObject FARMING_ECO_PROFILE_ANSWERS_JSON;


    public static Intent getStartIntent(Context context) {
        return new Intent(context, PlotReviewActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toggleFullScreen(false, getWindow());
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_plot_review);

        getActivityComponent().inject(this);
        mPresenter.takeView(this);
        setUnBinder(ButterKnife.bind(this));


        FARMER = getGson().fromJson(getIntent().getStringExtra("farmer"), RealFarmer.class);
        getAppDataManager().setBooleanValue("refreshViewPager", false);


        if (FARMER != null) {
            if (FORM_AND_QUESTIONS != null)
                for (FormAndQuestions formAndQuestions : FORM_AND_QUESTIONS) {
                    if (formAndQuestions.getForm().getDisplayTypeC().equalsIgnoreCase(AppConstants.DISPLAY_TYPE_PLOT_FORM))
                        ALL_PLOT_DATA_QUESTIONS.addAll(formAndQuestions.getQuestions());


                   // AppLogger.largeLog(TAG, getGson().toJson(ALL_PLOT_DATA_QUESTIONS));
                }
            setPlotQuestions(ALL_PLOT_DATA_QUESTIONS);
        }


    }


    @Override
    public void setPlotQuestions(List<Question> questions) {

        PLOTS_LIST = getAppDataManager().getDatabaseManager().plotsDao().getFarmersPlots(FARMER.getCode()).blockingGet();
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


            //titleTextView.setText("Edit" + plotMonitoringTableDataList.get(0).getTitle());

           /* viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {



                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });*/

            findViewById(R.id.noData).setVisibility(View.GONE);

        } else findViewById(R.id.noData).setVisibility(View.VISIBLE);


        setupLaborTypeAndSpinner();

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
                    historicalTableViewDataList.add(new HistoricalTableViewData(plotSizeQuestion.getCaptionC(), ComputationUtils.getDataValue(plotSizeQuestion, jsonObject), "", "", null));


                Question estProd = getAppDataManager().getDatabaseManager().questionDao().get("plot_estimate_production");
                if (estProd != null)
                    historicalTableViewDataList.add(new HistoricalTableViewData(estProd.getCaptionC(), ComputationUtils.getDataValue(estProd, jsonObject), "", "", null));


                Question limeNeeded = getAppDataManager().getDatabaseManager().questionDao().get("lime_");
                if (limeNeeded != null)
                    historicalTableViewDataList.add(new HistoricalTableViewData(limeNeeded.getCaptionC(), ComputationUtils.getDataValue(limeNeeded, jsonObject), "", "", null));


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


    void setupLaborTypeAndSpinner() {
        Integer farmingEcoProfileFormId = getAppDataManager().getDatabaseManager().formsDao().getId(AppConstants.FARMING_ECONOMIC_PROFILE).blockingGet();


        if (farmingEcoProfileFormId != null) {
            farmingEcoProfileFormAnswerData = getAppDataManager().getDatabaseManager().formAnswerDao().getFormAnswerData(FARMER.getCode(), farmingEcoProfileFormId);

            if (farmingEcoProfileFormAnswerData != null) {

                FARMING_ECO_PROFILE_ANSWERS_JSON = farmingEcoProfileFormAnswerData.getJsonData();

                labourSpinner.setItems("-select-", "Yes", "No");
                labourSpinner.setSelectedIndex(0);
                MaterialSpinner labourType = findViewById(R.id.labour_type_spinner);
                labourType.setItems("-select-", "Full", "Seasonal");
                labourType.setSelectedIndex(0);


                final Question labourQuestion = getAppDataManager().getDatabaseManager().questionDao().get("labour");
                final Question labourTypeQuestion = getAppDataManager().getDatabaseManager().questionDao().get("labour_type");


                if (labourQuestion != null) {
                    if (FARMING_ECO_PROFILE_ANSWERS_JSON.has(labourQuestion.getLabelC()))
                        try {
                            labourSpinner.setText(FARMING_ECO_PROFILE_ANSWERS_JSON.getString(labourQuestion.getLabelC()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    labourSpinner.setOnItemSelectedListener((view, position, id, item) -> {

                        if (FARMING_ECO_PROFILE_ANSWERS_JSON.has(labourQuestion.getLabelC()))
                            FARMING_ECO_PROFILE_ANSWERS_JSON.remove(labourQuestion.getLabelC());
                        try {
                            FARMING_ECO_PROFILE_ANSWERS_JSON.put(labourQuestion.getLabelC(), item.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });

                } else
                    showMessage("Labour Question is missing. Labour value won't be saved.");


                if (labourTypeQuestion != null) {
                    if (FARMING_ECO_PROFILE_ANSWERS_JSON.has(labourTypeQuestion.getLabelC()))
                        try {
                            labourType.setText(FARMING_ECO_PROFILE_ANSWERS_JSON.getString(labourTypeQuestion.getLabelC()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    labourType.setOnItemSelectedListener((view, position, id, item) -> {

                        if (FARMING_ECO_PROFILE_ANSWERS_JSON.has(labourTypeQuestion.getLabelC()))
                            FARMING_ECO_PROFILE_ANSWERS_JSON.remove(labourTypeQuestion.getLabelC());
                        try {
                            FARMING_ECO_PROFILE_ANSWERS_JSON.put(labourTypeQuestion.getLabelC(), item.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ;


                    });

                } else
                    CustomToast.makeToast(this, "Labour Question is missing. Labour value won't be saved.", Toast.LENGTH_LONG).show();


                if (FARMER.getHasSubmitted().equalsIgnoreCase(AppConstants.YES) && FARMER.getSyncStatus() == 1) {
                    findViewById(R.id.save).setVisibility(View.GONE);
                    labourSpinner.setEnabled(false);
                    labourType.setEnabled(false);
                }


            }
        }

        save.setOnClickListener(v -> {

            if (farmingEcoProfileFormAnswerData != null) {
                farmingEcoProfileFormAnswerData.setData(FARMING_ECO_PROFILE_ANSWERS_JSON.toString());

                //mPresenter.saveAnswerData(farmingEcoProfileFormAnswerData);

                getAppDataManager().getDatabaseManager().formAnswerDao().insertOne(farmingEcoProfileFormAnswerData);

                finish();

            } else
                finish();

        });


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