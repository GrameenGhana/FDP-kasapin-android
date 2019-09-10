package org.grameen.fdp.kasapin.ui.familyMembers;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.evrencoskun.tableview.TableView;
import com.google.gson.Gson;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Form;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Monitoring;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.data.db.model.HistoricalTableViewData;
import org.grameen.fdp.kasapin.parser.MathFormulaParser;
import org.grameen.fdp.kasapin.ui.addFarmer.AddEditFarmerActivity;
import org.grameen.fdp.kasapin.ui.addPlotMonitoring.AddPlotMonitoringActivity;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.base.model.Cell;
import org.grameen.fdp.kasapin.ui.base.model.ColumnHeader;
import org.grameen.fdp.kasapin.ui.base.model.PlotMonitoringTableData;
import org.grameen.fdp.kasapin.ui.base.model.RowHeader;
import org.grameen.fdp.kasapin.ui.farmerProfile.FarmerProfileActivity;
import org.grameen.fdp.kasapin.ui.plotReview.HistoricalTableHeaderAdapter;
import org.grameen.fdp.kasapin.ui.plotReview.PlotMonitoringTablePagerAdapter;
import org.grameen.fdp.kasapin.ui.plotReview.PlotMonitoringTableViewAdapter;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.ComputationUtils;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;
import org.grameen.fdp.kasapin.utilities.NetworkUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import io.reactivex.functions.Action;


public class FamilyMembersActivity extends BaseActivity implements FamilyMembersContract.View,  FdpCallbacks.UpdateJsonArray {

    @Inject
    FamilyMembersPresenter mPresenter;
    RealFarmer FARMER;

    FormAndQuestions familyMembersFormAndQuestions;

    static  int COLUMN_SIZE;
    static int ROW_SIZE = 1;
    private List<RowHeader> mRowHeaderList;
    private List<ColumnHeader> mColumnHeaderList;
    private List<List<Cell>> mCellList;
    List<List<Question>> mQuestionsList;

    @BindView(R.id.table_view)
    TableView tableView;

    private FineTableViewAdapter mTableViewAdapter;

    JSONArray allFamilyMembersArrayData = new JSONArray();
    static JSONArray oldValuesArray ;


    int SCROLL_POSITION = 3;
    int noFamilyMembers;


    @BindView(R.id.save)
    Button save;

    @BindView(R.id.name)
    TextView nameTextView;

    @BindView(R.id.code)
    TextView codeTextView;

