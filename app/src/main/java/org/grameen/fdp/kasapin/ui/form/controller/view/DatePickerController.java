package org.grameen.fdp.kasapin.ui.form.controller.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.grameen.fdp.kasapin.R;
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
 * Represents a field that allows selecting a specific date via a date picker.
 * <p/>
 * For the field value, the associated FormModel must return a {@link Date} instance. No selected date can be
 * represented by returning {@code null} for the value of the field.
 */
public class DatePickerController extends MyLabeledFieldController {
    private final int editTextId = MyFormController.generateViewId();
    private final SimpleDateFormat displayFormat;
    private final TimeZone timeZone;
    boolean isEnabled = true;
    private DatePickerDialog datePickerDialog = null;

    /**
     * Constructs a new instance of a date picker field.
     *
     * @param ctx           the Android context
     * @param name          the name of the field
     * @param labelText     the label to display beside the field. Set to {@code null} to not show a label.
     * @param validators    contains the validations to process on the field
     * @param displayFormat the format of the date to show in the text box when a date is set
     */
    public DatePickerController(Context ctx, String name, String content_desc, String labelText, Set<InputValidator> validators, SimpleDateFormat displayFormat, boolean enable) {
        super(ctx, name, content_desc, labelText, validators);
        this.displayFormat = displayFormat;
        this.timeZone = displayFormat.getTimeZone();
        this.isEnabled = enable;
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
    public DatePickerController(Context ctx, String name, String content_desc, String labelText, boolean isRequired, SimpleDateFormat displayFormat, boolean enable) {
        super(ctx, name, content_desc, labelText, isRequired);
        this.displayFormat = displayFormat;
        this.timeZone = displayFormat.getTimeZone();
        this.isEnabled = enable;
    }

    /**
     * Constructs a new instance of a date picker field, with the selected date displayed in "MMM d, yyyy" format.
     *
     * @param name      the name of the field
     * @param labelText the label to display beside the field
     */
    public DatePickerController(Context context, String name, String content_desc, String labelText, boolean enable, Boolean isRequired, Set<InputValidator> validators) {
        this(context, name, content_desc, labelText, false, new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()), enable);
        if (isRequired)
            this.setValidators(validators);
    }

    @Override
    protected View createFieldView() {
        if (isEnabled) {
            final MaterialEditText editText = new MaterialEditText(getContext());
            editText.setId(editTextId);
            editText.setContentDescription(getContentDesc());
            editText.setSingleLine(true);
            editText.setTextSize(15f);
            editText.setPaddings(20, 0, 0, 0);
            editText.setInputType(InputType.TYPE_CLASS_DATETIME);
            editText.setKeyListener(null);
            refresh(editText);
            editText.setOnClickListener(v -> showDatePickerDialog(getContext(), editText));

            editText.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    showDatePickerDialog(getContext(), editText);
                }
            });

            editText.setEnabled(isEnabled);
            return editText;
        } else
            return inflateViewOnlyView();
    }

    private void showDatePickerDialog(final Context context, final EditText editText) {
        // don't show dialog again if it's already being shown
        if (datePickerDialog == null) {
            Date date = new Date();

            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            calendar.setTimeZone(timeZone);
            calendar.setTime(date);

            datePickerDialog = new DatePickerDialog(context, R.style.DatePickerSpinner, (view, year, monthOfYear, dayOfMonth) -> {
                calendar.set(year, monthOfYear, dayOfMonth);
                String date1 = displayFormat.format(calendar.getTime());
                getModel().setValue(getName(), date1);
                editText.setText(date1);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setOnDismissListener(dialog -> datePickerDialog = null);
            datePickerDialog.show();
        }
    }

    private EditText getEditText() {
        return (EditText) getView().findViewById(editTextId);
    }

    private void refresh(EditText editText) {
        String value = null;
        if (getModel().getValue(getName()) != null) {
            value = getModel().getValue(getName()).toString();
        }
        editText.setHint(value != null ? value : "Click to add date");
    }

    public void refresh() {
        refresh(getEditText());
    }
}
