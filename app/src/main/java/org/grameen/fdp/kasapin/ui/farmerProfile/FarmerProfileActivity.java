package org.grameen.fdp.kasapin.ui.farmerProfile;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.parser.MathFormulaParser;
import org.grameen.fdp.kasapin.ui.AddEditFarmerPlot.AddEditFarmerPlotActivity;
import org.grameen.fdp.kasapin.ui.addFarmer.AddEditFarmerActivity;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.pandl.ProfitAndLossActivity;
import org.grameen.fdp.kasapin.ui.plotDetails.PlotDetailsActivity;
import org.grameen.fdp.kasapin.ui.viewImage.ImageViewActivity;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.ImageUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A login screen that offers login via email/password.
 */
public class FarmerProfileActivity extends BaseActivity implements FarmerProfileContract.View {

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


    RealFarmer FARMER;
    private PlotsListAdapter plotsListAdapter;

    int plotsSize = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_details);
        setUnBinder(ButterKnife.bind(this));


        getActivityComponent().inject(this);
        mPresenter.takeView(this);
        Toolbar toolbar = setToolbar(getStringResources(R.string.farmer_details));


        if (getAppDataManager().isMonitoring()) {
            //Todo add the rest of the views to hide
            edit.setVisibility(View.GONE);
            addPlot.setVisibility(View.GONE);

        } else
            farmAssessmentLayout.setVisibility(View.GONE);

        FARMER = new Gson().fromJson(getIntent().getStringExtra("farmer"), RealFarmer.class);


        if (FARMER != null)
            initializeViews(true);
        else
            showMessage(getStringResources(R.string.error_getting_farmer_info));


        CURRENT_FORM = 0;
    }


    @Override
    public void initializeViews(boolean shouldLoadButtons) {

        mPresenter.getFarmersPlots(FARMER.getCode());

        name.setText(FARMER.getFarmerName());
        code.setText(FARMER.getCode());
        villageName.setText(FARMER.getVillageName());
        landSize.setText(FARMER.getLandArea());

        if (FARMER.getSyncStatus() == 0) {
            syncIndicator.setImageResource(R.drawable.ic_sync_problem_black_24dp);
            syncIndicator.setColorFilter(ContextCompat.getColor(this, R.color.cpb_red));

        } else if (FARMER.getSyncStatus() == 1) {
            syncIndicator.setImageResource(R.drawable.ic_check_circle_black_24dp);
            syncIndicator.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
        }



        /*try{

            String farmAcre = ALL_FARMER_ANSWERS_JSON.getString(prefs.getString("totalLandSize", ""));
            String totalUnit = ALL_FARMER_ANSWERS_JSON.getString(prefs.getString("totalAreaUnit", ""));

            landArea.setText(farmAcre + " " + totalUnit);

        } catch (Exception ignored) {
            //e.printStackTrace();

            landArea.setVisibility(View.GONE);

        }*/


        lastSyncDate.setText((FARMER.getLastVisitDate() != null) ? FARMER.getLastVisitDate().toString() : "--");
        lastVisitDate.setText((FARMER.getLastModifiedDate() != null) ? FARMER.getLastModifiedDate().toString() : "--");


        if (FARMER.getImageUrl() != null && !FARMER.getImageUrl().equals("")) {

            circleImageView.setImageBitmap(ImageUtil.base64ToBitmap(FARMER.getImageUrl()));
            initials.setText("");

            //Picasso.with(this).load(farmer.getImageUrl()).resize(200, 200).into(circleImageView);

        } else {

            try {
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


        FILTERED_FORMS = new ArrayList<>();

        if(FORM_AND_QUESTIONS != null)
        for(FormAndQuestions formAndQuestions : FORM_AND_QUESTIONS){
            if(formAndQuestions.getForm().getDisplayTypeC().equalsIgnoreCase(AppConstants.DISPLAY_TYPE_FORM))
                FILTERED_FORMS.add(formAndQuestions);
        }


        mPresenter.loadDynamicButtons(FILTERED_FORMS);

        /*

        if (IS_MONITIRING_MODE && BuildConfig.DEBUG) {
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

        plotsSize = plots.size();

        AppLogger.i(TAG, "PLOT SIZE IS " +plotsSize);

        int plotsSize = plots.size();

        if(plotsSize > 0){

        noOfPlots.setText((plotsSize > 1) ? getStringResources(R.string.plot_aos) + "(" + plotsSize + ")" : getStringResources(R.string.plot_ao) + "(" + plotsSize + ")");


        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        plotsRecyclerView.setLayoutManager(horizontalLayoutManagaer);
               /* SpacesGridItemDecoration decoration = new SpacesGridItemDecoration(4);
            plotsRecyclerView.removeItemDecoration(decoration);
            plotsRecyclerView.addItemDecoration(decoration);*/


        plotsListAdapter = new PlotsListAdapter(this, plots);
        plotsListAdapter.setHasStableIds(true);

        plotsRecyclerView.setAdapter(plotsListAdapter);

        plotsListAdapter.setOnItemClickListener((view, position) -> {

            //Todo go to plot details

            if (!getAppDataManager().isMonitoring()) {
                Intent intent = new Intent(FarmerProfileActivity.this, PlotDetailsActivity.class);
                intent.putExtra("plot", getGson().toJson(plots.get(position)));
                intent.putExtra("plotSize", plotsSize);
                intent.putExtra("hasSubmitted", FARMER.getHasSubmitted());
                intent.putExtra("syncStatus", FARMER.getSyncStatus());

                startActivity(intent);

            } else {

               /* Intent intent = new Intent(FarmerProfileActivity.this, MonitoringYearSelectionActivity.class);
                intent.putExtra("farmer", getGson().toJson(FARMER));
                intent.putExtra("hasSubmitted", FARMER.getHasSubmitted());
                intent.putExtra("syncStatus", FARMER.getSyncStatus());
                intent.putExtra("plot", getGson().toJson(plots.get(position)));
                startActivity(intent);*/

            }

        });


        if (!getAppDataManager().isMonitoring())
            plotsListAdapter.OnLongClickListener((view, position) -> {

                final Plot plot = plots.get(position);

                showDialog(true, "Delete Plot Info", "Do you want to delete data for " + plot.getName() + "?", (dialogInterface, i) -> {

                    dialogInterface.dismiss();
                    mPresenter.deletePlot(plot);

                    //TODO DELETE monitoring for a plot
                    plots.remove(position);
                    plotsListAdapter.notifyItemRemoved(position);
                }, "YES", (dialogInterface, i) -> dialogInterface.dismiss(), "No", 0);
            });
    } else noOfPlots.setText("Plot Adoption Observations");





}



    @Override
    public void addButtons(List<Button> buttons) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        for (Button b:buttons) {
            runOnUiThread(() -> dynamicButtons.addView(b, params));

            b.setOnClickListener(v -> {

                CURRENT_FORM = (int) b.getTag();

                Log.i(TAG, "BUTTON CLICKED, CURRENT FORM IS " + CURRENT_FORM);

                Intent intent = new Intent(FarmerProfileActivity.this, AddEditFarmerActivity.class);
                intent.putExtra("farmer", getGson().toJson(FARMER));
                //intent.putExtra("formAndQuestions", getGson().toJson(FILTERED_FORMS.get(CURRENT_FORM)));
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
        if(getAppDataManager().getBooleanValue("reload")){

            mPresenter.getFarmersPlots(FARMER.getCode());

            getAppDataManager().setBooleanValue("reload", false);
        }
    }




    @Override
    public void openLoginActivityOnTokenExpire() {

    }

    @OnClick({R.id.photo, R.id.addPlot, R.id.review_page, R.id.pandl, R.id.farm_assessment, R.id.sync_farmer, R.id.historical_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.photo:

                    Intent intent = new Intent(FarmerProfileActivity.this, ImageViewActivity.class);
                    intent.putExtra("image_string", FARMER.getImageUrl());
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

              /*

                        intent = new Intent(FarmerDetailsActivity.this, PlotsReviewActivity.class);
                        intent.putExtra("farmerCode", FARMER.getId());
                        startActivity(intent);
              */

                break;
            case R.id.pandl:

                List<Plot> plots = getAppDataManager().getDatabaseManager().plotsDao().getFarmersPlots(FARMER.getCode()).blockingGet();
                if(plots != null && plots.size() > 0) {
                    if(!checkIfFarmSizeCorresponds(plots))
                        return;

                    else if(!checkIfCocoaProdCorresponds(plots))
                        return;

                    else {

                        for (Plot plot : plots) {
                            if (plot.getRecommendationId() == -1) {
                                showMessage(getStringResources(R.string.enter_all_ao_data) + plot.getName());
                                return;
                            }
                        }

                        intent = new Intent(this, ProfitAndLossActivity.class);
                        intent.putExtra("farmer", new Gson().toJson(FARMER));
                        startActivity(intent);

                    }

                } else
                    showDialog(true, getStringResources(R.string.no_plots), getStringResources(R.string.add_plot_to_access_pl),
                            (dialogInterface, i) -> dialogInterface.dismiss(), getStringResources(R.string.ok),
                            null, "", 0);


                break;
            case R.id.farm_assessment:


                break;
            case R.id.sync_farmer:


                break;



            case R.id.historical_view:

                //showSelectFormDialog();


                break;


        }
    }





    void openProfitAndLossActivity(){




    }




    boolean checkIfFarmSizeCorresponds(List<Plot> plots){

        boolean value = true;

        Double farmAcre;

        Question cocoaAreaQuestion = getAppDataManager().getDatabaseManager().questionDao().get("cocoa_area_").blockingGet();

        if(cocoaAreaQuestion != null) {

            FormAnswerData answer = getAppDataManager().getDatabaseManager().formAnswerDao().getFormAnswerData(FARMER.getCode(), cocoaAreaQuestion.getFormTranslationId()).blockingGet();
            if(answer != null){
                try {
                    farmAcre = round(Double.parseDouble(answer.getJsonData().get(cocoaAreaQuestion.getLabelC()).toString().replace(",", "")), 2);
                } catch (JSONException e) {
                    e.printStackTrace();
                    farmAcre = 0.0; }


                StringBuilder stringBuilder = new StringBuilder();
                // StringBuilder stringUnitBuilder = new StringBuilder();



                Double totalSizes = -1.0;

                if(plots != null)
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

            }else{
                showMessage(R.string.fill_in_plot_size_values);
                return false;
            }
        }else{

            showMessage(R.string.error_has_occurred);
            return false;
        }

    }


    boolean checkIfCocoaProdCorresponds(List<Plot> plots){

        boolean value = true;

        Double farmAcre;

        Question cocoaProdQuestion = getAppDataManager().getDatabaseManager().questionDao().get("cocoa_production_ly").blockingGet();

        if(cocoaProdQuestion != null) {

            FormAnswerData answer = getAppDataManager().getDatabaseManager().formAnswerDao().getFormAnswerData(FARMER.getCode(), cocoaProdQuestion.getFormTranslationId()).blockingGet();
            if(answer != null){
                try {
                    farmAcre = round(Double.parseDouble(answer.getJsonData().get(cocoaProdQuestion.getLabelC()).toString().replace(",", "")), 2);
                } catch (JSONException e) {
                    e.printStackTrace();
                    farmAcre = 0.0; }


                StringBuilder stringBuilder = new StringBuilder();
                // StringBuilder stringUnitBuilder = new StringBuilder();


                Double totalSizes = -1.0;

                if(plots != null)
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

            }else{
                showMessage(R.string.error_missing_estimated_production);
                return false;
            }
        }else{

            showMessage(R.string.error_has_occurred);
            return false;
        }

    }
}