package org.grameen.fdp.kasapin.ui.familyMembers;


import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.util.StringUtil;

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
import org.grameen.fdp.kasapin.utilities.CustomToast;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;
import org.grameen.fdp.kasapin.utilities.Validator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Single;

public class FamilyMembersActivity extends BaseActivity implements FamilyMembersContract.View, FdpCallbacks.UpdateJsonArray {
    static JSONArray oldValuesArray;
    static int COLUMN_SIZE;
    static int ROW_SIZE = 1;

    //Input Type Constants
    static final int TYPE_DECIMAL = 9998;
    static final int TYPE_NUMBER = 9999;
    static final int TYPE_TEXT = 8888;

    //Field Tags
    static final String TAG_EDITTEXT = "edittext";
    static final String TAG_CHECKBOX = "checkbox";
    static final String TAG_SPINNER = "spinner";
    static final String TAG_TEXTVIEW = "textview";
    static final String TAG_MULTISELECT = "multi_select";

    @Inject
    FamilyMembersPresenter mPresenter;
    Farmer FARMER;
    FormAndQuestions familyMembersFormAndQuestions;
    List<List<Question>> mQuestionsList;
//    @BindView(R.id.table_view)
//    TableView tableView;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.farmer_name_et)
    TextView nameTextView;
    @BindView(R.id.farmer_code_et)
    TextView codeTextView;
    @BindView(R.id.hscrollDataTable)
    ScrollView hScroll;

//    int SCROLL_POSITION;
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
        //setContentView(R.layout.activity_family_members);
        setContentView(R.layout.activity_family_members2);
        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
//        ButterKnife.bind(this);

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
//
        if (FARMER != null)
            setUpViews();
        onBackClicked();

    }

    /**
     * The function which now populates the table. This handles both
     * the loading aspect to the display of the views.
     */
    private void populateTable(){

//        Get the family members questions
        FormAndQuestions familyMembersForm = getAppDataManager()
                .getDatabaseManager()
                .formAndQuestionsDao()
                .getFormAndQuestionsByName(AppConstants.FAMILY_MEMBERS).blockingGet();

        List<Question> questions = familyMembersForm.getQuestions();

        //Row Container
        LinearLayout horizontalRow = hScroll.findViewById(R.id.llDataTable);

        for(int x=0;x<ROW_SIZE + 1;x++){
            HorizontalScrollView rowHS = new HorizontalScrollView(FamilyMembersActivity.this);

            //This is to provide an illusion that the layout is moving as a whole when scrolled.
            //Scroll one row, scroll all.
            if(Build.VERSION.SDK_INT >= 23){
                rowHS.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        for(int z=0;z<horizontalRow.getChildCount();z++){
                            HorizontalScrollView ccHorizontalView =(HorizontalScrollView)horizontalRow.getChildAt(z);
                            //Scroll everything except the view being scrolled.
                            if(!rowHS.equals(ccHorizontalView)){
                                ccHorizontalView.scrollTo(v.getScrollX(),v.getScrollY());
                            }
                        }
                    }
                });
            }

            rowHS.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));

            //The additional layout container for each row.
            LinearLayout llContainer = new LinearLayout(FamilyMembersActivity.this);

            //Load Headers
            for(int y=0;y<questions.size();y++){
                //First row as header
                if(x == 0){
                    //Textview as header view
                    llContainer.addView(getHeaderView(questions.get(y).getCaptionC(),TAG_TEXTVIEW));
                }
            }

            for(int z=0;z<questions.size();z++){
                if(x > 0){
                    if(questions.get(z).getTypeC().equalsIgnoreCase(AppConstants.TYPE_TEXT)){
                        llContainer.addView(getEditTextView(TYPE_TEXT,x,TAG_EDITTEXT,questions.get(z)));
                    }
                    else if(questions.get(z).getTypeC().equalsIgnoreCase(AppConstants.TYPE_NUMBER)){
                        llContainer.addView(getEditTextView(TYPE_NUMBER,x,TAG_EDITTEXT,questions.get(z)));
                    }
                    else if(questions.get(z).getTypeC().equalsIgnoreCase(AppConstants.TYPE_NUMBER_DECIMAL)){
                        llContainer.addView(getEditTextView(TYPE_DECIMAL,x,TAG_EDITTEXT,questions.get(z)));
                    }
                    else if(questions.get(z).getTypeC().equalsIgnoreCase(AppConstants.TYPE_SELECTABLE)){
                        llContainer.addView(getSpinnerView(questions.get(z).getOptionsC(),TAG_SPINNER,questions.get(z),x));
                    }
                }
            }

            rowHS.addView(llContainer);
            horizontalRow.addView(rowHS);
        }

        addValidatorToViews();
    }

    /** View Objects **/

    private TextView getHeaderView(String question, String tag){
        TextView headerView = new TextView(FamilyMembersActivity.this);
        headerView.setText(question);
        headerView.setTextSize(16f);
        headerView.setWidth(600);
        headerView.setTag(tag);
        headerView.setPadding(10,10,10,10);
        headerView.setHeight(100);
        headerView.setTypeface(Typeface.DEFAULT_BOLD);
        headerView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        headerView.setBackgroundResource(R.drawable.table_view_border_background);

        return headerView;
    }

    private EditText getEditTextView(int inpType, int rowPosition, String tag, Question q){
        EditText etContainer = new EditText(FamilyMembersActivity.this);
        etContainer.setHint("Enter answer here...");
        etContainer.setMaxLines(1);
        etContainer.setSingleLine(true);
        etContainer.setWidth(600);
        etContainer.setHeight(100);
        etContainer.setTag(tag);
        etContainer.setPadding(10,10,10,10);

        etContainer.setBackgroundResource(R.drawable.table_cell_background);

        etContainer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                onItemValueChanged(rowPosition-1,q.getLabelC(),s.toString());
            }
        });

        switch (inpType){
            case TYPE_TEXT:
                etContainer.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                break;
            case TYPE_NUMBER:
                etContainer.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case TYPE_DECIMAL:
                etContainer.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                etContainer.setOnFocusChangeListener((v, hasFocus) -> {
                    if (!hasFocus && etContainer.getText() != null
                            && !etContainer.getText().toString().isEmpty()) {
                        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                        DecimalFormat formatter = (DecimalFormat) nf;
                        formatter.applyPattern("#,###,###.##");
                        Double doubleValue = Double.parseDouble(etContainer.getText()
                                .toString().replace(",", ""));
                        etContainer.setText(formatter.format(doubleValue));
                        onItemValueChanged(rowPosition-1,q.getLabelC(),doubleValue.toString());
                    }
                });
                break;
            default:
        }
        return etContainer;
    }

    /**
     * Splits a comma separated list of integers to integer list.
     * <p>
     * If an input is malformed, it is omitted from the result.
     *
     * @param input Comma separated list of integers.
     * @return A List containing the integers or null if the input is null.
     */
    @Nullable
    public static List<Integer> splitToIntList(@Nullable String input) {
        if (input == null) {
            return null;
        }
        List<Integer> result = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(input, ",");
        while (tokenizer.hasMoreElements()) {
            final String item = tokenizer.nextToken();
            try {
                result.add(Integer.parseInt(item));
            } catch (NumberFormatException ex) {
                Log.e("ROOM", "Malformed integer list", ex);
            }
        }
        return result;
    }

    /**
     * Joins the given list of integers into a comma separated list.
     *
     * @param input The list of integers.
     * @return Comma separated string composed of integers in the list. If the list is null, return
     * value is null.
     */
    @Nullable
    public static String joinIntoString(@Nullable List<Integer> input) {
        if (input == null) {
            return null;
        }

        final int size = input.size();
        if (size == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(Integer.toString(input.get(i)));
            if (i < size - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private Spinner getSpinnerView(String listOfChoices, String tag, Question q, int rowPosition){
        Spinner sp = new Spinner(FamilyMembersActivity.this);
        String[] choices = listOfChoices.split(",");
        ArrayAdapter<String> arrDapt = new ArrayAdapter<>(FamilyMembersActivity.this,
                android.R.layout.simple_dropdown_item_1line,choices);
        sp.setAdapter(arrDapt);
        sp.setMinimumHeight(100);
        sp.setTag(tag);
        sp.setMinimumWidth(600);
        sp.setDropDownWidth(400);
        sp.setPadding(10,10,10,10);
        //sp.setBackgroundResource(R.drawable.table_view_borderless_background);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    onItemValueChanged(rowPosition-1,q.getLabelC(),choices[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return sp;
    }

    private View getViewObject(int rowIndex, int colIndex){

        Log.d("INDEX",Integer.valueOf(rowIndex).toString() +","+ Integer.valueOf(colIndex).toString());

        LinearLayout rowContainer = hScroll.findViewById(R.id.llDataTable);
        HorizontalScrollView hs = (HorizontalScrollView)rowContainer.getChildAt(rowIndex+1);
        LinearLayout llx = (LinearLayout)(hs.getChildAt(0));
        return llx.getChildAt(colIndex);
    }

    /** End of View Objects */

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

        populateTable();
//        setupTableView(familyMembersFormAndQuestions);
    }

    @Override
    public void setupTableView(FormAndQuestions _familyMembersFormAndQuestions) {
//        this.familyMembersFormAndQuestions = _familyMembersFormAndQuestions;
//
//        mTableViewAdapter = new FineTableViewAdapter(this, familyMembersFormAndQuestions.getQuestions(), ROW_SIZE);
//        tableView.setAdapter(mTableViewAdapter);
//        tableView.setTableViewListener(new TableViewListener());
//
//        tableView.setVerticalScrollBarEnabled(true);
//        tableView.setHorizontalScrollBarEnabled(true);
//        tableView.setScrollBarSize(50);
//
//        List<RowHeader> rowHeaders = getRowHeaderList();
//        List<ColumnHeader> columnHeaders = getColumnHeaderList();
//        List<List<Cell>> cellList = getCellList();
//
//        mRowHeaderList.addAll(rowHeaders);
//        mColumnHeaderList.addAll(columnHeaders);
//
//        for (int i = 0; i < ROW_SIZE; i++)
//            mCellList.get(i).addAll(cellList.get(i));
//        mTableViewAdapter.setAllItems(mColumnHeaderList, mRowHeaderList, mCellList);
//
//        SpinnerViewHolder.UpdateJsonArrayListener(this);
//        CellViewHolder.UpdateJsonArrayListener(this);
//        CheckBoxViewHolder.UpdateJsonArrayListener(this);
//        MultiSelectViewHolder.UpdateJsonArrayListener(this);
//
//        try {
//            new Handler().postDelayed(() -> {
//                if (tableView != null) {
//                    LinearLayoutManager linearLayoutManager = tableView.getRowHeaderLayoutManager();
//                    if (linearLayoutManager != null)
//                        lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
//                    SCROLL_POSITION = lastVisibleItemPosition;
//                }
//            }, 2000);
//        } catch (Exception ignore) {
//        }
    }

    @SuppressLint("CutPasteId")
    boolean validate() {
        List<ValidationError> errors = new ArrayList<>();

        for (int i = 0; i < ROW_SIZE; i++) {
            AppLogger.e(TAG, "#################################################");

            for (int j = 0; j < COLUMN_SIZE; j++) {
                View view = getViewObject(i,j);//.getCellViews(i, j);
                if (view != null) {
                    String name = mQuestionsList.get(i).get(j).getLabelC();
                    Log.d("CAPTION",name);//Integer.valueOf(i).toString() + Integer.valueOf(j).toString();//mTableViewAdapter.getCellItem(j, i).getId();
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
                EditText edittext = (EditText)view;
                edittext.setError(message);
            } else if (view.getTag().toString().equals("spinner")) {
                Spinner spinner = (Spinner)view;
                TextView itemView = spinner.findViewById(android.R.id.text1);
                itemView.setError(message);
            }
        } catch (Exception ignore) {
            ignore.printStackTrace();
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
        LinearLayout horizontalRow = hScroll.findViewById(R.id.llDataTable);
        HorizontalScrollView rowHS = new HorizontalScrollView(FamilyMembersActivity.this);

        for(int z=0;z<horizontalRow.getChildCount();z++){
            HorizontalScrollView ccHorizontalView =(HorizontalScrollView)horizontalRow.getChildAt(z);
            if(!rowHS.equals(ccHorizontalView)){
                ccHorizontalView.scrollTo(ccHorizontalView.getScrollX() + 200,ccHorizontalView.getScrollY());
            }
        }
    }


    @OnClick(R.id.scrollLeft)
    void scrollTableToTheLeft() {
        LinearLayout horizontalRow = hScroll.findViewById(R.id.llDataTable);
        HorizontalScrollView rowHS = new HorizontalScrollView(FamilyMembersActivity.this);

        for(int z=0;z<horizontalRow.getChildCount();z++){
            HorizontalScrollView ccHorizontalView =(HorizontalScrollView)horizontalRow.getChildAt(z);
            if(!rowHS.equals(ccHorizontalView)){
                ccHorizontalView.scrollTo(ccHorizontalView.getScrollX() - 300,ccHorizontalView.getScrollY());
            }
        }
    }

    @OnClick(R.id.save)
    void saveData() {
        AppLogger.e(oldValuesArray.toString());
        // check validations here

        if (!validate()) {
            CustomToast.makeToast(FamilyMembersActivity.this,
                    "Please complete all fields first before saving.",
                    CustomToast.LENGTH_LONG).show();
            return;
        }
//        /*
//         ** Calculate income from all family members, save total family income value in Socio-EconomicProfile AnswerData
//         */
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

    private void addValidatorToViews(){
        for(int i=0;i<ROW_SIZE;i++){
            for(int j=0;j<COLUMN_SIZE;j++){
                Question q = mQuestionsList.get(i).get(j);
                String value = getValue(i,q.getLabelC());
                if(value != null){
                    q.setDefaultValueC(value);
                }
                Log.d("VALUE_VALIDATOR",value);
//                Set view info
                View vx = (View)getViewObject(i,j);

                if(value != null){
                    if(vx != null){
                        if(vx instanceof EditText){
                            EditText et = (EditText)vx;
                            et.setText(value);
                        }
                        else if(vx instanceof Spinner){
                            Spinner sp = (Spinner)vx;
                            List<String> lChoices = Arrays.asList(q.getOptionsC().split(","));
                            sp.setSelection(lChoices.indexOf(value));
                        }
                    }
                    else{
                        Log.d("VALIDATOR_NULL","VIEW NULL");
                    }
                }

                validator.addValidation(i,q);
            }
        }
    }

//    private List<List<Cell>> getCellList() {
//        List<List<Cell>> list = new ArrayList<>();
//        for (int i = 0; i < ROW_SIZE; i++) {
//            List<Cell> cellList = new ArrayList<>();
//            for (int j = 0; j < COLUMN_SIZE; j++) {
//                Question q = mQuestionsList.get(i).get(j);
//                //q.setMax_value__c(i + ");
//                String value = getValue(i, q.getLabelC());
//                if (value != null)
//                    q.setDefaultValueC(value);
//                Cell cell = new Cell(q.getLabelC(), q);
//                cellList.add(cell);
//
//                validator.addValidation(i, q);
//            }
//            list.add(cellList);
//        }
//        return list;
//    }

//    private List<RowHeader> getRowHeaderList() {
//        List<RowHeader> list = new ArrayList<>();
//        for (int i = 0; i < ROW_SIZE; i++) {
//            int rowNumber = i + 1;
//            RowHeader header = new RowHeader(String.valueOf(i), String.valueOf(rowNumber));
//            list.add(header);
//        }
//        return list;
//    }

//    private List<ColumnHeader> getColumnHeaderList() {
//        List<ColumnHeader> list = new ArrayList<>();
//        for (int i = 0; i < COLUMN_SIZE; i++) {
//            String helperText;
//            if (familyMembersFormAndQuestions.getQuestions().get(i).getHelpTextC() == null)
//                helperText = "--";
//            else
//                helperText = familyMembersFormAndQuestions.getQuestions().get(i).getHelpTextC();
//
//            String title = familyMembersFormAndQuestions.getQuestions().get(i).getCaptionC();
//            ColumnHeader header = new ColumnHeader(String.valueOf(i), title, helperText);
//            list.add(header);
//        }
//        return list;
//    }

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
        }
    }
}