package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public AnswerEntity editAnswer(AnswerEntity answerEntity) {
        entityManager.merge(answerEntity);
        return answerEntity;
    }

    public AnswerEntity deleteAnswer(AnswerEntity answerEntity) {
        entityManager.remove(answerEntity);
        return answerEntity;
    }

    public UserAuthTokenEntity getUserAuthToken(String accessToken) {
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public AnswerEntity getAnswer(String answerId) {
        try {
            return entityManager.createNamedQuery("answerByUuid", AnswerEntity.class).setParameter("answerId", answerId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<AnswerEntity> getAllAnswers(final QuestionEntity questionEntity) {
        try {
            return entityManager.createNamedQuery("answerByQuestionId", AnswerEntity.class).setParameter("question", questionEntity).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
