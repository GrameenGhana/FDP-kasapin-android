package org.grameen.fdp.kasapin.ui.main;


import android.support.annotation.Nullable;

import org.grameen.fdp.kasapin.data.db.entity.CommunitiesAndFarmers;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.ui.base.BaseContract;
import org.grameen.fdp.kasapin.ui.base.model.MySearchItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:09 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class MainContract {


    public interface View extends BaseContract.View {


        void showSearchDialog(@Nullable android.view.View view);

        void toggleDrawer();

        void instantiateSearchDialog(ArrayList<MySearchItem> myItems);

        void showNoFarmersMessage();

        void setFragmentAdapter(List<CommunitiesAndFarmers> villageAndFarmersList);

        void cacheFormsAndQuestionsData(List<FormAndQuestions> formAndQuestions);

        void openAddNewFarmerActivity(FormAndQuestions formAndQuestions);

        void viewFarmerProfile(RealFarmer farmer);

        void restartUI();

    }

    public interface Presenter {

        void startDelay(long delayTime);

        void openSearchDialog();

        void toggleDrawer();

        void getVillagesDataFromDbAndUpdateUI();

        void getFormsAndQuestionsData();

        void getFarmerProfileFormAndQuestions();

        void syncData(boolean showProgress);

        void downloadResourcesData(boolean showProgress);

        void downloadFarmersData(boolean showProgress);

        void initializeSearchDialog(List<CommunitiesAndFarmers> villageAndFarmers);

        void getFarmer(String farmerCode);
    }


    public interface FragmentView extends BaseContract.View {
        void setRecyclerAdapter();

        void setListAdapter(List<RealFarmer> farmerList);

        void showDeleteFarmerDialog(RealFarmer farmer, int position);

        void showFarmerDeletedMessage(String farmerName, int position);


    }


    public interface FragmentPresenter {


        void getFarmerData();

        void showDeleteFarmerDialog(RealFarmer farmer, int position);

        void deleteFarmer(RealFarmer farmer, int position);


    }

}
