package org.grameen.fdp.kasapin.ui.form.controller.view;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.ui.form.InputValidator;
import org.grameen.fdp.kasapin.ui.form.MyFormController;
import org.grameen.fdp.kasapin.ui.form.controller.MyLabeledFieldController;
import org.grameen.fdp.kasapin.ui.viewImage.ImageViewActivity;
import org.grameen.fdp.kasapin.utilities.ImageUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.Set;

/**
 * Represents a field that allows selecting a specific date via a date picker.
 * <p/>
 * For the field value, the associated FormModel must return a {@link Date} instance. No selected date can be
 * represented by returning {@code null} for the value of the field.
 */
public class PhotoButtonController extends MyLabeledFieldController {
    private final int editTextId = MyFormController.generateViewId();
    Location location;
    boolean GpsStatus = false;
    View.OnClickListener onClickListener;
    Context context;
    private DatePickerDialog datePickerDialog = null;
    ImageView IMAGE_VIEW;
    boolean isEnabled = true;



    /**
     * Constructs a new instance of a date picker field.
     *
     * @param ctx           the Android context
     * @param name          the name of the field
     * @param labelText     the label to display beside the field. Set to {@code null} to not show a label.
     * @param validators    contains the validations to process on the field
     * @param onClickListener the format of the date to show in the text box when a date is set
     */
    public PhotoButtonController(Context ctx, String name, String labelText, Set<InputValidator> validators, View.OnClickListener onClickListener) {
        super(ctx, name, labelText, validators);
        this.onClickListener = onClickListener;
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
    public PhotoButtonController(Context ctx, String name, String labelText, boolean isRequired, OnClickListener displayFormat) {
        super(ctx, name, labelText, isRequired);
        this.onClickListener = displayFormat;
        this.context = ctx;

    }

    /**
     * Constructs a new instance of a date picker field, with the selected date displayed in "MMM d, yyyy" format.
     *
     * @param name      the name of the field
     * @param labelText the label to display beside the field
     */
    public PhotoButtonController(Context context, String name, String labelText, OnClickListener locationListener, Boolean enabled) {
        this(context, name, labelText, false, locationListener);
        this.context = context;
        this.isEnabled = enabled;

    }

    @Override
    protected View createFieldView() {

        final LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(params);


        @SuppressLint("RestrictedApi") ContextThemeWrapper newContext = new ContextThemeWrapper(context, R.style.PrimaryButton);
        final Button button = new Button(newContext);
        button.setText(context.getString(R.string.take_image));
        button.setContentDescription(getName());
        button.setPadding(0, 20, 0, 20);
        button.setTextSize(12);
        button.setId(editTextId);
        button.setOnClickListener(onClickListener);


        final ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);



        this.IMAGE_VIEW = imageView;

        getModel().addPropertyChangeListener(getName(), new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Log.i("PHOTO BUTTON", evt.getNewValue().toString() );

                if(evt.getNewValue() != null && !evt.getNewValue().toString().equalsIgnoreCase(""))
                try {

                    imageView.setAdjustViewBounds(true);
                    imageView.setMaxHeight(200);
                    imageView.setImageBitmap(ImageUtil.base64ToBitmap(evt.getNewValue().toString()));
                    linearLayout.addView(imageView);
                    linearLayout.requestLayout();

                }catch (Exception e){e.printStackTrace();}

            }
        });

        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppDialog);
                builder.setMessage(R.string.image_options);
                builder.setCancelable(true);
                    builder.setPositiveButton(R.string.view, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(context, ImageViewActivity.class);
                            intent.putExtra("image_string", getModel().getValue(getName()).toString());
                            context.startActivity(intent);


                        }
                    });
                     builder.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {

                             imageView.setImageBitmap(null);

                             getModel().setValue(getName(), "");
                             dialog.dismiss();
                             linearLayout.removeView(imageView);
                             linearLayout.requestLayout();
                         }
                     });
                builder.show();
            }
        });


        try {
            button.setEnabled(isEnabled);
            imageView.setEnabled(isEnabled);
        } catch (Exception e) {
            e.printStackTrace();
        }

        refresh(imageView);

        linearLayout.addView(button);

        linearLayout.addView(imageView);


        linearLayout.requestLayout();

        return linearLayout;
    }




    private Button getButton() {
        return (Button) getView().findViewById(editTextId);
    }


    private ImageView getImageView(){
        return IMAGE_VIEW;

    }

    private void refresh(ImageView imageView) {
        Object value = getModel().getValue(getName());
       if(value != null){

           imageView.setImageBitmap(ImageUtil.base64ToBitmap(value.toString()));
       }


    }

    public void refresh() {
        refresh(getImageView());
    }






}







