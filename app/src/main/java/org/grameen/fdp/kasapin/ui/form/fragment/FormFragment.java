package org.grameen.fdp.kasapin.ui.form.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.ui.base.BaseFragment;
import org.grameen.fdp.kasapin.ui.form.MyFormController;
import org.grameen.fdp.kasapin.ui.form.model.FormModel;
import org.grameen.fdp.kasapin.utilities.CustomToast;
import org.grameen.fdp.kasapin.utilities.FileUtils;
import org.grameen.fdp.kasapin.utilities.ImageUtil;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static org.grameen.fdp.kasapin.utilities.ActivityUtils.getFormModelFragment;

public abstract class FormFragment extends BaseFragment {
    private static int CAMERA_INTENT = 506;
    private String ID;
    private Uri URI;
    private String TAG = "FormFragment";
    private FormModelFragment formModelFragment;
    private MyFormController formController;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         return inflater.inflate(R.layout.form_activity, null);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.formModelFragment = getFormModelFragment(requireActivity());
        this.formController = new MyFormController(context, formModelFragment.getModel());

        if(getActivity() != null)
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initForm(formController);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recreateViews();
    }

    /**
     * An abstract method that must be overridden by subclasses where the form fields are initialized.
     */
    abstract public void initForm(MyFormController controller);

    /**
     * Returns the associated <code>MyFormController</code> that manages the form fields.
     */
    public MyFormController getFormController() {
        return formController;
    }


    /**
     * Returns the associated model of this form.
     */
    public FormModel getModel() {
        return formModelFragment.getModel();
    }

    /**
     * Sets the model to use for this form
     *
     * @param formModel the model to use
     */
    public void setModel(FormModel formModel) {
        this.formModelFragment.setModel(formModel);
        formController.setModel(formModel);
    }

    /**
     * Recreates the views for all the elements that are in the form. This method needs to be called when field are dynamically added or
     * removed
     */
    private void recreateViews() {
        ViewGroup containerView = getActivity().findViewById(R.id.form_elements_container);
        formController.recreateViews(containerView);
    }


    void startCameraIntent(String id) {
        this.ID = id;

        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("shouldSave", false).apply();
        if (hasPermissions(getActivity(), Manifest.permission.CAMERA)) {
            int PERMISSION_CAMERA = 505;
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
        } else {
            Intent takePictureIntent;
            takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                File photo;
                // place where to store camera taken picture
                photo = FileUtils.createTemporaryFile("picture", ".jpg");
                if(photo.exists())
                    photo.delete();
                URI = FileProvider.getUriForFile(getActivity(),
                        getActivity().getApplicationContext().getPackageName() + ".org.grameen.fdp.provider", photo);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, URI);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, CAMERA_INTENT);
            }
        }
    }

    void getCurrentLocation(final Context context, Question q) {
        getModel().setValue(q.getLabelC(), "Getting location...");
        boolean GpsStatus;
        LocationManager locationManager;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (GpsStatus) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setSpeedRequired(false);
            criteria.setCostAllowed(true);
            criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
            // This is the Best And IMPORTANT part
            final Looper looper = null;
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                locationManager.requestSingleUpdate(criteria, getLocationListener(q), looper);
            }
        } else
            CustomToast.makeToast(context, "Please Enable GPS First", Toast.LENGTH_LONG).show();
    }

    private LocationListener getLocationListener(Question q) {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                getModel().setValue(q.getLabelC(), location.getLatitude() + ", " + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };
    }

    boolean hasPermissions(Context context, String permission) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == CAMERA_INTENT) {
                    try {
                        String BASE64_STRING;
                        Bitmap bitmap = ImageUtil.handleSamplingAndRotationBitmap(getActivity(), URI);
                        BASE64_STRING = ImageUtil.bitmapToBase64(bitmap);
                        getModel().setValue(ID, BASE64_STRING);

                        bitmap.recycle();
                        CustomToast.makeToast(getActivity(), getResources().getString(R.string.click_photo_to_delete), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        CustomToast.makeToast(getActivity(), "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            } else
                CustomToast.makeToast(getActivity(), getResources().getString(R.string.no_image_taken), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            CustomToast.makeToast(getActivity(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_LONG)
                    .show();
        }
    }
}
