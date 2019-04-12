package org.grameen.fdp.kasapin.ui.fdpStatus;


import android.text.TextUtils;

import com.balsikandar.crashreporter.CrashReporter;
import com.balsikandar.crashreporter.utils.CrashUtil;
import com.balsikandar.crashreporter.utils.FileUtils;
import com.crashlytics.android.Crashlytics;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class FDPStatusPresenter extends BasePresenter<FDPStatusContract.View> implements FDPStatusContract.Presenter{

    private AppDataManager mAppDataManager;




    @Inject
    public FDPStatusPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;




    }




}
