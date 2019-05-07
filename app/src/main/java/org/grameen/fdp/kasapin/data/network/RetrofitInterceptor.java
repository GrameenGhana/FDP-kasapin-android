package org.grameen.fdp.kasapin.data.network;


/**
 * Created by AangJnr on 05, December, 2018 @ 8:16 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

import org.grameen.fdp.kasapin.data.prefs.AppPreferencesHelper;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetrofitInterceptor implements Interceptor {

    @Inject
    AppPreferencesHelper preferencesHelper;
    private String token = null;

    @Override
    public Response intercept(Chain chain) throws IOException {
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