package org.grameen.fdp.kasapin.ui.farmAssessment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.FarmResult;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.PlotAssessment;
import org.grameen.fdp.kasapin.data.db.model.HistoricalTableViewData;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.detailedYearMonthlyView.DetailedYearTableHeaderAdapter;

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
    Farmer FARMER;
    FarmResult FARM_RESULT;
    List<PlotAssessment> plotAssessmentList;
    @BindView(R.id.name)
    TextView farmerName;
    @BindView(R.id.code)
    TextView farmerCode;
    @BindView(R.id.table_view)
    TableView<HistoricalTableViewData> tableView;
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
        FARMER = getGson().fromJson(getIntent().getStringExtra("farmer"), Farmer.class);
        FARM_RESULT = getGson().fromJson(getIntent().getStringExtra("farmResults"), FarmResult.class);
        setUpViews();
        onBackClicked();
    }

    @Override
    public void setUpViews() {
        if (FARMER != null) {
            farmerName.setText(FARMER.getFarmerName());
            farmerCode.setText(FARMER.getCode());
        }

        if (FARM_RESULT != null) {
            plotAssessmentList = FARM_RESULT.getPlotAssessmentList();
            farmStatus.setText(FARM_RESULT.getStatus());
            tableView.setColumnCount(2);
            String[] TABLE_HEADERS = {getString(R.string.plot_name), getString(R.string.farm_assessment)};
            TableColumnWeightModel columnModel = new TableColumnWeightModel(tableView.getColumnCount());
            columnModel.setColumnWeight(0, 1);
            columnModel.setColumnWeight(1, 1);
            tableView.setColumnModel(columnModel);
            DetailedYearTableHeaderAdapter tableHeaderAdapter = new DetailedYearTableHeaderAdapter(this, TABLE_HEADERS);
            tableHeaderAdapter.setTypeface(Typeface.BOLD);
            tableView.setHeaderAdapter(tableHeaderAdapter);

            List<HistoricalTableViewData> dataList = new ArrayList<>();

            for (PlotAssessment plotAssessment : FARM_RESULT.getPlotAssessmentList())
                dataList.add(new HistoricalTableViewData(plotAssessment.getPlotName(), plotAssessment.getResults()));
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