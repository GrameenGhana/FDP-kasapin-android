package org.grameen.fdp.kasapin.ui.form.controller.view;

import android.app.TimePickerDialog;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import org.grameen.fdp.kasapin.ui.form.InputValidator;
import org.grameen.fdp.kasapin.ui.form.MyFormController;
import org.grameen.fdp.kasapin.ui.form.controller.MyLabeledFieldController;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

/**
 * Created by aangjnr on 05/01/2018.
 */

public class TimePickerController extends MyLabeledFieldController {
    private final int editTextId = MyFormController.generateViewId();
    private final SimpleDateFormat displayFormat;
    private final TimeZone timeZone;
    private final boolean is24HourView;
    private TimePickerDialog timePickerDialog = null;

    /**
     * Constructs a new instance of a time picker field.
     *
     * @param ctx           the Android context
     * @param name          the name of the field
     * @param labelText     the label to display beside the field. Set to {@code null} to not show a label.
     * @param validators    contains the validations to process on the field
     * @param displayFormat the format of the time to show in the text box when a time is set
     * @param is24HourView  the format of time picker dialog should be 24 hour format or not
     */
    public TimePickerController(Context ctx, String name, String content_desc, String labelText, Set<InputValidator> validators, SimpleDateFormat displayFormat, boolean is24HourView) {
        super(ctx, name, content_desc, labelText, validators);
        this.displayFormat = displayFormat;
        this.timeZone = displayFormat.getTimeZone();
        this.is24HourView = is24HourView;
    }


    /**
     * Constructs a new instance of a time picker field.
     *
     * @param ctx           the Android context
     * @param name          the name of the field
     * @param labelText     the label to display beside the field. Set to {@code null} to not show a label.
     * @param isRequired    indicates if the field is required or not
     * @param displayFormat the format of the time to show in the text box when a time is set
     * @param is24HourView  the format of time picker dialog should be 24 hour format or not
     */
    public TimePickerController(Context ctx, String name, String content_desc, String labelText, boolean isRequired, SimpleDateFormat displayFormat, boolean is24HourView) {
        super(ctx, name, content_desc, labelText, isRequired);
        this.displayFormat = displayFormat;
        this.timeZone = displayFormat.getTimeZone();
        this.is24HourView = is24HourView;
    }

    /**
     * Constructs a new instance of a time picker field, with the selected time displayed in "HH:mm" format.
     *
     * @param name      the name of the field
     * @param labelText the label to display beside the field
     */
    public TimePickerController(Context context, String name, String content_desc, String labelText, Boolean isRequired, Set<InputValidator> validators) {
        this(context, name, content_desc, labelText, false, new SimpleDateFormat("hh:mm a", Locale.getDefault()), false);
        if (isRequired)
            this.setValidators(validators);
    }

    @Override
    protected View createFieldView() {
        final EditText editText = new EditText(getContext());
        editText.setId(editTextId);
        editText.setContentDescription(getContentDesc());
        editText.setSingleLine(true);
        editText.setInputType(InputType.TYPE_CLASS_DATETIME);
        editText.setKeyListener(null);
        refresh(editText);
        editText.setOnClickListener(v -> showTimePickerDialog(getContext(), editText));

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showTimePickerDialog(getContext(), editText);
            }
        });

        return editText;
    }

    private void showTimePickerDialog(Context context, final EditText editText) {
        // don't show dialog again if it's already being shown
        if (timePickerDialog == null) {
            Date date = (Date) getModel().getValue(getName());
            if (date == null) {
                date = new Date();
            }
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTimeZone(timeZone);
            calendar.setTime(date);

            timePickerDialog = new TimePickerDialog(context, (view, hourOfDay, minute) -> {
                Calendar calendar1 = Calendar.getInstance(Locale.getDefault());
                calendar1.setTimeZone(timeZone);
                calendar1.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar1.set(Calendar.MINUTE, minute);
                getModel().setValue(getName(), calendar1.getTime());
                editText.setText(displayFormat.format(calendar1.getTime()));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), is24HourView);

            timePickerDialog.setOnDismissListener(dialog -> timePickerDialog = null);

            timePickerDialog.show();
        }
    }

    private EditText getEditText() {
        return (EditText) getView().findViewById(editTextId);
    }

    private void refresh(EditText editText) {
        Date value = (Date) getModel().getValue(getName());
        editText.setText(value != null ? displayFormat.format(value) : "");
    }

    @Override
    public void refresh() {
        refresh(getEditText());
    }
}
