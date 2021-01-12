package org.grameen.fdp.kasapin.ui.familyMembers;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.evrencoskun.tableview.TableView;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.parser.MathFormulaParser;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.base.model.Cell;
import org.grameen.fdp.kasapin.ui.base.model.ColumnHeader;
import org.grameen.fdp.kasapin.ui.base.model.RowHeader;
import org.grameen.fdp.kasapin.ui.form.InputValidator;
import org.grameen.fdp.kasapin.ui.form.ValidationError;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;
import org.grameen.fdp.kasapin.utilities.Validator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Single;

public class FamilyMembersActivity extends BaseActivity implements FamilyMembersContract.View, FdpCallbacks.UpdateJsonArray {
    static JSONArray oldValuesArray;
    static int COLUMN_SIZE;
    static int ROW_SIZE = 1;
    @Inject
    FamilyMembersPresenter mPresenter;
    Farmer FARMER;
    FormAndQuestions familyMembersFormAndQuestions;
    List<List<Question>> mQuestionsList;
    @BindView(R.id.table_view)
    TableView tableView;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.name)
    TextView nameTextView;
    @BindView(R.id.code)
    TextView codeTextView;
    int SCROLL_POSITION;
    int lastVisibleItemPosition;
    int noFamilyMembers;
    FormAnswerData answerData;
    FineTableViewAdapter mTableViewAdapter;
    Validator validator = new Validator();
    private List<RowHeader> mRowHeaderList;
    private List<ColumnHeader> mColumnHeaderList;
    private List<List<Cell>> mCellList;

    public static String getValue(int index, String key) {
        String value = "";
        try {
            if (oldValuesArray.getJSONObject(index).has(key))
                return oldValuesArray.getJSONObject(index).getString(key);
        } catch (JSONException ignored) {
        }
        return value;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_members);
        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));

        mPresenter.takeView(this);

        String farmerCode = getIntent().getStringExtra("farmerCode");

        FARMER = getAppDataManager()
                .getDatabaseManager().realFarmersDao()
                .get(farmerCode)
                .blockingGet();

