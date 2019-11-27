package org.grameen.fdp.kasapin.ui.serverUrl;


import org.grameen.fdp.kasapin.data.db.entity.ServerUrl;
import org.grameen.fdp.kasapin.ui.base.BaseContract;

import java.util.List;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:09 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class AddEditServerUrlContract {


    public interface View extends BaseContract.View {
        void showServerList(List<ServerUrl> serverUrls);
    }

    public interface Presenter {
        void fetchData();
        void deleteUrl(ServerUrl serverUrl);
        void saveUrl(ServerUrl serverUrl);

    }


}
