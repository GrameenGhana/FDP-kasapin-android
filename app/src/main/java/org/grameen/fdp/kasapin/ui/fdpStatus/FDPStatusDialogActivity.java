package org.grameen.fdp.kasapin.ui.fdpStatus;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.form.fragment.DynamicFormFragment;
import org.grameen.fdp.kasapin.utilities.ActivityUtils;
import org.grameen.fdp.kasapin.utilities.AppConstants;

import javax.inject.Inject;

public class FDPStatusDialogActivity extends BaseActivity implements FDPStatusContract.View {
    @Inject
    FDPStatusPresenter mPresenter;
    Farmer FARMER;
    FormAndQuestions formAndQuestions;
    private DynamicFormFragment dynamicFormFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fdp_status);
        getActivityComponent().inject(this);
        mPresenter.takeView(this);
        if (getIntent() != null) {
            FARMER = gson.fromJson(getIntent().getStringExtra("farmer"), Farmer.class);
            if (FARMER != null)
                setUpViews();
            else {
                showMessage(R.string.error_has_occurred_loading_data);
                finish();
            }
        }
    }


    @Override
    public void setUpViews() {
        for (int i = 0; i < FILTERED_FORMS.size(); i++)
            if (FILTERED_FORMS.get(i).getForm().getFormNameC().equalsIgnoreCase(AppConstants.FDP_STATUS)) {
                formAndQuestions = FILTERED_FORMS.get(i);
                //mPresenter.getAnswerData(FARMER.getCode(), formAndQuestions.getForm().getFormTranslationId());
                showFormFragment();
                break;
            }

        if (getAppDataManager().isMonitoring())
            findViewById(R.id.save_button).setVisibility(View.GONE);
        findViewById(R.id.cancel_button).setOnClickListener((v) -> finish());
        findViewById(R.id.save_button).setOnClickListener((v) -> saveData());
    }

    @Override
    public void dismiss() {
        super.showMessage(R.string.data_saved);
        finish();
    }

    @Override
    public void onDestroy() {
        if (mPresenter != null)
            mPresenter.dropView();
        super.onDestroy();
    }

    @Override
    public void showFormFragment() {
        dynamicFormFragment = DynamicFormFragment.newInstance(formAndQuestions, true, FARMER.getCode(), getAppDataManager().isMonitoring());
        ActivityUtils.loadDynamicView(getSupportFragmentManager(), dynamicFormFragment, formAndQuestions.getForm().getFormNameC());
    }

    @Override
    public void saveData() {
        dynamicFormFragment.getFormController().resetValidationErrors();
        //Validate inputs
        if (!dynamicFormFragment.getFormController().isValidInput()) {
            dynamicFormFragment.getFormController().showValidationErrors();
        } else {
            mPresenter.saveData(FARMER, dynamicFormFragment.getAnswerData());
        }
    }
}