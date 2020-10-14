package org.grameen.fdp.kasapin.ui.form.fragment;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.SkipLogic;
import org.grameen.fdp.kasapin.ui.AddEditFarmerPlot.AddEditFarmerPlotActivity;
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
import org.grameen.fdp.kasapin.utilities.ComputationUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DynamicPlotFormFragment extends FormFragment {
    private ComputationUtils computationUtils;
    private JSONObject ANSWERS_JSON;
    private boolean shouldLoadOldValues = false;
    private boolean IS_CONTROLLER_ENABLED;
    private List<Question> ALL_QUESTIONS = new ArrayList<>();

    public DynamicPlotFormFragment() {
    }

    public static DynamicPlotFormFragment newInstance(boolean shouldLoadOldValues, @Nullable String farmerId, boolean isMonitoring, @Nullable String data) {
        DynamicPlotFormFragment formFragment = new DynamicPlotFormFragment();

        Bundle bundle = new Bundle();
        bundle.putBoolean("loadOldValues", shouldLoadOldValues);
        bundle.putBoolean("isMonitoring", isMonitoring);
        bundle.putString("farmerId", farmerId);
        bundle.putString("answerData", data);
        formFragment.setArguments(bundle);
        return formFragment;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        if (getArguments() != null) {
            shouldLoadOldValues = getArguments().getBoolean("loadOldValues");
            try {
                ANSWERS_JSON = new JSONObject(getArguments().getString("answerData"));
            } catch (JSONException ignore) {
                ANSWERS_JSON = new JSONObject();
            }
            IS_CONTROLLER_ENABLED = !getArguments().getBoolean("isMonitoring");
        }
        super.onAttach(context);
    }

    @Override
    protected void setUp(View view) {
    }

    @Override
    public void initForm(MyFormController controller) {
        int count = 0;
        Context context = getContext();
        computationUtils = ComputationUtils.newInstance(controller);

        if (BaseActivity.PLOT_FORM_AND_QUESTIONS != null) {
            for (FormAndQuestions formAndQuestions : BaseActivity.PLOT_FORM_AND_QUESTIONS) {
                MyFormSectionController formSectionController = new MyFormSectionController(getContext(), formAndQuestions.getForm().getTranslation());
                loadQuestionsValues(context, formAndQuestions.getQuestions(), formSectionController);
                controller.addSection(formSectionController);
                ALL_QUESTIONS.addAll(formAndQuestions.getQuestions());
                count++;

                if (count > 1 && getActivity() instanceof AddEditFarmerPlotActivity)
                    break;
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (shouldLoadOldValues)
            Observable.fromIterable(ALL_QUESTIONS)
                    .subscribeOn(Schedulers.io())
                    .doOnNext(question ->
                             addDisposable(getDatabaseManager().skipLogicsDao().getAllByQuestionId(question.getId())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(skipLogics -> {
                                        if (skipLogics != null && skipLogics.size() > 0)
                                            applySkipLogicAndHideViews(question, skipLogics);

                                    }))
                    ).subscribe();
    }


    private void loadQuestionsValues(Context context, List<Question> questions, MyFormSectionController formSectionController) {
        for (final Question q : questions) {
            HashSet<InputValidator> validation = new HashSet<>();
            validation.add(new FieldValidator(q.getDefaultValueC(), q.getErrorMessage()));

            if (!q.shouldHide()) {
                String storedValue = (shouldLoadOldValues) ? getComputationUtils().getFormAnswerValue(q, ANSWERS_JSON) : q.getDefaultValueC();
                switch (q.getTypeC().toLowerCase()) {
                    case AppConstants.TYPE_TEXT:
                        validation.add(new TextFieldValidator(q.getDefaultValueC(), q.getErrorMessage()));
                        formSectionController.addElement(new EditTextController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), storedValue, q.isRequired(), InputType.TYPE_CLASS_TEXT, IS_CONTROLLER_ENABLED, q.getHelpTextC(), validation));
                        break;
                    case AppConstants.TYPE_NUMBER:
                        validation.add(new NumericalFieldValidator(q.getMinValue(), q.getMaxValue(), q.getErrorMessage()));
                        formSectionController.addElement(new EditTextController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), storedValue, q.isRequired(), InputType.TYPE_CLASS_NUMBER, IS_CONTROLLER_ENABLED, q.getHelpTextC(), validation));
                        break;

                    case AppConstants.TYPE_NUMBER_DECIMAL:
                        validation.add(new NumericalFieldValidator(q.getMinValue(), q.getMaxValue(), q.getErrorMessage()));
                        formSectionController.addElement(new EditTextController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), storedValue, q.isRequired(), InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL,
                                IS_CONTROLLER_ENABLED, q.getHelpTextC(), validation));
                        break;

                    case AppConstants.TYPE_SELECTABLE:
                        formSectionController.addElement(new SelectionController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), q.isRequired(), storedValue, q.formatQuestionOptions(), true, IS_CONTROLLER_ENABLED, q.getHelpTextC(), validation));
                        break;

                    case AppConstants.TYPE_MULTI_SELECTABLE:
                        formSectionController.addElement(new CheckBoxController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), q.isRequired(), storedValue, q.formatQuestionOptions(), true, IS_CONTROLLER_ENABLED, validation));
                        break;

                    case AppConstants.TYPE_TIMEPICKER:
                        formSectionController.addElement(new TimePickerController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), q.isRequired(), validation));
                        break;
                    case AppConstants.TYPE_DATEPICKER:
                        formSectionController.addElement(new DatePickerController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), IS_CONTROLLER_ENABLED, q.isRequired(), validation));
                        break;

                    case AppConstants.TYPE_MATH_FORMULA:
                    case AppConstants.TYPE_LOGIC_FORMULA:
                        formSectionController.addElement(new EditTextController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), storedValue, q.isRequired(), InputType.TYPE_CLASS_TEXT, false));
                        break;

                    case AppConstants.TYPE_LOCATION:
                        formSectionController.addElement(new ButtonController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), storedValue, v -> {
                            if (hasPermissions(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION))
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                            else {
                                getCurrentLocation(context, q);
                            }
                        }, IS_CONTROLLER_ENABLED, q.isRequired(), validation));
                        break;

                    case AppConstants.TYPE_PHOTO:
                        formSectionController.addElement(new PhotoButtonController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(),
                                v -> {
                                    try {
                                        startCameraIntent(String.valueOf(q.getLabelC()));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }, IS_CONTROLLER_ENABLED));
                        getComputationUtils().getFormAnswerValue(q, ANSWERS_JSON);
                        break;
                }
            }

            if(getDatabaseManager() != null)
             addDisposable(getDatabaseManager().skipLogicsDao().getAllByQuestionId(q.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .subscribe(skipLogic -> applyPropertyChangeListeners(q, skipLogic),
                            Throwable::printStackTrace)
            );
        }
    }




    private void applyPropertyChangeListeners(Question q, List<SkipLogic> skipLogic) {
        getComputationUtils().setUpPropertyChangeListeners(q.getLabelC(), skipLogic);
    }


    private void applySkipLogicAndHideViews(Question question, List<SkipLogic> skipLogic) {
        getComputationUtils().initiateSkipLogicAndHideViews(question.getLabelC(), skipLogic);
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

    private ComputationUtils getComputationUtils() {
        return computationUtils;
    }

    public JSONObject getAnswersData() {
        JSONObject jsonObject = new JSONObject();
        for (Question q : ALL_QUESTIONS) {
            try {
                jsonObject.put(q.getLabelC(), getModel().getValue(q.getLabelC()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }
}