package org.grameen.fdp.kasapin.ui.form.fragment;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.google.gson.Gson;

import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.SkipLogic;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.form.MyFormController;
import org.grameen.fdp.kasapin.ui.form.controller.MyFormSectionController;
import org.grameen.fdp.kasapin.ui.form.controller.view.ButtonController;
import org.grameen.fdp.kasapin.ui.form.controller.view.CheckBoxController;
import org.grameen.fdp.kasapin.ui.form.controller.view.DatePickerController;
import org.grameen.fdp.kasapin.ui.form.controller.view.EditTextController;
import org.grameen.fdp.kasapin.ui.form.controller.view.PhotoButtonController;
import org.grameen.fdp.kasapin.ui.form.controller.view.SelectionController;
import org.grameen.fdp.kasapin.ui.form.controller.view.TimePickerController;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.ComputationUtils;
import org.grameen.fdp.kasapin.utilities.TimeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by aangjnr on 01/12/2017.
 */


public class DynamicFormFragment extends FormFragment {


    //@Inject
    //DynamicFormFragmentPresenter mPresenter;
    ComputationUtils computationUtils;
    FormAndQuestions FORM_AND_QUESTIONS;
    FormAnswerData ANSWER_DATA;
    JSONObject ANSWERS_JSON = new JSONObject();
    List<Question> QUESTIONS;
    MyFormSectionController formSectionController;
    String FARMER_ID = "";
    boolean shouldLoadOldValues = false;
    boolean IS_CONTROLLER_ENABLED;

    String TAG = "MYFORMFRAGMENT";


    public DynamicFormFragment() {


    }

    public static DynamicFormFragment newInstance(FormAndQuestions formAndQuestions, boolean shouldLoadOldValues, @Nullable String farmerId, boolean isMonitoring, @Nullable FormAnswerData answer) {
        DynamicFormFragment formFragment = new DynamicFormFragment();

        Bundle bundle = new Bundle();
        bundle.putString("formAndQuestions", BaseActivity.getGson().toJson(formAndQuestions));
        bundle.putBoolean("loadOldValues", shouldLoadOldValues);
        bundle.putBoolean("isMonitoring", isMonitoring);
        bundle.putString("farmerId", farmerId);
        bundle.putString("answerData", BaseActivity.getGson().toJson(answer));
        formFragment.setArguments(bundle);
        return formFragment;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //getBaseActivity().getActivityComponent().inject(this);
        // mPresenter.takeView(this);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        AppLogger.i(TAG, "ON ATTACHED");

        if (getArguments() != null) {
            FORM_AND_QUESTIONS = BaseActivity.getGson().fromJson(getArguments().getString("formAndQuestions"), FormAndQuestions.class);
            QUESTIONS = FORM_AND_QUESTIONS.getQuestions();
            FARMER_ID = getArguments().getString("farmerId");


            AppLogger.i(TAG, "QUESTIONS SIZE IS " + new Gson().toJson(QUESTIONS.size()));

            shouldLoadOldValues = getArguments().getBoolean("loadOldValues");

            try {
                ANSWER_DATA = BaseActivity.getGson().fromJson(getArguments().getString("answerData"), FormAnswerData.class);
                if (ANSWER_DATA != null)
                    ANSWERS_JSON = new JSONObject(ANSWER_DATA.getData());
                else
                    initializeAnswersData();
            } catch (JSONException ignore) {
                initializeAnswersData();
            }

            IS_CONTROLLER_ENABLED = !getArguments().getBoolean("isMonitoring");
        }

        Collections.sort(QUESTIONS, (o, t1) -> {
            try {
                return o.getDisplayOrderC() - t1.getDisplayOrderC();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1;
        });

        super.onAttach(context);

    }

    @Override
    protected void setUp(View view) {

    }


    void initializeAnswersData() {
        ANSWERS_JSON = new JSONObject();

        AppLogger.e(TAG, "Initializing survey answers...");

        ANSWER_DATA = new FormAnswerData();
        ANSWER_DATA.setFormId(FORM_AND_QUESTIONS.getForm().getId());
        ANSWER_DATA.setFarmerCode(FARMER_ID);
        ANSWER_DATA.setCreatedAt(TimeUtils.getCurrentDateTime());

    }


    @Override
    public void initForm(MyFormController controller) {
        AppLogger.i(TAG, "**************   INIT FORM");

        Context context = getContext();
        computationUtils = ComputationUtils.newInstance(controller);


        if (QUESTIONS != null) {
            formSectionController = new MyFormSectionController(getContext(), FORM_AND_QUESTIONS.getForm().getFormNameC());


            loadQuestionsValues(context, QUESTIONS, formSectionController);
            controller.addSection(formSectionController);

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if (shouldLoadOldValues)
            Observable.fromIterable(QUESTIONS)
                    .subscribeOn(Schedulers.io())
                    .doOnNext(question ->
                            getAppDataManager().getCompositeDisposable().add(getAppDataManager()
                                    .getDatabaseManager().skipLogicsDao().getAllByQuestionId(question.getId())
                                    .subscribe(skipLogics -> {
                                        if (skipLogics != null && skipLogics.size() > 0)
                                            applySkipLogicsAndHideViews(question, skipLogics);

                                    }))
                    ).observeOn(AndroidSchedulers.mainThread())
                    .subscribe();


        Observable.fromIterable(QUESTIONS)
                .subscribeOn(Schedulers.io())
                .doOnNext(question ->
                        getAppDataManager().getCompositeDisposable().add(Observable.just(question).subscribeOn(Schedulers.io())
                                .filter(question1 -> question1.getTypeC().equalsIgnoreCase(AppConstants.TYPE_FORMULA))
                                .observeOn(Schedulers.computation())
                                .subscribe(this::applyFormulas)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();


    }


    void loadQuestionsValues(Context context, List<Question> questions, MyFormSectionController formSectionController) {

        for (final Question q : questions) {

            if (!q.shouldHide()) {

                String storedValue;

                if (shouldLoadOldValues) {
                    storedValue = getComputationUtils().getValue(q, ANSWERS_JSON);
                    if (storedValue.isEmpty() || storedValue.equalsIgnoreCase("null"))
                        storedValue = q.getDefaultValueC();
                } else
                    storedValue = q.getDefaultValueC();

                switch (q.getTypeC().toLowerCase()) {
                    case AppConstants.TYPE_TEXT:
                        formSectionController.addElement(new EditTextController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), storedValue, true, InputType.TYPE_CLASS_TEXT, IS_CONTROLLER_ENABLED && q.caEdit(), q.getHelpTextC()));
                        //getValue(q);
                        break;

                    case AppConstants.TYPE_NUMBER_DECIMAL:
                        formSectionController.addElement(new EditTextController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), storedValue, true, InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL, IS_CONTROLLER_ENABLED && q.caEdit(), q.getHelpTextC()));
                        //getValue(q);

                        break;

                    case AppConstants.TYPE_NUMBER:
                        formSectionController.addElement(new EditTextController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), storedValue, true, InputType.TYPE_CLASS_NUMBER, IS_CONTROLLER_ENABLED && q.caEdit(), q.getHelpTextC()));
                        //getValue(q);

                        break;

                    case AppConstants.TYPE_SELECTABLE:
                        formSectionController.addElement(new SelectionController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), true, storedValue, q.formatQuestionOptions(), true, IS_CONTROLLER_ENABLED && q.caEdit(), q.getHelpTextC()));
                        //getValue(q);

