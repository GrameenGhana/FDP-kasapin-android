package org.grameen.fdp.kasapin.ui.AddEditFarmerPlot;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.form.fragment.DynamicPlotFormFragment;
import org.grameen.fdp.kasapin.ui.map.MapActivity;
import org.grameen.fdp.kasapin.ui.plotDetails.PlotDetailsActivity;
import org.grameen.fdp.kasapin.utilities.ActivityUtils;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.TimeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * A login screen that offers login via email/password.
 */
public class AddEditFarmerPlotActivity extends BaseActivity implements AddEditFarmerPlotContract.View {

    @Inject
    AddEditFarmerPlotPresenter mPresenter;
    @BindView(R.id.plotNameEdittext)
    EditText plotNameEdittext;
    @BindView(R.id.plotSizeEdittext)
    EditText plotSizeEdittext;
    @BindView(R.id.estimatedProductionEdittext)
    EditText estimatedProductionEdittext;
    @BindView(R.id.phEdittext)
    EditText phEdittext;
    @BindView(R.id.content)
    CardView content;
    @BindView(R.id.back)
    Button back;
    @BindView(R.id.saveButton)
    Button saveButton;
    boolean isEditMode = false;

    Plot PLOT;
    String FARMER_CODE;
    RealFarmer FARMER;

    DynamicPlotFormFragment dynamicPlotFormFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_a_plot);
        setUnBinder(ButterKnife.bind(this));

        getActivityComponent().inject(this);
        mPresenter.takeView(this);
        mAppDataManager = mPresenter.getAppDataManager();

        FARMER = getGson().fromJson(getIntent().getStringExtra("farmer"), RealFarmer.class);
        FARMER_CODE = FARMER.getCode();

        AppLogger.e(TAG, "Farmer Code is " + FARMER_CODE);

        setupViews();

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
    public void showForm(List<FormAndQuestions> formAndQuestionsList) {

        AppLogger.e(TAG, "Plot Questions size is " + formAndQuestionsList.size());


        PLOT_FORM_AND_QUESTIONS = formAndQuestionsList;

        dynamicPlotFormFragment = DynamicPlotFormFragment.newInstance(isEditMode, FARMER_CODE,
                getAppDataManager().isMonitoring(), PLOT.getAnswersData());

        ActivityUtils.loadDynamicView(getSupportFragmentManager(), dynamicPlotFormFragment, FARMER_CODE);



    }


    void setupViews(){

        //Edit Plot

        saveButton.setEnabled(false);
        plotNameEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.length() < 2) {
                    saveButton.setEnabled(false);
                    showMessage(R.string.enter_valid_plot_name);

                } else saveButton.setEnabled(true);
            }
        });


        if (getIntent().getStringExtra("flag") != null && getIntent().getStringExtra("flag").equals("edit")) {
            isEditMode = true;
            setToolbar(getStringResources(R.string.edit_plot));

            PLOT = getGson().fromJson(getIntent().getStringExtra("plot"), Plot.class);
            plotNameEdittext.setText(PLOT.getName());




        } else {
            PLOT = new Plot();
            PLOT.setFarmerCode(FARMER_CODE);
            PLOT.setAnswersData(new JSONObject().toString());

            String name = "Plot " + (getIntent().getIntExtra("plotSize", 0) + 1);

            plotNameEdittext.setText(name);
            PLOT.setName(name);



            //New Plot
            setToolbar(getStringResources(R.string.add_new_plot));
        }


        mPresenter.getPlotQuestions();
        saveButton.setOnClickListener(v->{savePlotData(null);});

    }



    void savePlotData(String flag){
    PLOT.setAnswersData(dynamicPlotFormFragment.getAnswersData().toString());
    PLOT.setPh(phEdittext.getText().toString());
    PLOT.setName(plotNameEdittext.getText().toString());
    PLOT.setEstimatedProductionSize(estimatedProductionEdittext.getText().toString());
    PLOT.setLastVisitDate(TimeUtils.getCurrentDateTime());

    mPresenter.saveData(PLOT, flag);
  }


    @OnClick(R.id.plot_area_calculation)
    void openPlotAreaCalculationActivity(){

        //Todo go to Map Activity
        if (!plotNameEdittext.getText().toString().isEmpty() || !plotNameEdittext.getText().toString().equals(""))
            savePlotData("MAP");
         else
            showMessage(R.string.provide_plot_name);
    }





    @Override
    public void showPlotDetailsActivity(Plot plot) {

        final Intent intent = new Intent(this, PlotDetailsActivity.class);
        intent.putExtra("plot", new Gson().toJson(PLOT));
        new Handler().postDelayed(() -> {
            startActivity(intent);
            finish();
        }, 500);

    }

    @Override
    public void moveToMapActivity(Plot plot) {

        final Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("plot", new Gson().toJson(PLOT));
        new Handler().postDelayed(() -> {
            startActivity(intent);
            finish();
        }, 500);


    }
}