    FormAnswerData answerData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_members);
        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));

        mPresenter.takeView(this);

        FARMER = new Gson().fromJson(getIntent().getStringExtra("farmer"), RealFarmer.class);
        noFamilyMembers = getIntent().getIntExtra("noFamilyMembers", 1);
        ROW_SIZE = noFamilyMembers;

        familyMembersFormAndQuestions = FILTERED_FORMS.get(CURRENT_FORM_POSITION);


        COLUMN_SIZE = familyMembersFormAndQuestions.getQuestions().size();

        if(FARMER != null)
        setUpViews();

        onBackClicked();



    }


    @Override
    public void setUpViews() {


        setToolbar("Family Members Table");

        nameTextView.setText(FARMER.getFarmerName());
        codeTextView.setText(FARMER.getCode());


        if(getAppDataManager().isMonitoring())
            save.setVisibility(View.GONE);



        mRowHeaderList = new ArrayList<>();
        mColumnHeaderList = new ArrayList<>();
        mCellList = new ArrayList<>();
        mQuestionsList = new ArrayList<>();

        for (int i = 0; i < ROW_SIZE; i++) {
            mCellList.add(new ArrayList<Cell>());
        }

        for (int i = 0; i < ROW_SIZE; i++) {
            mQuestionsList.add(familyMembersFormAndQuestions.getQuestions());
        }

        for(int i = 0; i < ROW_SIZE; i++){
            JSONObject jsonObject = new JSONObject();
            try {
                allFamilyMembersArrayData.put(i, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for(int i = 0; i < ROW_SIZE; i++){
            JSONObject jsonObject = new JSONObject();
            try {
                allFamilyMembersArrayData.put(i, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        answerData = getAppDataManager().getDatabaseManager().formAnswerDao().getFormAnswerData(FARMER.getCode(), familyMembersFormAndQuestions.getForm().getId());


        if(answerData == null) {
            answerData = new FormAnswerData();
            answerData.setFormId(familyMembersFormAndQuestions.getForm().getId());
            answerData.setFarmerCode(FARMER.getCode());

        }

        try {
            oldValuesArray = new JSONArray(answerData.getData());
        } catch (Exception e) {
            e.printStackTrace();
            oldValuesArray = new JSONArray();
        }


        setupTableView(familyMembersFormAndQuestions);

    }


    @Override
    public void setupTableView(FormAndQuestions _familyMembersFormAndQuestions) {
        this.familyMembersFormAndQuestions = _familyMembersFormAndQuestions;

        mTableViewAdapter = new FineTableViewAdapter(this, familyMembersFormAndQuestions.getQuestions());
        tableView.setAdapter(mTableViewAdapter);
        tableView.setTableViewListener(new TableViewListener());

        List<RowHeader> rowHeaders = getRowHeaderList();
        List<ColumnHeader> columnHeaders = getColumnHeaderList(); //getRandomColumnHeaderList(); //
        List<List<Cell>> cellList =  getCellList(); //getCellListForSortingTest();

        mRowHeaderList.addAll(rowHeaders);
        mColumnHeaderList.addAll(columnHeaders);


        for (int i = 0; i < ROW_SIZE; i++) {
            mCellList.get(i).addAll(cellList.get(i));
        }

        mTableViewAdapter.setAllItems(mColumnHeaderList, mRowHeaderList, mCellList);

        SpinnerViewHolder.UpdateJsonArrayListener(this);
        CellViewHolder.UpdateJsonArrayListener(this);
        CheckBoxViewHolder.UpdateJsonArrayListener(this);

    }




    @OnClick(R.id.save)
    void saveData(){

        StringBuilder familyMembersIncomeStringBuilder = new StringBuilder();

        /*
        ** Calculate income from all family members, save total family income value in Socio-EconomicProfile AnswerData
        *
        */

        int socioEconomicFormId = getAppDataManager().getDatabaseManager().formsDao().getId(AppConstants.SOCIO_ECONOMIC_PROFILE).blockingGet(0);
        FormAnswerData socioEconomicProfileFormAnswerData = getAppDataManager().getDatabaseManager().formAnswerDao().getFormAnswerData(FARMER.getCode(), socioEconomicFormId);

        if(socioEconomicProfileFormAnswerData == null){
            socioEconomicProfileFormAnswerData = new FormAnswerData();
            socioEconomicProfileFormAnswerData.setFarmerCode(FARMER.getCode());
            socioEconomicProfileFormAnswerData.setFormId(socioEconomicFormId);
            socioEconomicProfileFormAnswerData.setData(new JSONObject().toString());
        }

        Question familyIncomeQuestion = getAppDataManager().getDatabaseManager().questionDao().get("family_income_");
        Question totalFamilyIncomeQuestion = getAppDataManager().getDatabaseManager().questionDao().get("total_family_income_");


        if(familyIncomeQuestion != null && totalFamilyIncomeQuestion != null){
            for (int i = 0; i < ROW_SIZE; i++){
                String value;
                try {
                    value = allFamilyMembersArrayData.getJSONObject(i).getString(familyIncomeQuestion.getLabelC());

                } catch (JSONException ignore) {
                    value = "0";
                }
                familyMembersIncomeStringBuilder.append(value).append("+");
            }

            familyMembersIncomeStringBuilder.append("0");



            JSONObject data = socioEconomicProfileFormAnswerData.getJsonData();

            if(data.has(totalFamilyIncomeQuestion.getLabelC()))
                data.remove(totalFamilyIncomeQuestion.getLabelC());

            try {
                data.put(totalFamilyIncomeQuestion.getLabelC(), MathFormulaParser.getInstance().evaluate(familyMembersIncomeStringBuilder.toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            socioEconomicProfileFormAnswerData.setData(data.toString());
            getAppDataManager().getDatabaseManager().formAnswerDao().insertOne(socioEconomicProfileFormAnswerData);



            //Now Save the family members answer data
            answerData.setData(allFamilyMembersArrayData.toString());
            getAppDataManager().getDatabaseManager().formAnswerDao().insertOne(answerData);


            moveToNextForm(FARMER);




        }












    }




    @Override
    protected void onDestroy() {
        mPresenter.dropView();
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void openNextActivity() {


    }



    @OnClick(R.id.scrollRight)
    void scrollTableToTheRight(){


        if (SCROLL_POSITION >= COLUMN_SIZE + 1) {
            tableView.scrollToColumnPosition(COLUMN_SIZE - 1);

            SCROLL_POSITION = 0;
        } else {
            tableView.scrollToColumnPosition(SCROLL_POSITION);
            SCROLL_POSITION += 3;
        }


    }

    @OnClick(R.id.scrollLeft)
    void scrollTableToTheLeft(){

        if (SCROLL_POSITION > 3) {
            SCROLL_POSITION -= 3;
            tableView.scrollToColumnPosition(SCROLL_POSITION);

        } else
            tableView.scrollToColumnPosition(0);


    }


    @Override
    public void onItemValueChanged(int index, String uid, String value) {
        try {
            JSONObject object = allFamilyMembersArrayData.getJSONObject(index);
            if(object != null){
                if(object.has(uid))
                    object.remove(uid);

                    object.put(uid, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<List<Cell>> getCellList() {
        List<List<Cell>> list = new ArrayList<>();
        for (int i = 0; i < ROW_SIZE; i++) {

            Log.i(TAG, "ROW INDEX " + i);

            List<Cell> cellList = new ArrayList<>();
            for (int j = 0; j < COLUMN_SIZE; j++) {

                Log.i(TAG, "COLUMN INDEX " + j);

                Question q = mQuestionsList.get(i).get(j);

                //q.setMax_value__c(i + "");

                String value = getValue(i, q.getLabelC());

                if (value != null)
                    q.setDefaultValueC(value);


                Cell cell = new Cell(q.getLabelC(), q);
                cellList.add(cell);
            }
            list.add(cellList);



        }

        Log.i(TAG, "########  CELL LIST SIZE IS " + list.size());

        return list;
    }
    private List<RowHeader> getRowHeaderList() {
        List<RowHeader> list = new ArrayList<>();
        for (int i = 0; i < ROW_SIZE; i++) {

            int rowNumber = i + 1;

            RowHeader header = new RowHeader(String.valueOf(i), String.valueOf(rowNumber));
            list.add(header);
        }

        return list;
    }
    private List<ColumnHeader> getColumnHeaderList() {
        List<ColumnHeader> list = new ArrayList<>();
        for (int i = 0; i < COLUMN_SIZE; i++) {
            String helperText;
            if (familyMembersFormAndQuestions.getQuestions().get(i).getHelpTextC() == null)
                helperText = "--";
            else
                helperText = familyMembersFormAndQuestions.getQuestions().get(i).getHelpTextC();

            String title = familyMembersFormAndQuestions.getQuestions().get(i).getCaptionC();
            ColumnHeader header = new ColumnHeader(String.valueOf(i), title, helperText);
            list.add(header);

        }

        return list;
    }
    public static String getValue(int index, String uid){
        String value = null;
        try {
            JSONObject object = oldValuesArray.getJSONObject(index);
            if(object.has(uid))
                return object.getString(uid);
        } catch (JSONException ignored) {}

        return value;
    }



}