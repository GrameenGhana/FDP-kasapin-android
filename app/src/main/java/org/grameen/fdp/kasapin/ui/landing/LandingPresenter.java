package org.grameen.fdp.kasapin.ui.landing;


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

public class LandingPresenter extends BasePresenter<LandingContract.View> implements LandingContract.Presenter {
    private AppDataManager mAppDataManager;

    @Inject
    public LandingPresenter(AppDataManager appDataManager) {
        super(appDataManager);
        this.mAppDataManager = appDataManager;
    }

    @Override
    public void showPopupDialog() {
    }

    @Override
    public void uploadLogsToServer() {
        //Todo upload logs using Firebase Crashytics and update the ui
        getView().showMessage("Sending logs...");
        List<File> logFiles = getAllCrashes();
        if (logFiles.size() > 0) {
            Crashlytics.setUserIdentifier(mAppDataManager.getUserEmail());
            Crashlytics.log(FileUtils.readFromFile(new File(logFiles.get(0).getAbsolutePath())));
            getView().showMessage(R.string.logs_sent);
            new Thread(() -> {
                try {
                    File[] logs = new File(CrashReporter.getCrashReportPath()).listFiles();
                    for (File file : logs) {
                        FileUtils.delete(file);
                    }
                } catch (Exception ignored) {
                }
            });
        }
    }

    private ArrayList<File> getAllCrashes() {
        String directoryPath;
        String crashReportPath = CrashReporter.getCrashReportPath();
        if (TextUtils.isEmpty(crashReportPath)) {
            directoryPath = CrashUtil.getDefaultPath();
        } else {
            directoryPath = crashReportPath;
        }
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new RuntimeException("The path provided doesn't exists : " + directoryPath);
        }
        ArrayList<File> listOfFiles = new ArrayList<>(Arrays.asList(directory.listFiles()));
        for (Iterator<File> iterator = listOfFiles.iterator(); iterator.hasNext(); ) {
            if (iterator.next().getName().contains(com.balsikandar.crashreporter.utils.Constants.EXCEPTION_SUFFIX)) {
                iterator.remove();
            }
        }
        Collections.sort(listOfFiles, Collections.reverseOrder());
        return listOfFiles;
    }

    @Override
    public void openNextActivity() {
        getView().openMainActivity();
    }
}