//        Get the family members questions
//        FormAndQuestions familyMembersForm = getAppDataManager()
//                .getDatabaseManager()
//                .formAndQuestionsDao()
//                .getFormAndQuestionsByName(AppConstants.FAMILY_MEMBERS).blockingGet();
//
//        answerData = getAppDataManager()
//                .getDatabaseManager()
//                .formAnswerDao()
//                .getFormAnswerData(FARMER.getCode(),
//                        familyMembersForm.getForm().getFormTranslationId());


        noFamilyMembers = getIntent().getIntExtra("noFamilyMembers", 1);
        ROW_SIZE = noFamilyMembers;

        familyMembersFormAndQuestions = FILTERED_FORMS.get(CURRENT_FORM_POSITION);
        COLUMN_SIZE = familyMembersFormAndQuestions.getQuestions().size();

        if (FARMER != null)
            setUpViews();
        onBackClicked();
    }

    @Override
    public void setUpViews() {
        setToolbar("Family Members Table");

        nameTextView.setText(FARMER.getFarmerName());
        codeTextView.setText(FARMER.getCode());

        if (getAppDataManager().isMonitoring())
            save.setVisibility(View.GONE);

        mRowHeaderList = new ArrayList<>();
        mColumnHeaderList = new ArrayList<>();
        mCellList = new ArrayList<>();
        mQuestionsList = new ArrayList<>();

        answerData = getAppDataManager().getDatabaseManager().formAnswerDao().getFormAnswerData(FARMER.getCode(), familyMembersFormAndQuestions.getForm().getFormTranslationId());
        if (answerData == null) {
            answerData = new FormAnswerData();
            answerData.setFormId(familyMembersFormAndQuestions.getForm().getFormTranslationId());
            answerData.setFarmerCode(FARMER.getCode());
        }
        try {
            oldValuesArray = new JSONArray(answerData.getData());
        } catch (Exception ignore) {
            oldValuesArray = new JSONArray();
        }

        for (int i = 0; i < ROW_SIZE; i++)
            mCellList.add(new ArrayList<>());

        for (int i = 0; i < ROW_SIZE; i++)
            mQuestionsList.add(familyMembersFormAndQuestions.getQuestions());

        for (int i = 0; i < ROW_SIZE; i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                if (i < oldValuesArray.length()) {
                    if (oldValuesArray.get(i) == null)
                        oldValuesArray.put(i, jsonObject);
                } else
                    oldValuesArray.put(i, jsonObject);
                //allFamilyMembersArrayData.put(i, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setupTableView(familyMembersFormAndQuestions);
    }

    @Override
    public void setupTableView(FormAndQuestions _familyMembersFormAndQuestions) {
        this.familyMembersFormAndQuestions = _familyMembersFormAndQuestions;

        mTableViewAdapter = new FineTableViewAdapter(this, familyMembersFormAndQuestions.getQuestions(), ROW_SIZE);
        tableView.setAdapter(mTableViewAdapter);
        tableView.setTableViewListener(new TableViewListener());

        tableView.setVerticalScrollBarEnabled(true);
        tableView.setHorizontalScrollBarEnabled(true);
        tableView.setScrollBarSize(50);

        List<RowHeader> rowHeaders = getRowHeaderList();
        List<ColumnHeader> columnHeaders = getColumnHeaderList();
        List<List<Cell>> cellList = getCellList();

        mRowHeaderList.addAll(rowHeaders);
        mColumnHeaderList.addAll(columnHeaders);

        for (int i = 0; i < ROW_SIZE; i++)
            mCellList.get(i).addAll(cellList.get(i));
        mTableViewAdapter.setAllItems(mColumnHeaderList, mRowHeaderList, mCellList);

        SpinnerViewHolder.UpdateJsonArrayListener(this);
        CellViewHolder.UpdateJsonArrayListener(this);
        CheckBoxViewHolder.UpdateJsonArrayListener(this);
        MultiSelectViewHolder.UpdateJsonArrayListener(this);

        try {
            new Handler().postDelayed(() -> {
                if (tableView != null) {
                    LinearLayoutManager linearLayoutManager = tableView.getRowHeaderLayoutManager();
                    if (linearLayoutManager != null)
                        lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                    SCROLL_POSITION = lastVisibleItemPosition;
                }
            }, 2000);
        } catch (Exception ignore) {
        }
    }

    @SuppressLint("CutPasteId")
    boolean validate() {
        List<ValidationError> errors = new ArrayList<>();


        for (int i = 0; i < ROW_SIZE; i++) {
            AppLogger.e(TAG, "#################################################");

            for (int j = 0; j < COLUMN_SIZE; j++) {
                View view = mTableViewAdapter.getCellViews(i, j);
                if (view != null) {
                    String name = mTableViewAdapter.getCellItem(j, i).getId();
                    HashSet<InputValidator> validators = validator.getValidators(name + i);
                    if (validators != null) {
                        ValidationError error;
                        for (InputValidator inputVal : validators) {
                            error = inputVal.validate(getValue(i, name), name, "");
                            if (error != null) {
                                errors.add(error);
                                setError(view, error.getMessage(getResources()));
                            }else {
                                setError(view, null);
                            }
                        }
                    }else
                        setError(view, null);
                }
            }
        }


        AppLogger.e(TAG, "ERRORS SIZE IS " + errors.size());
        return errors.isEmpty();
    }

    void setError(View view, String message){
        try {
            if (view.getTag().toString().equals("edittext") || view.getTag().toString().equals("multi_select")) {
                EditText edittext = view.findViewById(R.id.cell_data);
                edittext.setError(message);
            } else if (view.getTag().toString().equals("spinner")) {
                Spinner spinner = view.findViewById(R.id.cell_data);
                TextView itemView = spinner.findViewById(android.R.id.text1);
                itemView.setError(message);
            }
        } catch (Exception ignore) {
        }
    }


    @Override
    protected void onDestroy() {
        mPresenter.dropView();
        super.onDestroy();
    }


    @Override
    public void openNextActivity() {
    }


    @OnClick(R.id.scrollRight)
    void scrollTableToTheRight() {
        if (COLUMN_SIZE - SCROLL_POSITION < lastVisibleItemPosition) {
            tableView.scrollToColumnPosition(COLUMN_SIZE - 1);
            SCROLL_POSITION = 0;
        } else {
            if (SCROLL_POSITION == 0) {
                tableView.scrollToColumnPosition(0);
                SCROLL_POSITION = lastVisibleItemPosition;
            } else {
                SCROLL_POSITION += lastVisibleItemPosition;
                tableView.scrollToColumnPosition(SCROLL_POSITION);
            }
        }
    }


    @OnClick(R.id.scrollLeft)
    void scrollTableToTheLeft() {
        if (SCROLL_POSITION <= lastVisibleItemPosition) {
            SCROLL_POSITION = lastVisibleItemPosition;
            tableView.scrollToColumnPosition(0);
            return;
        }
        SCROLL_POSITION -= lastVisibleItemPosition;
        tableView.scrollToColumnPosition(SCROLL_POSITION);
    }

    @OnClick(R.id.save)
    void saveData() {
        AppLogger.e(oldValuesArray.toString());
        // check validations here

        if (!validate()) {
            return;
        }
        /*
         ** Calculate income from all family members, save total family income value in Socio-EconomicProfile AnswerData
         */
        StringBuilder familyMembersIncomeStringBuilder = new StringBuilder();

        int socioEconomicFormId = getAppDataManager().getDatabaseManager().formsDao().getTranslationId(AppConstants.SOCIO_ECONOMIC_PROFILE).blockingGet(0);
        FormAnswerData socioEconomicProfileFormAnswerData = getAppDataManager().getDatabaseManager().formAnswerDao().getFormAnswerData(FARMER.getCode(), socioEconomicFormId);

        if (socioEconomicProfileFormAnswerData == null) {
            socioEconomicProfileFormAnswerData = new FormAnswerData();
            socioEconomicProfileFormAnswerData.setFarmerCode(FARMER.getCode());
            socioEconomicProfileFormAnswerData.setFormId(socioEconomicFormId);
            socioEconomicProfileFormAnswerData.setData(new JSONObject().toString());
        }
        Question familyIncomeQuestion = getAppDataManager().getDatabaseManager().questionDao().get("family_income_");
        Question totalFamilyIncomeQuestion = getAppDataManager().getDatabaseManager().questionDao().get("total_family_income_");

        if (familyIncomeQuestion != null && totalFamilyIncomeQuestion != null) {
            for (int i = 0; i < ROW_SIZE; i++) {
                String value;
                try {
                    value = oldValuesArray.getJSONObject(i).getString(familyIncomeQuestion.getLabelC());
                } catch (JSONException ignore) {
                    value = "0";
                }
                familyMembersIncomeStringBuilder.append(value).append("+");
            }
            familyMembersIncomeStringBuilder.append("0");

            JSONObject data = socioEconomicProfileFormAnswerData.getJsonData();
            if (data.has(totalFamilyIncomeQuestion.getLabelC()))
                data.remove(totalFamilyIncomeQuestion.getLabelC());
            try {
                data.put(totalFamilyIncomeQuestion.getLabelC(), MathFormulaParser.getInstance().evaluate(familyMembersIncomeStringBuilder.toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            socioEconomicProfileFormAnswerData.setData(data.toString());
            getAppDataManager().getDatabaseManager().formAnswerDao().insertOne(socioEconomicProfileFormAnswerData);

            //Now Save the family members answer data
            //answerData.setData(allFamilyMembersArrayData.toString());
            answerData.setData(oldValuesArray.toString());
            getAppDataManager().getDatabaseManager().formAnswerDao().insertOne(answerData);
            mPresenter.setFarmerAsUnSynced(FARMER);
            getAppDataManager().setBooleanValue("reload", true);
            moveToNextForm(FARMER);
        }
    }

    private List<List<Cell>> getCellList() {
        List<List<Cell>> list = new ArrayList<>();
        for (int i = 0; i < ROW_SIZE; i++) {
            List<Cell> cellList = new ArrayList<>();
            for (int j = 0; j < COLUMN_SIZE; j++) {
                Question q = mQuestionsList.get(i).get(j);
                //q.setMax_value__c(i + ");
                String value = getValue(i, q.getLabelC());
                if (value != null)
                    q.setDefaultValueC(value);
                Cell cell = new Cell(q.getLabelC(), q);
                cellList.add(cell);

                validator.addValidation(i, q);
            }
            list.add(cellList);
        }
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

    @Override
    public void onItemValueChanged(int index, String uid, String value) {
        AppLogger.e("ItemValueChanged --> Uuid == " + uid + " Value == " + value);
        try {
            if (oldValuesArray.getJSONObject(index) != null) {
                if (oldValuesArray.getJSONObject(index).has(uid))
                    oldValuesArray.getJSONObject(index).remove(uid);
                oldValuesArray.getJSONObject(index).put(uid, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            e.printStackTrace();
        }
    }
}