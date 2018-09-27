package org.grameen.fdp.kasapin.ui.form.controller.view;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.Toast;


import org.grameen.fdp.kasapin.ui.form.InputValidator;
import org.grameen.fdp.kasapin.ui.form.MyFormController;
import org.grameen.fdp.kasapin.ui.form.controller.MyLabeledFieldController;
import org.grameen.fdp.kasapin.utilities.CustomToast;

import java.util.Date;
import java.util.Set;

/**
 * Represents a field that allows selecting a specific date via a date picker.
 * <p/>
 * For the field value, the associated FormModel must return a {@link Date} instance. No selected date can be
 * represented by returning {@code null} for the value of the field.
 */
public class ButtonController extends MyLabeledFieldController {
    private final int editTextId = MyFormController.generateViewId();
    private final LocationListener locationListener;
    Location location;
    boolean GpsStatus = false;
    LocationManager locationManager;
    Context context;
    private DatePickerDialog datePickerDialog = null;
    boolean isEnabled = true;

    /**
     * Constructs a new instance of a date picker field.
     *
     * @param ctx           the Android context
     * @param name          the name of the field
     * @param labelText     the label to display beside the field. Set to {@code null} to not show a label.
     * @param validators    contains the validations to process on the field
     * @param displayFormat the format of the date to show in the text box when a date is set
     */
    public ButtonController(Context ctx, String name, String labelText, Set<InputValidator> validators, LocationListener displayFormat) {
        super(ctx, name, labelText, validators);
        this.locationListener = displayFormat;
        this.context = ctx;
    }

    /**
     * Constructs a new instance of a date picker field.
     *
     * @param ctx           the Android context
     * @param name          the name of the field
     * @param labelText     the label to display beside the field. Set to {@code null} to not show a label.
     * @param isRequired    indicates if the field is required or not
     * @param displayFormat the format of the date to show in the text box when a date is set
     */
    public ButtonController(Context ctx, String name, String labelText, boolean isRequired, LocationListener displayFormat, boolean enabled) {
        super(ctx, name, labelText, isRequired);
        this.locationListener = displayFormat;
        this.context = ctx;
        this.isEnabled = enabled;

    }

    /**
     * Constructs a new instance of a date picker field, with the selected date displayed in "MMM d, yyyy" format.
     *
     * @param name      the name of the field
     * @param labelText the label to display beside the field
     */
    public ButtonController(Context context, String name, String labelText, LocationListener locationListener, boolean enabled) {
        this(context, name, labelText, false, locationListener, enabled);
        this.context = context;

    }

    @Override
    protected View createFieldView() {
        final EditText editText = new EditText(getContext());

        editText.setId(editTextId);
        editText.setContentDescription(getName());
        editText.setSingleLine(true);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setKeyListener(null);
        refresh(editText);
        editText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation(getContext());
                //obtainLocation(getContext());

            }
        });

        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getCurrentLocation(getContext());

                    //obtainLocation(getContext());

                }
            }
        });
        try {

            editText.setEnabled(isEnabled);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return editText;
    }


    private void getCurrentLocation(final Context context) {

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


            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            // This is the Best And IMPORTANT part
            final Looper looper = null;

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                if (locationManager != null) {
                    locationManager.requestSingleUpdate(criteria, locationListener, looper);
                }
            }
        } else {

            CustomToast.makeToast(context, "Please Enable GPS First", Toast.LENGTH_LONG).show();

        }


    }


    private EditText getEditText() {
        return (EditText) getView().findViewById(editTextId);
    }

    private void refresh(EditText editText) {
        Object value = getModel().getValue(getName());
        String valueStr = value != null ? value.toString() : "Click to obtain location";
        if (!valueStr.equals(editText.getText().toString()))
            editText.setText(valueStr);


    }

    public void refresh() {
        refresh(getEditText());
    }


    void obtainLocation(Context context) {

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);


        String Holder;
        final Looper looper = null;


        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        Holder = locationManager.getBestProvider(criteria, false);

        if (GpsStatus) {

            // CustomToast.makeToast(getContext(), "Getting location", Toast.LENGTH_SHORT).show();

            if (Holder != null) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                location = locationManager.getLastKnownLocation(Holder);
                //locationManager.requestLocationUpdates(Holder, 12000, 7, locationListener);

                locationManager.requestSingleUpdate(criteria, locationListener, Looper.myLooper());

                getEditText().setHint("Getting location...");

            }
        } else {

            CustomToast.makeToast(context.getApplicationContext(), "Please Enable GPS First", Toast.LENGTH_LONG).show();

        }
    }

}







