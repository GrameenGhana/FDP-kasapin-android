package org.grameen.fdp.kasapin.utilities;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.grameen.fdp.kasapin.R;

public class CustomToast extends Toast {
    static Toast toast;
    public CustomToast(Context context) {
        super(context);
    }

    public static Toast makeToast(Context c, String message, int duration) {
        LayoutInflater inflater = LayoutInflater.from(c);
        View layout = inflater.inflate(R.layout.custom_toast, null, false);
        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);
        toast = new Toast(c);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, (ScreenUtils.getScreenHeight(c) / 3));
        toast.setDuration(duration);
        toast.setView(layout);
        return toast;
    }
}