package org.grameen.fdp.kasapin.data.network;

import android.app.Application;

import org.grameen.fdp.kasapin.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class KeyPinStore {
    private static KeyPinStore instance = null;
    private final SSLContext sslContext = SSLContext.getInstance("TLS");
    X509TrustManager trustManager = null;

    public static synchronized KeyPinStore getInstance(Application context) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, javax.security.cert.CertificateException {
        if (instance == null) {
            instance = new KeyPinStore(context);
        }
        return instance;
    }

    private KeyPinStore(Application context) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, javax.security.cert.CertificateException {
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);


            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new BufferedInputStream(context.getResources().openRawResource(R.raw.cert));
            Certificate ca = cf.generateCertificate(caInput);

            // Create a KeyStore containing our trusted CAs
            keyStore.setCertificateEntry("ca", ca);

        // Use custom trust manager to trusts the CAs in our KeyStore
        TrustManager[] trustManagers = {new CustomTrustManager(keyStore)};
        trustManager = (X509TrustManager) trustManagers[0];

        // Create an SSLContext that uses our TrustManager
        // SSLContext context = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, null);
    }

    public X509TrustManager getTrustManager(){
        return trustManager;
    }
    public SSLContext getContext() {
        return sslContext;
    }
}