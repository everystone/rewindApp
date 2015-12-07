package uia.is213.eirik.rewind.Comparators;

import java.util.Comparator;

import uia.is213.eirik.rewind.Models.Question;

/**
 * Created by Eirik on 31.10.2015.
 */
public class QuestionVotesComparator implements Comparator<Question> {
    @Override
    public int compare(Question lhs, Question rhs) {
        return rhs.getVotes() - lhs.getVotes();
    }
}
