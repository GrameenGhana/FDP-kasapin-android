package org.grameen.fdp.kasapin.utilities;

public class FdpCallbacks {
    public interface OnDownloadResourcesListener {
        void onSuccess(String message);

        void onError(Throwable throwable);
    }

    public interface UploadDataListener {
        void onUploadComplete(String message);

        void onUploadError(Throwable throwable);
    }

    public interface UrlSelectedListener {
        void onUrlSelected(String url);
    }

    public interface AnItemSelectedListener {
        void onItemSelected(String item);
    }

    public interface UpdateJsonArray {
        void onItemValueChanged(int id, String uid, String value);
    }

}
