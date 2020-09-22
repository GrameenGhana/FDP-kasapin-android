package org.grameen.fdp.kasapin.ui.main;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.grameen.fdp.kasapin.BuildConfig;
import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.CommunitiesAndFarmers;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.model.FarmerNameAndCode;
import org.grameen.fdp.kasapin.ui.addFarmer.AddEditFarmerActivity;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.base.model.MySearchItem;
import org.grameen.fdp.kasapin.ui.farmerProfile.FarmerProfileActivity;
import org.grameen.fdp.kasapin.ui.preferences.SettingsActivity;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.NetworkUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;

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
    @BindView(R.id.translation_switch)
    Switch toggleTranslation;
    @BindView(R.id.custom_toolbar_layout)
    LinearLayout toolBarLayout;
    String SELECTED_VILLAGE = "";
    SimpleSearchDialogCompat<MySearchItem> searchDialogCompat;
    private FragmentPagerItemAdapter viewPagerAdapter;

    public static void setInMemoryRoomDatabases(SupportSQLiteDatabase... database) {
        if (BuildConfig.DEBUG) {
            try {
                Class<?> debugDB = Class.forName("com.amitshekhar.DebugDB");
                Class[] argTypes = new Class[]{HashMap.class};
                HashMap<String, SupportSQLiteDatabase> inMemoryDatabases = new HashMap<>();
                // set your inMemory databases
                inMemoryDatabases.put("InMemoryOne.db", database[0]);
                Method setRoomInMemoryDatabase = debugDB.getMethod("setInMemoryRoomDatabases", argTypes);
                setRoomInMemoryDatabase.invoke(null, inMemoryDatabases);
            } catch (Exception ignore) {
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUnBinder(ButterKnife.bind(this));
        getActivityComponent().inject(this);
        mPresenter.takeView(this);

        mPresenter.getVillagesDataFromDatabase();

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


        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerLayout = navigationView.getHeaderView(0);

        TextView nameTextView = headerLayout.findViewById(R.id.name_textView);
        TextView emailTextView = headerLayout.findViewById(R.id.email_textView);
        TextView versionNumberTextView = navigationView.findViewById(R.id.version_number);

        nameTextView.setText(getAppDataManager().getUserFullName());
        emailTextView.setText(getAppDataManager().getUserEmail());

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionNumberTextView.setText(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException ignore) {
        }
        navigationView.setNavigationItemSelectedListener(this);

        if (FORM_AND_QUESTIONS == null)
            mPresenter.getFormsAndQuestionsData();

        //if(BuildConfig.DEBUG)
        //setInMemoryRoomDatabases(getAppDataManager().getDatabaseManager().getOpenHelper().getWritableDatabase());
    }

    @Override
    public void setFragmentAdapter(List<CommunitiesAndFarmers> villageAndFarmersList) {
        int index = 0;
        FragmentPagerItems fragmentPagerItems = new FragmentPagerItems(this);
        List<String> array = new ArrayList<>();
        for (CommunitiesAndFarmers villageAndFarmers : villageAndFarmersList) {
            if (villageAndFarmers.getFarmerList() != null && villageAndFarmers.getFarmerList().size() > 0) {
                array.clear();

                for (FarmerNameAndCode farmerNameAndCode : villageAndFarmers.getFarmerList())
                    array.add(farmerNameAndCode.getCode());

                fragmentPagerItems.add(FragmentPagerItem.of(villageAndFarmers.getVillage().getName(), FarmerListFragment.class, new Bundler()
                        .putString("villageName", SELECTED_VILLAGE)
                        .putInt("index", index)
                        .putString("farmers", getGson().toJson(array)).get()));
                index += 1;

                viewPagerAdapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), fragmentPagerItems);
                viewPager.setAdapter(viewPagerAdapter);
                smartTabLayout.setViewPager(viewPager);
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i1) {
                    }
                    @Override
                    public void onPageSelected(int i) {
                        SELECTED_VILLAGE = fragmentPagerItems.get(i).getTitle().toString();
                        CURRENT_PAGE = i;
                    }
                    @Override
                    public void onPageScrollStateChanged(int i) {
                    }
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
        openAddNewFarmerActivity(null);
    }

    @Override
    public void openAddNewFarmerActivity(@Nullable FormAndQuestions formAndQuestion) {
        new Handler().post(() -> {
            Intent intent = new Intent(MainActivity.this, AddEditFarmerActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void cacheFormsAndQuestionsData(List<FormAndQuestions> formAndQuestions) {
        FORM_AND_QUESTIONS = formAndQuestions;
        CURRENT_FORM_POSITION = 0;
    }

    @Override
    public void instantiateSearchDialog(ArrayList<MySearchItem> items) {
        searchDialogCompat = new SimpleSearchDialogCompat<>(MainActivity.this, "Search Farmer",
                "Who are you looking for?", null, items,
                (dialog, item, position) -> {
                    Toast.makeText(this, item.getTitle(),
                            Toast.LENGTH_SHORT).show();
                    mPresenter.getFarmer(item.getExtId());
                    dialog.dismiss();
                });
    }

    @Override
    public void viewFarmerProfile(Farmer farmer) {
        Intent intent = new Intent(this, FarmerProfileActivity.class);
        intent.putExtra("farmerCode",  farmer.getCode());
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
                if (NetworkUtils.isNetworkConnected(MainActivity.this))
                    //Todo Sync data down
                    new Handler().postDelayed(() -> mPresenter.downloadResourcesData(true), 500);
                else
                    showMessage(R.string.no_internet_connection_available);
                break;
            case R.id.logout:
                logOut();
                break;
            case R.id.sync_farmer:
                syncData();
                break;
            case R.id.download_farmer_data:
                //Todo Sync down new data from server
                if (NetworkUtils.isNetworkConnected(MainActivity.this))
                    new Handler().postDelayed(() -> mPresenter.downloadFarmersData(true), 500);
                else
                    showMessage(R.string.no_internet_connection_available);
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return true;
    }

    @OnClick(R.id.sync)
    void syncData(){
        //Generate the json object here, pass the object as a value
        if (NetworkUtils.isNetworkConnected(MainActivity.this))
            new Handler().postDelayed(() -> mPresenter.syncData(true), 500);
        else
            showMessage(R.string.no_internet_connection_available);
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
        if (getAppDataManager().getBooleanValue("reload")) {
            restartUI();
            getAppDataManager().setBooleanValue("reload", false);
        }
        super.onResume();
    }

    @Override
    public void restartUI() {
        startActivity(new Intent(new Intent(this, MainActivity.class)));
        overridePendingTransition(0, 0);
        finish();
    }
}