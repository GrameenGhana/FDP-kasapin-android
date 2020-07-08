package org.grameen.fdp.kasapin.ui.main;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.base.BaseFragment;
import org.grameen.fdp.kasapin.ui.farmerProfile.FarmerProfileActivity;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.grameen.fdp.kasapin.ui.base.BaseActivity.IS_TABLET;

public class FarmerListFragment extends BaseFragment implements MainContract.FragmentView {
    @Inject
    FarmerListFragmentPresenter mPresenter;
    @BindView(R.id.grid_view)
    GridView listView;
    View rootView;
    FarmerListRecyclerViewAdapter mAdapter;
    List<Farmer> mFarmers;
    FarmerListViewAdapter farmerListViewAdapter;
    int index = 0;
    String SELECTED_VILLAGE;
    public FarmerListFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getBaseActivity().getActivityComponent().inject(this);
        mPresenter.takeView(this);
        rootView = inflater.inflate(R.layout.fragment_farmer_list, container, false);
        setUnBinder(ButterKnife.bind(this, rootView));
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        if (getArguments() != null) {
            mFarmers = new Gson().fromJson(getArguments().getString("farmers"), new TypeToken<List<Farmer>>() {
            }.getType());
            SELECTED_VILLAGE = getArguments().getString("village");
            index = getArguments().getInt("index");
            setListAdapter(mFarmers);
        }
    }

    @Override
    public void setRecyclerAdapter() {}

    @Override
    public void setListAdapter(List<Farmer> mFarmers) {
        if (index == 0) {
            listView.setAlpha(0);
            listView.animate().alpha(1).setDuration(1500).setInterpolator(new LinearInterpolator()).start();
        }

        new Handler().post(() -> {
            if (mFarmers.size() > 0) {
                if (IS_TABLET)
                    listView.setNumColumns(AppConstants.TABLET_COLUMN_COUNT);
                else
                    listView.setNumColumns(AppConstants.PHONE_COLUMN_COUNT);

                farmerListViewAdapter = new FarmerListViewAdapter(getActivity(), mFarmers);
                listView.setAdapter(farmerListViewAdapter);
                listView.setOnItemClickListener((adapterView, view, i, l) -> {
                    Farmer farmer = mFarmers.get(i);
                    //Todo uncomment this
                    Intent intent = new Intent(getActivity(), FarmerProfileActivity.class);
                    intent.putExtra("farmer", BaseActivity.getGson().toJson(farmer));
                    startActivity(intent);
                });

                listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
                    if (getAppDataManager().isMonitoring())
                        return false;
                    final Farmer farmer = mFarmers.get(i);
                    mPresenter.showDeleteFarmerDialog(farmer, i);
                    return true;
                });
            }
        });
    }

    @Override
    public void showFarmerDeletedMessage(String farmerName, int position) {
        showMessage(R.string.farmer_data_deleted_message);
        mFarmers.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    @Override
    public void showDeleteFarmerDialog(Farmer farmer, int position) {
        showDialog(true, getString(R.string.delete_farmer), getString(R.string.delete_farmer_rational) + farmer.getFarmerName() + "?",
                (dialogInterface, j) -> {
                    dialogInterface.dismiss();
                    mPresenter.deleteFarmer(farmer, position);
                }, getString(R.string.yes), (dialogInterface, j) -> dialogInterface.dismiss(), getString(R.string.no), 0);
    }

    @Override
    public void openNextActivity() {}

    @Override
    public void showLoading(String title, String message, boolean indeterminate, int icon, boolean cancelableOnTouchOutside) {}

    @Override
    public void openLoginActivityOnTokenExpire() {}

    @Override
    public void toggleFullScreen(Boolean hideNavBar, Window W) {}

    @Override
    public void onDestroy() {
        if (mPresenter != null)
            mPresenter.dropView();
        super.onDestroy();
    }
}
