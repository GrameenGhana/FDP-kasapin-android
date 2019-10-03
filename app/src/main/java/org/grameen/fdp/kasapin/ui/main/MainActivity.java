package org.grameen.fdp.kasapin.ui.main;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.CommunitiesAndFarmers;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.ui.addFarmer.AddEditFarmerActivity;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.base.model.MySearchItem;
import org.grameen.fdp.kasapin.ui.farmerProfile.FarmerProfileActivity;
import org.grameen.fdp.kasapin.ui.preferences.SettingsActivity;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;


/**
 * A login screen that offers login via email/password.
 */
public class MainActivity extends BaseActivity implements MainContract.View, NavigationView.OnNavigationItemSelectedListener {

    @Inject
    MainPresenter mPresenter;

    @BindView(R.id.menu)
    View navDrawerMenu;

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

    @BindView(R.id.custom_toolbar_layout)
    LinearLayout toolBarLayout;

    String SELECTED_VILLAGE = "";
    SimpleSearchDialogCompat searchDialogCompat;
    private FragmentPagerItemAdapter viewPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setUnBinder(ButterKnife.bind(this));

        getActivityComponent().inject(this);
        mPresenter.takeView(this);


        AppLogger.e(TAG, "STORED SERVER URL IS *************   " + mPresenter.getAppDataManager().getPreferences().getString(AppConstants.SERVER_URL, "NONE"));

        AppLogger.e(TAG, "STORED SERVER URL IS *************   " + PreferenceManager.getDefaultSharedPreferences(this).getString(AppConstants.SERVER_URL, "NONE"));


        if (FORM_AND_QUESTIONS == null)
            mPresenter.getFormsAndQuestionsData();


        navigationView.setNavigationItemSelectedListener(this);
        toggleTranslation.setChecked(mPresenter.getAppDataManager().isTranslation());

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {

            @Override
            public void onDrawerSlide(View drawer, float slideOffset) {
                findViewById(R.id.main_content).setX(drawer.getWidth() * slideOffset);
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
                syncState();

            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                syncState();

            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        if (mPresenter.getAppDataManager().isMonitoring()) {
            addFarmerButton.setVisibility(View.GONE);
            toolBarLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.gradient_background_monitoring));
        }

        mPresenter.getVillagesDataFromDbAndUpdateUI();


       //AppLogger.e(TAG, "FARMERS >>> " + getGson().toJson(getAppDataManager().getDatabaseManager().realFarmersDao().getAll().blockingGet()));


        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerLayout = navigationView.getHeaderView(0);


        TextView nameTV = headerLayout.findViewById(R.id.name_textView);
        TextView emailTV = headerLayout.findViewById(R.id.email_textView);
        TextView versionNumberTV = navigationView.findViewById(R.id.version_number);


        nameTV.setText(getAppDataManager().getUserFullName());
        emailTV.setText(getAppDataManager().getUserEmail());


        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionNumberTV.setText(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException ignore) {}


