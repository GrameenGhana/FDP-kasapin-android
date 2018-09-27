package org.grameen.fdp.kasapin.ui.form.controller;

import android.content.Context;
import android.view.View;

import org.grameen.fdp.kasapin.ui.form.model.FormModel;


/**
 * Created by aangjnr on 05/01/2018.
 */

public abstract class MyFormElementController {
    private final Context context;
    private final String name;
    private FormModel model;
    private View view;

    /**
     * Constructs a new instance with the specified name.
     *
     * @param ctx  the Android context
     * @param name the name of this instance
     */
    protected MyFormElementController(Context ctx, String name) {
        this.context = ctx;
        this.name = name;
    }

    /**
     * Returns the Android context associated with this element.
     *
     * @return the Android context associated with this element
     */
    public Context getContext() {
        return context;
    }

    /**
     * Returns the name of this form element.
     *
     * @return the name of the element
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the associated model of this form element.
     *
     * @return the associated model of this form element
     */
    public FormModel getModel() {
        return model;
    }

    void setModel(FormModel model) {
        this.model = model;
    }

    /**
     * Returns the associated view for this element.
     *
     * @return the view for this element
     */
    public View getView() {
        if (view == null) {
            view = createView();
        }
        return view;
    }

    /**
     * Indicates if the view has been created.
     *
     * @return true if the view was created, or false otherwise.
     */
    public boolean isViewCreated() {
        return view != null;
    }

    /**
     * Constructs the view for this element.
     *
     * @return a newly created view for this element
     */
    protected abstract View createView();

    /**
     * Refreshes the view of this element to reflect current model.
     */
    public abstract void refresh();

    /**
     * Display an error message on the element.
     *
     * @param message The message to display.
     */
    public abstract void setError(String message);
}
