package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AnswerBusinessService {

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(AnswerEntity answerEntity, final String questionId, final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionId);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }
        UserAuthTokenEntity userAuthTokenEntity = answerDao.getUserAuthToken(authorizationToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if(userAuthTokenEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }
        answerEntity.setUsers(userAuthTokenEntity.getUser());

        answerEntity.setQuestion(questionEntity);
        return answerDao.createAnswer(answerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(AnswerEntity answerEntity, final String answerId, final String authorizationToken) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = answerDao.getUserAuthToken(authorizationToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if(userAuthTokenEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
        }
        AnswerEntity answerEntity1 = answerDao.getAnswer(answerId);
        if (answerEntity1 == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        if(userAuthTokenEntity.getUser().getId() != answerEntity1.getUsers().getId()){
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }
        answerEntity.setId(answerEntity1.getId());
        answerEntity.setUuid(answerEntity1.getUuid());
        answerEntity.setUsers(answerEntity1.getUsers());
        answerEntity.setQuestion(answerEntity1.getQuestion());

        return answerDao.editAnswer(answerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswer(final String answerId, final String authorizationToken) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if(userAuthTokenEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        }

        AnswerEntity answerEntity = answerDao.getAnswer(answerId);
        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        String userRole = userAuthTokenEntity.getUser().getRole();
        Integer signinUserId = userAuthTokenEntity.getUser().getId();
        Integer answerUserId = answerEntity.getUsers().getId();
        if(!(userRole.equals("admin")) && (signinUserId != answerUserId)){
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }

        return answerDao.deleteAnswer(answerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<AnswerEntity> getAllAnswersToQuestion(final String questionId, final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionId);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
        }
        UserAuthTokenEntity userAuthTokenEntity = answerDao.getUserAuthToken(authorizationToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if(userAuthTokenEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        }
        List<AnswerEntity> answerEntityList = answerDao.getAllAnswers(questionEntity);

        return answerEntityList;
    }

}
