package org.grameen.fdp.kasapin.ui.form.controller.view;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.grameen.fdp.kasapin.ui.form.InputValidator;
import org.grameen.fdp.kasapin.ui.form.MyFormController;
import org.grameen.fdp.kasapin.ui.form.controller.MyLabeledFieldController;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Set;

public class EditTextController extends MyLabeledFieldController {
    private final int editTextId = MyFormController.generateViewId();
    private final String placeholder;
    private boolean IS_ENABLED;
    private String helperText;
    private int inputType;

    /**
     * Constructs a new instance of an edit text field.
     *
     * @param ctx         the Android context
     * @param name        the name of the field
     * @param labelText   the label to display beside the field. Set to {@code null} to not show a label.
     * @param placeholder a placeholder text to show when the input field is empty. If null, no placeholder is displayed
     * @param validators  contains the validations to process on the field
     * @param inputType   the content type of the text box as a mask; possible values are defined by {@link InputType}.
     *                    For example, to enable multi-line, enable {@code InputType.TYPE_TEXT_FLAG_MULTI_LINE}.
     */
    private EditTextController(Context ctx, String name, String content_desc, String labelText, String placeholder, Set<InputValidator> validators, int inputType, boolean isEnabled) {
        super(ctx, name, content_desc, labelText, validators, isEnabled);
        this.placeholder = placeholder;
        this.inputType = inputType;
        this.IS_ENABLED = isEnabled;
    }

    /**
     * Constructs a new instance of an edit text field.
     *
     * @param ctx         the Android context
     * @param name        the name of the field
     * @param labelText   the label to display beside the field
     * @param placeholder a placeholder text to show when the input field is empty. If null, no placeholder is displayed
     * @param validators  contains the validations to process on the field
     */
    public EditTextController(Context ctx, String name, String content_desc, String labelText, String placeholder, Set<InputValidator> validators, boolean isEnabled) {
        this(ctx, name, content_desc, labelText, placeholder, validators, InputType.TYPE_CLASS_TEXT, isEnabled);
    }

    public EditTextController(Context context, String id, String content_desc, String caption__c, String default_value__c, boolean isRequired, int typeClassText, boolean isEnabled, String help_text__c,
                              Set<InputValidator> validators) {
        this(context, id, content_desc, caption__c, default_value__c, isRequired, typeClassText, isEnabled);
        this.helperText = help_text__c;

        if (isRequired)
            this.setValidators(validators);
    }

    /**
     * Constructs a new instance of an edit text field.
     *
     * @param ctx         the Android context
     * @param name        the name of the field
     * @param labelText   the label to display beside the field. Set to {@code null} to not show a label.
     * @param placeholder a placeholder text to show when the input field is empty. If null, no placeholder is displayed
     * @param isRequired  indicates if the field is required or not
     * @param inputType   the content type of the text box as a mask; possible values are defined by {@link InputType}.
     *                    For example, to enable multi-line, enable {@code InputType.TYPE_TEXT_FLAG_MULTI_LINE}.
     */
    public EditTextController(Context ctx, String name, String content_desc, String labelText, String placeholder, boolean isRequired, int inputType, boolean isEnabled) {
        super(ctx, name, content_desc, labelText, isRequired, isEnabled);
        this.placeholder = placeholder;
        this.inputType = inputType;
        IS_ENABLED = true;
        this.IS_ENABLED = isEnabled;
    }

    /**
     * Constructs a new instance of an edit text field.
     *
     * @param ctx         the Android context
     * @param name        the name of the field
     * @param labelText   the label to display beside the field
     * @param placeholder a placeholder text to show when the input field is empty. If null, no placeholder is displayed
     * @param isRequired  indicates if the field is required or not
     */
    public EditTextController(Context ctx, String name, String content_desc, String labelText, String placeholder, boolean isRequired, boolean isEnabled, String helperText) {
        this(ctx, name, content_desc, labelText, placeholder, isRequired, InputType.TYPE_CLASS_TEXT, isEnabled);
        this.helperText = helperText;
    }

    /**
     * Constructs a new instance of an edit text field.
     *
     * @param ctx         the Android context
     * @param name        the name of the field
     * @param labelText   the label to display beside the field
     * @param placeholder a placeholder text to show when the input field is empty. If null, no placeholder is displayed
     */
    public EditTextController(Context ctx, String name, String content_desc, String labelText, String placeholder, boolean isEnabled) {
        this(ctx, name, content_desc, labelText, placeholder, false, InputType.TYPE_CLASS_TEXT, isEnabled);
    }

