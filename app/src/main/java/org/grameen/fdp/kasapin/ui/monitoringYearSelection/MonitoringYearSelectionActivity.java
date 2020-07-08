package org.grameen.fdp.kasapin.ui.monitoringYearSelection;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Community;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.landing.LandingActivity;
import org.grameen.fdp.kasapin.ui.plotMonitoringActivity.PlotMonitoringActivity;
import org.grameen.fdp.kasapin.ui.viewImage.ImageViewActivity;
import org.grameen.fdp.kasapin.utilities.CustomToast;
import org.grameen.fdp.kasapin.utilities.ImageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MonitoringYearSelectionActivity extends BaseActivity implements MonitoringYearSelectionContract.View {
    @Inject
    MonitoringYearSelectionPresenter mPresenter;
    @BindView(R.id.photo)
    CircleImageView circleImageView;
    @BindView(R.id.initials)
    TextView initials;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.code)
    TextView code;
    @BindView(R.id.villageName)
    TextView villageName;
    @BindView(R.id.landSize)
    TextView landSize;
    @BindView(R.id.lastVisitDate)
    TextView lastVisitDate;
    @BindView(R.id.lastSyncDate)
    TextView lastSyncDate;
    @BindView(R.id.syncIndicator)
    ImageView syncIndicator;
    @BindView(R.id.list_view)
    ListView listView;
    Farmer FARMER;
    Plot PLOT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_year_selection);
        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.takeView(this);
        FARMER = new Gson().fromJson(getIntent().getStringExtra("farmer"), Farmer.class);
        PLOT = new Gson().fromJson(getIntent().getStringExtra("plot"), Plot.class);
        setUpViews();
        onBackClicked();
    }

    @Override
    public void setupListAdapter() {
        List<String> YEARS = new ArrayList<>();
        YEARS.add(getStringResources(R.string.year_1));
        YEARS.add(getStringResources(R.string.year_2));
        YEARS.add(getStringResources(R.string.year_3));
        YEARS.add(getStringResources(R.string.year_4));
        YEARS.add(getStringResources(R.string.year_5));
        YEARS.add(getStringResources(R.string.year_6));
        YEARS.add(getStringResources(R.string.year_7));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, YEARS);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (PLOT.getRecommendationId() > 0) {
                int year = position + 1;
                Intent intent = new Intent(MonitoringYearSelectionActivity.this, PlotMonitoringActivity.class);
                intent.putExtra("farmer", new Gson().toJson(FARMER));
                intent.putExtra("plot", new Gson().toJson(PLOT));
                intent.putExtra("selectedYear", year);
                startActivity(intent);
            } else
                showDialog(true, getStringResources(R.string.incomplete_ao_monitoring),
                        getStringResources(R.string.incomplete_ao_rational) + PLOT.getName(), (dialogInterface, i) -> dialogInterface.dismiss(), getStringResources(R.string.ok), null, "", 0);
        });
    }

    @Override
    public void setUpViews() {
        setToolbar(getStringResources(R.string.farmer_details));
        name.setText(FARMER.getFarmerName());
        code.setText(FARMER.getCode());
        if (FARMER.getVillageId() > 0) {
            Community village = getAppDataManager().getDatabaseManager().villagesDao().getVillageById(FARMER.getVillageId());

            if (village != null) {
                FARMER.setVillageName(village.getName());
                villageName.setText(FARMER.getVillageName());
            }
        }

        landSize.setText(FARMER.getLandArea());
        if (FARMER.getSyncStatus() == 0) {
            syncIndicator.setImageResource(R.drawable.ic_sync_problem_black_24dp);
            syncIndicator.setColorFilter(ContextCompat.getColor(this, R.color.cpb_red));

        } else if (FARMER.getSyncStatus() == 1) {
            syncIndicator.setImageResource(R.drawable.ic_check_circle_black_24dp);
            syncIndicator.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
        }
        lastSyncDate.setText((FARMER.getLastModifiedDate() != null) ? FARMER.getLastModifiedDate().toString() : "--");
        lastVisitDate.setText((FARMER.getLastVisitDate() != null) ? FARMER.getLastVisitDate().toString() : "--");

        if (FARMER.getImageUrl() != null && !FARMER.getImageUrl().equals("")) {
            circleImageView.setImageBitmap(ImageUtil.base64ToBitmap(FARMER.getImageUrl()));
            initials.setText("");
            circleImageView.setOnClickListener(v -> {
                Intent intent = new Intent(MonitoringYearSelectionActivity.this, ImageViewActivity.class);
                intent.putExtra("image_string", FARMER.getImageUrl());
                startActivity(intent);
            });
        } else {
            try {
                String[] valueArray = FARMER.getFarmerName().split(" ");
                String value = valueArray[0].substring(0, 1) + valueArray[1].substring(0, 1);
                initials.setText(value);
            } catch (Exception e) {
                initials.setText(FARMER.getFarmerName().substring(0, 1));
            }

            int[] mColors = getResources().getIntArray(R.array.recommendations_colors);
            int randomColor = mColors[new Random().nextInt(mColors.length)];
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(1000);
            drawable.setColor(randomColor);
            circleImageView.setBackground(drawable);
            circleImageView.setOnClickListener(v -> CustomToast.makeToast(MonitoringYearSelectionActivity.this, "No image to display!", Toast.LENGTH_LONG).show());
        }
        setupListAdapter();
    }

    @Override
    protected void onDestroy() {
        mPresenter.dropView();
        super.onDestroy();
    }


    @Override
    public void openNextActivity() {
        startActivity(new Intent(this, LandingActivity.class));
        supportFinishAfterTransition();
    }
}