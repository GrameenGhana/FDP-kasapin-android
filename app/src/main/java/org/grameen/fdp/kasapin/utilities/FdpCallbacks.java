package org.grameen.fdp.kasapin.utilities;

public class FdpCallbacks {

    public interface OnDownloadResourcesListener{

        void onSuccess(String message);
        void onError(Throwable throwable);

    }


    public interface UploadDataListener{

        void onUploadComplete(String message);
        void onUploadError(Throwable throwable);

    }
}
