package org.grameen.fdp.kasapin.ui.main;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
 import android.os.Bundle;
 import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Form;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.data.db.entity.Village;
import org.grameen.fdp.kasapin.data.db.entity.VillageAndFarmers;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.base.model.MySearchItem;
import org.grameen.fdp.kasapin.utilities.CommonUtils;
import org.grameen.fdp.kasapin.utilities.CustomToast;
import org.grameen.fdp.kasapin.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;


/**
 * A login screen that offers login via email/password.
 */
public class MainActivity extends BaseActivity implements MainContract.View, NavigationView.OnNavigationItemSelectedListener {

    @Inject
    MainPresenter mPresenter;

    @BindView(R.id.menu)
    View navDrawerMenu;

    @BindView(R.id.place_holder)
    View placeHolderView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.viewpagertab)
    SmartTabLayout smartTabLayout;


    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    @BindView(R.id.add_farmer)
    Button addFarmerButton;


    @BindView(R.id.sync)
    Button syncAllDataButton;

    @BindView(R.id.translation_switch)
    Switch toggleTranslation;

    List<Village> villages;
    SimpleSearchDialogCompat searchDialogCompat;
    private FragmentPagerItemAdapter viewPagerAdapter;


    public static Intent getStartIntent(Context context) {
        return new Intent(context, MainActivity.class);
     }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setUnBinder(ButterKnife.bind(this));

        getActivityComponent().inject(this);
        mPresenter.takeView(this);


        navigationView.setNavigationItemSelectedListener(this);
        toggleTranslation.setChecked(mPresenter.getAppDataManager().isTranslation());

        if(mPresenter.getAppDataManager().isMonitoring())
            addFarmerButton.setVisibility(View.GONE);

        mPresenter.getVillagesData();


    }



    @Override
    public void setFragmentAdapter(List<VillageAndFarmers> villageAndFarmersList) {

        FragmentPagerItems fragmentPagerItems = new FragmentPagerItems(this);

       for(VillageAndFarmers villageAndFarmers : villageAndFarmersList){

            if(villageAndFarmers.getFarmerList() != null && villageAndFarmers.getFarmerList().size() > 0){

                fragmentPagerItems.add(FragmentPagerItem.of(villageAndFarmers.getName(), FarmerListFragment.class, new Bundler()
                        .putString("farmers", new Gson().toJson(villageAndFarmers.getFarmerList())).get()));


            }


           viewPagerAdapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), fragmentPagerItems);

           viewPager.setAdapter(viewPagerAdapter);

           smartTabLayout.setViewPager(viewPager);

           togglePlaceHolder(viewPagerAdapter.getCount() == 0);

        }


        //mPresenter.getFarmersData();
    }


    @OnClick(R.id.add_farmer)
     void addFarmerActivity() {

        //Todo get forms, check size of forms, move to next activity
//        mPresenter.getAppDataManager().getDatabaseManager().formsDao().getAllForms().size()






    }

    @Override
    public void togglePlaceHolder(boolean value) {
        placeHolderView.setVisibility(value ? View.VISIBLE : View.GONE);
    }

    @Override
    public void instantiateSearchDialog(List<RealFarmer> farmers) {


        ArrayList<MySearchItem> items = new ArrayList<>();
        items.add(new MySearchItem("001", "First item"));
        items.add(new MySearchItem("002","Second item"));
        items.add(new MySearchItem("003", "Third item"));
        items.add(new MySearchItem("004", "The ultimate item"));
        items.add(new MySearchItem("005", "Last item"));



        searchDialogCompat = new SimpleSearchDialogCompat(MainActivity.this, "Search Farmer",
                "Who are you looking for?", null, items,
                (SearchResultListener<MySearchItem>) (dialog, item, position) -> {
                    Toast.makeText(MainActivity.this, item.getTitle(),
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });

    }


    @Override
    public void showSearchDialog(@Nullable View view) {

        if(searchDialogCompat != null)
        searchDialogCompat.show();
        else
            showNoFarmersMessage();

    }



    @Override
    protected void onDestroy() {
        if(mPresenter != null)
            mPresenter.dropView();
        super.onDestroy();
    }


    @Override
    public void openNextActivity() {


    }

    @Override
    public void openLoginActivityOnTokenExpire() {

    }



    @Override
    public void onToggleFullScreenClicked(Boolean hideNavBar) {

    }



    @Override
    public void toggleDrawer() {

        if(drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.closeDrawers();
        else
        drawerLayout.openDrawer(Gravity.START);
    }


    public void toggleDrawer(@Nullable View v){
        toggleDrawer();
    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {



        final int id = menuItem.getItemId();

        mPresenter.toggleDrawer();


                switch (id) {


                    case R.id.sync:

                        if (NetworkUtils.isNetworkConnected(MainActivity.this)) {

                            //Todo Sync data down



                        }

                        else
                            CustomToast.makeToast(MainActivity.this, getStringResources(R.string.no_internet_connection_available), Toast.LENGTH_LONG).show();



                        break;

                    case R.id.logout:

                        logOut();

                        break;

                    case R.id.sync_farmer:

                        //Todo Sync all unsynced data

                        break;

                    case R.id.download_farmer_data:


                        //Todo Sync down new data from server

                        break;




                }
        return true;
    }





    @Override
    public void showNoFarmersMessage() {

        showDialog(true, getString(R.string.no_data), getString(R.string.no_farmers),
                (dialogInterface, i) -> dialogInterface.dismiss(),
                getString(R.string.ok),
                null,
                "",
                0);
     }






}