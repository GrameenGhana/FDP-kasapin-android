package org.grameen.fdp.kasapin.ui.familyMembers;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.util.StringUtil;

import com.evrencoskun.tableview.TableView;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.ShadowData;
import org.grameen.fdp.kasapin.parser.MathFormulaParser;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.base.model.Cell;
import org.grameen.fdp.kasapin.ui.base.model.ColumnHeader;
import org.grameen.fdp.kasapin.ui.base.model.RowHeader;
import org.grameen.fdp.kasapin.ui.form.FieldValidator;
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
import java.util.Iterator;
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

    boolean HAS_CHANGED = false;
    boolean WAS_SAVED = false;
    int RUN_COUNT = 0;

    int INTERVAL = 5000;

    //Field Tags
    static final String TAG_EDITTEXT = "edittext";
//    static final String TAG_CHECKBOX = "checkbox";
    static final String TAG_SPINNER = "spinner";
    static final String TAG_TEXTVIEW = "textview";
//    static final String TAG_MULTISELECT = "multi_select";

    @Inject
    FamilyMembersPresenter mPresenter;
    Farmer FARMER;
    FormAndQuestions familyMembersFormAndQuestions;
    List<List<Question>> mQuestionsList;

    @BindView(R.id.save)
    Button save;
    @BindView(R.id.farmer_name_et)
    TextView nameTextView;
    @BindView(R.id.farmer_code_et)
    TextView codeTextView;
    @BindView(R.id.hscrollDataTable)
    ScrollView hScroll;

    int lastVisibleItemPosition;
    int noFamilyMembers;

    FormAnswerData answerData;
//    FineTableViewAdapter mTableViewAdapter;
    Validator validator = new Validator();

    private List<RowHeader> mRowHeaderList;
    private List<ColumnHeader> mColumnHeaderList;
    private List<List<Cell>> mCellList;

    private Handler h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_family_members);
        setContentView(R.layout.activity_family_members2);
        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
//        ButterKnife.bind(this);

        mPresenter.takeView(this);
        h = new Handler(this.getMainLooper());

        String farmerCode = getIntent().getStringExtra("farmerCode");

        FARMER = getAppDataManager()
                .getDatabaseManager().realFarmersDao()
                .get(farmerCode)
                .blockingGet();

        noFamilyMembers = getIntent().getIntExtra("noFamilyMembers", 1);
        ROW_SIZE = noFamilyMembers;

        familyMembersFormAndQuestions = FILTERED_FORMS.get(CURRENT_FORM_POSITION);
        COLUMN_SIZE = familyMembersFormAndQuestions.getQuestions().size();
//
        if (FARMER != null)
            setUpViews();
        onBackClicked();

        AppLogger.d("onC Has changed: " + Boolean.valueOf(HAS_CHANGED).toString());

    }

    /** VIEW SETUP */
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
        AppLogger.d("sV Has changed: " + Boolean.valueOf(HAS_CHANGED).toString());
        populateTable();
    }

    @Override
    public void setupTableView(FormAndQuestions _familyMembersFormAndQuestions) {
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
            rowHS.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
//            rowHS.setFocusable(true);
//            rowHS.setFocusableInTouchMode(true);
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

                rowHS.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        v.requestFocusFromTouch();
                        return false;
                    }
                });
            }

            rowHS.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));

            //The additional layout container for each row.
            LinearLayout llContainer = new LinearLayout(FamilyMembersActivity.this);
