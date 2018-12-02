package org.grameen.fdp.kasapin.ui.form.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;


import org.grameen.fdp.kasapin.ui.form.MyFormController;
import org.grameen.fdp.kasapin.ui.form.model.FormModel;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aangjnr on 05/01/2018.
 */

public class MonitoringCustomController extends MonitoringLabeledFieldController {

    private final int spinnerId = MyFormController.generateViewId();
    private final int competenceId = MyFormController.generateViewId();
    private final int reasonForFailureId = MyFormController.generateViewId();


    JSONObject jsonObject = new JSONObject();

    boolean IS_ENABLED = true;

    private final List<List<String>> itemsLists;
    private final List<String> values;

    List<String> options = new ArrayList<>();



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


    /**
     * Constructs a selection field
     *
     * @param ctx        the Android context
     * @param name       the name of the field
     * @param labelText  the label to display beside the field
     *                   the same order as the {@code items}.
     */


    public MonitoringCustomController(Context ctx, String name, String content_desc, String labelText, List<List<String>> itemsLists, boolean isEnabled) {
        super(ctx, name, content_desc, labelText, isEnabled);
        this.itemsLists = itemsLists;
        this.IS_ENABLED = isEnabled;
        values = null;
        options = new ArrayList<>();
        options.add("one");
        options.add("two");
        options.add("three");
        options.add("four");
        options.add("five");
    }


    public MonitoringCustomController(Context ctx, String name, String content_desc, String labelText, List<List<String>> itemsLists, List<String> values, boolean isEnabled) {
        super(ctx, name, content_desc, labelText, isEnabled);
        this.itemsLists = itemsLists;
        this.IS_ENABLED = isEnabled;
        this.values = values;

    }




    /**
     * Constructs a selection field
     *
     * @param ctx              the Android context
     * @param name             the name of the field
     * @param labelText        the label to display beside the field. Set to {@code null} to not show a label.
     *                         *                         the same string of the selected item when getting or setting the field; otherwise,
     *                         {@code SelectionController} expects the form model to use index (as an Integer) to
     *                         represent the selected item
     */


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

    /**
     * Returns the Spinner view associated with this element.
     *
     * @return the Spinner view associated with this element
     */
    public Spinner getSpinner() {
        return (Spinner) getView().findViewById(spinnerId);
    }


    public Spinner getSpinnerCompetence() {
        return (Spinner) getView().findViewById(competenceId);
    }


    public Spinner getSpinnerReasonForFailure() {
        return (Spinner) getView().findViewById(reasonForFailureId);
    }


    @Override
    protected View createFieldView() {

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        Spinner spinnerView = new Spinner(getContext());
        spinnerView.setId(spinnerId);
        spinnerView.setContentDescription(getContentDesc());
        spinnerView.setPrompt("Select");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, itemsLists.get(0)) {
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
                    if (pos == itemsLists.get(0).size() - 1) {
                        value = null;
                    } else {    // if something is selected, set the value on the model
                        value = values.get(pos);
                    }
                }

                getModel().setValue(getName(), value);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        refresh(spinnerView);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 2;

        linearLayout.addView(spinnerView, params);

//        spinnerView.setEnabled(IS_ENABLED);




        Spinner spinnerView2 = new Spinner(getContext());
        spinnerView2.setId(competenceId);
        spinnerView2.setPrompt("Select");
        spinnerView2.setContentDescription(getContentDesc());
        ArrayAdapter<String> spinnerAdapter2 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, itemsLists.get(1) ) {
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
        spinnerAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerView2.setAdapter(spinnerAdapter2);

        spinnerView2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object value;
                // if no values are specified, set the index on the model
                if (values == null) {
                    value = pos;
                } else {
                    // last pos indicates nothing is selected
                    if (pos == itemsLists.get(0).size() - 1) {
                        value = null;
                    } else {    // if something is selected, set the value on the model
                        value = values.get(pos);
                    }
                }

               // getModel().setValue(getName(), value);




            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        refresh(spinnerView2);




        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 3;

        linearLayout.addView(spinnerView2, params2);








        Spinner spinnerView3 = new Spinner(getContext());
        spinnerView3.setId(reasonForFailureId);
        spinnerView3.setPrompt("Select");
        spinnerView3.setContentDescription(getContentDesc());
        ArrayAdapter<String> spinnerAdapter3 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,  itemsLists.get(2)) {
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
        spinnerAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerView3.setAdapter(spinnerAdapter3);

        spinnerView3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object value;
                // if no values are specified, set the index on the model
                if (values == null) {
                    value = pos;
                } else {
                    // last pos indicates nothing is selected
                    if (pos == itemsLists.get(0).size() - 1) {
                        value = null;
                    } else {    // if something is selected, set the value on the model
                        value = values.get(pos);
                    }
                }

                // getModel().setValue(getName(), value);




            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        refresh(spinnerView3);

        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 3;

        linearLayout.addView(spinnerView3, params3);


        return linearLayout;

    }

    private void refresh(Spinner spinner) {
        //Todo modify


        Object value = getModel().getValue(getName());
        int selectionIndex = options.size() - 1;    // index of last item shows the 'prompt'

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
        refresh(getSpinnerCompetence());
        refresh(getSpinnerReasonForFailure());

    }


    @Override
    public FormModel getModel() {

        Log.i("MONITORING CUSTOM CON",  "GET MODEL" );





        return super.getModel();

    }
}
