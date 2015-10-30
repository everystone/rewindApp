package uia.is213.eirik.rewind;

/**
 * Created by Eirik on 29.09.2015.
 */
public class Vote {
    public String getAuthor() {
        return author;
    }

    public String getLectureCode() {
        return lectureCode;
    }

    public String getId() {
        return id;
    }

    private String id;
    private String lectureCode;
    private String author;
    private String questionId;
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

    public String getQuestionId() {
        return questionId;
    }
}
