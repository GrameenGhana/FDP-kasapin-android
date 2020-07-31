package org.grameen.fdp.kasapin.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import org.grameen.fdp.kasapin.data.db.dao.CalculationsDao;
import org.grameen.fdp.kasapin.data.db.dao.CommunitiesDao;
import org.grameen.fdp.kasapin.data.db.dao.CommunityAndFarmersDao;
import org.grameen.fdp.kasapin.data.db.dao.CountryDao;
import org.grameen.fdp.kasapin.data.db.dao.DistrictsDao;
import org.grameen.fdp.kasapin.data.db.dao.FormAndQuestionsDao;
import org.grameen.fdp.kasapin.data.db.dao.FormAnswersDao;
import org.grameen.fdp.kasapin.data.db.dao.FormsDao;
import org.grameen.fdp.kasapin.data.db.dao.MappingsDao;
import org.grameen.fdp.kasapin.data.db.dao.MonitoringDao;
import org.grameen.fdp.kasapin.data.db.dao.PlotsDao;
import org.grameen.fdp.kasapin.data.db.dao.QuestionDao;
import org.grameen.fdp.kasapin.data.db.dao.FarmersDao;
import org.grameen.fdp.kasapin.data.db.dao.RecommendationActivitiesDao;
import org.grameen.fdp.kasapin.data.db.dao.RecommendationsDao;
import org.grameen.fdp.kasapin.data.db.dao.ServerUrlsDao;
import org.grameen.fdp.kasapin.data.db.dao.SkipLogicDao;
import org.grameen.fdp.kasapin.data.db.entity.Calculation;
import org.grameen.fdp.kasapin.data.db.entity.Community;
import org.grameen.fdp.kasapin.data.db.entity.ComplexCalculation;
import org.grameen.fdp.kasapin.data.db.entity.Country;
import org.grameen.fdp.kasapin.data.db.entity.District;
import org.grameen.fdp.kasapin.data.db.entity.FarmResult;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.data.db.entity.Form;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Mapping;
import org.grameen.fdp.kasapin.data.db.entity.Monitoring;
import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.data.db.entity.PlotAssessment;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.Recommendation;
import org.grameen.fdp.kasapin.data.db.entity.RecommendationActivity;
import org.grameen.fdp.kasapin.data.db.entity.ServerUrl;
import org.grameen.fdp.kasapin.data.db.entity.SkipLogic;
import org.grameen.fdp.kasapin.data.db.entity.Submission;
import org.grameen.fdp.kasapin.data.db.entity.SuppliesCost;

import javax.inject.Singleton;

/**
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */
@Singleton
@Database(entities = {Country.class, District.class, Community.class, Form.class, Question.class, SkipLogic.class, Mapping.class, Recommendation.class,
        RecommendationActivity.class, Calculation.class, ComplexCalculation.class,
        Farmer.class, FormAnswerData.class, Plot.class, Monitoring.class, PlotAssessment.class, FarmResult.class, Submission.class, SuppliesCost.class, ServerUrl.class
}, version = 1, exportSchema = false)

@TypeConverters({DateTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract CountryDao countryDao();

    public abstract DistrictsDao districtsDao();

    public abstract CommunitiesDao villagesDao();

    public abstract FormsDao formsDao();

    public abstract QuestionDao questionDao();

    public abstract SkipLogicDao skipLogicsDao();

    public abstract MappingsDao mappingDao();

    public abstract RecommendationsDao recommendationsDao();

    public abstract RecommendationActivitiesDao recommendationPlusActivitiesDao();

    public abstract CalculationsDao calculationsDao();

    public abstract FarmersDao realFarmersDao();

    public abstract FormAnswersDao formAnswerDao();

    public abstract PlotsDao plotsDao();

    public abstract MonitoringDao monitoringDao();

    public abstract CommunityAndFarmersDao villageAndFarmersDao();

    public abstract FormAndQuestionsDao formAndQuestionsDao();

    public abstract ServerUrlsDao serverUrlsDao();
}
