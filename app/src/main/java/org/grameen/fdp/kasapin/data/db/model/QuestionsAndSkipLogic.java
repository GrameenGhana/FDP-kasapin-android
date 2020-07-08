package org.grameen.fdp.kasapin.data.db.model;

import org.grameen.fdp.kasapin.data.db.entity.Mapping;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.SkipLogic;

import java.util.List;

public class QuestionsAndSkipLogic {

    Question question;

    List<SkipLogic> skipLogic;

    List<Mapping> map;

    public QuestionsAndSkipLogic() {
    }

    public List<Mapping> getMap() {
        return map;
    }

    public void setMap(List<Mapping> map) {
        this.map = map;
    }

    public List<SkipLogic> getSkipLogic() {
        return skipLogic;
    }

    public void setSkipLogic(List<SkipLogic> skipLogic) {
        this.skipLogic = skipLogic;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

}
