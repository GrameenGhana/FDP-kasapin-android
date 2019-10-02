package org.grameen.fdp.kasapin.ui.farmAssessment;


import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.FarmResult;
import org.grameen.fdp.kasapin.data.db.entity.PlotAssessment;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.data.db.model.HistoricalTableViewData;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.detailedYearMonthlyView.DetailedYearTableHearderAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;


public class FarmAssessmentActivity extends BaseActivity implements FarmAssessmentContract.View {

    @Inject
    FarmAssessmentPresenter mPresenter;

    RealFarmer FARMER;

    FarmResult FARM_RESULT;
    List<PlotAssessment> PLOT_ASESSMENTS;

    @BindView(R.id.name)
    TextView farmerName;

    @BindView(R.id.code)
    TextView farmerCode;

    @BindView(R.id.table_view)
    TableView tableView;

    @BindView(R.id.save)
    Button save;

    @BindView(R.id.submit_agreement)
    Button submit;

    @BindView(R.id.farm_status)
    TextView farmStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_assessment);
        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));

        mPresenter.takeView(this);

        FARMER = getGson().fromJson(getIntent().getStringExtra("farmer"), RealFarmer.class);
        FARM_RESULT = getGson().fromJson(getIntent().getStringExtra("farmResults"), FarmResult.class);




        setUpViews();
        onBackClicked();



    }


    @Override
    public void setUpViews() {
        //setToolbar(getStringResources(R.string.plot_monitoring));

        if (FARMER != null) {
            farmerName.setText(FARMER.getFarmerName());
            farmerCode.setText(FARMER.getCode());
        }



        if(FARM_RESULT != null){
            PLOT_ASESSMENTS = FARM_RESULT.getPlotAssessmentList();


            farmStatus.setText(FARM_RESULT.getStatus());

            tableView.setColumnCount(2);
            String[] TABLE_HEADERS = {getStringResources(R.string.plot_name), getStringResources(R.string.farm_assessment)};
            TableColumnWeightModel columnModel = new TableColumnWeightModel(tableView.getColumnCount());
            columnModel.setColumnWeight(0, 1);
            columnModel.setColumnWeight(1, 1);
            tableView.setColumnModel(columnModel);
            DetailedYearTableHearderAdapter tableHearderAdapter = new DetailedYearTableHearderAdapter(this, TABLE_HEADERS);
            tableHearderAdapter.setTypeface(Typeface.BOLD);
            tableView.setHeaderAdapter(tableHearderAdapter);


            List<HistoricalTableViewData> dataList = new ArrayList<>();

            for(PlotAssessment plotAssessment : FARM_RESULT.getPlotAssessmentList()){

                dataList.add(new HistoricalTableViewData(plotAssessment.getPlotName(), plotAssessment.getResults()));

            }


            tableView.setDataAdapter(new FarmAssessmentTableViewAdapter(FarmAssessmentActivity.this, dataList, tableView));

        }


        onBackClicked();

    }



    @Override
    protected void onDestroy() {
        mPresenter.dropView();
        super.onDestroy();
    }


    @Override
    public void openNextActivity() {

    }

}