        navigationView.setNavigationItemSelectedListener(this);


    }


    @Override
    public void setFragmentAdapter(List<CommunitiesAndFarmers> villageAndFarmersList) {
        int index = 0;

        FragmentPagerItems fragmentPagerItems = new FragmentPagerItems(this);

        for (CommunitiesAndFarmers villageAndFarmers : villageAndFarmersList) {

            AppLogger.i(TAG, "VILLAGE ID IS " + villageAndFarmers.getVillage().getId() + " AND FARMERS SIZE IS " + villageAndFarmers.getFarmerList().size());

            if (villageAndFarmers.getFarmerList() != null && villageAndFarmers.getFarmerList().size() > 0) {

                fragmentPagerItems.add(FragmentPagerItem.of(villageAndFarmers.getVillage().getName(), FarmerListFragment.class, new Bundler()
                        .putString("villageName", SELECTED_VILLAGE)
                        .putInt("index", index)
                        .putString("farmers", getGson().toJson(villageAndFarmers.getFarmerList())).get()));

                index++;

                new Handler().postDelayed(() -> {
                    viewPagerAdapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), fragmentPagerItems);
                    viewPager.setAdapter(viewPagerAdapter);
                    smartTabLayout.setViewPager(viewPager);
                }, 500);


                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i1) {}

                    @Override
                    public void onPageSelected(int i) {
                        SELECTED_VILLAGE = fragmentPagerItems.get(i).getTitle().toString();
                        CURRENT_PAGE = i;
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {}
                });

                SELECTED_VILLAGE = fragmentPagerItems.get(0).getTitle().toString();
                CURRENT_PAGE = 0;
            }
        }

        mPresenter.initializeSearchDialog(villageAndFarmersList);


        if (fragmentPagerItems.size() > 0)
            hideNoDataView();

    }


    @OnClick(R.id.add_farmer)
    void addFarmerActivity() {
        //Todo get forms, check size of forms, move to next activity

        //mPresenter.getFarmerProfileFormAndQuestions();
        openAddNewFarmerActivity(null);


    }

    @Override
    public void openAddNewFarmerActivity(@Nullable FormAndQuestions formAndQuestion) {

        new Handler().post(() -> {

            Intent intent = new Intent(MainActivity.this, AddEditFarmerActivity.class);
            //intent.putExtra("formAndQuestions", getGson().toJson(formAndQuestion));
            startActivity(intent);
            finish();
        });


    }

    @Override
    public void cacheFormsAndQuestionsData(List<FormAndQuestions> formAndQuestions) {
        AppLogger.i(TAG, "FORMS SIZE IS " + formAndQuestions.size());

        FORM_AND_QUESTIONS = formAndQuestions;
        CURRENT_FORM_POSITION = 0;

    }


    @Override
    public void instantiateSearchDialog(ArrayList<MySearchItem> items) {

        AppLogger.i(TAG, "SEARCH DIALOG ITEMS = " + items.size());

        searchDialogCompat = new SimpleSearchDialogCompat(MainActivity.this, "Search Farmer",
                "Who are you looking for?", null, items,
                (SearchResultListener<MySearchItem>) (dialog, item, position) -> {
                    Toast.makeText(this, item.getTitle(),
                            Toast.LENGTH_SHORT).show();

                    mPresenter.getFarmer(item.getmExtId());

                    dialog.dismiss();
                });
    }


    @Override
    public void viewFarmerProfile(RealFarmer farmer) {

        Intent intent = new Intent(this, FarmerProfileActivity.class);
        intent.putExtra("farmer", getGson().toJson(farmer));
        startActivity(intent);
    }

    @Override
    public void showSearchDialog(@Nullable View view) {

        if (searchDialogCompat != null)
            searchDialogCompat.show();
        else
            showNoFarmersMessage();
    }


    @Override
    protected void onDestroy() {
        if (mPresenter != null)
            mPresenter.dropView();
        super.onDestroy();
    }


    @Override
    public void openNextActivity() {


    }


    @Override
    public void toggleDrawer() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawers();
        else
            drawerLayout.openDrawer(GravityCompat.START);
    }


    public void toggleDrawer(@Nullable View v) {
        toggleDrawer();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        final int id = menuItem.getItemId();

        toggleDrawer();


        switch (id) {


            case R.id.download_resources:

                if (NetworkUtils.isNetworkConnected(MainActivity.this)) {

                    //Todo Sync data down
                    new Handler().postDelayed(() -> mPresenter.downloadResourcesData(true), 500);

                } else
                    showMessage(R.string.no_internet_connection_available);


                break;

            case R.id.logout:

                logOut();

                break;

            case R.id.action_settings:

                startActivity(new Intent(this, SettingsActivity.class));

                break;

            case R.id.sync_farmer:
                //Todo Sync all un synced farmer data
                //Generate the json object here, pass the object as a value
                if (NetworkUtils.isNetworkConnected(MainActivity.this))
                    mPresenter.syncData(true);
                else
                    showMessage(R.string.no_internet_connection_available);
                break;

            case R.id.download_farmer_data:
                //Todo Sync down new data from server
                if (NetworkUtils.isNetworkConnected(MainActivity.this))
                mPresenter.downloadFarmersData(true);
                else
                    showMessage(R.string.no_internet_connection_available);
                break;


        }
        return true;
    }


    @Override
    public void showNoFarmersMessage() {

        showDialog(true, getString(R.string.no_data), getString(R.string.no_new_data),
                (dialogInterface, i) -> dialogInterface.dismiss(),
                getString(R.string.ok),
                null,
                "",
                0);
    }




  @Override
    protected void onResume() {

        AppLogger.e(TAG, "On Resume...");

        if(getAppDataManager().getBooleanValue("reload")){
            AppLogger.e(TAG, "Reload data!");

            restartUI();
            getAppDataManager().setBooleanValue("reload", false);
        }

      super.onResume();

        AppLogger.e(TAG, "URL IS >>> " + mAppDataManager.getPreferences().getString(AppConstants.SERVER_URL, "DEFAULT IS SET"));


    }


    @Override
    public void restartUI(){
        startActivity(new Intent(new Intent(this, MainActivity.class)));
        overridePendingTransition(0, 0);
        finish();

    }
}