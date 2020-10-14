package org.grameen.fdp.kasapin.ui.form.fragment;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.SkipLogic;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.form.FieldValidator;
import org.grameen.fdp.kasapin.ui.form.InputValidator;
import org.grameen.fdp.kasapin.ui.form.MyFormController;
import org.grameen.fdp.kasapin.ui.form.NumericalFieldValidator;
import org.grameen.fdp.kasapin.ui.form.TextFieldValidator;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DynamicFormFragment extends FormFragment {
    private ComputationUtils computationUtils;
    private FormAndQuestions FORM_AND_QUESTIONS;
    private FormAnswerData ANSWER_DATA;
    private JSONObject ANSWERS_JSON = new JSONObject();
    private List<Question> QUESTIONS = new ArrayList<>();
    private String farmerCode = "";
    private boolean shouldLoadOldValues = false;
    private boolean IS_CONTROLLER_ENABLED;
    private String TAG = "MYFORMFRAGMENT";

    public DynamicFormFragment() {
    }

    public static DynamicFormFragment newInstance(FormAndQuestions formAndQuestions, boolean shouldLoadOldValues, @Nullable String farmerCode, boolean isMonitoring) {
        DynamicFormFragment formFragment = new DynamicFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString("formAndQuestions", BaseActivity.getGson().toJson(formAndQuestions));
        bundle.putBoolean("loadOldValues", shouldLoadOldValues);
        bundle.putBoolean("isMonitoring", isMonitoring);
        bundle.putString("farmerCode", farmerCode);
        formFragment.setArguments(bundle);
        return formFragment;
    }

    public static DynamicFormFragment newInstance(FormAndQuestions formAndQuestions, boolean shouldLoadOldValues, @Nullable String farmerCode, boolean isMonitoring, @Nullable FormAnswerData answerData) {
        DynamicFormFragment formFragment = newInstance(formAndQuestions, shouldLoadOldValues, farmerCode, isMonitoring);
        if (formFragment.getArguments() != null) {
            formFragment.getArguments().putString("answerData", BaseActivity.getGson().toJson(answerData));
        }
        return formFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        if (getArguments() != null) {
            FORM_AND_QUESTIONS = BaseActivity.getGson().fromJson(getArguments().getString("formAndQuestions"), FormAndQuestions.class);
            QUESTIONS = FORM_AND_QUESTIONS.getQuestions();
            farmerCode = getArguments().getString("farmerCode");
            shouldLoadOldValues = getArguments().getBoolean("loadOldValues");
            IS_CONTROLLER_ENABLED = !getArguments().getBoolean("isMonitoring");
            ANSWER_DATA = BaseActivity.getGson().fromJson(getArguments().getString("answerData"), FormAnswerData.class);

            super.onAttach(context);
        }
    }

    @Override
    protected void setUp(View view) {
    }

    private FormAnswerData getDefaultFormAnswerData() {
       FormAnswerData answerData = new FormAnswerData();
        answerData.setFormId(FORM_AND_QUESTIONS.getForm().getFormTranslationId());
        answerData.setFarmerCode(farmerCode);
        answerData.setCreatedAt(TimeUtils.getCurrentDateTime());
        answerData.setData("{}");
        return answerData;
    }

    @Override
    public void initForm(MyFormController controller) {
        MyFormSectionController formSectionController = new MyFormSectionController(getContext(), FORM_AND_QUESTIONS.getForm().getTranslation());
        computationUtils = ComputationUtils.newInstance(controller);

        if (ANSWER_DATA == null)
            ANSWER_DATA =  getDatabaseManager().formAnswerDao().getFormAnswerDataOrNull(farmerCode, FORM_AND_QUESTIONS.getForm()
                    .getFormTranslationId()).blockingGet(getDefaultFormAnswerData());
        try {
            ANSWERS_JSON = new JSONObject(ANSWER_DATA.getData());
        } catch (JSONException e) {
            e.printStackTrace();
            ANSWERS_JSON = new JSONObject();
        }

        if (QUESTIONS != null) {
            Collections.sort(QUESTIONS, (o, t1) -> {
                try {
                    return o.getDisplayOrderC() - t1.getDisplayOrderC();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return -1;
            });
            loadQuestions(getContext(), QUESTIONS, formSectionController);
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
                            addDisposable(getDatabaseManager().skipLogicsDao().getAllByQuestionId(question.getId())
                                    .subscribe(skipLogics -> {
                                        if (skipLogics != null && skipLogics.size() > 0)
                                            applySkipLogicAndHideViews(question, skipLogics);

                                    }, Throwable::printStackTrace))
                    ).observeOn(AndroidSchedulers.mainThread())
                    .subscribe();

        Observable.fromIterable(QUESTIONS)
                .subscribeOn(Schedulers.io())
                .doOnNext(question ->
                         addDisposable(Observable.just(question).subscribeOn(Schedulers.io())
                                .filter(question1 -> question1.getTypeC().equalsIgnoreCase(AppConstants.TYPE_MATH_FORMULA))
                                .observeOn(Schedulers.computation())
                                .subscribe(this::applyFormulas)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private void loadQuestions(Context context, List<Question> questions, MyFormSectionController formSectionController) {
        for (final Question q : questions) {
            HashSet<InputValidator> validation = new HashSet<>();
            validation.add(new FieldValidator(q.getDefaultValueC(), q.getErrorMessage()));
            if (!q.shouldHide()) {
                String storedValue = (shouldLoadOldValues) ? getComputationUtils().getFormAnswerValue(q, ANSWERS_JSON) : q.getDefaultValueC();
                switch (q.getTypeC().toLowerCase()) {
                    case AppConstants.TYPE_TEXT:
                        //Define validations
                        validation.add(new TextFieldValidator(q.getDefaultValueC(), q.getErrorMessage()));
                        formSectionController.addElement(new EditTextController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), storedValue, q.isRequired(), InputType.TYPE_CLASS_TEXT,
                                IS_CONTROLLER_ENABLED && q.caEdit(), q.getHelpTextC(), validation));
                        break;
                    case AppConstants.TYPE_NUMBER_DECIMAL:
                        validation.add(new NumericalFieldValidator(q.getMinValue(), q.getMaxValue(), q.getErrorMessage()));
                        formSectionController.addElement(new EditTextController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), storedValue, q.isRequired(),
                                InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL,
                                IS_CONTROLLER_ENABLED && q.caEdit(), q.getHelpTextC(), validation));
                        break;
                    case AppConstants.TYPE_NUMBER:
                        validation.add(new NumericalFieldValidator(q.getMinValue(), q.getMaxValue(), q.getErrorMessage()));
                        formSectionController.addElement(new EditTextController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), storedValue, q.isRequired(),
                                InputType.TYPE_CLASS_NUMBER, IS_CONTROLLER_ENABLED && q.caEdit(), q.getHelpTextC(), validation));
                        break;
                    case AppConstants.TYPE_SELECTABLE:
                        formSectionController.addElement(new SelectionController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(),
                                q.isRequired(), storedValue, q.formatQuestionOptions(), true,
                                IS_CONTROLLER_ENABLED && q.caEdit(), q.getHelpTextC(), validation));
                        break;
                    case AppConstants.TYPE_MULTI_SELECTABLE:
                        formSectionController.addElement(new CheckBoxController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(),
                                q.isRequired(), storedValue, q.formatQuestionOptions(), true, IS_CONTROLLER_ENABLED && q.caEdit(), validation));
                        break;
                    case AppConstants.TYPE_TIMEPICKER:
                        formSectionController.addElement(new TimePickerController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), q.isRequired(), validation));
                        break;
                    case AppConstants.TYPE_DATEPICKER:
                        formSectionController.addElement(new DatePickerController(context, q.getLabelC(), q.getLabelC(),
                                q.getCaptionC(), IS_CONTROLLER_ENABLED, q.isRequired(), validation));
                        break;

                    case AppConstants.TYPE_MATH_FORMULA:
                    case AppConstants.TYPE_FORMULA:
                    case AppConstants.TYPE_LOGIC_FORMULA:
                        formSectionController.addElement(new EditTextController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(),
                                storedValue, q.isRequired(), InputType.TYPE_CLASS_TEXT, IS_CONTROLLER_ENABLED && q.caEdit()));
                        break;
                    case AppConstants.TYPE_LOCATION:
                        formSectionController.addElement(new ButtonController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), storedValue, v -> {
                            if (hasPermissions(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION))
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                            else {
                                getCurrentLocation(context, q);
                            }
                        }, IS_CONTROLLER_ENABLED && q.caEdit(), q.isRequired(), validation));
                        break;
                    case AppConstants.TYPE_PHOTO:
                        formSectionController.addElement(new PhotoButtonController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(),
                                (View v) -> {
                                    try {
                                        startCameraIntent(String.valueOf(q.getLabelC()));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }, IS_CONTROLLER_ENABLED && q.caEdit()));

                        break;
                }
            }

       addDisposable(getDatabaseManager().skipLogicsDao().getAllByQuestionId(q.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .subscribe(skipLogic -> applyPropertyChangeListeners(q, skipLogic), Throwable::printStackTrace)
            );
        }
    }

    private void applyPropertyChangeListeners(Question q, List<SkipLogic> skipLogic) {
        getComputationUtils().setUpPropertyChangeListeners(q.getLabelC(), skipLogic);
    }

    private void applySkipLogicAndHideViews(Question question, List<SkipLogic> skipLogic) {
        getComputationUtils().initiateSkipLogicAndHideViews(question.getLabelC(), skipLogic);
    }

    private void applyFormulas(Question question) {
        getComputationUtils().applyAndParseFormulas(question);
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

    public FormAndQuestions getFormAndQuestions() {
        return FORM_AND_QUESTIONS;
    }

    private ComputationUtils getComputationUtils() {
        return computationUtils;
    }

    public JSONObject getDataJson() {

        if(!getModel().getEditedElements().isEmpty())
             getLogRecorder().add(farmerCode, getModel().getEditedElements());


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

    public FormAnswerData getAnswerData() {
        ANSWER_DATA.setData(getDataJson().toString());
        return ANSWER_DATA;
    }
}