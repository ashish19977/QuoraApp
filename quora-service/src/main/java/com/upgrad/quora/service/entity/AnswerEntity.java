package com.upgrad.quora.service.entity;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "answer" , schema = "public")
@NamedQueries(
        {
                @NamedQuery(name = "answerByUuid", query = "select a from AnswerEntity a where a.uuid = :answerId"),
                @NamedQuery(name = "answerByQuestionId", query = "select a from AnswerEntity a where a.question = :question")
        }
)

public class AnswerEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @NotNull
    private String uuid;

    @Column(name = "ANS")
    @NotNull
    private String ans;

    @Column(name = "DATE")
    @NotNull
    private ZonedDateTime Date;

    @ManyToOne
    @JoinColumn(name="USER_ID")
    private UserEntity users;

    @ManyToOne
    @JoinColumn(name="QUESTION_ID")
    private QuestionEntity question;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    public ZonedDateTime getDate() {
        return Date;
    }

    public void setDate(ZonedDateTime date) {
        Date = date;
    }

    public UserEntity getUsers() {
        return users;
    }

    public void setUsers(UserEntity users) {
        this.users = users;
    }

    public QuestionEntity getQuestion() {
        return question;
    }

    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }
}
