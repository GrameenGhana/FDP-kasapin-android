package org.grameen.fdp.kasapin.di.component;


import org.grameen.fdp.kasapin.di.Scope.PerActivity;
import org.grameen.fdp.kasapin.di.module.ViewModule;
import org.grameen.fdp.kasapin.ui.AddEditFarmerPlot.AddEditFarmerPlotActivity;
import org.grameen.fdp.kasapin.ui.addFarmer.AddEditFarmerActivity;
import org.grameen.fdp.kasapin.ui.addPlotMonitoring.AddPlotMonitoringActivity;
import org.grameen.fdp.kasapin.ui.detailedYearMonthlyView.DetailedMonthActivity;
import org.grameen.fdp.kasapin.ui.familyMembers.FamilyMembersActivity;
import org.grameen.fdp.kasapin.ui.farmAssessment.FarmAssessmentActivity;
import org.grameen.fdp.kasapin.ui.farmerProfile.FarmerProfileActivity;
import org.grameen.fdp.kasapin.ui.fdpStatus.FDPStatusDialogActivity;
import org.grameen.fdp.kasapin.ui.form.fragment.DynamicFormFragment;
import org.grameen.fdp.kasapin.ui.gpsPicker.MapActivity;
import org.grameen.fdp.kasapin.ui.landing.LandingActivity;
import org.grameen.fdp.kasapin.ui.login.LoginActivity;
import org.grameen.fdp.kasapin.ui.main.FarmerListFragment;
import org.grameen.fdp.kasapin.ui.main.MainActivity;
import org.grameen.fdp.kasapin.ui.monitoringYearSelection.MonitoringYearSelectionActivity;
import org.grameen.fdp.kasapin.ui.pandl.ProfitAndLossActivity;
import org.grameen.fdp.kasapin.ui.plotDetails.PlotDetailsActivity;
import org.grameen.fdp.kasapin.ui.plotMonitoringActivity.PlotMonitoringActivity;
import org.grameen.fdp.kasapin.ui.plotReview.PlotReviewActivity;
import org.grameen.fdp.kasapin.ui.serverUrl.AddEditServerUrlActivity;
import org.grameen.fdp.kasapin.ui.splash.SplashActivity;
import org.grameen.fdp.kasapin.ui.viewImage.ImageViewActivity;

import dagger.Component;

@PerActivity
@Component(modules = {ViewModule.class}, dependencies = {ApplicationComponent.class})
public interface ActivityComponent {
    void inject(SplashActivity view);

    void inject(LoginActivity view);

    void inject(LandingActivity view);

    void inject(MainActivity view);

    void inject(FarmerListFragment view);

    void inject(AddEditFarmerActivity view);

    void inject(FarmerProfileActivity view);

    void inject(DynamicFormFragment view);

    void inject(AddEditFarmerPlotActivity view);

    void inject(PlotDetailsActivity view);

    void inject(MapActivity view);

    void inject(ProfitAndLossActivity view);

    void inject(FDPStatusDialogActivity view);

    void inject(DetailedMonthActivity view);

    void inject(PlotReviewActivity view);

    void inject(MonitoringYearSelectionActivity view);

    void inject(PlotMonitoringActivity view);

    void inject(AddPlotMonitoringActivity view);

    void inject(FamilyMembersActivity view);

    void inject(FarmAssessmentActivity view);

    void inject(AddEditServerUrlActivity view);

    void inject(ImageViewActivity view);

}