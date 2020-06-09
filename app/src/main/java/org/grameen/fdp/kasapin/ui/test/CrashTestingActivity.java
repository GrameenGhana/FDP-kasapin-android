package org.grameen.fdp.kasapin.ui.test;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.balsikandar.crashreporter.CrashReporter;
import com.balsikandar.crashreporter.ui.CrashReporterActivity;

import org.grameen.fdp.kasapin.R;

import java.util.ArrayList;

/**
 * Created by AangJnr on 19, November, 2018 @ 2:27 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class CrashTestingActivity extends AppCompatActivity {
    Context context;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crashtesting);

        findViewById(R.id.nullPointer).setOnClickListener(v -> {
            context = null;
            context.getResources();
        });

        findViewById(R.id.indexOutOfBound).setOnClickListener(V -> {
                    ArrayList<String> list = new ArrayList();
                    list.add("hello");
                    String crashMe = list.get(2);
                }
        );

        findViewById(R.id.classCastExeption).setOnClickListener(v -> {
            Object x = new Integer(0);
            System.out.println((String) x);

        });

        findViewById(R.id.arrayStoreException).setOnClickListener(v -> {
            Object[] x = new String[3];
            x[0] = new Integer(0);

        });


        //Crashes and exceptions are also captured from other threads
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    context = null;
                    context.getResources();
                } catch (Exception e) {
                    //log caught Exception
                    CrashReporter.logException(e);
                }

            }
        }).start();

        mContext = this;
        findViewById(R.id.crashLogActivity).setOnClickListener(v -> {
            Intent intent = new Intent(mContext, CrashReporterActivity.class);
            startActivity(intent);
        });

    }
}