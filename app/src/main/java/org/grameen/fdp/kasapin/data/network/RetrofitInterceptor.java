package org.grameen.fdp.kasapin.data.network;


/**
 * Created by AangJnr on 05, December, 2018 @ 8:16 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

import org.grameen.fdp.kasapin.BuildConfig;

import java.io.IOException;
import android.util.Base64;
import android.util.Log;


import org.grameen.fdp.kasapin.data.prefs.AppPreferencesHelper;
import org.json.JSONException;
import org.json.JSONObject;


import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import timber.log.Timber;

public class RetrofitInterceptor implements Interceptor {

    private String token = null;

    @Inject
    AppPreferencesHelper preferencesHelper;


    @Override public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        token = preferencesHelper.getAccessToken();


        if (token != null && !token.isEmpty()) {
            token = "Bearer " + token;
            request = request.newBuilder()
                    .addHeader("Authorization", token)
                    .build();
        }
        return chain.proceed(request);
    }

}