package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {


    @Autowired
    private QuestionBusinessService questionBusinessService;

    @Autowired
    private AuthenticationService userAuthBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") final String authorization, final QuestionRequest questionRequest) throws AuthorizationFailedException, SignOutRestrictedException {

        final ZonedDateTime now = ZonedDateTime.now();
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(now);

        final QuestionEntity createdQuestion = questionBusinessService.createQuestion(questionEntity , authorization);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> GetAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, SignOutRestrictedException{


        final List<QuestionEntity> allQuestion = questionBusinessService.getAllQuestion(authorization);

        List<QuestionDetailsResponse> questionResponse = questionslist(allQuestion);

        return new ResponseEntity<List<QuestionDetailsResponse>>(questionResponse, HttpStatus.OK);


    }
    @RequestMapping(method=RequestMethod.DELETE,path="/question/delete/{questionId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse>DeleteQuestion(@RequestHeader("authorization") final String authorization, @PathVariable("questionId") final String questionid) throws AuthorizationFailedException, InvalidQuestionException, SignOutRestrictedException {

        QuestionEntity deletedQuestion = questionBusinessService.deleteQuestion(questionid, authorization);
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(deletedQuestion.getUuid()).status("QUESTION DELETED");

        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET , path = "/question/all/{userId}" , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@PathVariable("userId") final String userId, @RequestHeader("authorization") final String authorization )
            throws AuthorizationFailedException , UserNotFoundException, SignOutRestrictedException {

        final List<QuestionEntity> questionEntityList = questionBusinessService.getAllQuestionsByUser(userId, authorization);
        final List<QuestionDetailsResponse> allQuestionDetailsResponse = new ArrayList<QuestionDetailsResponse>();
        for(QuestionEntity questionEntity : questionEntityList) {
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse().id(questionEntity.getUuid()).content(questionEntity.getContent());
            allQuestionDetailsResponse.add(questionDetailsResponse);
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(allQuestionDetailsResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT , path = "/question/edit/{questionId}" ,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<QuestionEditResponse> editQuestionContent(@PathVariable("questionId") final String questionId , @RequestHeader("authorization") final String authorization, QuestionEditRequest questionEditRequest)
            throws AuthorizationFailedException, InvalidQuestionException, SignOutRestrictedException {

        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionEditRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());
        QuestionEntity editedQuestion = questionBusinessService.editQuestionContent(questionId, authorization, questionEntity);
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(editedQuestion.getUuid()).status("QUESTION EDITED");

        return new ResponseEntity<QuestionEditResponse>(questionEditResponse,HttpStatus.OK);
    }



    public List<QuestionDetailsResponse> questionslist(List<QuestionEntity> allQuestion){
        List<QuestionDetailsResponse> listofquestions = new ArrayList<>();
        for ( QuestionEntity questionEntity : allQuestion){
            QuestionDetailsResponse Response = new QuestionDetailsResponse();
            Response.id(questionEntity.getUuid());
            Response.content(questionEntity.getContent());
            listofquestions.add(Response);
        }
        return listofquestions;
    }
}