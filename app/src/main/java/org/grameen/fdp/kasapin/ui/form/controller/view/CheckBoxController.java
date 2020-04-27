package org.grameen.fdp.kasapin.ui.form.controller.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.ui.form.InputValidator;
import org.grameen.fdp.kasapin.ui.form.MyFormController;
import org.grameen.fdp.kasapin.ui.form.controller.MyLabeledFieldController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a field that allows a user to select multiple items from checkboxes.
 * <p/>
 * For the field value, the associated FormModel must return either a Set<String> or a Set<0-based index>, representing the
 * currently selected items. Which representation to use is specified by the constructor. In either representation, no
 * selection can be represented by returning {@code null} for the value of the field.
 */
public class CheckBoxController extends MyLabeledFieldController {
    private final int CHECKBOX_ID = MyFormController.generateViewId();
    private final List<String> items;
    private final List<?> values;
    private boolean IS_ENABLED;
    String oldValues;

    /**
     * Constructs a new instance of a checkboxes field.
     *
     * @param ctx              the Android context
     * @param name             the name of the field
     * @param labelText        the label to display beside the field. Set to {@code null} to not show a label
     * @param validators       contains the validations to process on the field
     * @param items            a list of Strings defining the selection items to show
     * @param useItemsAsValues if true, {@code CheckBoxController} expects the associated form model to use
     *                         the same string of the selected item when getting or setting the field; otherwise,
     *                         {@code CheckBoxController} expects the form model to use index (as an Integer) to
     *                         represent the selected item
     */
    public CheckBoxController(Context ctx, String name, String content_desc, String labelText, Set<InputValidator> validators, List<String> items, boolean useItemsAsValues, boolean isEnabled) {
        this(ctx, name, content_desc, labelText, validators, items, useItemsAsValues ? items : null, isEnabled);
    }

    /**
     * Constructs a new instance of a checkboxes field.
     *
     * @param ctx        the Android context
     * @param name       the name of the field
     * @param labelText  the label to display beside the field
     * @param validators contains the validations to process on the field
     * @param items      a list of Strings defining the selection items to show
     * @param values     a list of Objects representing the values to set the form model on a selection (in
     *                   the same order as the {@code items}.
     */
    private CheckBoxController(Context ctx, String name, String content_desc, String labelText, Set<InputValidator> validators, List<String> items, List<?> values, boolean isEnabled) {
        super(ctx, name, content_desc, labelText, validators, isEnabled);
        this.items = items;
        this.values = values;
        this.IS_ENABLED = isEnabled;


        if (values != null && items.size() != values.size()) {
            throw new IllegalArgumentException("Size of Values and Items must be equal.");
        }
    }


    /**
     * Constructs a new instance of a checkboxes field.
     *
     * @param ctx              the Android context
     * @param name             the name of the field
     * @param labelText        the label to display beside the field. Set to {@code null} to not show a label
     * @param isRequired       indicates if the field is required or not
     * @param items            a list of Strings defining the selection items to show
     * @param useItemsAsValues if true, {@code CheckBoxController} expects the associated form model to use
     *                         the same string of the selected item when getting or setting the field; otherwise,
     *                         {@code CheckBoxController} expects the form model to use index (as an Integer) to
     *                         represent the selected item
     */
    /*public CheckBoxController(Context ctx, String name, String labelText, boolean isRequired, List<String> items, boolean useItemsAsValues, boolean isEnabled, String oldValues) {
        this(ctx, name, labelText, isRequired, items, useItemsAsValues ? items : null, isEnabled);

        this.oldValues = oldValues;

        Log.i("CHECK BOX CONTROLLER", oldValues);

    }*/
    public CheckBoxController(Context ctx, String name, String content_desc, String labelText, boolean isRequired, List<String> items, boolean useItemsAsValues, boolean isEnabled,  Set<InputValidator> validators) {
        this(ctx, name, content_desc, labelText, isRequired, items, useItemsAsValues ? items : null, isEnabled);

        if(isRequired)
            this.setValidators(validators);
    }

    /**
     * Constructs a new instance of a checkboxes field.
     *
     * @param ctx        the Android context
     * @param name       the name of the field
     * @param labelText  the label to display beside the field
     * @param isRequired indicates if the field is required or not
     * @param items      a list of Strings defining the selection items to show
     * @param values     a list of Objects representing the values to set the form model on a selection (in
     *                   the same order as the {@code items}.
     */
    private CheckBoxController(Context ctx, String name, String content_desc, String labelText, boolean isRequired, List<String> items, List<?> values, boolean isEnabled) {
        super(ctx, name, content_desc, labelText, isRequired, isEnabled);
        this.items = items;
        this.values = values;
        this.IS_ENABLED = isEnabled;

        if (values != null && items.size() != values.size()) {
            throw new IllegalArgumentException("Size of Values and Items must be equal.");
        }
    }

