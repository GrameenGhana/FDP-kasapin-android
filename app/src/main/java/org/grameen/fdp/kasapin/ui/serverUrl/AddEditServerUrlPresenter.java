package org.grameen.fdp.kasapin.ui.serverUrl;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.DataManager;
import org.grameen.fdp.kasapin.data.db.entity.Country;
import org.grameen.fdp.kasapin.data.db.entity.ServerUrl;
import org.grameen.fdp.kasapin.data.db.model.User;
import org.grameen.fdp.kasapin.data.network.model.LoginResponse;
import org.grameen.fdp.kasapin.syncManager.DownloadResources;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static org.grameen.fdp.kasapin.ui.base.BaseActivity.getGson;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:06 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class AddEditServerUrlPresenter extends BasePresenter<AddEditServerUrlContract.View> implements AddEditServerUrlContract.Presenter{

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
