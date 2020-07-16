package org.grameen.fdp.kasapin.ui.splash;


import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.landing.LandingActivity;
import org.grameen.fdp.kasapin.ui.login.LoginActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        setStatusBarColor(getWindow(), R.color.colorPrimary, true);
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_splash);
        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.takeView(this);
        toggleFullScreen(false, getWindow());

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
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void openNextActivity() {
        startActivity(new Intent(this, LandingActivity.class));
        supportFinishAfterTransition();
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
                                    public void onAnimationStart(Animator animation) {}

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        try {
                                            int waited = 0;
                                            // Splash screen pause time
                                            while (waited < 1000) {
                                                sleep(100);
                                                waited += 100;
                                            }
                                            mPresenter.checkIfIsLoggedIn();
                                        } catch (InterruptedException ignored) {
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
                    public void onAnimationCancel(Animator animation) {}

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                }).start();
    }
}