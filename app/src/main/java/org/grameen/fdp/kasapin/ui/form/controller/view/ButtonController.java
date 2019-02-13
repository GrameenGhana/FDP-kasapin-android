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
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.Toast;


import org.grameen.fdp.kasapin.ui.form.InputValidator;
import org.grameen.fdp.kasapin.ui.form.MyFormController;
import org.grameen.fdp.kasapin.ui.form.controller.MyLabeledFieldController;
import org.grameen.fdp.kasapin.utilities.AppLogger;
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
    private final OnClickListener onClickListener;
    Location location;

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
     * @param listener the format of the date to show in the text box when a date is set
     */
    public ButtonController(Context ctx, String name, String content_desc, String labelText, Set<InputValidator> validators, OnClickListener listener) {
        super(ctx, name, content_desc, labelText, validators);
        this.onClickListener = listener;
        this.context = ctx;
    }

    /**
     * Constructs a new instance of a date picker field.
     *
     * @param ctx           the Android context
     * @param name          the name of the field
     * @param labelText     the label to display beside the field. Set to {@code null} to not show a label.
     * @param isRequired    indicates if the field is required or not
     * @param listener the format of the date to show in the text box when a date is set
     */
    public ButtonController(Context ctx, String name, String content_desc, String labelText, boolean isRequired, OnClickListener listener, boolean enabled) {
        super(ctx, name, content_desc, labelText, isRequired);
        this.onClickListener = listener;
        this.context = ctx;
        this.isEnabled = enabled;

    }

    /**
     * Constructs a new instance of a date picker field, with the selected date displayed in "MMM d, yyyy" format.
     *
     * @param name      the name of the field
     * @param labelText the label to display beside the field
     */
    public ButtonController(Context context, String name, String content_desc, String labelText, OnClickListener onClickListener, boolean enabled) {
        this(context, name, content_desc, labelText, false, onClickListener, enabled);
        this.context = context;

    }

    @Override
    protected View createFieldView() {
        final EditText editText = new EditText(getContext());

        editText.setId(editTextId);

        editText.setContentDescription(getContentDesc());
        editText.setSingleLine(true);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setKeyListener(null);
        editText.setHint("Click to obtain location");
        refresh(editText);

        editText.setOnClickListener(onClickListener);


        try {
            editText.setEnabled(isEnabled);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return editText;
    }




    private EditText getEditText() {
        return (EditText) getView().findViewById(editTextId);
    }

    private void refresh(EditText editText) {
        Object value = getModel().getValue(getName());

        if(value != null) {
            String valueStr = value.toString();
            if (!valueStr.equals(editText.getText().toString()))
                editText.setText(valueStr);

        }


    }

    public void refresh() {
        refresh(getEditText());
    }


}







