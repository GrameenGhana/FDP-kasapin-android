package org.grameen.fdp.kasapin.data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import org.grameen.fdp.kasapin.data.db.dao.ActivitiesDao;
import org.grameen.fdp.kasapin.data.db.dao.ActivitiesPlusInputsDao;
import org.grameen.fdp.kasapin.data.db.dao.CalculationsDao;
import org.grameen.fdp.kasapin.data.db.dao.ComplexCalculationDao;
import org.grameen.fdp.kasapin.data.db.dao.CountryDao;
import org.grameen.fdp.kasapin.data.db.dao.FarmResultsDao;
import org.grameen.fdp.kasapin.data.db.dao.FormsDao;
import org.grameen.fdp.kasapin.data.db.dao.InputsDao;
import org.grameen.fdp.kasapin.data.db.dao.LogicsDao;
import org.grameen.fdp.kasapin.data.db.dao.MonitoringsDao;
import org.grameen.fdp.kasapin.data.db.dao.PlotAndAssessmentsDao;
import org.grameen.fdp.kasapin.data.db.dao.PlotAssessmentDao;
import org.grameen.fdp.kasapin.data.db.dao.PlotsDao;
import org.grameen.fdp.kasapin.data.db.dao.QuestionDao;
import org.grameen.fdp.kasapin.data.db.dao.RealFarmersDao;
import org.grameen.fdp.kasapin.data.db.dao.RecommendationPlusActivitiesDao;
import org.grameen.fdp.kasapin.data.db.dao.RecommendationsDao;
import org.grameen.fdp.kasapin.data.db.dao.SkipLogicsDao;
import org.grameen.fdp.kasapin.data.db.dao.SubmissionsDao;
import org.grameen.fdp.kasapin.data.db.dao.SuppliesCostsDao;
import org.grameen.fdp.kasapin.data.db.dao.VillageAndFarmersDao;
import org.grameen.fdp.kasapin.data.db.dao.VillagesDao;
import org.grameen.fdp.kasapin.data.db.entity.ActivitiesPlusInput;
import org.grameen.fdp.kasapin.data.db.entity.Activity;
import org.grameen.fdp.kasapin.data.db.entity.Calculation;
import org.grameen.fdp.kasapin.data.db.entity.ComplexCalculation;
import org.grameen.fdp.kasapin.data.db.entity.Country;
import org.grameen.fdp.kasapin.data.db.entity.FarmResult;
import org.grameen.fdp.kasapin.data.db.entity.Form;
import org.grameen.fdp.kasapin.data.db.entity.Input;
import org.grameen.fdp.kasapin.data.db.entity.Logic;
import org.grameen.fdp.kasapin.data.db.entity.Monitoring;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.PlotAssessment;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.data.db.entity.Recommendation;
import org.grameen.fdp.kasapin.data.db.entity.RecommendationsPlusActivity;
import org.grameen.fdp.kasapin.data.db.entity.SkipLogic;
import org.grameen.fdp.kasapin.data.db.entity.Submission;
import org.grameen.fdp.kasapin.data.db.entity.SuppliesCost;
import org.grameen.fdp.kasapin.data.db.entity.Village;

import javax.inject.Singleton;


/**
 * Created by AangJnr on 18, September, 2018 @ 2:46 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */
@Singleton
@Database(entities = {Country.class, Village.class, Form.class, Question.class, SkipLogic.class, Logic.class, Recommendation.class,
        RecommendationsPlusActivity.class, ActivitiesPlusInput.class, Activity.class, Input.class, Calculation.class, ComplexCalculation.class,
        RealFarmer.class, Plot.class, Monitoring.class, PlotAssessment.class, FarmResult.class, Submission.class, SuppliesCost.class
}, version = 1, exportSchema = false)

@TypeConverters({DateTypeConverter.class, ListTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract CountryDao countryDao();
    public abstract VillagesDao villagesDao();
    public abstract FormsDao formsDao();
    public abstract QuestionDao questionDao();
    public abstract SkipLogicsDao skipLogicsDao();
    public abstract LogicsDao logicsDao();
    public abstract RecommendationsDao recommendationsDao();
    public abstract RecommendationPlusActivitiesDao recommendationPlusActivitiesDao();
    public abstract ActivitiesPlusInputsDao activitiesPlusInputsDao();
    public abstract ActivitiesDao activitiesDao();
    public abstract InputsDao inputsDao();
    public abstract CalculationsDao calculationsDao();
    public abstract ComplexCalculationDao complexCalculationDao();
    public abstract RealFarmersDao realFarmersDao();
    public abstract PlotsDao plotsDao();
    public abstract MonitoringsDao monitoringsDao();
    public abstract PlotAssessmentDao plotAssessmentDao();
    public abstract FarmResultsDao farmResultsDao();
    public abstract SubmissionsDao submissionsDao();
    public abstract SuppliesCostsDao suppliesCostsDao();
    public abstract PlotAndAssessmentsDao plotAndAssessmentsDao();
    public abstract VillageAndFarmersDao villageAndFarmersDao();


}
