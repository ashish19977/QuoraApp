package com.upgrad.quora.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "question" , schema = "public")
@NamedQueries(
        {
                @NamedQuery(name = "allQuestions" , query = "select q from QuestionEntity q"),
                @NamedQuery(name = "questionByUuid", query = "select q from QuestionEntity q where q.uuid =:questionId"),
                @NamedQuery(name = "questionsByUser", query = "select q from QuestionEntity q where q.users =:users")
        }
)

public class QuestionEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @NotNull
    private String uuid;

    @Column(name = "CONTENT")
    @NotNull
    private String content;

    @Column(name = "DATE")
    @NotNull
    private ZonedDateTime Date;

    @ManyToOne
    @JoinColumn(name="USER_ID")
    private UserEntity users;

    @OneToMany(mappedBy = "question")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<AnswerEntity> answer;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getDate() {
        return Date;
    }

    public void setDate(ZonedDateTime date) {
        Date = date;
    }

    public List<AnswerEntity> getAnswer() {
        return answer;
    }

    public void setAnswer(List<AnswerEntity> answer) {
        this.answer = answer;
    }

    public UserEntity getUsers() {
        return users;
    }

    public void setUsers(UserEntity users) {
        this.users = users;
    }
}
