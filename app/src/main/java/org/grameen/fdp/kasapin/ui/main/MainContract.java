package org.grameen.fdp.kasapin.ui.main;


import android.support.annotation.Nullable;

import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.data.db.entity.Village;
import org.grameen.fdp.kasapin.data.db.entity.VillageAndFarmers;
import org.grameen.fdp.kasapin.ui.base.BaseContract;

import java.util.List;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:09 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class MainContract {



    public interface View extends  BaseContract.View {


        void showSearchDialog(@Nullable android.view.View view);
        void toggleDrawer();

        void instantiateSearchDialog(List<RealFarmer> farmers);

        void showNoFarmersMessage();

        void setFragmentAdapter(List<VillageAndFarmers> villageAndFarmersList);

        void togglePlaceHolder(boolean value);


    }

     public interface Presenter {

        void startDelay(long delayTime);
        void openSearchDialog();


         void toggleDrawer();

        void getFarmersData();

        void getVillagesData();



    }



}
