package org.grameen.fdp.kasapin.ui.main;


import androidx.annotation.Nullable;

import org.grameen.fdp.kasapin.data.db.entity.CommunitiesAndFarmers;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.ui.base.BaseContract;
import org.grameen.fdp.kasapin.ui.base.model.MySearchItem;

import java.util.ArrayList;
import java.util.List;

public class MainContract {
    public interface View extends BaseContract.View {
        void showSearchDialog(@Nullable android.view.View view);

        void toggleDrawer();

        void instantiateSearchDialog(ArrayList<MySearchItem> myItems);

        void showNoFarmersMessage();

        void setFragmentAdapter(List<CommunitiesAndFarmers> villageAndFarmersList);

        void cacheFormsAndQuestionsData(List<FormAndQuestions> formAndQuestions);

        void openAddNewFarmerActivity(FormAndQuestions formAndQuestions);

        void viewFarmerProfile(Farmer farmer);

        void restartUI();
    }

    public interface Presenter {
        void getVillagesDataFromDatabase();

        void getFormsAndQuestionsData();

        void getFarmerProfileFormAndQuestions();

        void openSearchDialog();

        void syncData(boolean showProgress);

        void downloadResourcesData(boolean showProgress);

        void downloadFarmersData(boolean showProgress);

        void initializeSearchDialog(List<CommunitiesAndFarmers> villageAndFarmers);

        void getFarmer(String farmerCode);
    }

    public interface FragmentView extends BaseContract.View {
        void setListAdapter(List<Farmer> farmerList);

        void showDeleteFarmerDialog(Farmer farmer, int position);

        void showFarmerDeletedMessage(String farmerName, int position);
    }

    public interface FragmentPresenter {
        void getFarmerData(List<String> farmerCodes);

        void showDeleteFarmerDialog(Farmer farmer, int position);

        void deleteFarmer(Farmer farmer, int position);
    }
}
