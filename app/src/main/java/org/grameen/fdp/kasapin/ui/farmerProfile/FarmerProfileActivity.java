package org.grameen.fdp.kasapin.ui.farmerProfile;


import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Community;
import org.grameen.fdp.kasapin.data.db.entity.FarmResult;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Monitoring;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.PlotAssessment;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.parser.LogicFormulaParser;
import org.grameen.fdp.kasapin.parser.MathFormulaParser;
import org.grameen.fdp.kasapin.services.LocationPrepareService;
import org.grameen.fdp.kasapin.ui.AddEditFarmerPlot.AddEditFarmerPlotActivity;
import org.grameen.fdp.kasapin.ui.addFarmer.AddEditFarmerActivity;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.farmAssessment.FarmAssessmentActivity;
import org.grameen.fdp.kasapin.ui.monitoringYearSelection.MonitoringYearSelectionActivity;
import org.grameen.fdp.kasapin.ui.pandl.ProfitAndLossActivity;
import org.grameen.fdp.kasapin.ui.plotDetails.PlotDetailsActivity;
import org.grameen.fdp.kasapin.ui.plotReview.PlotReviewActivity;
import org.grameen.fdp.kasapin.ui.viewImage.ImageViewActivity;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.ImageUtil;
import org.grameen.fdp.kasapin.utilities.NetworkUtils;
import org.grameen.fdp.kasapin.utilities.TimeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class FarmerProfileActivity extends BaseActivity implements FarmerProfileContract.View {
    public static int familyMembersFormPosition = 0;
    @Inject
    FarmerProfilePresenter mPresenter;
    @BindView(R.id.photo)
    CircleImageView circleImageView;
    @BindView(R.id.initials)
    TextView initials;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.code)
    TextView code;
    @BindView(R.id.villageName)
    TextView villageName;
    @BindView(R.id.landSize)
    TextView landSize;
    @BindView(R.id.lastVisitDate)
    TextView lastVisitDate;
    @BindView(R.id.lastSyncDate)
    TextView lastSyncDate;
    @BindView(R.id.syncIndicator)
    ImageView syncIndicator;
    @BindView(R.id.syncIndicator1)
    View syncIndicator1;
    @BindView(R.id.farm_assessment_layout)
    View farmAssessmentLayout;
    @BindView(R.id.edit)
    View edit;
    @BindView(R.id.dynamicButtons)
    LinearLayout dynamicButtons;
    @BindView(R.id.noOfPlots)
    TextView noOfPlots;
    @BindView(R.id.plotsRecyclerView)
    RecyclerView plotsRecyclerView;
    @BindView(R.id.addPlot)
    TextView addPlot;
    @BindView(R.id.review_page)
    ImageView reviewPage;
    @BindView(R.id.pandl)
    ImageView pandl;
    @BindView(R.id.farm_assessment)
    ImageView farmAssessment;
    @BindView(R.id.sync_farmer)
    Button syncFarmer;
    List<Plot> PLOTS;
    List<PlotAssessment> PLOT_ASSESSMENTS;
    List<String> PLOT_ASSESSMENT_VALUES;
    JSONObject MONITORING_DATA_JSON;
    Farmer FARMER;
    int plotsSize = 0;
    int currentMonitoringYear = -1;
    private PlotsListAdapter plotsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_details);
        setUnBinder(ButterKnife.bind(this));

        getActivityComponent().inject(this);
        mPresenter.takeView(this);
        setToolbar(getString(R.string.farmer_details));


        if (getAppDataManager().isMonitoring()) {
            edit.setVisibility(View.GONE);
            addPlot.setVisibility(View.GONE);

        } else
            farmAssessmentLayout.setVisibility(View.GONE);


        mPresenter.getFarmer(getIntent()
                .getStringExtra("farmerCode"));

        CURRENT_FORM_POSITION = 0;
    }

    @Override
    public void initializeViews(boolean shouldLoadButtons, Farmer farmer) {
        FARMER = farmer;

        mPresenter.getFarmersPlots(FARMER.getCode());
        name.setText(FARMER.getFarmerName());
        code.setText(FARMER.getCode());

        if (FARMER.getVillageId() > 0) {
            Community village = getAppDataManager().getDatabaseManager().villagesDao().getVillageById(FARMER.getVillageId());
            if (village != null) {
                FARMER.setVillageName(village.getName());
                villageName.setText(FARMER.getVillageName());
            }
        }

        landSize.setText(FARMER.getLandArea());

        if (FARMER.getSyncStatus() == 0) {
            syncIndicator.setImageResource(R.drawable.ic_sync_problem_black_24dp);
            syncIndicator.setColorFilter(ContextCompat.getColor(this, R.color.cpb_red));

        } else if (FARMER.getSyncStatus() == 1) {
            syncIndicator.setImageResource(R.drawable.ic_check_circle_black_24dp);
            syncIndicator.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
        }

        lastSyncDate.setText((FARMER.getLastModifiedDate() != null) ? FARMER.getLastModifiedDate().toString() : "--");
        lastVisitDate.setText((FARMER.getLastVisitDate() != null) ? FARMER.getLastVisitDate().toString() : "--");


        if (FARMER.getImageBase64() != null && !FARMER.getImageBase64().equals("")) {
            circleImageView.setImageBitmap(ImageUtil.base64ToBitmap(FARMER.getImageBase64()));
            initials.setText("");
        } else {
            try {
                circleImageView.setImageBitmap(null);

                String[] valueArray = FARMER.getFarmerName().split(" ");
                String value = valueArray[0].substring(0, 1) + valueArray[1].substring(0, 1);
                initials.setText(value);

            } catch (Exception e) {
                initials.setText(FARMER.getFarmerName().substring(0, 1));
            }

            int[] mColors = getResources().getIntArray(R.array.recommendations_colors);

            int randomColor = mColors[new Random().nextInt(mColors.length)];

            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(1000);
            drawable.setColor(randomColor);
            circleImageView.setBackground(drawable);

            circleImageView.setOnClickListener(v -> showMessage("No image to display!"));
        }

        if (shouldLoadButtons) {
            FILTERED_FORMS = new ArrayList<>();
            mPresenter.loadDynamicButtons(FORM_AND_QUESTIONS);
        }

        /*
        if (IS_MONITORING_MODE && BuildConfig.DEBUG) {
            findViewById(R.id.historical_view).setVisibility(View.VISIBLE);

            findViewById(R.id.historical_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSelectFormDialog();
                }
            });
        }
        */
    }

    @Override
    public void setUpFarmersPlotsAdapter(List<Plot> plots) {
        PLOTS = plots;
        plotsSize = PLOTS.size();
        if (plotsSize > 0) {
            noOfPlots.setText((plotsSize > 1) ? getString(R.string.plot_aos) + "(" + plotsSize + ")" : getString(R.string.plot_ao) + "(" + plotsSize + ")");
            LinearLayoutManager horizontalLayoutManager
                    = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

            plotsRecyclerView.setLayoutManager(horizontalLayoutManager);
            plotsListAdapter = new PlotsListAdapter(this, PLOTS);
            plotsListAdapter.setHasStableIds(true);
            plotsRecyclerView.setAdapter(plotsListAdapter);
            plotsListAdapter.setOnItemClickListener((view, position) -> {

                if (!getAppDataManager().isMonitoring()) {
                    Intent intent = new Intent(FarmerProfileActivity.this, PlotDetailsActivity.class);
                    intent.putExtra("plot", getGson().toJson(PLOTS.get(position)));
                    intent.putExtra("plotSize", plotsSize);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(FarmerProfileActivity.this, MonitoringYearSelectionActivity.class);
                    intent.putExtra("farmerCode", FARMER.getCode());
                    intent.putExtra("plotExternalId", PLOTS.get(position).getExternalId());
                    startActivity(intent);
                }
            });

            if (!getAppDataManager().isMonitoring())
                plotsListAdapter.OnLongClickListener((view, position) -> showDialog(true, "Delete Plot Info", "Do you want to delete data for " + PLOTS.get(position).getName() + "?", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    mPresenter.deletePlot(PLOTS.get(position));

                    PLOTS.remove(position);
                    plotsListAdapter.notifyItemRemoved(position);
                }, "YES", (dialogInterface, i) -> dialogInterface.dismiss(), "No", 0));
        } else noOfPlots.setText(getString(R.string.plot_adoption_observations));
    }


    @Override
    public void updateFarmerSyncStatus() {
        FARMER.setSyncStatus(AppConstants.SYNC_OK);
        FARMER.setLastModifiedDate(TimeUtils.getDateTime());
        FARMER.setLastVisitDate(TimeUtils.getDateTime());
        mPresenter.updateFarmerData(FARMER);
        lastSyncDate.setText((FARMER.getLastModifiedDate() != null) ? FARMER.getLastModifiedDate().toString() : "--");
        syncIndicator.setImageResource(R.drawable.ic_check_circle_black_24dp);
        syncIndicator.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
    }

    @Override
    public void addButtons(List<Button> buttons) {
        runOnUiThread(() -> dynamicButtons.removeAllViews());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        for (Button b : buttons) {
            runOnUiThread(() -> dynamicButtons.addView(b, params));
            b.setOnClickListener(v -> {
                CURRENT_FORM_POSITION = (int) b.getTag();
                if (CURRENT_FORM_POSITION == familyMembersFormPosition) {
                    goToFamilyMembersTable(FARMER);
                    return;
                }
                Intent intent = new Intent(FarmerProfileActivity.this, AddEditFarmerActivity.class);
                intent.putExtra("farmerCode", FARMER.getCode());
                startActivity(intent);
            });
        }
        mPresenter.getFarmersPlots(FARMER.getCode());
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null)
            mPresenter.dropView();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getAppDataManager().getBooleanValue("reload") && FARMER != null) {
            mPresenter.getFarmer(FARMER.getCode());
        }

        //check if service is running
        if (isServiceRunning(LocationPrepareService.class))
            stopService(new Intent().setClass(getApplicationContext(), LocationPrepareService.class));
    }

    @Override
    public void showErrorAndExit(String errorMessage) {
        showMessage(errorMessage);
    }

    @Override
    public void openLoginActivityOnTokenExpire() {
    }

    @OnClick({R.id.photo, R.id.addPlot, R.id.review_page, R.id.pandl, R.id.farm_assessment, R.id.sync_farmer, R.id.historical_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.photo:
                Intent intent = new Intent(FarmerProfileActivity.this, ImageViewActivity.class);
                intent.putExtra("farmerCode", FARMER.getCode());
                startActivity(intent);
                break;
            case R.id.addPlot:

                //Todo go to add plot activity
                intent = new Intent(FarmerProfileActivity.this, AddEditFarmerPlotActivity.class);
                intent.putExtra("farmer", getGson().toJson(FARMER));
                intent.putExtra("plotSize", plotsSize);
                startActivity(intent);
                break;
            case R.id.review_page:
                intent = new Intent(FarmerProfileActivity.this, PlotReviewActivity.class);
                intent.putExtra("farmer", getGson().toJson(FARMER));
                startActivity(intent);
                break;

            case R.id.pandl:
                if (PLOTS != null && PLOTS.size() > 0) {
                    if (!checkIfFarmSizeCorresponds(PLOTS))
                        return;
                    if (!checkIfCocoaProdCorresponds(PLOTS))
                        return;

                    for (Plot plot : PLOTS) {
                        if (plot.getRecommendationId() == -1) {
                            showMessage(getString(R.string.enter_all_ao_data) + plot.getName());
                            return;
                        }
                    }
                    intent = new Intent(this, ProfitAndLossActivity.class);
                    intent.putExtra("farmerCode", FARMER.getCode());
                    startActivity(intent);
                } else
                    showDialog(true, getString(R.string.no_plots), getString(R.string.add_plot_to_access_pl),
                            (dialogInterface, i) -> dialogInterface.dismiss(), getString(R.string.ok),
                            null, "", 0);
                break;
            case R.id.farm_assessment:
                PLOT_ASSESSMENTS = new ArrayList<>();
                PLOT_ASSESSMENT_VALUES = new ArrayList<>();

                LogicFormulaParser logicFormulaParser = LogicFormulaParser.getInstance();
                MathFormulaParser mathFormulaParser = MathFormulaParser.getInstance();
                FormAndQuestions farmResultsFormAndQuestions = getAppDataManager().getDatabaseManager()
                        .formAndQuestionsDao().maybeGetFormAndQuestionsByName("Farm results").blockingGet();
                AppLogger.e(TAG, "Farm Results Questions >>> " + getGson().toJson(farmResultsFormAndQuestions));

                if (checkIfAllPlotsHaveAssessments(PLOTS) && farmResultsFormAndQuestions != null) {

                    JSONObject valuesJsonObject = new JSONObject();
                    Question farmResultsQuestion = null;
                    //Get farm results questions
                    //Loop through the farm results questions
                    //Loop through each plot
                    //Get value for question for each plot and store

                    for (Question question : farmResultsFormAndQuestions.getQuestions()) {
                        if (question.getLabelC().startsWith("farm_result_")) {
                            farmResultsQuestion = question;
                            continue;
                        }

                        AppLogger.e(TAG, "********************************************************");
                        AppLogger.e(TAG, "EVALUATING QUESTION WITH LABEL " + question.getLabelC() + " AND TYPE " + question.getTypeC());
                        AppLogger.e(TAG, "FORMULA == " + question.getFormulaC());

                        StringBuilder value = new StringBuilder("0");

                        for (Plot plot : PLOTS) {
                            switch (question.getTypeC().toLowerCase()) {
                                case AppConstants.FORMULA_TYPE_COMPLEX_FORMULA:
                                    value = new StringBuilder(parseCollectionsFormula(question.getFormulaC(), PLOT_ASSESSMENT_VALUES));
                                    break;
                                case AppConstants.TYPE_MATH_FORMULA:
                                    mathFormulaParser.setJsonObject(valuesJsonObject);
                                    mathFormulaParser.setMathFormula(question.getFormulaC());
                                    value = new StringBuilder(mathFormulaParser.evaluate());
                                    break;
                            }
                            value.append("+");
                        }
                        try {
                            valuesJsonObject.put(question.getLabelC(), mathFormulaParser.evaluate(value.toString() + "0"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mathFormulaParser.setJsonObject(valuesJsonObject);
                        logicFormulaParser.setJsonObject(valuesJsonObject);
                    }
                    try {
                        String farmResultsValue = logicFormulaParser.evaluate(farmResultsQuestion.getFormulaC());
                        FarmResult farmResult = new FarmResult();
                        farmResult.setCaption(getString(R.string.farm_result));

                        farmResult.setStatus(farmResultsValue);
                        farmResult.setPlotAssessmentList(PLOT_ASSESSMENTS);

                        intent = new Intent(FarmerProfileActivity.this, FarmAssessmentActivity.class);
                        intent.putExtra("farmResults", getGson().toJson(farmResult));
                        intent.putExtra("farmer", new Gson().toJson(FARMER));
                        startActivity(intent);
                    } catch (Exception e) {
                        showMessage("Could not obtain farm results question.");
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.sync_farmer:
                if(checkIfHasUnsavedFarmerMember()){
                    showMessage("This farmer has unsaved data in family members. Please finalize by " +
                            "pressing save in Family Members form.");
                    return;
                }
                if (FARMER.getSyncStatus() == AppConstants.SYNC_OK) {
                    showMessage(R.string.no_new_data);
                    return;
                }
                if (!NetworkUtils.isNetworkConnected(this)) {
                    showMessage(R.string.no_internet_connection_available);
                    return;
                }
                mPresenter.syncFarmerData(FARMER, true);
                break;

            case R.id.historical_view:
                //showSelectFormDialog();
                break;
        }
    }

    boolean checkIfHasUnsavedFarmerMember(){
        return getAppDataManager().getDatabaseManager().shadowDataDao().getShadowDataForFarmer(FARMER.getCode()) != null;
    }

    boolean checkIfFarmSizeCorresponds(List<Plot> plots) {
        boolean value = true;
        double farmAcre;
        Question cocoaAreaQuestion = getAppDataManager().getDatabaseManager().questionDao().get("cocoa_area_");

        if (cocoaAreaQuestion != null) {
            FormAnswerData answer = getAppDataManager().getDatabaseManager().formAnswerDao().getFormAnswerData(FARMER.getCode(), cocoaAreaQuestion.getFormTranslationId());
            if (answer != null) {
                try {
                    farmAcre = round(Double.parseDouble(answer.getJsonData().get(cocoaAreaQuestion.getLabelC()).toString().replace(",", "")), 2);
                } catch (Exception e) {
                    e.printStackTrace();
                    farmAcre = 0.0;
                }

                StringBuilder stringBuilder = new StringBuilder();
                double totalSizes;
                if (plots != null)
                    for (Plot plot : plots) {
                        try {
                            AppLogger.i(TAG, "******* PLOT NAME " + plot.getName() + " AND DATA IS " + plot.getAnswersData());
                            stringBuilder.append(plot.getArea()).append("+");
                        } catch (Exception e) {
                            e.printStackTrace();
                            stringBuilder.append("0").append("+");
                        }
                    }
                stringBuilder.append("0");
                totalSizes = round(Double.parseDouble(MathFormulaParser.getInstance().evaluate(stringBuilder.toString())), 2);
                AppLogger.i(TAG, "$$$$$$$$$$$$$    TOTAL SIZES " + farmAcre + " AND TOTAL PLOTS SIZES " + totalSizes);

                if (totalSizes > farmAcre || totalSizes < farmAcre) {
                    value = false;
                    showMessage(R.string.ensure_farm_acre_equal);
                }
                return value;

            } else {
                showMessage(R.string.fill_in_plot_size_values);
                return false;
            }
        } else {
            showMessage(R.string.error_has_occurred);
            return false;
        }
    }

    boolean checkIfCocoaProdCorresponds(List<Plot> plots) {
        boolean value = true;
        double farmAcre;
        Question cocoaProdQuestion = getAppDataManager().getDatabaseManager().questionDao().get("cocoa_production_ly");

        if (cocoaProdQuestion != null) {
            FormAnswerData answer = getAppDataManager().getDatabaseManager().formAnswerDao().getFormAnswerData(FARMER.getCode(), cocoaProdQuestion.getFormTranslationId());
            if (answer != null) {
                try {
                    farmAcre = round(Double.parseDouble(answer.getJsonData().get(cocoaProdQuestion.getLabelC()).toString().replace(",", "")), 2);
                } catch (JSONException e) {
                    e.printStackTrace();
                    farmAcre = 0.0;
                }

                StringBuilder stringBuilder = new StringBuilder();
                double totalSizes;
                if (plots != null)
                    for (Plot plot : plots) {
                        try {
                            AppLogger.i(TAG, "******* PLOT NAME " + plot.getName() + " AND DATA IS " + plot.getAnswersData());
                            stringBuilder.append(plot.getEstimatedProductionSize()).append("+");
                        } catch (Exception e) {
                            e.printStackTrace();
                            stringBuilder.append("0").append("+");
                        }
                    }
                stringBuilder.append("0");

                totalSizes = round(Double.parseDouble(MathFormulaParser.getInstance().evaluate(stringBuilder.toString())), 2);
                AppLogger.i(TAG, "$$$$$$$$$$$$$    TOTAL FARM PROD " + farmAcre + " AND TOTAL PLOTS PROD SIZES " + totalSizes);

                if (totalSizes > farmAcre || totalSizes < farmAcre) {
                    value = false;
                    showMessage(R.string.error_total_plot_estimated_production);
                }
                return value;
            } else {
                showMessage(R.string.error_missing_estimated_production);
                return false;
            }
        } else {
            showMessage(R.string.error_has_occurred);
            return false;
        }
    }

    boolean checkIfAllPlotsHaveAssessments(List<Plot> plots) {
        MONITORING_DATA_JSON = new JSONObject();
        checkIfAllPlotsHaveSameNumberOfMonitoring(plots);

        //Get the plot assessment question from the DB
        Question plotAssessmentQuestion = getAppDataManager().getDatabaseManager().questionDao().get("monitoring_plot_assessment_");
        Question passPlotsQuestion = getAppDataManager().getDatabaseManager().questionDao().get("pass_plots_");

        if (plotAssessmentQuestion != null) {
            boolean useLastMonitoring = passPlotsQuestion != null && passPlotsQuestion.getFormulaC().contains("_Last");

            for (Plot p : plots) {
                Monitoring monitoring = (useLastMonitoring)
                        ? getAppDataManager().getDatabaseManager().monitoringDao().getLastMonitoringForSelectedYear(p.getExternalId(), currentMonitoringYear)
                        : getAppDataManager().getDatabaseManager().monitoringDao().getFirstMonitoringForSelectedYear(p.getExternalId(), currentMonitoringYear);
                AppLogger.e(TAG, "MONITORING >>> " + getGson().toJson(monitoring));

                try {
                    String assessment = AppConstants.NO_MONITORING_PLACE_HOLDER;
                    if (monitoring != null)
                        assessment = monitoring.getMonitoringAOJsonData().getString(plotAssessmentQuestion.getLabelC());
                    if (assessment.contains(AppConstants.NO_MONITORING_PLACE_HOLDER)) {
                        showMessage(p.getName() + "\n" + getString(R.string.incomplete_monitoring_prefix) + currentMonitoringYear + getString(R.string.incomplete_monitoring_suffix));
                        return false;
                    }
                    PLOT_ASSESSMENTS.add(new PlotAssessment(p.getName(), assessment));
                    PLOT_ASSESSMENT_VALUES.add(assessment.trim());

                    //Save the monitoring assessment data per plot for later use in the parsers
                    MONITORING_DATA_JSON.put(p.getExternalId(), assessment.trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                    showMessage(p.getName() + "\n" + getString(R.string.incomplete_monitoring_prefix) + currentMonitoringYear + getString(R.string.incomplete_monitoring_suffix));
                    break;
                }
            }
            return true;
        } else
            showMessage("Could no obtain Plot Assessment Question");
        return false;
    }

    void checkIfAllPlotsHaveSameNumberOfMonitoring(List<Plot> plots) {

        List<Integer> numberOfMonitoringsPerPlot = new ArrayList<>();
        int noOfPlots;
        int maxValueOfMonitoring;
        try {
            currentMonitoringYear = Integer.parseInt(getAppDataManager().getStringValue(FARMER.getCode()));
        } catch (Exception ignore) {
        }

        AppLogger.e(TAG, "CURRENT MONITORING YEAR == " + currentMonitoringYear);

        if (plots != null && plots.size() > 0) {
            noOfPlots = plots.size();
            for (Plot plot : plots) {
                //Check number of monitoring for each plot for the current monitoring year
                numberOfMonitoringsPerPlot.add(getAppDataManager().getDatabaseManager().monitoringDao()
                        .countMonitoringForSelectedYear(plot.getExternalId(), currentMonitoringYear)
                        .blockingGet(0));
            }

            maxValueOfMonitoring = Collections.max(numberOfMonitoringsPerPlot);

            //After finding the mam number of monitoring for the list of plots, check to see if the number of times the max value == number of plots
            int numberOfMaximumMonitoringOccurred = Collections.frequency(numberOfMonitoringsPerPlot, maxValueOfMonitoring);

            if (!Objects.equals(numberOfMaximumMonitoringOccurred, noOfPlots)) {
                showMessage(getString(R.string.monitoring_for_year) + currentMonitoringYear + getString(R.string.for_) + FARMER.getFarmerName() + getResources() + getString(R.string.incorrect_no_plots_suffix));
                return;
            }
            //Check to make sure a plot has at least 1 monitoring for selected year
            for (int j = 0; j < numberOfMonitoringsPerPlot.size(); j++) {
                if (numberOfMonitoringsPerPlot.get(j) == 0) {
                    showMessage(getString(R.string.farmer) + FARMER.getFarmerName() + getString(R.string.no_monitoring_added) + currentMonitoringYear + "of plot " + plots.get(j).getName() + " " + getString(R.string.please_add_monitoring_suffix));
                    return;
                }
            }
        } else
            showMessage(getString(R.string.farmer) + FARMER.getFarmerName() + getString(R.string.no_plots_added_suffix));
    }

    String parseCollectionsFormula(String formula, List<String> plotAssessmentValues) {
        int value;
        try {
            String parsedEquation = formula.split(",")[1];
            parsedEquation = parsedEquation.split(Pattern.quote(")"))[0];
            parsedEquation = parsedEquation.replace("\"", "");
            value = Collections.frequency(plotAssessmentValues, parsedEquation.trim());
            return String.valueOf(value);
        } catch (Exception ignored) {
            return "0";
        }
    }

}