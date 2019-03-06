package org.grameen.fdp.kasapin.ui.plotDetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.ui.AddEditFarmerPlot.AddEditFarmerPlotActivity;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.form.fragment.DynamicPlotFormFragment;
import org.grameen.fdp.kasapin.utilities.ActivityUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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


    String limeNeededValue = "--";
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
        if (PLOT != null)
            setupViews();
        else
            showMessage(R.string.generic_error);


       mPresenter.getPlotQuestions();

    }


    @Override
    public void showForm(List<FormAndQuestions> formAndQuestions) {

        BaseActivity.PLOT_FORM_AND_QUESTIONS = formAndQuestions;

        dynamicPlotFormFragment = DynamicPlotFormFragment.newInstance(true, PLOT.getFarmerCode(),
                true, PLOT.getAnswersData());

        ActivityUtils.loadDynamicView(getSupportFragmentManager(), dynamicPlotFormFragment, PLOT.getFarmerCode());


    }

    void setupViews(){

        //Todo get units and apply
        setToolbar(getStringResources(R.string.plot_info));

        plotName.setText(PLOT.getName());

        mPresenter.getAreaUnits(PLOT.getFarmerCode());

        ph.setText(PLOT.getPh());


        //Todo apply formula

        if (limeNeededValue.equalsIgnoreCase("yes") || limeNeededValue.equalsIgnoreCase("--")) {
            limeNeeded.setText(limeNeededValue);
            limeNeeded.setTextColor(ContextCompat.getColor(this, R.color.cpb_red));
        } else
            limeNeeded.setText("No");



        if(getAppDataManager().isMonitoring())
            editButton.setVisibility(View.GONE);


    }


    @Override
    public void setAreaUnits(String unit) {

        estimatedProductionSize.setText(PLOT.getEstimatedProductionSize() + " " + unit);

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