//            llContainer.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

            //Load Headers
            for(int y=0;y<questions.size();y++){
                //First row as header
                if(x == 0){
                    //Textview as header view
                    llContainer.addView(getHeaderView(questions.get(y),TAG_TEXTVIEW));
                }
            }

            for(int z=0;z<questions.size();z++){
                if(x > 0){
                    if(questions.get(z).getTypeC().equalsIgnoreCase(AppConstants.TYPE_TEXT)){
                        llContainer.addView(getEditTextView(TYPE_TEXT,x,z,TAG_EDITTEXT,questions.get(z)));
                    }
                    else if(questions.get(z).getTypeC().equalsIgnoreCase(AppConstants.TYPE_NUMBER)){
                        llContainer.addView(getEditTextView(TYPE_NUMBER,x,z,TAG_EDITTEXT,questions.get(z)));
                    }
                    else if(questions.get(z).getTypeC().equalsIgnoreCase(AppConstants.TYPE_NUMBER_DECIMAL)){
                        llContainer.addView(getEditTextView(TYPE_DECIMAL,x,z,TAG_EDITTEXT,questions.get(z)));
                    }
                    else if(questions.get(z).getTypeC().equalsIgnoreCase(AppConstants.TYPE_SELECTABLE)){
                        llContainer.addView(getSpinnerView(questions.get(z).getOptionsC(),TAG_SPINNER,questions.get(z),x,z));
                    }
                }
            }

            rowHS.addView(llContainer);
            horizontalRow.addView(rowHS);
        }

        ShadowData sdata = getAppDataManager().getDatabaseManager()
                .shadowDataDao().getShadowDataForFarmer(FARMER.getCode());

        if(sdata != null){
            try{
                oldValuesArray = new JSONArray(sdata.farmer_member_data);
                CustomToast.makeToast(FamilyMembersActivity.this,
                        "Loaded saved data.",Toast.LENGTH_LONG).show();
            }
            catch (JSONException jEx){
                jEx.printStackTrace();
            }
        }
//
//        finally {
//            h.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    CustomToast.makeToast(FamilyMembersActivity.this,"Saving...", Toast.LENGTH_SHORT).show();
//                    saveShadowData();
//                    h.postDelayed(this,1000);
//                }
//            },1000);
//            addValidatorToViews();
//        }

