package org.grameen.fdp.kasapin.ui.serverUrl;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Selection;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karan.churi.PermissionManager.PermissionManager;

import org.grameen.fdp.kasapin.FDPKasapin;
import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.ServerUrl;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.landing.LandingActivity;
import org.grameen.fdp.kasapin.ui.login.LoginActivity;
import org.grameen.fdp.kasapin.ui.map.PointsListAdapter;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A login screen that offers login via email/password.
 */
public class AddEditServerUrlActivity extends BaseActivity implements AddEditServerUrlContract.View, ServerUrlListAdapter.OnItemClickListener, ServerUrlListAdapter.OnDeleteClickListener {

    @Inject
    AddEditServerUrlPresenter mPresenter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    ServerUrlListAdapter mAdapter;

    @BindView(R.id.place_holder)
    View placceHolderView;

    boolean wereChangesMade = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setStatusBarColor(getWindow(), R.color.colorPrimaryDark, false);

        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_add_edit_server_url);

        setToolbar("Server Url List");

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));

        mPresenter.takeView(this);

        mPresenter.fetchData();



        onBackClicked();
    }


    @Override
    public void showServerList(List<ServerUrl> serverUrls) {



        mAdapter = new ServerUrlListAdapter(this, serverUrls, getAppDataManager().getStringValue(AppConstants.SERVER_URL));
        mAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnDeleteClickListener(this);

        togglePlaceholder();


    }

    @Override
    protected void onDestroy() {
        mPresenter.dropView();
        super.onDestroy();
    }


     @Override
    public void openLoginActivityOnTokenExpire() {
    }

    void togglePlaceholder(){
        placceHolderView.setVisibility((mAdapter.getUrlss().size() > 0) ? View.GONE : View.VISIBLE);
    }


    @OnClick(R.id.add_url)
    void showAddUrlDialog(){

        Dialog dialog = new Dialog(this, R.style.AppAlertDialog);
        dialog.setContentView(R.layout.custom_dialog_view_add_url);
        dialog.setCancelable(true);

        EditText nameEdittext = dialog.findViewById(R.id.name_view);
        EditText urlEdittext = dialog.findViewById(R.id.url_view);

        dialog.findViewById(R.id.cancel).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.ok).setOnClickListener(v -> {

            String url = urlEdittext.getText().toString().trim();
            if(!URLUtil.isValidUrl(url))
                showMessage("Please enter a valid url");
            else{

                if(!url.endsWith("/"))
                    url += "/";

                dialog.dismiss();

                ServerUrl serverUrl = new ServerUrl();
                serverUrl.setName(nameEdittext.getText().toString().trim());
                serverUrl.setUrl(url);

                mPresenter.saveUrl(serverUrl);
                mAdapter.add(serverUrl);

                togglePlaceholder();
            }
            wereChangesMade = true;
        });


        dialog.show();

    }


    @Override
    public void onItemClick(View view, int position) {
        //Confirm default setting of currently clicked url
        showDialog(true, "Default Url", "Do you want to set " + mAdapter.getUrlss().get(position).getUrl() + " as default url?", (d, w)->{
            getAppDataManager().setStringValue(AppConstants.SERVER_URL, mAdapter.getUrlss().get(position).getUrl());

            mAdapter.setCurrentUrl(mAdapter.getUrlss().get(position).getUrl());
            showMessage(getString(R.string.default_url_set_message));

                    wereChangesMade = true;

                }, getStringResources(R.string.yes),
                (d, w) -> d.dismiss(), getStringResources(R.string.cancel), 0);

    }

    @Override
    public void onDeleteClick(View view, int position) {
        String discardedUrl = mAdapter.getUrlss().get(position).getUrl();
        //Confirm delete? Delete if user clicks on ok
        showDialog(true, "Delete url", "Are you sure you want to delete the selected url?", (d, w)->{

            mPresenter.deleteUrl(mAdapter.getUrlss().get(position));
            mAdapter.remove(position);

            if(discardedUrl.equals(getAppDataManager().getStringValue(AppConstants.SERVER_URL)))
                getAppDataManager().setStringValue(AppConstants.SERVER_URL, null);

                    togglePlaceholder();
                    wereChangesMade = true;
                }, getStringResources(R.string.yes),
                (d, w) -> d.dismiss(), getStringResources(R.string.cancel), 0);

    }



    @Override
    public void onBackPressed() {

        if(wereChangesMade)
        restartApp();
        else
        super.onBackPressed();
    }

    void restartApp(){
        new Handler().post(() -> {
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());

            new Handler().postDelayed(() -> {
                Intent i = new Intent(this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }, 1000);
        });

    }
}