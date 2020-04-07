package org.grameen.fdp.kasapin.ui.viewImage;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.utilities.CustomToast;
import org.grameen.fdp.kasapin.utilities.ImageUtil;
import org.grameen.fdp.kasapin.utilities.TouchImageView;


/**
 * Created by aangjnr on 19/11/2017.
 */

public class ImageViewActivity extends BaseActivity {
    String decodableString = "";
    RelativeLayout appBar;
    TouchImageView touchImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));

            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


          /*  getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);*/
        }

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_image_fullscreen);
        appBar = findViewById(R.id.appBar);
        hideToolBr();


        Intent intent = getIntent();
        decodableString = intent.getStringExtra("image_string");
        if (decodableString != null && !decodableString.equalsIgnoreCase("")) {
            touchImageView = findViewById(R.id.touch_image_view);
            try {
                touchImageView.setImageBitmap(ImageUtil.base64ToBitmap(decodableString));
            } catch (Exception e) {
                e.printStackTrace();
                finish();
                CustomToast.makeToast(this, "Could not preview image!", Toast.LENGTH_LONG).show();
            }
            touchImageView.setOnClickListener(v -> hideToolBr());
        } else {
            CustomToast.makeToast(this, "Could not load image!", Toast.LENGTH_SHORT).show();
        }
    }


    public void hideToolBr() {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled =
                false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        }
        if (isImmersiveModeEnabled) {
            Log.i("Image View Activity", "Turning immersive mode mode off. ");

            appBar.animate().alpha(1f).translationY(0).setDuration(500).start();


        } else {
            Log.i("Image View Activity", "Turning immersive mode mode on.");
            appBar.animate().alpha(0f).translationY(-(appBar.getHeight() * 2)).setDuration(500).start();

        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT > 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)

    }

    @Override
    public void openNextActivity() {

    }


    @Override
    public void toggleFullScreen(Boolean hideNavBar, Window window) {
        super.toggleFullScreen(hideNavBar, window);
    }
}
