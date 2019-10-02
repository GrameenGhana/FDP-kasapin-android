package org.grameen.fdp.kasapin.ui.plotDetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Recommendation;
import org.grameen.fdp.kasapin.parser.LogicFormulaParser;
import org.grameen.fdp.kasapin.ui.AddEditFarmerPlot.AddEditFarmerPlotActivity;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.form.fragment.DynamicPlotFormFragment;
import org.grameen.fdp.kasapin.utilities.ActivityUtils;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
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
    @BindView(R.id.recommended_intervention_text)
    TextView recommendedInterventionText;
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
    @BindView(R.id.lime_needed_text)
    TextView limeNeededText;



    JSONObject PLOT_ANSWERS_JSON;
    Recommendation PLOT_RECOMMENDATION = null;
    Recommendation GAPS_RECOMENDATION_FOR_START_YEAR = null;
    String recNames;

    //String limeNeededValue = "--";
    private DynamicPlotFormFragment dynamicPlotFormFragment;

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
            AppLogger.e(TAG, "PLOT >>> " + getGson().toJson(PLOT));
            setupViews();
        } else {
            showMessage(R.string.error_has_occurred);
            finish();
        }
    }


    @Override
    public void showForm(List<FormAndQuestions> formAndQuestions) {

        //Usually, all answers pertaining to Form of type *Plot Form* are bundled as a JSON and saved in a the answerData field on the Plot object
        //So when obtaining the answers for the plot form questions, they're all obtained from the answerData of Plot object
        // On sync down from the server, some of the answers to the Plot Form questions are separated as FormAnswerData object and saved in the DB with their corresponding form_translation_ids
        //So here, we just check those answers of Plot Form questions which are separated as FormAnswerData objects, iterate through them and add them back to the answer field of Plot object
        //If the Plot hasn't been synced yet, FormAnswerData will be null for all forms of type Plot Form.
        try {
            JSONObject plotAnswersDataJson = PLOT.getAOJsonData();

            BaseActivity.PLOT_FORM_AND_QUESTIONS = formAndQuestions;

            for(FormAndQuestions formAndQuestions1 : BaseActivity.PLOT_FORM_AND_QUESTIONS){
                FormAnswerData formAnswerData = getAppDataManager().getDatabaseManager().formAnswerDao().getFormAnswerData(PLOT.getFarmerCode(), formAndQuestions1.getForm().getFormTranslationId());

                if(formAnswerData != null){
                    Iterator iterator = formAnswerData.getJsonData().keys();

                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        try {
                            if (!plotAnswersDataJson.has(key))
                            plotAnswersDataJson.put(key, formAnswerData.getJsonData().get(key));
                        } catch (JSONException ignored) {
                        }
                    }
                }
            }

            PLOT.setAnswersData(plotAnswersDataJson.toString());
        } catch (JSONException ignored) {}



        dynamicPlotFormFragment = DynamicPlotFormFragment.newInstance(true, PLOT.getFarmerCode(),
                true, PLOT.getAnswersData());

        ActivityUtils.loadDynamicView(getSupportFragmentManager(), dynamicPlotFormFragment, PLOT.getFarmerCode());

        //mPresenter.getAreaUnits(PLOT.getFarmerCode());
        checkRecommendation();
        //


    }

    void setupViews() {
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
        while (iterator.hasNext()) {
            String tmp_key = (String) iterator.next();
            if (tmp_key.toLowerCase().contains("lime")) {
                try {
                    limeNeeded.setText(PLOT_ANSWERS_JSON.getString(tmp_key));
                    limeNeeded.setTextColor(ContextCompat.getColor(this, R.color.cpb_red));
                    break;
                } catch (JSONException ignored) {
                }
            }
        }


        if (getAppDataManager().isMonitoring())
            editButton.setVisibility(View.GONE);

        mPresenter.getPlotQuestions();

        //SetCaptionLabels
        Completable.fromAction(() -> {

            String estimatedProductionSizeCaption = getAppDataManager().getDatabaseManager().questionDao().getCaption("plot_estimate_production_");
            if (!TextUtils.isEmpty(estimatedProductionSizeCaption))
                plotEstProdText.setText(estimatedProductionSizeCaption);

            String recommendedInterventionCaption = getAppDataManager().getDatabaseManager().questionDao().getCaption("plot_recommendation_");
            if (!TextUtils.isEmpty(recommendedInterventionCaption))
                recommendedInterventionText.setText(recommendedInterventionCaption);


            String soilPhCaption = getAppDataManager().getDatabaseManager().questionDao().getCaption("plot_ph_");
            if (!TextUtils.isEmpty(soilPhCaption))
                plotPhText.setText(soilPhCaption);


            String limeNeededCaption = getAppDataManager().getDatabaseManager().questionDao().getCaption("lime_");
            if (!TextUtils.isEmpty(limeNeededCaption))
                limeNeededText.setText(limeNeededCaption);

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        mPresenter.getAreaUnits(PLOT.getFarmerCode());
                    }

                    @Override
                    public void onError(Throwable ignored) {
                    }
                });


    }


    @Override
    public void setAreaUnits(String unit) {

        landSize.setText(PLOT.getArea() + " " + unit);
    }

    @Override
    public void setProductionUnit(String unit) {
        estimatedProductionSize.setText(PLOT.getEstimatedProductionSize() + " " + unit);
    }

    void checkRecommendation() {
        AppLogger.e(TAG, "#########   RECOMMENDATION ID IS " + PLOT.getRecommendationId());

      /*  if (PLOT.getAnswersData() != null && PLOT.getAnswersData().contains("--")) {
            recommendedIntervention.setText(R.string.fill_out_ao_data);
            recommendedIntervention.setTextColor(ContextCompat.getColor(PlotDetailsActivity.this, R.color.cpb_red));
            return;
        }*/

        if (!getAppDataManager().getBooleanValue("reloadRecommendation")) {

            getAppDataManager().getCompositeDisposable().add(getAppDataManager().getDatabaseManager().recommendationsDao().getByRecommendationId(PLOT.getRecommendationId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(s -> {
                        recommendedIntervention.setText(s);
                        recommendedIntervention.setTextColor((s.equalsIgnoreCase(AppConstants.RECOMMENDATION_NO_FDP)
                                ? ContextCompat.getColor(PlotDetailsActivity.this, R.color.cpb_red)
                                : ContextCompat.getColor(PlotDetailsActivity.this, R.color.colorAccent)));


                    }, throwable -> showMessage("Couldn't load plot's recommendation")));
        }else {
            AppLogger.e(TAG, "********************   RELOAD RECOMMENDATION ******************** ");

            recommendedIntervention.setText("");
            recommendationProgress.setVisibility(View.VISIBLE);
            mPresenter.getRecommendations(1);
        }
    }


    @Override
    public void loadRecommendation(List<Recommendation> recommendations) {
        LogicFormulaParser logicFormulaParser = LogicFormulaParser.getInstance();
        logicFormulaParser.setJsonObject(PLOT_ANSWERS_JSON);


        AppLogger.i("" + TAG, "############    REAPPLYING LOGIC TO RECOMMENDATION    ##################");

        for (Recommendation recommendation : recommendations) {
            AppLogger.e(TAG, "---------   RECOMMENDATION NAME IN ENGLISH >>  " + recommendation.getRecommendationName() + "   ---------");
            AppLogger.e(TAG, "---------   HIERARCHY >>  " + recommendation.getHierarchy() + "   ---------");


            if (recommendation.getHierarchy() != 0 && recommendation.getCondition() != null && !recommendation.getCondition().equalsIgnoreCase("null")) {

                try {
                    String value = logicFormulaParser.evaluate(recommendation.getCondition());
                    if (value.trim().equalsIgnoreCase("true")) {

                        if (recommendation.getRecommendationName().equalsIgnoreCase("replant") || recommendation.getRecommendationName().equalsIgnoreCase("replant + extra soil"))
                            GAPS_RECOMENDATION_FOR_START_YEAR = getAppDataManager().getDatabaseManager().recommendationsDao()
                                    .getByRecommendationName("Minimal GAPs").blockingGet();

                        else if (recommendation.getRecommendationName().equalsIgnoreCase("grafting") || recommendation.getRecommendationName().equalsIgnoreCase("grafting + extra soil"))
                            GAPS_RECOMENDATION_FOR_START_YEAR = getAppDataManager().getDatabaseManager().recommendationsDao()
                                    .getByRecommendationName("Modest GAPs").blockingGet();
                        else
                            GAPS_RECOMENDATION_FOR_START_YEAR = getAppDataManager().getDatabaseManager().recommendationsDao()
                                    .getByRecommendationName("Maintenance (GAPs)").blockingGet();

                        PLOT_RECOMMENDATION = recommendation;

                        break;
                    } else
                        GAPS_RECOMENDATION_FOR_START_YEAR = getAppDataManager().getDatabaseManager().recommendationsDao()
                                .getByRecommendationName("Maintenance (GAPs)").blockingGet();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            AppLogger.e(TAG, "---------------------------------------------------------------------------");
            AppLogger.e(TAG, "---------------------------------------------------------------------------");

        }

        if (PLOT_RECOMMENDATION == null)
            PLOT_RECOMMENDATION = GAPS_RECOMENDATION_FOR_START_YEAR;

        if (PLOT_RECOMMENDATION != null) {
            recNames = PLOT_RECOMMENDATION.getLabel();

            if (GAPS_RECOMENDATION_FOR_START_YEAR != null)
                PLOT.setGapsId(GAPS_RECOMENDATION_FOR_START_YEAR.getId());

            PLOT.setRecommendationId(PLOT_RECOMMENDATION.getId());

            mPresenter.saveData(PLOT);

        }
    }

    @Override
    public void showRecommendation() {

        recommendationProgress.setVisibility(View.GONE);
        recommendedIntervention.setText(recNames);

        recommendedIntervention.setTextColor((recNames.equalsIgnoreCase(AppConstants.RECOMMENDATION_NO_FDP)
                ? ContextCompat.getColor(PlotDetailsActivity.this, R.color.cpb_red)
                : ContextCompat.getColor(PlotDetailsActivity.this, R.color.colorAccent)));

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