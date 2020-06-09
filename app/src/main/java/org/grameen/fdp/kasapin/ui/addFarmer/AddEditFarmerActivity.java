package org.grameen.fdp.kasapin.ui.addFarmer;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.jaredrummler.materialspinner.MaterialSpinner;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Community;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.base.model.MySearchItem;
import org.grameen.fdp.kasapin.ui.form.fragment.DynamicFormFragment;
import org.grameen.fdp.kasapin.ui.main.MainActivity;
import org.grameen.fdp.kasapin.utilities.ActivityUtils;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.CustomToast;
import org.grameen.fdp.kasapin.utilities.ImageUtil;
import org.grameen.fdp.kasapin.utilities.TimeUtils;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;

public class AddEditFarmerActivity extends BaseActivity implements AddEditFarmerContract.View {

    @Inject
    AddEditFarmerPresenter mPresenter;
    Uri URI;
    @BindView(R.id.farmerName)
    EditText farmerName;
    @BindView(R.id.farmerCode)
    EditText farmerCode;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.takeImage)
    TextView takePhoto;
    @BindView(R.id.village)
    TextView villageTextView;
    @BindView(R.id.educationLevelSpinner)
    MaterialSpinner educationLevelSpinner;
    @BindView(R.id.genderSpinner)
    MaterialSpinner genderSpinner;
    @BindView(R.id.birthdayYearEdittext)
    EditText birthYearEdittext;
    @BindView(R.id.photo)
    CircleImageView circleImageView;
    @BindView(R.id.initials)
    TextView initials;
    RealFarmer FARMER;
    boolean isEditMode = false;
    boolean isNewFarmer = true;
    boolean shouldSaveData = true;
    String BASE64_STRING = "";
    ArrayList<MySearchItem> villageItems = new ArrayList<>();
    String[] educationLevels;
    String[] genders = {"Male", "Female"};
    String gender, educationLevel, villageName = null;
    int villageId;
    private FormAndQuestions CURRENT_FORM_QUESTION;
    private DynamicFormFragment dynamicFormFragment;
    private SimpleSearchDialogCompat communitySearchDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_farmer_details);
        setUnBinder(ButterKnife.bind(this));
        getActivityComponent().inject(this);
        mPresenter.takeView(this);

        if (getIntent() != null) {
            FARMER = gson.fromJson(getIntent().getStringExtra("farmer"), RealFarmer.class);
            AppLogger.e(TAG, "CURRENT FORM IS >>> " + getGson().toJson(CURRENT_FORM_QUESTION));
            if (FARMER != null)
                isNewFarmer = false;
            setUpViews();
        }
    }

    @Override
    public void setUpViews() {
        List<Community> villages = getAppDataManager().getDatabaseManager().villagesDao().getAll().blockingGet();
        villageItems.clear();
        for (Community v : villages) {
            villageItems.add(new MySearchItem(String.valueOf(v.getId()), v.getName()));
        }
        educationLevels = getAppDataManager().getDatabaseManager().questionDao().get("education_level_").formatQuestionOptions().toArray(new String[0]);
        genders = getAppDataManager().getDatabaseManager().questionDao().get("gender_").formatQuestionOptions().toArray(new String[0]);

        /*
         * If its Monitoring mode or farmer has agreed with fdp (is submitted/has agreed) and his
         * data has been synced, prevent views from being accessible - View Only Mode
         * Agents can still edit data if the farmer has agreed but agent has not synced data to server yet
         * @param boolean isMonitoring
         *                hasAgreed
         * @param int syncStatus
         */

        educationLevelSpinner.setItems(educationLevels);
        educationLevelSpinner.setOnItemSelectedListener((view, position, id, item) -> educationLevel = educationLevels[position]);

        genderSpinner.setItems(genders);
        genderSpinner.setOnItemSelectedListener((view, position, id, item) -> gender = genders[position]);

        if (!isNewFarmer) {
            CURRENT_FORM_QUESTION = FILTERED_FORMS.get(CURRENT_FORM_POSITION);
            if (getAppDataManager().isMonitoring() || (FARMER.hasAgreed() && FARMER.getSyncStatus() == 1)) {
                disableViews();
                setToolbar("View Farmer Details");
            } else setToolbar("Edit Farmer Data");

            farmerName.setText(FARMER.getFarmerName());
            farmerCode.setText(FARMER.getCode());
            birthYearEdittext.setText(FARMER.getBirthYear());

            if (FARMER.getVillageId() > 0) {
                Community village = getAppDataManager().getDatabaseManager().villagesDao().getVillageById(FARMER.getVillageId());
                if (village != null) {
                    villageName = village.getName();
                    villageId = FARMER.getVillageId();
                    villageTextView.setText(village.getName());
                }
            }
            if (FARMER.getEducationLevel() != null) {
                educationLevel = FARMER.getEducationLevel();
                for (int i = 0; i < educationLevels.length; i++)
                    if (educationLevel.equalsIgnoreCase(educationLevels[i])) {
                        educationLevelSpinner.setSelectedIndex(i);
                        break;
                    }
            }

            if (FARMER.getGender() != null) {
                gender = FARMER.getGender();
                for (int i = 0; i < genders.length; i++)
                    if (gender.equalsIgnoreCase(genders[i])) {
                        genderSpinner.setSelectedIndex(i);
                        break;
                    }
            }

            if (FARMER.getImageUrl() != null && !FARMER.getImageUrl().isEmpty()) {
                try {
                    circleImageView.setImageBitmap(ImageUtil.base64ToBitmap(BASE64_STRING));
                } catch (Exception ignored) {
                }
            } else {
                circleImageView.setImageBitmap(null);
                if (FARMER.getFarmerName().contains(" ")) {
                    String[] valueArray = FARMER.getFarmerName().split(" ");
                    String value = valueArray[0].substring(0, 1) + valueArray[1].substring(0, 1);
                    initials.setText(value);
                } else {
                    if (!FARMER.getFarmerName().trim().isEmpty())
                        initials.setText(FARMER.getFarmerName().substring(0, 1));
                }

                int[] mColors = getResources().getIntArray(R.array.recommendations_colors);
                int randomColor = mColors[new Random().nextInt(mColors.length)];
                GradientDrawable drawable = new GradientDrawable();
                drawable.setCornerRadius(1000);
                drawable.setColor(randomColor);
                circleImageView.setBackground(drawable);
            }

            mPresenter.loadFormFragment(FARMER.getCode(), CURRENT_FORM_QUESTION.getForm().getFormTranslationId());
        } else {
            FARMER = new RealFarmer();
            FARMER.setExternalId(UUID.randomUUID().toString());
            FARMER.setCode(FARMER.getExternalId());
            farmerCode.setText(FARMER.getCode());
            setToolbar("Add a new Farmer");
            save.setText(getStringResources(R.string.save));

            gender = genders[0];
            educationLevel = educationLevels[0];
            villageId = 0;
            villageName = null;

            for (FormAndQuestions formAndQuestions : FORM_AND_QUESTIONS) {
                AppLogger.e("FORM NAME IS " + formAndQuestions.getForm().getFormNameC());
                if (formAndQuestions.getForm().getFormNameC().equalsIgnoreCase(AppConstants.FARMER_PROFILE)) {
                    CURRENT_FORM_QUESTION = formAndQuestions;
                    break;
                }
            }

            showFormFragment(null);
        }
    }

    @Override
    public void showFormFragment(FormAnswerData answerData) {
        dynamicFormFragment = DynamicFormFragment.newInstance(CURRENT_FORM_QUESTION, !isNewFarmer, FARMER.getCode(), getAppDataManager().isMonitoring(), answerData);
        ActivityUtils.loadDynamicView(getSupportFragmentManager(), dynamicFormFragment, CURRENT_FORM_QUESTION.getForm().getFormNameC());
    }

    @OnClick(R.id.save)
    void saveAndContinue() {
        if (farmerName.getText().toString().trim().isEmpty()) {
            showMessage(R.string.enter_valid_farmer_name);
            return;
        }

        if (birthYearEdittext.getText().toString().isEmpty()) {
            showMessage("Please enter birth year of farmer");
            return;
        }

        if (villageId == 0 || villageName == null) {
            showMessage("Please select community of farmer");
            return;
        }

        if (educationLevel == null || educationLevel.isEmpty() || educationLevel.equalsIgnoreCase("-select-")) {
            showMessage("Please select education level of farmer");
            return;
        }

        if (gender == null || gender.isEmpty() || gender.equalsIgnoreCase("-select-")) {
            showMessage("Please select gender of farmer");
            return;
        }

        if (isNewFarmer) {
            FARMER = new RealFarmer();
            FARMER.setFirstVisitDate(new Date(System.currentTimeMillis()));
        }

        dynamicFormFragment.getFormController().resetValidationErrors();
        //Validate inputs
        if (!dynamicFormFragment.getFormController().isValidInput()) {
            dynamicFormFragment.getFormController().showValidationErrors();
            AppLogger.i(TAG, "Validations did not pass.");
        } else {
            AppLogger.i(TAG, "Validations passed.");

            FARMER.setFarmerName(farmerName.getText().toString().trim());
            FARMER.setBirthYear(birthYearEdittext.getText().toString().trim());
            FARMER.setCode(farmerCode.getText().toString().trim());
            FARMER.setGender(gender);
            FARMER.setEducationLevel(educationLevel);
            FARMER.setVillageId(villageId);
            FARMER.setVillageName(villageName);
            FARMER.setLastModifiedDate(TimeUtils.getDateTime());
            FARMER.setImageUrl(BASE64_STRING);

            mPresenter.saveData(FARMER, dynamicFormFragment.getAnswerData(), isNewFarmer);
        }
    }

    @Override
    public void moveToNextForm() {
        moveToNextForm(FARMER);
    }


    @Override
    public void finishActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void disableViews() {
        findViewById(R.id.save).setVisibility(View.GONE);
        farmerName.setEnabled(false);
        farmerCode.setEnabled(false);
        villageTextView.setEnabled(false);
        educationLevelSpinner.setEnabled(false);
        genderSpinner.setEnabled(false);
        birthYearEdittext.setEnabled(false);
        takePhoto.setVisibility(View.GONE);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @OnClick(R.id.takeImage)
    @Override
    public void startCameraIntent() {
        File photo;
        Intent takePictureIntent;

        if (!hasPermissions(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(AddEditFarmerActivity.this, new String[]{Manifest.permission.CAMERA}, AppConstants.PERMISSION_CAMERA);
        } else {
            //Prevents data being saved on pause
            shouldSaveData = false;
            takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                // place where to store camera taken picture
                photo = createTemporaryFile("picture", ".jpg");
                photo.delete();
                //URI = Uri.fromFile(photo);
                URI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".org.grameen.fdp.provider", photo);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, URI);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    AppLogger.d(TAG, "Starting camera intent");
                    startActivityForResult(takePictureIntent, AppConstants.CAMERA_INTENT);
                }
            } catch (Exception e) {
                AppLogger.e(TAG, e.getMessage());
            }
        }
    }

    @OnClick(R.id.village)
    public void chooseVillage() {
        if (communitySearchDialog == null)
            communitySearchDialog = new SimpleSearchDialogCompat<>(this, "Search Community",
                    "Which community are you looking for?", null, villageItems,
                    (dialog, item, position) -> {
                        dialog.dismiss();
                        villageId = Integer.parseInt(item.getmExtId());
                        villageName = item.getTitle();
                        villageTextView.setText(villageName);
                    });
        communitySearchDialog.show();
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null)
            mPresenter.dropView();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppLogger.d(TAG, "RESULT CODE = " + resultCode + " REQUEST CODE " + requestCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == AppConstants.CAMERA_INTENT) {
                Bitmap bitmap;
                try {
                    bitmap = ImageUtil.handleSamplingAndRotationBitmap(AddEditFarmerActivity.this, URI);
                    BASE64_STRING = ImageUtil.bitmapToBase64(bitmap);

                    if (circleImageView != null) {
                        circleImageView.setImageBitmap(bitmap);
                        initials.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                    AppLogger.d(TAG, "Failed to load", e);
                }
            }
        } else CustomToast.makeToast(this, "Something went wrong", Toast.LENGTH_LONG).show();
        shouldSaveData = true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == AppConstants.PERMISSION_CAMERA)
                startCameraIntent();

        } else {
            showDialog(true, getString(R.string.permission_required), getString(R.string.camera_permission_rationale),
                    (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        startCameraIntent();
                    }, getString(R.string.ok), (dialogInterface, i) -> dialogInterface.dismiss(), getString(R.string.no), 0);
        }
    }

    @Override
    public void setBackListener(@Nullable View view) {
        if (getAppDataManager().isMonitoring())
            finish();
        else
            //Todo save data and exit if user clicks on yes
            showDialog(true, getStringResources(R.string.save_data), getStringResources(R.string.save_data_explanation),
                    (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        isNewFarmer = true;
                        saveAndContinue();
                    }
                    , getStringResources(R.string.yes),

                    (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        getAppDataManager().setBooleanValue("reload", true);
                        finish();
                    }, getStringResources(R.string.no), 0);
    }

    @Override
    public void onBackPressed() {
        setBackListener(null);
    }
}