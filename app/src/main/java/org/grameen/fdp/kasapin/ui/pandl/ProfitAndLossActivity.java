package org.grameen.fdp.kasapin.ui.pandl;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.grameen.fdp.kasapin.BuildConfig;
import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.base.model.Data;
import org.grameen.fdp.kasapin.ui.fdpStatus.FDPStatusActivity;
import org.grameen.fdp.kasapin.ui.main.MainActivity;
import org.grameen.fdp.kasapin.ui.test.CrashTestingActivity;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.FileUtils;
import org.grameen.fdp.kasapin.utilities.NetworkUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.codecrafters.tableview.TableView;

/**
 * A login screen that offers login via email/password.
 */
public class ProfitAndLossActivity extends BaseActivity implements ProfitAndLossContract.View {

    @Inject
    ProfitAndLossPresenter mPresenter;
    @BindView(R.id.name)
    TextView farmerName;
    @BindView(R.id.code)
    TextView farmerCode;
    @BindView(R.id.nameLayout)
    LinearLayout nameLayout;
    @BindView(R.id.currency)
    TextView currency;
    @BindView(R.id.currency_layout)
    LinearLayout currencyLayout;
    @BindView(R.id.tableView)
    TableView tableView;
    @BindView(R.id.print)
    ImageView print;
    @BindView(R.id.fdpStatus)
    TextView fdpStatus;
    @BindView(R.id.back)
    Button back;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.submitAgreement)
    Button submitAgreement;
    @BindView(R.id.bottom_buttons)
    LinearLayout bottomButtons;
    @BindView(R.id.main_layout)
    RelativeLayout mainLayout;

    JSONObject VALUES_JSON_OBJECT;
    List<Data> TABLE_DATA_LIST ;
    List<String> TOTAL_LABOR_COST;
    List<String> TOTAL_LABOR_DAYS ;
    List<String> TOTAL_MAINTENANCE_COST;
    List<String> TOTAL_GROSS_INCOME_FROM_COCOA;
    List<String> TOTAL_NET_INCOME_FROM_OTHER_CROPS;
    List<String> TOTAL_FARMING_INCOME;
    List<String> TOTAL_NET_INCOME_FROM_OTHER_SOURCES;
    List<String> TOTAL_INCOME;
    List<String> NET_FAMILY_INCOME;
    List<String> calculationsList;
    List<Plot> realPlotList;


    String TOTAL_FAMILY_EXPENSES = "";
    List<String> TOTAL_INVESTMENT_IN_FDP;
    List<String> TOTAL_P_AND_L_LIST;


    List<StringBuilder> GROSS_COCOA_STRING_BUILDERS = new ArrayList<>();
    List<StringBuilder> MAINTENANCE_COST_STRING_BUILDERS = new ArrayList<>();
    List<StringBuilder> LABOR_DAYS_STRING_BUILDERS = new ArrayList<>();
    List<StringBuilder> LABOR_COST_STRING_BUILDERS = new ArrayList<>();


    String startYearId = "nil";
    String CSSV_ID = "nil";
    String CSSV_VALUE = "--";
    Boolean DID_LABOUR = false;
    String LABOUR_TYPE;
    Boolean shouldHideStartYear = null;


    MyTableViewAdapter myTableViewAdapter;
    private JSONObject PLOT_ANSWERS_JSON_OBJECT = new JSONObject();
    JSONObject PLOT_SIZES_IN_HA;
    boolean isTranslation;
    static final int MAX_YEARS = 7;

    ScriptEngine engine;

    RealFarmer farmer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toggleFullScreen(false, getWindow());
        setContentView(R.layout.activity_profit_and_loss);

        getActivityComponent().inject(this);
        mPresenter.takeView(this);
        setUnBinder(ButterKnife.bind(this));
        engine = new ScriptEngineManager().getEngineByName("rhino");


        farmer = getGson().fromJson(getIntent().getStringExtra("farmer"), RealFarmer.class);

        startYearId = getAppDataManager().getDatabaseManager().questionDao().getLabel("start_year_").blockingGet();
        currency.setText(getAppDataManager().getStringValue("currency"));




        if (farmer != null)
            setUpViews();
        else {
            finish();
            showMessage(getStringResources(R.string.error_has_occurred));
        }



        if (BuildConfig.DEBUG)
            issuePrint();


        if(getAppDataManager().isMonitoring()){
            save.setVisibility(View.GONE);
            submitAgreement.setVisibility(View.GONE);
        }

    }

    @Override
    public void setUpViews() {
        farmerName.setText(farmer.getFarmerName());
        farmerCode.setText(farmer.getCode());


        if (farmer.getHasSubmitted().equalsIgnoreCase(AppConstants.YES) && farmer.getSyncStatus() == 1) {
            submitAgreement.setVisibility(View.GONE);
            findViewById(R.id.save).setVisibility(View.GONE);
        }

        getAllFarmerDataValues();


        submitAgreement.setOnClickListener(v->{

                if (farmer.getHasSubmitted() == null || farmer.getHasSubmitted().equalsIgnoreCase(AppConstants.NO)) {

                    if (checkIfFarmerFdpStatusFormFilled(farmer.getCode())) {


                        if (NetworkUtils.isNetworkConnected(ProfitAndLossActivity.this)) {

                            showDialog(true, getStringResources(R.string.caution), getStringResources(R.string.read_only_rational),
                                    (dialog, which) -> {

                                       //Todo sync up, set agreement submitted


                                    }, getStringResources(R.string.ok), (dialog, which) -> dialog.dismiss(), getStringResources(R.string.cancel), 0);


                        } else {

                            showDialog(false, getStringResources(R.string.status_submitted), getStringResources(R.string.no_internet_connection_available) + "\n" +
                                            getStringResources(R.string.you_can_still_make_edits) + farmer.getFarmerName() +
                                            getStringResources(R.string.apostrophe_s) + getStringResources(R.string.data_before_sync),
                                    (d, w)-> {
                                            d.dismiss();

                                            //Todo update farmer data, set agreement submitted to true

                                    }, getStringResources(R.string.ok), null, "", 0);

                        }


                    } else {


                        showDialog(true, getStringResources(R.string.fdp_status_incomplete), getStringResources(R.string.fill_out_fdp_status) + farmer.getFarmerName(),
                                (d, w)->d.dismiss(), getStringResources(R.string.ok), (d, w1)->{

                                        d.dismiss();
                                        moveToFdpStatusActivity();

                                }, getStringResources(R.string.go_to_fdp_status_form), 0);

                    }


                } else if (farmer.getHasSubmitted().equalsIgnoreCase(AppConstants.YES)) {

                    showDialog(false, getStringResources(R.string.status_submitted), getStringResources(R.string.no_more_mods) + farmer.getFarmerName()
                                    + getStringResources(R.string.apostrophe_s) + getStringResources(R.string.data),
                            (d, w) ->{
                                    d.dismiss();
                                    submitAgreement.setVisibility(View.GONE);

                            }, getStringResources(R.string.ok), null, "", 0);
                }
        });



    }

    @Override
    protected void onDestroy() {
        mPresenter.dropView();
        super.onDestroy();
    }



    @Override
    public void openLoginActivityOnTokenExpire() {


    }


    @OnClick(R.id.fdp_status)
    @Override
    public void moveToFdpStatusActivity(){

        Intent intent = new Intent(this, FDPStatusActivity.class);
        intent.putExtra("farmer", getGson().toJson(farmer));
        startActivity(intent);
    }


    @Override
    public void issuePrint(){
        findViewById(R.id.print).setVisibility(View.VISIBLE);

        findViewById(R.id.print).setOnClickListener(v -> {

            findViewById(R.id.bottom_buttons).setVisibility(View.GONE);
            findViewById(R.id.fdpStatus).setVisibility(View.GONE);
            findViewById(R.id.currency_layout).setVisibility(View.GONE);


            showLoading( "Initializing print", "Please wait...", false, 0, false);


            new Handler().postDelayed(() -> {
                String fileLocation = captureScreenshot(findViewById(R.id.main_layout), "pandl");
                hideLoading();

                if (fileLocation != null) {
                    showMessage("File saved!");

                       /* Intent intent = new Intent(this, PrintingActivity.class);
                        intent.putExtra("file_location", fileLocation);
                        startActivity(intent);

                        findViewById(R.id.bottom_buttons).setVisibility(View.VISIBLE);
                        findViewById(R.id.fdpStatus).setVisibility(View.VISIBLE);
                        findViewById(R.id.currency_layout).setVisibility(View.VISIBLE);*/


                } else
                    showMessage("Error starting the printer service!");



            }, 2000);


        });

    }


    @Override
    public void getAllFarmerDataValues() {



        /*final Question labourQuestion = getAppDataManager().getDatabaseManager().questionDao().get("labour").blockingGet();
        final Question labourTypeQuestion = getAppDataManager().getDatabaseManager().questionDao().get("labour_type").blockingGet();


        String jsonString = getAppDataManager().getDatabaseManager().formAnswerDao().getFormAnswersData(farmer.getCode());

        Log.d("P & L ACTIVITY", "FOUND STRING " + jsonString);

        if (jsonString != null && !jsonString.equals("null") && !jsonString.equals("") && !jsonString.equals("{}") && !jsonString.isEmpty() && !jsonString.equals("empty"))

            try {
                VALUES_JSON_OBJECT = new JSONObject(jsonString);

                try {
                    String val = VALUES_JSON_OBJECT.getString(labourQuestion.getId());
                    if (val.equalsIgnoreCase("Yes"))
                        DID_LABOUR = true;

                    LABOUR_TYPE = VALUES_JSON_OBJECT.getString(labourTypeQuestion.getId());


                    // CustomToast.makeToast(this, "Labour? " + val + " LABOUR TYPE = " + LABOUR_TYPE, Toast.LENGTH_LONG).show();

                } catch (Exception e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            //CustomToast.makeToast(PandLActivity.this, "Labour question is missing in SF.\nPlease consider adding a new question with translation \"Labour\" and another with translation \"Labour type\" ", Toast.LENGTH_LONG).show();

                        }
                    });
                }





            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("P & L ACTIVITY", "####### JSON ERROR" + e.getMessage());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        //CustomToast.makeToast(PandLActivity.this, "No labour type provided!", Toast.LENGTH_LONG).show();

                    }
                });

            }


        Log.d("P & L ACTIVITY", "MAIN JSON OBJECT ITERATION COMPLETE. DATA IS \n" + VALUES_JSON_OBJECT);


*/








    }


    @Override
    public boolean checkIfFarmerFdpStatusFormFilled(String code) {





        return false;
    }
}