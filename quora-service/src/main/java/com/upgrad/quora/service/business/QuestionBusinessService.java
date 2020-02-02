package com.upgrad.quora.service.business;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity, String authorization) throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }
        questionEntity.setUsers(userAuthTokenEntity.getUser());
        return questionDao.createQuestion(questionEntity);
    }

   public List<QuestionEntity> getAllQuestion(String authorization) throws AuthorizationFailedException{

       UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
       if (userAuthTokenEntity == null) {
           throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
       }
       if (userAuthTokenEntity.getLogoutAt() != null) {
           throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
       }
       return questionDao.getAllQuestion();
   }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(String questionId, String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionId);
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
        }
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        if (!(userAuthTokenEntity.getUser().getRole().equals("admin")) && (userAuthTokenEntity.getUser().getId() != questionEntity.getUsers().getId())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }
        return questionDao.deleteQuestion(questionEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestionsByUser(String userId, String authorization) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        UserEntity userEntity = userDao.getUser(userId);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
        }
        if(userEntity == null){
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }
        return questionDao.getAllQuestionsByUser(userEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContent(String questionId, String authorization, QuestionEntity questionEntity) throws AuthorizationFailedException, InvalidQuestionException {

        QuestionEntity questionEntity1 = questionDao.getQuestionByUuid(questionId);
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if(userAuthTokenEntity == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        }
        if (questionEntity1 == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        if (userAuthTokenEntity.getUser().getId() != questionEntity1.getUsers().getId()){
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }
        questionEntity.setId(questionEntity1.getId());
        questionEntity.setUuid(questionEntity1.getUuid());
        questionEntity.setUsers(questionEntity1.getUsers());
        return  questionDao.editQuestion(questionEntity);
    }


}

