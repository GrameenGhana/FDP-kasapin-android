package org.grameen.fdp.kasapin.ui.serverUrl;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.ServerUrl;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class AddEditServerUrlPresenter extends BasePresenter<AddEditServerUrlContract.View> implements AddEditServerUrlContract.Presenter {
    @Inject
    AddEditServerUrlPresenter(AppDataManager appDataManager) {
        super(appDataManager);
    }
    @Override
    public void fetchData() {
        getAppDataManager().getDatabaseManager().serverUrlsDao().getAllUrls()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<ServerUrl>>() {
                    @Override
                    public void onSuccess(List<ServerUrl> serverUrls) {
                        getView().showServerList(serverUrls);
                    }
                    @Override
                    public void onError(Throwable e) {
                        getView().showMessage(e.getMessage());
                    }
                });
    }
    @Override
    public void deleteUrl(ServerUrl serverUrl) {
        getAppDataManager().getDatabaseManager().serverUrlsDao().deleteOne(serverUrl);
    }

    @Override
    public void saveUrl(ServerUrl serverUrl) {
        getAppDataManager().getDatabaseManager().serverUrlsDao().insertOne(serverUrl);
    }
}