//        if(sdata != null){
//            try {
//                JSONArray dataToReload = new JSONArray(sdata.farmer_member_data);
//                showConfirmReloadDialog(dataToReload);
//            }
//            catch (JSONException jEx){
//                jEx.printStackTrace();
//            }
//        }
//        else{
//            addValidatorToViews();
//        }

        AppLogger.d("pT Has changed: " + Boolean.valueOf(HAS_CHANGED).toString());
        addValidatorToViews();

    }

    /** END OF VIEW SETUP */

    /** THREAD GROUP */

    private Runnable saveDataRunnable = new Runnable() {
        @Override
        public void run() {
            AppLogger.d("RUN Count: " + Integer.valueOf(RUN_COUNT).toString());
            AppLogger.d("RUN Has changed: " + Boolean.valueOf(HAS_CHANGED).toString());
            if(RUN_COUNT > 1){
                saveShadowData();
            }
            else{
                HAS_CHANGED = false;
            }
            RUN_COUNT++;
            h.postDelayed(this,INTERVAL);
        }
    };
    /** END OF THREAD GROUP */

    /** ACTIVITY LIFECYCLE OVERRIDES */
    @Override
    protected void onStop() {
        AppLogger.d("OnS Has changed: " + Boolean.valueOf(HAS_CHANGED).toString());
        h.removeCallbacks(saveDataRunnable);
        if(!WAS_SAVED){
            if(HAS_CHANGED){
                saveShadowData();
            }
        }

        super.onStop();
    }

    @Override
    protected void onPostResume() {
        AppLogger.d("onP Has changed: " + Boolean.valueOf(HAS_CHANGED).toString());
        HAS_CHANGED = false;
        startSaving();
        super.onPostResume();
    }

    @Override
    protected void onDestroy() {
        mPresenter.dropView();
        AppLogger.d("onD Has changed: " + Boolean.valueOf(HAS_CHANGED).toString());
        h.removeCallbacks(saveDataRunnable);

        if(!WAS_SAVED){
            if(HAS_CHANGED){
                saveShadowData();
            }
        }

        super.onDestroy();
    }

    /** END OF LIFECYCLE OVERRIDES */

    /** VIEW VALIDATION */
    @SuppressLint("CutPasteId")
    boolean validate() {
        List<ValidationError> errors = new ArrayList<>();
        for (int i = 0; i < ROW_SIZE; i++) {
            AppLogger.e(TAG, "#################################################");

            for (int j = 0; j < COLUMN_SIZE; j++) {
                View view = getViewObject(i,j);//.getCellViews(i, j);
                if (view != null) {
                    String name = mQuestionsList.get(i).get(j).getLabelC();
                    HashSet<InputValidator> validators = validator.getValidators(name + i);
                    if (validators != null) {
                        ValidationError error;
                        for (InputValidator inputVal : validators) {
                            error = inputVal.validate(getValue(i, name), name, "");
                            if (error != null) {
                                errors.add(error);
                                setError(view, error.getMessage(getResources()));
                                if(view instanceof EditText){
                                    ((MaterialEditText)view).setTextColor(Color.RED);
                                }
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
                MaterialEditText edittext = (MaterialEditText) view;
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

    /**
     * This function adds the validator as well as the inputs
     * loaded from the database.
     */
    private void addValidatorToViews() {
        AppLogger.d("aV Has changed: " + Boolean.valueOf(HAS_CHANGED).toString());
        for(int i=0;i<ROW_SIZE;i++){
            for(int j=0;j<COLUMN_SIZE;j++){
                Question q = mQuestionsList.get(i).get(j);
                String value = getValue(i,q.getLabelC());
                if(value != null){
                    q.setDefaultValueC(value);
                }

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
                }
                validator.addValidation(i,q);
            }
        }

        //Start the saving interval
//        startSaving();
        HAS_CHANGED = false;

    }

    /** END OF VIEW VALIDATION */

    /** BACKGROUND SAVE DATA */

    private void saveShadowData(){
        AppLogger.d("Has changed: " + Boolean.valueOf(HAS_CHANGED).toString());
        if(HAS_CHANGED){
            CustomToast.makeToast(FamilyMembersActivity.this,"Saving...", Toast.LENGTH_SHORT).show();
            ShadowData shadData = new ShadowData();
            shadData.setFarmerId(FARMER.getCode());
            shadData.setFarmerData(oldValuesArray.toString());

//        AppLogger.d("SAVEDATA");
//        AppLogger.d(shadData.getFarmerMemberData());

//            try{
//                for(int x=0;x<oldValuesArray.length();x++) {
//                    Iterator<String> keys = oldValuesArray.getJSONObject(x).keys();
//
//                    while(keys.hasNext()){
//                        String keyd = keys.next();
//                        AppLogger.d(keyd);
//                    }
//                    AppLogger.d("--------------------");
//                }
//            }
//            catch (JSONException jEx){
//                jEx.printStackTrace();
//            }
            //Save to database
            getAppDataManager().getDatabaseManager().shadowDataDao().addData(shadData);
            mPresenter.setFarmerAsUnSynced(FARMER);

            HAS_CHANGED = false;
        }
    }

    private void startSaving(){
        AppLogger.d("sS Has changed: " + Boolean.valueOf(HAS_CHANGED).toString());
        h.postDelayed(saveDataRunnable,INTERVAL);
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

            //Remove callbacks from this point
            h.removeCallbacks(saveDataRunnable);

            //Now Save the family members answer data
            //answerData.setData(allFamilyMembersArrayData.toString());
            answerData.setData(oldValuesArray.toString());
            getAppDataManager().getDatabaseManager().formAnswerDao().insertOne(answerData);
            mPresenter.setFarmerAsUnSynced(FARMER);
            //Remove shadow data once data is saved and ready for sync
            getAppDataManager().setBooleanValue("reload", true);

            int xx = getAppDataManager().getDatabaseManager().shadowDataDao().removeFarmerData(FARMER.getCode());
            AppLogger.d("Value stat for removal: " + Integer.valueOf(xx).toString());
            WAS_SAVED = true;

            moveToNextForm(FARMER);
        }
    }

    @Override
    public void onItemValueChanged(int index, String uid, String value) {
        AppLogger.e("ItemValueChanged --> Uuid == " + uid + " Value == " + value);
        try {
            if (oldValuesArray.getJSONObject(index) != null) {
//                if (oldValuesArray.getJSONObject(index).has(uid))
//                    oldValuesArray.getJSONObject(index).remove(uid);
                oldValuesArray.getJSONObject(index).put(uid, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /** END OF BACKGROUND SAVE DATA */

//    private void showConfirmReloadDialog(JSONArray jsonData){
//
//        AlertDialog.Builder ab = new AlertDialog.Builder(FamilyMembersActivity.this);
//        ab.setTitle("Reload saved data");
//        ab.setMessage("You still have a previously unsaved data. Would you like to reload it?");
//
//        ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                oldValuesArray = jsonData;
//
//                addValidatorToViews();
//            }
//        });
//
//        ab.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                addValidatorToViews();
//            }
//        });
//
//        ab.create().show();
//    }

    /** View Objects **/

    private View getHeaderView(Question question, String tag) {

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setVerticalGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,5,10,5);
        linearLayout.setBackgroundResource(R.drawable.table_view_border_background);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;

        if(question.getTypeC().equalsIgnoreCase(AppConstants.TYPE_SELECTABLE))
            params.width = 700;
        else
            params.width = 600;

        params.height = 100;
        linearLayout.setLayoutParams(params);

        TextView headerView = new TextView(FamilyMembersActivity.this);
        headerView.setText(question.getCaptionC());
        headerView.setTextSize(16f);
        headerView.setTextColor(ContextCompat.getColor(this, R.color.text_black_87));
         headerView.setTag(tag);
         headerView.setTypeface(Typeface.DEFAULT_BOLD);
        headerView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        linearLayout.addView(headerView);

        TextView helpertext = new TextView(FamilyMembersActivity.this);
        helpertext.setText(question.getHelpTextC());
        helpertext.setTextSize(12f);
        helpertext.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        linearLayout.addView(helpertext);

        return linearLayout;
    }

    private MaterialEditText getEditTextView(int inpType, int rowPosition, int colPosition, String tag, Question q){
        MaterialEditText materialEditText = new MaterialEditText(FamilyMembersActivity.this);
        materialEditText.setHint(q.getHelpTextC());
        materialEditText.setMaxLines(1);
        materialEditText.setSingleLine(true);
        materialEditText.setWidth(600);
        materialEditText.setTag(tag);
        materialEditText.setTextSize(15f);
        materialEditText.setTextSize(15f);
        materialEditText.setPaddings(10,20,10,80);

        materialEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                materialEditText.setTextColor(Color.BLACK);
                onItemValueChanged(rowPosition-1, q.getLabelC(), s.toString());
                HAS_CHANGED = true;
            }
        });
        switch (inpType){
            case TYPE_TEXT:
                materialEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                break;
            case TYPE_NUMBER:
                materialEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case TYPE_DECIMAL:
                materialEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                materialEditText.setOnFocusChangeListener((v, hasFocus) -> {
                    if (!hasFocus && materialEditText.getText() != null
                            && !materialEditText.getText().toString().isEmpty()) {
                        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                        DecimalFormat formatter = (DecimalFormat) nf;
                        formatter.applyPattern("#,###,###.##");
                        Double doubleValue = Double.parseDouble(materialEditText.getText()
                                .toString().replace(",", ""));
                        materialEditText.setText(formatter.format(doubleValue));
                        onItemValueChanged(rowPosition-1,q.getLabelC(),doubleValue.toString());
                    }
                });
                break;
            default:
        }
        AppLogger.d("Has changed: " + Boolean.valueOf(HAS_CHANGED).toString());
        return materialEditText;
    }

    private Spinner getSpinnerView(String listOfChoices, String tag, Question q, int rowPosition, int colPosition){
        Spinner sp = new Spinner(FamilyMembersActivity.this);
        String[] choices = listOfChoices.split(",");
        ViewGroup.LayoutParams sparams = new ViewGroup.LayoutParams(700,100);

        ArrayAdapter<String> arrDapt = new ArrayAdapter<>(FamilyMembersActivity.this,
                android.R.layout.simple_dropdown_item_1line,choices);
        sp.setAdapter(arrDapt);
        sp.setTag(tag);
        sp.setLayoutParams(sparams);
        sp.setFocusable(true);
        sp.setPadding(10,5,10,5);

        sp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                return false;
            }
        });

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    view.requestFocus();
                    onItemValueChanged(rowPosition-1,q.getLabelC(),choices[position]);
                    HAS_CHANGED = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        AppLogger.d("Has changed: " + Boolean.valueOf(HAS_CHANGED).toString());
        return sp;
    }

    private View getViewObject(int rowIndex, int colIndex){

        LinearLayout rowContainer = hScroll.findViewById(R.id.llDataTable);
        HorizontalScrollView hs = (HorizontalScrollView)rowContainer.getChildAt(rowIndex+1);
        LinearLayout llx = (LinearLayout)(hs.getChildAt(0));
        return llx.getChildAt(colIndex);
    }

    /** End of View Objects */

    /** SCROLL */
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

    /** END OF SCROLL */

    /** DATA UTILS */

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

    public static String getValue(int index, String key) {
        String value = "";
        try {
            if (oldValuesArray.getJSONObject(index).has(key))
                return oldValuesArray.getJSONObject(index).getString(key);
        } catch (JSONException ignored) {
        }
        return value;
    }

    /** END OF DATA UTILS */

    @Override
    public void openNextActivity() {
    }

}