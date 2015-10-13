package uia.is213.eirik.rewind;

/**
 * Created by Eirik on 29.09.2015.
 */
public class Vote {
    public String id;
    public String lectureCode;
    public String author;
    public String questionId;
    private Question question;

    public Vote(String id, String lectureCode, String author, String questionId){
        this.id = id;
        this.lectureCode = lectureCode;
        this.author = author;
        this.questionId =questionId;
    }


    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
