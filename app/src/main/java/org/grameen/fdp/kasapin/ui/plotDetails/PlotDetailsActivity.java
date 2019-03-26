package org.grameen.fdp.kasapin.ui.plotDetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Recommendation;
import org.grameen.fdp.kasapin.parser.LogicFormulaParser;
import org.grameen.fdp.kasapin.ui.AddEditFarmerPlot.AddEditFarmerPlotActivity;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.form.fragment.DynamicPlotFormFragment;
import org.grameen.fdp.kasapin.utilities.ActivityUtils;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * A login screen that offers login via email/password.
 */
public class PlotDetailsActivity extends BaseActivity implements PlotDetailsContract.View {

    @Inject
    PlotDetailsPresenter mPresenter;
    Plot PLOT;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.plotName)
    TextView plotName;
    @BindView(R.id.landSize)
    TextView landSize;
    @BindView(R.id.plot_est_prod_text)
    TextView plotEstProdText;
    @BindView(R.id.estimatedProductionSize)
    TextView estimatedProductionSize;
    @BindView(R.id.recommendationProgress)
    ProgressBar recommendationProgress;
    @BindView(R.id.recommended_intervention)
    TextView recommendedIntervention;
    @BindView(R.id.plot_ph_text)
    TextView plotPhText;
    @BindView(R.id.ph)
    TextView ph;
    @BindView(R.id.lime_needed)
    TextView limeNeeded;
    @BindView(R.id.aos)
    TextView aos;
    @BindView(R.id.good)
    TextView good;
    @BindView(R.id.bad)
    TextView bad;
    @BindView(R.id.editButton)
    Button editButton;

    JSONObject PLOT_ANSWERS_JSON;
    //String limeNeededValue = "--";
    private DynamicPlotFormFragment dynamicPlotFormFragment;

    Recommendation PLOT_RECOMMENDATION = null;
    Recommendation GAPS_RECOMENDATION_FOR_START_YEAR = null;
    String recNames;


    public static Intent getStartIntent(Context context) {
        return new Intent(context, PlotDetailsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_plot_v2);
        setUnBinder(ButterKnife.bind(this));


        getActivityComponent().inject(this);
        mPresenter.takeView(this);
        mAppDataManager = mPresenter.getAppDataManager();


        PLOT = getGson().fromJson(getIntent().getStringExtra("plot"), Plot.class);
        if (PLOT != null) {

            setupViews();
        }
        else {
            showMessage(R.string.error_has_occurred);
            finish();
        }



    }


    @Override
    public void showForm(List<FormAndQuestions> formAndQuestions) {

        BaseActivity.PLOT_FORM_AND_QUESTIONS = formAndQuestions;

        dynamicPlotFormFragment = DynamicPlotFormFragment.newInstance(true, PLOT.getFarmerCode(),
                true, PLOT.getAnswersData());

        ActivityUtils.loadDynamicView(getSupportFragmentManager(), dynamicPlotFormFragment, PLOT.getFarmerCode());

//        mPresenter.getAreaUnits(PLOT.getFarmerCode());
        checkRecommendation();
        //


    }

    void setupViews(){

        //Todo get units and apply
        setToolbar(getStringResources(R.string.plot_info));

        plotName.setText(PLOT.getName());



        ph.setText(PLOT.getPh());

        //Get values

        try {
             PLOT_ANSWERS_JSON = PLOT.getAOJsonData();
        } catch (JSONException e) {
            e.printStackTrace();
            PLOT_ANSWERS_JSON = new JSONObject();
        }


        Iterator iterator = PLOT_ANSWERS_JSON.keys();
        while (iterator.hasNext()){
            String tmp_key = (String) iterator.next();
            if(tmp_key.toLowerCase().contains("lime")){
                try {
                    limeNeeded.setText(PLOT_ANSWERS_JSON.getString(tmp_key));
                    limeNeeded.setTextColor(ContextCompat.getColor(this, R.color.cpb_red));
                    break;
                } catch (JSONException ignored) {}
            }
        }


        if(getAppDataManager().isMonitoring())
            editButton.setVisibility(View.GONE);

        mPresenter.getPlotQuestions();


    }


    @Override
    public void setAreaUnits(String unit) {

        estimatedProductionSize.setText(PLOT.getEstimatedProductionSize() + " " + unit);

    }


    void checkRecommendation(){


        if(PLOT.getRecommendationId() != -1){
            String recommendationLabel = getAppDataManager().getDatabaseManager().recommendationsDao().getLabel(PLOT.getRecommendationId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .blockingGet();
            recommendedIntervention.setText(recommendationLabel);
            recommendedIntervention.setTextColor(ContextCompat.getColor(PlotDetailsActivity.this, R.color.colorAccent));
            return;
        }

        if(PLOT.getAnswersData() != null && PLOT.getAnswersData().contains("--")){
            recommendedIntervention.setText(R.string.fill_out_ao_data);
            recommendedIntervention.setTextColor(ContextCompat.getColor(PlotDetailsActivity.this, R.color.cpb_red));
            return;
        }


        recommendedIntervention.setText("");
        recommendationProgress.setVisibility(View.VISIBLE);

        mPresenter.getRecommendations(1);


    }


    @Override
    public void loadRecommendation(List<Recommendation> recommendations) {

        LogicFormulaParser logicFormulaParser = LogicFormulaParser.getInstance();
        logicFormulaParser.setJsonObject(PLOT_ANSWERS_JSON);




        AppLogger.i("" + TAG, "############    REAPPLYING LOGIC TO RECOMMENDATION    ##################");

        for(Recommendation recommendation : recommendations){
            AppLogger.e( TAG, "---------   RECOMMENDATION NAME >>  " + recommendation.getLabel() + "   ---------");
            AppLogger.e( TAG, "---------   HIERARCHY >>  " + recommendation.getHierarchy() + "   ---------");


                if(recommendation.getHierarchy() != 0 && recommendation.getCondition() != null && !recommendation.getCondition().equalsIgnoreCase("null")){

                        try {

                            String value = logicFormulaParser.evaluate(recommendation.getCondition());
                            if (value.equalsIgnoreCase("true")) {


                                if (recommendation.getLabel().equalsIgnoreCase("replant") || recommendation.getLabel().equalsIgnoreCase("replant + extra soil"))
                                    GAPS_RECOMENDATION_FOR_START_YEAR = getAppDataManager().getDatabaseManager().recommendationsDao()
                                            .getLabel("Minimal GAPs").blockingGet();

                                else if (recommendation.getLabel().equalsIgnoreCase("grafting") || recommendation.getLabel().equalsIgnoreCase("grafting + extra soil"))
                                    GAPS_RECOMENDATION_FOR_START_YEAR = getAppDataManager().getDatabaseManager().recommendationsDao()
                                            .getLabel("Modest GAPs").blockingGet();
                                else
                                    GAPS_RECOMENDATION_FOR_START_YEAR = getAppDataManager().getDatabaseManager().recommendationsDao()
                                            .getLabel("Maintenance (GAPs)").blockingGet();

                                PLOT_RECOMMENDATION = recommendation;

                                break;
                            }else
                                GAPS_RECOMENDATION_FOR_START_YEAR = getAppDataManager().getDatabaseManager().recommendationsDao()
                                        .getLabel("Maintenance (GAPs)").blockingGet();

                        }catch(Exception e){e.printStackTrace();
                        }
                    }


            AppLogger.e(TAG, "---------------------------------------------------------------------------");
            AppLogger.e(TAG, "---------------------------------------------------------------------------");
        }

        AppLogger.e( TAG, "---------   RECOMMENDATION >>  " + getGson().toJson(PLOT_RECOMMENDATION));
        AppLogger.e( TAG, "---------   GAPS RECOMMENDATION >>  " + getGson().toJson(GAPS_RECOMENDATION_FOR_START_YEAR));




        if(PLOT_RECOMMENDATION == null)
        PLOT_RECOMMENDATION = GAPS_RECOMENDATION_FOR_START_YEAR;

        if(PLOT_RECOMMENDATION != null) {

            recNames = PLOT_RECOMMENDATION.getLabel();

            PLOT.setRecommendationId(PLOT_RECOMMENDATION.getId());
            if(GAPS_RECOMENDATION_FOR_START_YEAR != null)
            PLOT.setGapsId(GAPS_RECOMENDATION_FOR_START_YEAR.getId());

            mPresenter.saveData(PLOT);
        }
    }

    @Override
    public void showRecommendation() {

        recommendationProgress.setVisibility(View.GONE);
        recommendedIntervention.setText(recNames);
        recommendedIntervention.setTextColor(ContextCompat.getColor(PlotDetailsActivity.this, R.color.colorAccent));

    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null)
            mPresenter.dropView();
        super.onDestroy();
    }


    @Override
    public void openNextActivity() {


    }

    @Override
    public void openLoginActivityOnTokenExpire() {

    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onBackClicked(View v) {
        finish();
    }

    @OnClick(R.id.editButton)
    public void onViewClicked() {
        Intent intent = new Intent(PlotDetailsActivity.this, AddEditFarmerPlotActivity.class);
        intent.putExtra("flag", "edit");
        intent.putExtra("plot", getGson().toJson(PLOT));
        startActivity(intent);
        finish();
    }
}