package org.grameen.fdp.kasapin.data.db.entity;


import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by AangJnr on 26, September, 2018 @ 12:29 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class FormAndQuestions {
    @Embedded
    Form form;


    @Relation(parentColumn = "translationId", entityColumn = "formTranslationId", entity = Question.class)
    public List<Question> questions;

    private static int compare(Question o1, Question o2) {
        return Integer.compare(o1.getDisplayOrderC(), o2.getDisplayOrderC());
    }


    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public List<Question> getQuestions() {
        Collections.sort(questions, FormAndQuestions::compare);
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
