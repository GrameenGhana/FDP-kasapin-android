package org.grameen.fdp.kasapin.ui.main;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.base.BaseFragment;
import org.grameen.fdp.kasapin.ui.addFarmer.AddEditFarmerActivity;
import org.grameen.fdp.kasapin.ui.farmerProfile.FarmerProfileActivity;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.ScreenUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.grameen.fdp.kasapin.ui.base.BaseActivity.IS_TABLET;

/**
 * Created by aangjnr on 11/12/2017.
 */

public class FarmerListFragment extends BaseFragment implements MainContract.FragmentView{


    @Inject
    FarmerListFragmentPresenter mPresenter;


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    @BindView(R.id.grid_view)
    GridView listView;

    View rootView;
    FarmerListRecyclerViewAdapter mAdapter;
    List<RealFarmer> mFarmers;
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
            mFarmers = new Gson().fromJson(getArguments().getString("farmers"), new TypeToken<List<RealFarmer>>() {}.getType());
            SELECTED_VILLAGE = getArguments().getString("village");

            index = getArguments().getInt("index");

            setListAdapter(mFarmers);
        }

        //mPresenter.getFarmerData();



    }



    @Override
    public void setRecyclerAdapter() {

       /* if(mFarmers != null && mFarmers.size() > 0) {


            GridLayoutManager productsGridLayoutManager;

            if (ScreenUtils.isTablet((AppCompatActivity) getActivity()))
                productsGridLayoutManager = new GridLayoutManager(getActivity(), 6);
            else
                productsGridLayoutManager = new GridLayoutManager(getActivity(), 4);

            recyclerView.setLayoutManager(productsGridLayoutManager);


            SpacesGridItemDecoration decoration = new SpacesGridItemDecoration(8);
            //  mRecycler.addItemDecoration(decoration);


            mAdapter = new FarmerListRecyclerViewAdapter(getActivity(), mFarmers);
            mAdapter.setHasStableIds(true);
            recyclerView.setAdapter(mAdapter);


            mAdapter.setOnItemClickListener((view, position) -> {

                RealFarmer farmer = mFarmers.get(position);

                //Todo uncomment this

            Intent intent = new Intent(getActivity(), FarmerProfileActivity.class);
            intent.putExtra("farmer", BaseActivity.getGson().toJson(farmer));
            startActivity(intent);

            });


            mAdapter.OnLongClickListener((view, position) -> {

                if (getAppDataManager().isMonitoring())
                    return;

                final RealFarmer farmer = mFarmers.get(position);
                showDialog(true, getString(R.string.delete_farmer), getString(R.string.delete_farmer_rational) + farmer.getFarmerName() + "?",
                        (dialogInterface, i) -> {

                            dialogInterface.dismiss();

                            getAppDataManager().getDatabaseManager().realFarmersDao().deleteFarmerById(farmer.getId());
                            showMessage(farmer.getFarmerName() + getString(R.string.farmer_data_deleted_message));
                            mFarmers.remove(position);
                            mAdapter.notifyItemRemoved(position);
                        }, getString(R.string.yes), (dialogInterface, i) -> dialogInterface.dismiss(), getString(R.string.no), 0);

            });


        }

*/


    }


    @Override
    public void setListAdapter(List<RealFarmer> mFarmers) {


        if(index == 0) {
            listView.setAlpha(0);
            listView.animate().alpha(1).setDuration(1000).setInterpolator(new LinearInterpolator()).start();
        }


        new Handler().post(() -> {


        if(mFarmers.size() > 0) {

        if (IS_TABLET)
            listView.setNumColumns(AppConstants.TABLET_COLUMN_COUNT);
        else
            listView.setNumColumns(AppConstants.PHONE_COLUMN_COUNT);


        farmerListViewAdapter = new FarmerListViewAdapter(getActivity(), mFarmers);
        listView.setAdapter(farmerListViewAdapter);


       // BaseActivity.runLayoutAnimation(listView);


        listView.setOnItemClickListener((adapterView, view, i, l) -> {

            AppLogger.i(TAG, "ON CLICK ");

            RealFarmer farmer = mFarmers.get(i);
            //Todo uncomment this

            Intent intent = new Intent(getActivity(), FarmerProfileActivity.class);
            intent.putExtra("farmer", BaseActivity.getGson().toJson(farmer));
            startActivity(intent);

        });


        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {

            if (getAppDataManager().isMonitoring())
                return false;

            final RealFarmer farmer = mFarmers.get(i);
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
    public void showDeleteFarmerDialog(RealFarmer farmer, int position) {
        showDialog(true, getString(R.string.delete_farmer), getString(R.string.delete_farmer_rational) + farmer.getFarmerName() + "?",
                (dialogInterface, j) -> {

                    dialogInterface.dismiss();
                    mPresenter.deleteFarmer(farmer, position);
                }, getString(R.string.yes), (dialogInterface, j) -> dialogInterface.dismiss(), getString(R.string.no), 0);
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
    public void toggleFullScreen(Boolean hideNavBar, Window W) {

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


    @Override
    public void onDestroy() {
        if(mPresenter != null)
        mPresenter.dropView();


        super.onDestroy();

    }
}
