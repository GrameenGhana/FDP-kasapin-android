package org.grameen.fdp.kasapin.ui.form.fragment;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.SkipLogic;
import org.grameen.fdp.kasapin.ui.AddEditFarmerPlot.AddEditFarmerPlotActivity;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by aangjnr on 01/12/2017.
 */


public class DynamicPlotFormFragment extends FormFragment {

    //@Inject
    //DynamicFormFragmentPresenter mPresenter;
    ComputationUtils computationUtils;
    JSONObject ANSWERS_JSON;
    MyFormSectionController formSectionController;
    String FARMER_ID = "";
    boolean shouldLoadOldValues = false;
    boolean IS_CONTROLLER_ENABLED;
    String TAG = "MYFORMFRAGMENT";

    List<Question> ALL_QUESTIONS = new ArrayList<>();


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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //getBaseActivity().getActivityComponent().inject(this);
        // mPresenter.takeView(this);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {

        AppLogger.i(TAG, "ON ATTACHED");

        if (getArguments() != null) {

            FARMER_ID = getArguments().getString("farmerId");

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
        AppLogger.e(TAG, "**************   INIT FORM " + BaseActivity.PLOT_FORM_AND_QUESTIONS.size());

        Context context = getContext();
        computationUtils = ComputationUtils.newInstance(controller);


        if (BaseActivity.PLOT_FORM_AND_QUESTIONS != null) {
            //if(getActivity() instanceof AddEditFarmerPlotActivity)
            for (FormAndQuestions formAndQuestions : BaseActivity.PLOT_FORM_AND_QUESTIONS) {

                AppLogger.e(TAG, "Adding controller for " + formAndQuestions.getForm().getTranslation());


                formSectionController = new MyFormSectionController(getContext(), formAndQuestions.getForm().getTranslation());
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
                            getAppDataManager().getCompositeDisposable().add(getAppDataManager()
                                    .getDatabaseManager().skipLogicsDao().getAllByQuestionId(question.getId())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(skipLogics -> {
                                        if (skipLogics != null && skipLogics.size() > 0)
                                            applySkipLogicsAndHideViews(question, skipLogics);

                                    }))
                    ).subscribe();
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
                        formSectionController.addElement(new EditTextController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), storedValue, true, InputType.TYPE_CLASS_TEXT, IS_CONTROLLER_ENABLED, q.getHelpTextC()));
                        //getValue(q);

                        break;
                    case AppConstants.TYPE_NUMBER:
                        formSectionController.addElement(new EditTextController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), storedValue, true, InputType.TYPE_CLASS_NUMBER, IS_CONTROLLER_ENABLED, q.getHelpTextC()));
                        //getValue(q);

                        break;

                    case AppConstants.TYPE_NUMBER_DECIMAL:
                        formSectionController.addElement(new EditTextController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), storedValue, true, InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL, IS_CONTROLLER_ENABLED, q.getHelpTextC()));
                        //getValue(q);

                        break;

                    case AppConstants.TYPE_SELECTABLE:
                        formSectionController.addElement(new SelectionController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), true, storedValue, q.formatQuestionOptions(), true, IS_CONTROLLER_ENABLED, q.getHelpTextC()));
                        //getValue(q);

                        break;

                    case AppConstants.TYPE_MULTI_SELECTABLE:
                        formSectionController.addElement(new CheckBoxController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), true, q.formatQuestionOptions(), true, IS_CONTROLLER_ENABLED));
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
                        formSectionController.addElement(new EditTextController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), storedValue, true, InputType.TYPE_CLASS_TEXT, false));
                        //getValue(q);
                        //applyCalculation(databaseHelper.getCalculation(q.getId()));

                        break;

                    case AppConstants.TYPE_LOGIC_FORMULA:
                        formSectionController.addElement(new EditTextController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), storedValue, true, InputType.TYPE_CLASS_TEXT, false));
                        //getValue(q);
                        break;


                    case AppConstants.TYPE_LOCATION:
                        formSectionController.addElement(new ButtonController(context, q.getLabelC(), q.getLabelC(), q.getCaptionC(), v -> {

                            if (!hasPermissions(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION))
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                            else {

                                getCurrentLocation(context, q);

                            }
                        }, IS_CONTROLLER_ENABLED));
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
                                }, IS_CONTROLLER_ENABLED));
                        getComputationUtils().getValue(q, ANSWERS_JSON);
                        break;

                }
            }


            getAppDataManager().getCompositeDisposable().add(getAppDataManager().getDatabaseManager().skipLogicsDao().getAllByQuestionId(q.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .subscribe(skipLogics -> applyPropertyChangeListeners(q, skipLogics),
                            Throwable::printStackTrace)
            );


        }


    }


    public void applyPropertyChangeListeners(Question q, List<SkipLogic> skipLogics) {
        getComputationUtils().setUpPropertyChangeListeners2(q.getLabelC(), skipLogics);
    }


    public void applySkipLogicsAndHideViews(Question question, List<SkipLogic> skipLogics) {
        getComputationUtils().initiateSkipLogicsAndHideViews(question.getLabelC(), skipLogics);
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