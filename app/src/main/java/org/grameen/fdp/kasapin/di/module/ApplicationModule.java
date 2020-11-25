package org.grameen.fdp.kasapin.di.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.room.Room;

import org.grameen.fdp.kasapin.BuildConfig;
import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.AppDatabase;
import org.grameen.fdp.kasapin.data.network.FdpApi;
import org.grameen.fdp.kasapin.data.network.FdpApiService;
import org.grameen.fdp.kasapin.data.network.KeyPinStore;
 import org.grameen.fdp.kasapin.data.prefs.AppPreferencesHelper;
import org.grameen.fdp.kasapin.data.prefs.PreferencesHelper;
import org.grameen.fdp.kasapin.di.Scope.ApplicationContext;
import org.grameen.fdp.kasapin.utilities.AppConstants;

import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;

import javax.net.ssl.SSLSocketFactory;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;
import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


@Module
public class ApplicationModule {
    private final Application application;

    public ApplicationModule(Application app) {
        application = app;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return application;
    }

    @Provides
    Application providesApplication() {
        return application;
    }

    @Singleton
    @Provides
    public AppDatabase providesDatabase() {
        //Todo For future migrations
         /* Migration MIGRATION_1_2 = new Migration(1, 2) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                // Since we didn't alter the table, there's nothing else to do here.
            }
        };*/
        return Room.databaseBuilder(application, AppDatabase.class, AppConstants.DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }

    @Singleton
    @Provides
    PreferencesHelper providePreferencesHelper(AppPreferencesHelper appPreferencesHelper) {
        return appPreferencesHelper;
    }

    @Singleton
    @Provides
    SharedPreferences providesSharedPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Singleton
    @Provides
    CompositeDisposable providesCompositeDisposable() {
        return new CompositeDisposable();
    }


    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        OkHttpClient.Builder  builder =  new OkHttpClient.Builder();

        try {
            KeyPinStore keystore = KeyPinStore.getInstance(application);
            SSLSocketFactory tlsSocketFactory = keystore.getContext().getSocketFactory();
            if (tlsSocketFactory.getSupportedCipherSuites() != null) {
                 builder.sslSocketFactory(tlsSocketFactory, keystore.getTrustManager())
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(BuildConfig.DEBUG)
            builder.addInterceptor(new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY));


            return builder.callTimeout(30, TimeUnit.SECONDS)
                    .build();
    }



    @Singleton
    @Provides
    Retrofit providesRetrofit(OkHttpClient client) {
        String url = providesSharedPrefs().getString(AppConstants.SERVER_URL, BuildConfig.END_POINT);
        return new Retrofit.Builder().baseUrl((url != null && !url.equals("")) ? url : BuildConfig.END_POINT)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Singleton
    @Provides
    FdpApi providesFdpApi(Retrofit retrofit) {
        return retrofit.create(FdpApi.class);
    }

    @Singleton
    @Provides
    FdpApiService providesFdpApiService(FdpApi fdpApi) {
        return new FdpApiService(fdpApi);
    }

    X509Certificate getCert() throws CertificateException {
        return X509Certificate.getInstance(application.getResources().openRawResource(R.raw.cert)) ;
    }
}