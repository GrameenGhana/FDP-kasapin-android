package org.grameen.fdp.kasapin.data.db.model;

import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.SkipLogic;

import java.util.List;

public class QuestionsAndSkipLogic {

    Question question;

    List<SkipLogic> skiplogic;


    public QuestionsAndSkipLogic(){}


    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setSkiplogic(List<SkipLogic> skiplogic) {
        this.skiplogic = skiplogic;
    }

    public List<SkipLogic> getSkiplogic() {
        return skiplogic;
    }

    public Question getQuestion() {
        return question;
    }

 }