                        break;

                    case AppConstants.TYPE_MULTI_SELECTABLE:
                        formSectionController.addElement(new CheckBoxController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), true, q.formatQuestionOptions(), true, IS_CONTROLLER_ENABLED && q.caEdit()));
                        //getValue(q, jsonObject);

                        break;

                    case AppConstants.TYPE_TIMEPICKER:
                        formSectionController.addElement(new TimePickerController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC()));
                        //getValue(q);

                        break;
                    case AppConstants.TYPE_DATEPICKER:
                        formSectionController.addElement(new DatePickerController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), IS_CONTROLLER_ENABLED));
                        //getValue(q);

                        break;

                    case AppConstants.TYPE_MATH_FORMULA:
                        formSectionController.addElement(new EditTextController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), storedValue, true, InputType.TYPE_CLASS_TEXT, IS_CONTROLLER_ENABLED && q.caEdit()));
                        //getValue(q);
                        //applyCalculation(databaseHelper.getCalculation(q.getId()));

                        break;

                    case AppConstants.TYPE_FORMULA:
                        formSectionController.addElement(new EditTextController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), storedValue, true, InputType.TYPE_CLASS_TEXT, IS_CONTROLLER_ENABLED && q.caEdit()));
                        //getValue(q);
                        //applyCalculation(databaseHelper.getCalculation(q.getId()));
                        break;

                    case AppConstants.TYPE_LOGIC_FORMULA:
                        formSectionController.addElement(new EditTextController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), storedValue, true, InputType.TYPE_CLASS_TEXT, IS_CONTROLLER_ENABLED && q.caEdit()));
                        //getValue(q);
                        break;


                    case AppConstants.TYPE_LOCATION:
                        formSectionController.addElement(new ButtonController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), v -> {

                            if (!hasPermissions(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION))
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                            else {

                                getCurrentLocation(context, q);

                            }
                        }, IS_CONTROLLER_ENABLED && q.caEdit()));
                        //getValue(q);
                        break;


                    case AppConstants.TYPE_PHOTO:
                        formSectionController.addElement(new PhotoButtonController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(),
                                v -> {
                                    try {

                                        startCameraIntent(String.valueOf(q.getLabelC()));


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }, IS_CONTROLLER_ENABLED && q.caEdit()));
                        getComputationUtils().getValue(q, ANSWERS_JSON);
                        break;

                }

            }


            getAppDataManager().getCompositeDisposable().add(getAppDataManager().getDatabaseManager().skipLogicsDao().getAllByQuestionId(q.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .subscribe(skipLogics -> applyPropertyChangeListeners(q, skipLogics))
            );

        }


    }


    public void applyPropertyChangeListeners(Question q, List<SkipLogic> skipLogics) {
        getComputationUtils().setUpPropertyChangeListeners2(q.getLabelC(), skipLogics);
    }


    public void applySkipLogicsAndHideViews(Question question, List<SkipLogic> skipLogics) {

        getComputationUtils().initiateSkipLogicsAndHideViews(question.getLabelC(), skipLogics);

    }


    public void applyFormulas(Question question) {

        getComputationUtils().applyFormulas(question);

    }


    @Override
    public void openNextActivity() {

    }

    @Override
    public void showLoading(String title, String message, boolean indeterminate, int icon, boolean cancelableOnTouchOutside) {

    }

    @Override
    public void openLoginActivityOnTokenExpire() {

    }

    @Override
    public void toggleFullScreen(Boolean hideNavBar, Window W) {

    }


    public ComputationUtils getComputationUtils() {
        return computationUtils;
    }


    public JSONObject getAnswersData() {

        JSONObject jsonObject = new JSONObject();

        for (Question q : QUESTIONS) {
            try {
                jsonObject.put(q.getLabelC(), getModel().getValue(q.getLabelC()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonObject;
    }

    public FormAnswerData getSurveyAnswer() {
        ANSWER_DATA.setData(getAnswersData().toString());
        return ANSWER_DATA;
    }
}