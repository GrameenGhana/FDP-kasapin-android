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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.ui.base.BaseFragment;
import org.grameen.fdp.kasapin.ui.form.MyFormController;
import org.grameen.fdp.kasapin.ui.form.model.FormModel;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.CustomToast;
import org.grameen.fdp.kasapin.utilities.ImageUtil;

import java.io.File;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static org.grameen.fdp.kasapin.utilities.ActivityUtils.getFormModelFragment;
import static org.grameen.fdp.kasapin.utilities.AppConstants.ROOT_DIR;

/**
 * Created by aangjnr on 05/01/2018.
 */

public abstract class FormFragment extends BaseFragment {

    public static int CAMERA_INTENT = 506;
    final int PERMISSION_CAMERA = 505;
    String ID;
    Uri URI;
    String TAG = "FormFragment";
    SharedPreferences preferences;
    ImageView imageView;
    private FormModelFragment formModelFragment;
    private MyFormController formController;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return inflater.inflate(R.layout.form_activity, null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.formModelFragment = getFormModelFragment(this.getActivity());
        this.formController = new MyFormController(context, formModelFragment.getModel());

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initForm(formController);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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
    protected void recreateViews() {
        ViewGroup containerView = (ViewGroup) getActivity().findViewById(R.id.form_elements_container);
        formController.recreateViews(containerView);
    }


    public void startCameraIntent(String id) {
        this.ID = id;
        File photo;

        Intent takePictureIntent;
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("shouldSave", false).apply();

        if (!hasPermissions(getActivity(), Manifest.permission.CAMERA)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
        } else {


            takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            try {
                // place where to store camera taken picture
                photo = this.createTemporaryFile("picture", ".jpg");
                photo.delete();
                //URI = Uri.fromFile(photo);
                URI = FileProvider.getUriForFile(getActivity(),
                        getActivity().getApplicationContext().getPackageName() + ".org.grameen.fdp.provider", photo);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, URI);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


            } catch (Exception e) {
                Timber.tag(TAG).v("Can't create file to take picture!");
                //Toast.makeText(activity, "Please check SD card! Image shot is impossible!", 10000);
            }


            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

                Timber.d("Starting camera intent");

                startActivityForResult(takePictureIntent, CAMERA_INTENT);

            }


        }
    }


    public void getCurrentLocation(final Context context, Question q) {

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
        } else {

            CustomToast.makeToast(context, "Please Enable GPS First", Toast.LENGTH_LONG).show();

        }
    }


    public LocationListener getLocationListener(Question q) {

        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                AppLogger.i(TAG, "^^^^^^^^^^ LOCATION CHANGED ^^^^^^^^^^^^");

                AppLogger.i(TAG, "lat:" + location.getLatitude() + " lon:" + location.getLongitude());

                getModel().setValue(q.getLabelC(), location.getLatitude() + ", " + location.getLongitude());

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {


            }

            @Override
            public void onProviderEnabled(String s) {
                AppLogger.i(TAG, "^^^^^^^^^^ PROVIDER ENABLED ^^^^^^^^^^^^");

            }

            @Override
            public void onProviderDisabled(String s) {
                AppLogger.i(TAG, "^^^^^^^^^^ PROVIDER DISABLED ^^^^^^^^^^^^");


            }
        };
    }


    private File createTemporaryFile(String part, String ext) throws Exception {

        File dir = new File(ROOT_DIR + File.separator + ".temp/");
        if (!dir.exists()) Timber.i("Is DIR created?  %s", dir.mkdirs());
        Timber.i("Destination path is %s", dir);


        return File.createTempFile(part, ext, dir);
    }


    boolean hasPermissions(Context context, String permission) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {

            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {


                }
                return false;
            }

        }
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {


            Log.d(TAG, "RESULT CODE = " + resultCode + " REQUEST CODE " + requestCode);


            if (resultCode == RESULT_OK) {

                Log.d(TAG, " REQUEST CODE IS OK");

                if (requestCode == CAMERA_INTENT) {


                    try {
                        String BASE64_STRING;

                        Bitmap bitmap = ImageUtil.handleSamplingAndRotationBitmap(getActivity(), URI);
                        BASE64_STRING = ImageUtil.bitmapToBase64(bitmap);
                        getModel().setValue(ID, BASE64_STRING);

                        CustomToast.makeToast(getActivity(), getResources().getString(R.string.click_photo_to_delete), Toast.LENGTH_LONG).show();


                    } catch (Exception e) {
                        CustomToast.makeToast(getActivity(), "Failed to load image", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Failed to load", e);
                    }





/*
                    Log.d(TAG, "CAMERA INTENT");


                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");

                    //imageView.setImageBitmap(imageBitmap);

                    String value = ImageUtil.bitmapToBase64(imageBitmap);
                    getModel().setValue(ID, value);

                    //Log.i(TAG, "***** Value for Photo was " + value);

                    CustomToast.makeToast(getActivity(), getResources().getString(R.string.click_photo_to_delete), Toast.LENGTH_LONG).show();

                    */

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
