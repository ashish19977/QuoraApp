package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerDetailsResponse;
import com.upgrad.quora.api.model.AnswerEditRequest;
import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
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
public class AnswerController {

    @Autowired
    private AnswerBusinessService answerBusinessService;


    /* The endpoint "/question/{questionId}/answer/create" is used to  create an answer to a particular question.
    Any user can access this endpoint. It throws InvalidQuestionException and AuthorizationFailedException. */
    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer (@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization, final AnswerRequest answerRequest) throws AuthorizationFailedException, InvalidQuestionException {

        final ZonedDateTime now = ZonedDateTime.now();
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setAns(answerRequest.getAnswer());
        answerEntity.setDate(now);

        final AnswerEntity createdAnswer = answerBusinessService.createAnswer(answerEntity , questionId, authorization);
        AnswerResponse answerResponse = new AnswerResponse().id(createdAnswer.getUuid()).status("ANSWER CREATED!");

        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    /* The endpoint "/answer/edit/{answerId}" is used to edit an answer. Only the owner of the answer can edit the answer.
    It throws AuthorizationFailedException and AnswerNotFoundException. */
    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> editAnswerContent  (@PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String authorization, final AnswerEditRequest answerEditRequest) throws AuthorizationFailedException, AnswerNotFoundException {

        final ZonedDateTime now = ZonedDateTime.now();
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAns(answerEditRequest.getContent());
        answerEntity.setDate(now);

        final AnswerEntity editedAnswer = answerBusinessService.editAnswer(answerEntity , answerId, authorization);
        AnswerResponse answerResponse = new AnswerResponse().id(editedAnswer.getUuid()).status("ANSWER EDITED!");

        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.OK);
    }

    /* The endpoint "/answer/delete/{answerId}" is used to delete an answer. Only the owner of the answer
    or admin can delete an answer. It throws AuthorizationFailedException and AnswerNotFoundException. */
    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> deleteAnswer  (@PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        final AnswerEntity deletedAnswer = answerBusinessService.deleteAnswer(answerId, authorization);
        AnswerResponse answerResponse = new AnswerResponse().id(deletedAnswer.getUuid()).status("ANSWER DELETED!");

        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.OK);
    }

    /* The endpoint "answer/all/{questionId}" is used to get all answers to a particular question.
    Any user can access this endpoint. It throws AuthorizationFailedException and InvalidQuestionException. */
    @RequestMapping(method = RequestMethod.GET, path = "answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion  (@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        final List<AnswerEntity> allAnswers = answerBusinessService.getAllAnswersToQuestion(questionId, authorization);
        final List<AnswerDetailsResponse> allAnswerDetailsResponse = new ArrayList<AnswerDetailsResponse>();
        for(AnswerEntity answerEntity : allAnswers) {
            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse().id(answerEntity.getUuid()).questionContent(answerEntity.getQuestion().getContent()).answerContent(answerEntity.getAns());
            allAnswerDetailsResponse.add(answerDetailsResponse);
        }
        return new ResponseEntity<List<AnswerDetailsResponse>>(allAnswerDetailsResponse, HttpStatus.OK);
    }

}