    /**
     * Constructs a new instance of an edit text field.
     *
     * @param ctx       the Android context
     * @param name      the name of the field
     * @param labelText the label to display beside the field
     */
    public EditTextController(Context ctx, String name, String content_desc, String labelText, boolean isEnabled) {
        this(ctx, name, content_desc, labelText, null, false, InputType.TYPE_CLASS_TEXT, isEnabled);
    }

    /**
     * Returns the EditText view associated with this element.
     *
     * @return the EditText view associated with this element
     */
    private EditText getEditText() {
        return (EditText) getView().findViewById(editTextId);
    }

    /**
     * Returns a mask representing the content input type. Possible values are defined by {@link InputType}.
     *
     * @return a mask representing the content input type
     */
    public int getInputType() {
        return inputType;
    }

    private void setInputTypeMask(int mask, boolean enabled) {
        if (enabled) {
            inputType = inputType | mask;
        } else {
            inputType = inputType & ~mask;
        }
        if (isViewCreated()) {
            getEditText().setInputType(inputType);
        }
    }

    /**
     * Indicates whether this text box has multi-line enabled.
     *
     * @return true if this text box has multi-line enabled, or false otherwise
     */
    @SuppressWarnings("IncompatibleBitwiseMaskOperation")
    public boolean isMultiLine() {
        return (inputType | InputType.TYPE_TEXT_FLAG_MULTI_LINE) != 0;
    }

    /**
     * Enables or disables multi-line input for the text field. Default is false.
     *
     * @param multiLine if true, multi-line input is allowed, otherwise, the field will only allow a single line.
     */
    public void setMultiLine(boolean multiLine) {
        setInputTypeMask(InputType.TYPE_TEXT_FLAG_MULTI_LINE, multiLine);
    }

    /**
     * Indicates whether this text field hides the input text for security reasons.
     *
     * @return true if this text field hides the input text, or false otherwise
     */
    @SuppressWarnings("IncompatibleBitwiseMaskOperation")
    public boolean isSecureEntry() {
        return (inputType | InputType.TYPE_TEXT_VARIATION_PASSWORD) != 0;
    }

    /**
     * Enables or disables secure entry for this text field. If enabled, input will be hidden from the user. Default is
     * false.
     *
     * @param isSecureEntry if true, input will be hidden from the user, otherwise input will be visible.
     */
    public void setSecureEntry(boolean isSecureEntry) {
        setInputTypeMask(InputType.TYPE_TEXT_VARIATION_PASSWORD, isSecureEntry);
    }

    @Override
    protected View createFieldView() {
        if (IS_ENABLED) {
            final MaterialEditText editText = new MaterialEditText(getContext());
            editText.setId(editTextId);
            editText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_NONE);
            editText.setContentDescription(getContentDesc());
            editText.setTextSize(15f);
            editText.setSingleLine(!isMultiLine());
            editText.setInputType(inputType);

            if (placeholder != null) {
                if (getModel().getValue(getName()) == null || getModel().getValue(getName()).toString().equalsIgnoreCase(""))
                    getModel().setValue(getName(), placeholder);
                else
                    getModel().setValue(getName(), getModel().getValue(getName()));
            }
            try {
                refresh(editText);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (helperText != null) {
                editText.setHelperText(helperText);
                editText.setHelperTextAlwaysShown(true);
            }

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (inputType == InputType.TYPE_NUMBER_FLAG_DECIMAL) {
                        double doubleValue = 0;
                        if (editText.getText() != null) {
                            try {
                                NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                                DecimalFormat formatter = (DecimalFormat) nf;
                                formatter.applyPattern("#,###,###.##");
                                doubleValue = Double.parseDouble(editText.getText().toString().replace(",", ""));
                                getModel().setValue(getName(), formatter.format(doubleValue));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                getModel().setValue(getName(), editText.getText().toString());
                            }
                        }
                    } else
                        getModel().setValue(getName(), editText.getText().toString());
                }
            });
            editText.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    double doubleValue = 0;
                    if (editText.getText() != null) {
                        if (getModel().getValue(getName()) != null)
                            editText.setText(getModel().getValue(getName()).toString());
                    }
                }
            });
            editText.setEnabled(IS_ENABLED);
            return editText;
        } else
            return inflateViewOnlyView();

    }

    @Override
    public void setValidators(Set<InputValidator> newValidators) {
        super.setValidators(newValidators);
    }

    private void refresh(EditText editText) {
        if (editText != null) {
            Object value = getModel().getValue(getName());
            String valueStr = value != null ? value.toString() : placeholder;
            if (editText.getText() != null)
                if (valueStr != null && !valueStr.equals(editText.getText().toString())) {
                    try {
                        editText.setHint(valueStr);
                    } catch (Exception ignored) {
                    }
                }
        }
    }

    @Override
    public void refresh() {
        refresh(getEditText());
    }
}
