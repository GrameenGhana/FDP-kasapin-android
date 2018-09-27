package org.grameen.fdp.kasapin.ui.splash;


import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.landing.LandingActivity;
import org.grameen.fdp.kasapin.ui.setup.LoginActivity;
import org.grameen.fdp.kasapin.utilities.AppLogger;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.lang.Thread.sleep;



public class SplashActivity extends BaseActivity implements SplashContract.View {

    @Inject
    SplashPresenter mPresenter;

    @BindView(R.id.image_view1)
    View image1;

    @BindView(R.id.ll1)
    View textLayout;



    public static Intent getStartIntent(Context context) {
        return new Intent(context, SplashActivity.class);
     }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));

        mPresenter.takeView(this);
        mPresenter.toggleFullScreen(true);

        image1.setAlpha(0f);
        textLayout.setTranslationY(-100f);
        textLayout.setAlpha(0f);





     }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        mPresenter.startDelay();
    }

    @Override
    protected void onDestroy() {
        mPresenter.dropView();
        super.onDestroy();
    }


    @Override
    public void openLoginActivity() {

        AppLogger.i(TAG, "Opening login activity");

        startActivity(new Intent(this, LoginActivity.class));
        finish();



    }

    @Override
    public void openNextActivity() {
        AppLogger.i(TAG, "Opening Landing Page activity");

        startActivity(new Intent(this, LandingActivity.class));
        supportFinishAfterTransition();


    }





    @Override
    public void onToggleFullScreenClicked(Boolean hideNavBar) {

        if(hideNavBar){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else{
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 0);


        }

    }

    @Override
    public void animateLogoAndWait() {

        startAnimations();



    }

    private void startAnimations() {

        image1.animate()
                .alpha(1f)
                .setDuration(1500)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {


                        textLayout.animate()
                                .translationY(0)
                                .alpha(1f)
                                .setDuration(500)
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        try {
                                            int waited = 0;
                                            // Splash screen pause time
                                            while (waited < 1000) {
                                                sleep(100);
                                                waited += 100;
                                            }


                                            mPresenter.openNextActivity();


                                        } catch (InterruptedException e) {
                                            // do nothing
                                        } finally {

                                        }


                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                })
                                .setStartDelay(200)
                                .start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();


    }

}