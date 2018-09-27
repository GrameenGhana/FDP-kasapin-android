package org.grameen.fdp.kasapin.ui.farmerProfile;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import javax.inject.Inject;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A login screen that offers login via email/password.
 */
public class AddFarmerActivity extends BaseActivity implements AddFarmerContract.View {

    @Inject
    AddFarmerPresenter mPresenter;

    String IMAGE_URL = "";
    Uri URI;

    EditText farmerName;
    EditText farmerCode;
    TextView takePhoto;
    MaterialSpinner villageSpinner;
    MaterialSpinner educationLevelSpinner;
    MaterialSpinner genderSpinner;
    String gender = "";
    String village = "";
    String education = "";
    EditText birthYearEdittext;
    CircleImageView circleImageView;
    TextView initials;
    RealFarmer farmer;
    boolean isEditMode = false;
    boolean newFarmer = false;
    Button cancel;
    Button save;
    String formLabel = "";
    String BASE64_STRING = "";

    Question educationLevelQuestion;
    Question genderQuestion;

    MyFormFragment formFragment;
    String[] educationLevels = {"Primary", "Secondary", "Tertiary", "Professional Course", "Other"};
    String[] genders = {"Male", "Female"};
    private boolean newDataSaved = false;
    FragmentManager fm;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_farmer_details);
        setUnBinder(ButterKnife.bind(this));


        getActivityComponent().inject(this);
        mPresenter.takeView(this);









    }


    @Override
    protected void onDestroy() {
        if (mPresenter != null)
            mPresenter.dropView();
        super.onDestroy();
    }


    @Override
    public void openNextActivity() {


    }

    @Override
    public void openLoginActivityOnTokenExpire() {

    }


    @Override
    public void onToggleFullScreenClicked(Boolean hideNavBar) {

    }


}