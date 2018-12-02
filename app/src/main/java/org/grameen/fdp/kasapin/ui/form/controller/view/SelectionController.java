package org.grameen.fdp.kasapin.ui.form.controller.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.ui.form.InputValidator;
import org.grameen.fdp.kasapin.ui.form.MyFormController;
import org.grameen.fdp.kasapin.ui.form.controller.MyLabeledFieldController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by aangjnr on 05/01/2018.
 */

public class SelectionController extends MyLabeledFieldController {

    private final int spinnerId = MyFormController.generateViewId();

    private final String prompt;
    private final List<String> items;
    private final List<?> values;
    boolean IS_ENABLED = true;
    PopupWindow popupWindow;

    String helperText = null;


    /**
     * Constructs a selection field
     *
     * @param ctx              the Android context
     * @param name             the name of the field
     * @param labelText        the label to display beside the field. Set to {@code null} to not show a label.
     * @param validators       contains the validations to process on the field
     * @param prompt           if nothing is selected, 'prompt' is displayed
     * @param items            a list of Strings defining the selection items to show
     * @param useItemsAsValues if true, {@code SelectionController} expects the associated form model to use
     *                         the same string of the selected item when getting or setting the field; otherwise,
     *                         {@code SelectionController} expects the form model to use index (as an Integer) to
     *                         represent the selected item
     */
    public SelectionController(Context ctx, String name, String content_desc, String labelText, Set<InputValidator> validators, String prompt, List<String> items, boolean useItemsAsValues, boolean isEnabled) {
        this(ctx, name, content_desc, labelText, validators, prompt, items, useItemsAsValues ? items : null, isEnabled);
    }

    /**
     * Constructs a selection field
     *
     * @param ctx        the Android context
     * @param name       the name of the field
     * @param labelText  the label to display beside the field
     * @param validators contains the validations to process on the field
     * @param prompt     if nothing is selected, 'prompt' is displayed
     * @param items      a list of Strings defining the selection items to show
     * @param values     a list of Objects representing the values to set the form model on a selection (in
     *                   the same order as the {@code items}.
     */
    public SelectionController(Context ctx, String name, String content_desc, String labelText, Set<InputValidator> validators, String prompt, List<String> items, List<?> values, boolean isEnabled) {
        super(ctx, name, content_desc, labelText, validators, isEnabled);
        this.prompt = prompt;
        this.items = new ArrayList<>(items);
        this.items.add(prompt);     // last item is used for the 'prompt' by the SpinnerView
        this.values = new ArrayList<>(values);
        this.IS_ENABLED = isEnabled;

    }

    /**
     * Constructs a selection field
     *
     * @param ctx              the Android context
     * @param name             the name of the field
     * @param labelText        the label to display beside the field. Set to {@code null} to not show a label.
     * @param isRequired       indicates if the field is required or not
     * @param prompt           if nothing is selected, 'prompt' is displayed
     * @param items            a list of Strings defining the selection items to show
     * @param useItemsAsValues if true, {@code SelectionController} expects the associated form model to use
     *                         the same string of the selected item when getting or setting the field; otherwise,
     *                         {@code SelectionController} expects the form model to use index (as an Integer) to
     *                         represent the selected item
     */
    public SelectionController(Context ctx, String name, String content_desc, String labelText, boolean isRequired, String prompt, List<String> items, boolean useItemsAsValues, boolean isEnabled, String helperText) {
        this(ctx, name, content_desc, labelText, isRequired, prompt, items, useItemsAsValues ? items : null, isEnabled, helperText);
    }

    /**
     * Constructs a selection field
     *
     * @param ctx        the Android context
     * @param name       the name of the field
     * @param labelText  the label to display beside the field
     * @param isRequired indicates if the field is required or not
     * @param prompt     if nothing is selected, 'prompt' is displayed
     * @param items      a list of Strings defining the selection items to show
     * @param values     a list of Objects representing the values to set the form model on a selection (in
     *                   the same order as the {@code items}.
     */
    public SelectionController(Context ctx, String name, String content_desc, String labelText, boolean isRequired, String prompt, List<String> items, List<?> values, boolean isEnabled, String helperText) {
        super(ctx, name, content_desc, labelText, isRequired, isEnabled);
        this.prompt = prompt;
        this.items = new ArrayList<>(items);
        this.items.add(prompt);     // last item is used for the 'prompt' by the SpinnerView
        this.values = new ArrayList<>(values);
        this.IS_ENABLED = isEnabled;
        this.helperText = helperText;
    }

