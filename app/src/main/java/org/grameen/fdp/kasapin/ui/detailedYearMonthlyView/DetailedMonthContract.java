package org.grameen.fdp.kasapin.ui.detailedYearMonthlyView;


import org.grameen.fdp.kasapin.data.db.entity.Plot;
import org.grameen.fdp.kasapin.ui.base.BaseContract;

import java.util.List;

/**
 * Created by AangJnr on 18, September, 2018 @ 9:09 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class DetailedMonthContract {



    public interface View extends  BaseContract.View {


        void setPlotsData(List<Plot> plotsData);

        void setData();



    }

     public interface Presenter {

        void getPlotsData(String farmerCode);
    }



}
