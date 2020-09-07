package org.grameen.fdp.kasapin.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class LocationPrepareService extends Service {

    private Handler h;

    @Override
    public void onCreate() {
        super.onCreate();

        h = new Handler(this.getMainLooper());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
