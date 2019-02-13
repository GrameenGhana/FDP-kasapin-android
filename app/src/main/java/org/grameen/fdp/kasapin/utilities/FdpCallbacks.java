package org.grameen.fdp.kasapin.utilities;

public class FdpCallbacks {

    public interface OnDownloadResourcesListener{

        void onSuccess();


        void onError(Throwable throwable);

    }
}
