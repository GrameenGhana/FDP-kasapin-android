package org.grameen.fdp.kasapin.ui.form.controller.view;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.ui.form.InputValidator;
import org.grameen.fdp.kasapin.ui.form.MyFormController;
import org.grameen.fdp.kasapin.ui.form.controller.MyLabeledFieldController;
import org.grameen.fdp.kasapin.ui.viewImage.ImageViewActivity;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.ImageUtil;

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
    OnClickListener onClickListener;
    Context context;
    ImageView IMAGE_VIEW;
    boolean isEnabled = true;
    private DatePickerDialog datePickerDialog = null;


    /**
     * Constructs a new instance of a date picker field.
     *
     * @param ctx             the Android context
     * @param name            the name of the field
     * @param labelText       the label to display beside the field. Set to {@code null} to not show a label.
     * @param validators      contains the validations to process on the field
     * @param onClickListener the format of the date to show in the text box when a date is set
     */
    public PhotoButtonController(Context ctx, String name, String content_desc, String labelText, Set<InputValidator> validators, OnClickListener onClickListener) {
        super(ctx, name, content_desc, labelText, validators);
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
    public PhotoButtonController(Context ctx, String name, String content_desc, String labelText, boolean isRequired, OnClickListener displayFormat) {
        super(ctx, name, content_desc, labelText, isRequired);
        this.onClickListener = displayFormat;
        this.context = ctx;

    }

    /**
     * Constructs a new instance of a date picker field, with the selected date displayed in "MMM d, yyyy" format.
     *
     * @param name      the name of the field
     * @param labelText the label to display beside the field
     */
    public PhotoButtonController(Context context, String name, String content_desc, String labelText, OnClickListener locationListener, Boolean enabled) {
        this(context, name, content_desc, labelText, false, locationListener);
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
        button.setText("Take a photo");
        button.setContentDescription(getContentDesc());
        button.setPadding(0, 20, 0, 20);
        button.setTextSize(12);
        button.setId(editTextId);
        button.setOnClickListener(onClickListener);


        final ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);


        this.IMAGE_VIEW = imageView;

        getModel().addPropertyChangeListener(getName(), evt -> {
            AppLogger.i("PHOTO BUTTON", evt.getNewValue().toString());

            if (evt.getNewValue() != null && !evt.getNewValue().toString().equalsIgnoreCase(""))
                try {

                    imageView.setAdjustViewBounds(true);
                    imageView.setMaxHeight(300);
                    imageView.setImageBitmap(ImageUtil.base64ToBitmap(evt.getNewValue().toString()));
                    linearLayout.addView(imageView);
                    linearLayout.requestLayout();

                } catch (Exception e) {
                    e.printStackTrace();
                }

        });

        imageView.setOnClickListener(v -> {

            final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppDialog);
            builder.setMessage(R.string.image_options);
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.view, (dialog, which) -> {

                Intent intent = new Intent(context, ImageViewActivity.class);
                intent.putExtra("image_string", getModel().getValue(getName()).toString());
                context.startActivity(intent);


            });
            builder.setNegativeButton(R.string.delete, (dialog, which) -> {

                imageView.setImageBitmap(null);

                getModel().setValue(getName(), "");
                dialog.dismiss();
                linearLayout.removeView(imageView);
                linearLayout.requestLayout();
            });
            builder.show();
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


    private ImageView getImageView() {
        return IMAGE_VIEW;

    }

    private void refresh(ImageView imageView) {
        Object value = getModel().getValue(getName());
        if (value != null && !value.toString().contains("http://")) {
            try {
                imageView.setImageBitmap(ImageUtil.base64ToBitmap(value.toString()));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public void refresh() {
        refresh(getImageView());
    }


}