    /**
     * Returns the Spinner view associated with this element.
     *
     * @return the Spinner view associated with this element
     */
    public Spinner getSpinner() {
        return (Spinner) getView().findViewById(spinnerId);
    }

    @Override
    protected View createFieldView() {

        int id;
        AtomicInteger nextGeneratedViewId = new AtomicInteger(1);
        id = nextGeneratedViewId.get();

        RelativeLayout linearLayout = new RelativeLayout(getContext());
       // linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        Spinner spinnerView = new Spinner(getContext());
        spinnerView.setContentDescription(getContentDesc());
        spinnerView.setId(spinnerId);
        spinnerView.setPrompt(prompt);
         ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, items) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    TextView itemView = ((TextView) view.findViewById(android.R.id.text1));
                    itemView.setText("");
                    itemView.setHint(getItem(getCount()));
                }




                return view;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1; // don't display last item (it's used for the prompt)
            }
        };
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerView.setAdapter(spinnerAdapter);

        spinnerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object value;
                // if no values are specified, set the index on the model
                if (values == null) {
                    value = pos;
                } else {
                    // last pos indicates nothing is selected
                    if (pos == items.size() - 1) {

                        if(prompt != null)
                        value = prompt;
                        else value = null;

                    } else {    // if something is selected, set the value on the model
                        value = values.get(pos);
                    }
                }

                getModel().setValue(getName(), value);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                if(prompt != null)
                getModel().setValue(getName(), prompt);

            }
        });

        refresh(spinnerView);

        spinnerView.setEnabled(IS_ENABLED);



           RelativeLayout.LayoutParams spinnerViewLayoutParamsparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
           spinnerViewLayoutParamsparams.addRule(RelativeLayout.LEFT_OF, id);

           spinnerViewLayoutParamsparams.rightMargin = 5;

        spinnerView.setLayoutParams(spinnerViewLayoutParamsparams);

        spinnerView.requestLayout();

        linearLayout.addView(spinnerView);


        if(helperText != null && !helperText.equalsIgnoreCase("null") && !helperText.equalsIgnoreCase("")) {

            final ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.drawable.ic_info_grey_400_18dp);
            imageView.setPadding(4, 4, 4, 4);
            imageView.setId(id);
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
            imageView.setBackgroundResource(outValue.resourceId);
            imageView.setClickable(true);
            imageView.setFocusable(true);


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (popupWindow != null && popupWindow.isShowing())
                        popupWindow.dismiss();

                    else if (getContext() != null) {

                        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = layoutInflater.inflate(R.layout.popup_info_layout, null);
                        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        TextView infoText = popupView.findViewById(R.id.info_text);
                        infoText.setText(helperText);

                        popupWindow.setBackgroundDrawable(new BitmapDrawable());
                        popupWindow.setOutsideTouchable(true);

                        popupWindow.setFocusable(false);
                        popupWindow.showAsDropDown(imageView, 0, 5);

                    }


                }
            });

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.leftMargin = 10;
            imageView.setLayoutParams(params);

            imageView.requestLayout();

            linearLayout.addView(imageView);

          //  linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //linearLayout.requestLayout();


        }


        return linearLayout;
    }

    private void refresh(Spinner spinner) {
        Object value = getModel().getValue(getName());
        int selectionIndex = items.size() - 1;    // index of last item shows the 'prompt'

        if (values != null) {
            for (int i = 0; i < values.size(); i++) {
                if (values.get(i).equals(value)) {
                    selectionIndex = i;
                    break;
                }
            }
        } else if (value instanceof Integer) {
            selectionIndex = (Integer) value;
        }

        spinner.setSelection(selectionIndex);
    }


    @Override
    public void refresh() {

        refresh(getSpinner());

    }
}
