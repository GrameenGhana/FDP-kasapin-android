package org.grameen.fdp.kasapin.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
 import org.grameen.fdp.kasapin.data.prefs.PreferencesHelper;
import org.grameen.fdp.kasapin.ui.base.BaseFragment;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.ScreenUtils;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aangjnr on 11/12/2017.
 */

public class FarmerListFragment extends BaseFragment {


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    View rootView;


    FarmerListAdapter mAdapter;
    List<RealFarmer> mFarmers;


    AppDataManager appDataManager;


    public FarmerListFragment() {

        super();
    }


    public static FarmerListFragment newInstance(String filter, List<RealFarmer> farmers) {

        FarmerListFragment farmerListFragment = new FarmerListFragment();

        Bundle bundle = new Bundle();
        bundle.putString("filterTag", filter);
        bundle.putString("farmerList",  new Gson().toJson(farmers));
        farmerListFragment.setArguments(bundle);
        return farmerListFragment;


    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_farmer_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUp(view);
    }



    @Override
    protected void setUp(View view) {
        setUnBinder(ButterKnife.bind(view));
        setUpAdapter();

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



    }


    void setUpAdapter() {

        appDataManager = getAppDataManager();
        mFarmers = new Gson().fromJson(getArguments().getString("farmers"), new TypeToken<List<RealFarmer>>() {}.getType());

        if(mFarmers != null && mFarmers.size() > 0) {


            GridLayoutManager productsGridLayoutManager;

            if (ScreenUtils.isTablet((AppCompatActivity) getActivity()))
                productsGridLayoutManager = new GridLayoutManager(getActivity(), 6);
            else
                productsGridLayoutManager = new GridLayoutManager(getActivity(), 4);

            recyclerView.setLayoutManager(productsGridLayoutManager);


            SpacesGridItemDecoration decoration = new SpacesGridItemDecoration(8);
            //  mRecycler.addItemDecoration(decoration);


            mAdapter = new FarmerListAdapter(getActivity(), mFarmers);
            mAdapter.setHasStableIds(true);
            recyclerView.setAdapter(mAdapter);


            mAdapter.setOnItemClickListener((view, position) -> {

                RealFarmer farmer = mFarmers.get(position);

        /*    Intent intent = new Intent(getActivity(), FarmerDetailsActivity.class);
            intent.putExtra("farmer", new Gson().toJson(farmer));
            startActivity(intent);*/

            });


            mAdapter.OnLongClickListener((view, position) -> {

                if (appDataManager.isMonitoring())
                    return;

                final RealFarmer farmer = mFarmers.get(position);
                showDialog(true, getString(R.string.delete_farmer), getString(R.string.delete_farmer_rational) + farmer.getFarmerName() + "?",
                        (dialogInterface, i) -> {

                            dialogInterface.dismiss();

                            appDataManager.getDatabaseManager().realFarmersDao().deleteFarmerById(farmer.getId());
                            showMessage(farmer.getFarmerName() + getString(R.string.farmer_data_deleted_message));
                            mFarmers.remove(position);
                            mAdapter.notifyItemRemoved(position);
                        }, getString(R.string.yes), (dialogInterface, i) -> dialogInterface.dismiss(), getString(R.string.no), 0);

            });


        }
    }





    @Override
    public void openNextActivity() {

    }

    @Override
    public void showLoading(String title, String message, boolean indeterminate, int icon, boolean cancelableOnTouchOutside) {

    }

    @Override
    public void openLoginActivityOnTokenExpire() {

    }

    @Override
    public void onToggleFullScreenClicked(Boolean hideNavBar) {

    }

    @Override
    public void showDialog(Boolean cancelable, String title, String message, DialogInterface.OnClickListener onPositiveButtonClickListener, String positiveText, DialogInterface.OnClickListener onNegativeButtonClickListener, String negativeText, int icon_drawable) {
        super.showDialog(cancelable, title, message, onPositiveButtonClickListener, positiveText, onNegativeButtonClickListener, negativeText, icon_drawable);

    }


    public static class SpacesGridItemDecoration extends RecyclerView.ItemDecoration {
        private final int mSpace;

        public SpacesGridItemDecoration(int space) {
            this.mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {


            outRect.bottom = mSpace;
            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildAdapterPosition(view) == 0 || parent.getChildAdapterPosition(view) == 1 || parent.getChildAdapterPosition(view) == 2) {
                outRect.top = mSpace * 2;
            } else {

                outRect.top = mSpace;

            }

            if (parent.getChildAdapterPosition(view) % 3 == 0) {
                outRect.right = mSpace * 2;
                outRect.left = mSpace;

            } else if (parent.getChildAdapterPosition(view) % 3 == 2) {
                outRect.left = mSpace * 2;
                outRect.right = mSpace;

            }

        }

    }


}