    @Override
    protected View createFieldView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") ViewGroup checkboxContainer = (ViewGroup) inflater.inflate(R.layout.form_checkbox_container, null);

        if (IS_ENABLED) {
            final MaterialAutoCompleteTextView editText = new MaterialAutoCompleteTextView(getContext());
            editText.setId(CHECKBOX_ID);
            editText.setContentDescription(getContentDesc());
            editText.setTextSize(15f);
            editText.setPaddings(20, 0, 0, 0);
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setKeyListener(null);
            refresh(editText);
            editText.setOnClickListener(v -> showCheckboxDialog(editText));

            editText.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    showCheckboxDialog(editText);
                }
            });

            editText.setEnabled(IS_ENABLED);
            getModel().setValue(getName(), "");
            return editText;
        } else {

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            params.leftMargin = 4;
            params.rightMargin = 4;
            params.topMargin = 4;
            params.bottomMargin = 4;

            TextView textView;
            int nbItem = items.size();
            Set<Object> modelValues = retrieveModelValues();


            for (int index = 0; index < nbItem; index++) {
                if (modelValues.contains(areValuesDefined() ? values.get(index) : index)) {
                    textView = new TextView(getContext());
                    textView.setPadding(8, 4, 8, 4);

                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setCornerRadius(10);
                    drawable.setColor(ContextCompat.getColor(getContext(), R.color.divider));
                    textView.setBackground(drawable);
                    textView.setText(items.get(index).trim());
                    textView.setId(CHECKBOX_ID + index);
                    checkboxContainer.addView(textView, params);
                    //refresh(textView, index);
                    // checkBox.setEnabled(IS_ENABLED);
                }
            }
        }
        return checkboxContainer;


    }

    public void refresh(CheckBox checkbox, int index) {
        Set<Object> modelValues = retrieveModelValues();
        checkbox.setChecked(
                modelValues.contains(
                        areValuesDefined() ? values.get(index) : index
                )
        );
    }


    @Override
    public void refresh() {
        ViewGroup layout = getContainer();

       /* CheckBox checkbox;
        int nbItem = items.size();
        for (int index = 0; index < nbItem; index++) {
            checkbox = (CheckBox) layout.findViewById(CHECKBOX_ID + index);
            refresh(checkbox, index);
        }*/
    }

    /**
     * Returns the status of the values entry.
     *
     * @return true if values entry can be used. false otherwise.
     */
    private boolean areValuesDefined() {
        return values != null;
    }

    /**
     * Returns the values hold in the model.
     *
     * @return The values from the model.
     */
    private Set<Object> retrieveModelValues() {
        String value;
        try {
            value = getModel().getValue(getName()).toString();
        } catch (NullPointerException ignore) {
            value = "";
        }
        Set<Object> modelValues = new HashSet<>();
        for (int i = 0; i < items.size(); i++) {
            if (value.contains(items.get(i))) {
                modelValues.add(items.get(i));
            }
        }
        return modelValues;
    }

    /**
     * Returns the View containing the checkboxes.
     *
     * @return The View containing the checkboxes.
     */
    private ViewGroup getContainer() {
        return (ViewGroup) getView().findViewById(R.id.form_checkbox_container);
    }


    private void showCheckboxDialog(EditText editText) {
        Set<Object> modelValues = retrieveModelValues();
        final boolean[] checked = new boolean[items.size()];
        for (int index = 0; index < checked.length; index++)
            checked[index] = modelValues.contains(areValuesDefined() ? values.get(index) : index);

        String[] arr = new String[items.size()];
        items.toArray(arr);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppDialog);
        builder.setMultiChoiceItems(arr, checked, (dialog, which, isChecked) -> {

        });

        builder.setCancelable(false);
        builder.setTitle("Select options");
        builder.setPositiveButton("OK", (dialog, which) -> {
            Set<Object> newValues = new HashSet<>(retrieveModelValues());

            for (int i = 0; i < checked.length; i++) {
                boolean isChecked = checked[i];
                if (isChecked) {
                    newValues.add(items.get(i));
                } else
                    newValues.remove(items.get(i));
            }
            if (!newValues.isEmpty())
                getModel().setValue(getName(), newValues);
            else
                getModel().setValue(getName(), "");

            editText.setText(getModel().getValue(getName()).toString());
        });

        // Set the negative/no button click listener
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Do something when click the negative button
            dialog.dismiss();
        });

        AlertDialog dialog1 = builder.create();
        dialog1.show();
    }


    private void refresh(EditText editText) {
        String value = null;
        if (getModel().getValue(getName()) != null) {
            value = getModel().getValue(getName()).toString();
        }
        editText.setHint(value != null ? value : "Click to select");
    }


}