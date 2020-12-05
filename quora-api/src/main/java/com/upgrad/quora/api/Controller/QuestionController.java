package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.*;


@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    QuestionBusinessService questionBusinessService;


//the method is used to create the question and checks the authorization
    @RequestMapping(method = RequestMethod.POST, path ="/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
      public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
       final  QuestionEntity questionEntity = new QuestionEntity();

        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());

          QuestionEntity  createdQuestionEntity;
        try{
            String[] bearerAccessToken = authorization.split("Bearer ");
            createdQuestionEntity = questionBusinessService.postQuestion(questionEntity, bearerAccessToken[1]);
        } catch(ArrayIndexOutOfBoundsException are) {
            createdQuestionEntity = questionBusinessService.postQuestion(questionEntity, authorization);
        }

        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

//this methods gets all the question
    @RequestMapping(method = RequestMethod.GET, path ="/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException{
        List<QuestionEntity> listOfQuestions = new ArrayList<>();
        try{
            String[] bearerAccessToken = authorization.split("Bearer ");
            listOfQuestions = questionBusinessService.getAllQuestions(bearerAccessToken[1]);
        }catch(ArrayIndexOutOfBoundsException are) {
            listOfQuestions = questionBusinessService.getAllQuestions(authorization);
        }


        List<QuestionDetailsResponse> displayQuestionIdAndContent = new ArrayList<>();
        for(QuestionEntity question:  listOfQuestions){
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse().id(question.getUuid()).
                    content(question.getContent());

            displayQuestionIdAndContent.add(questionDetailsResponse);
         }

        return new ResponseEntity<List<QuestionDetailsResponse>>(displayQuestionIdAndContent, HttpStatus.OK);
    }


//this method is used to delete a question
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") final String questionUuid, @RequestHeader("authorization") final String authorization)throws AuthorizationFailedException, InvalidQuestionException {
        String uuid ;
        try {
            String[] accessToken = authorization.split("Bearer ");
            uuid = questionBusinessService.deleteQuestion(questionUuid, accessToken[1]);
        }catch(ArrayIndexOutOfBoundsException are) {
            uuid = questionBusinessService.deleteQuestion(questionUuid, authorization);
        }
        QuestionDeleteResponse authorizedDeletedResponse = new QuestionDeleteResponse().id(uuid).status("QUESTION DELETED");

        return new ResponseEntity<QuestionDeleteResponse>(authorizedDeletedResponse, HttpStatus.OK);
    }



//this method is used to edit the content of the question  and only the question author can edit the contents
    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes= MediaType.APPLICATION_JSON_UTF8_VALUE, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(@PathVariable("questionId") final String questionUuid, @RequestHeader("authorization") final String authorization, final QuestionEditRequest editRequest) throws AuthorizationFailedException,InvalidQuestionException {
        QuestionEntity questionEntity;
        QuestionEntity editedQuestion;
        try{
            String[] userToken = authorization.split("Bearer ");
            questionEntity = questionBusinessService.getQuestion(questionUuid, userToken[1]);
            questionEntity.setContent(editRequest.getContent());
            editedQuestion = questionBusinessService.editQuestion(questionEntity,userToken[1]);}
        catch(ArrayIndexOutOfBoundsException e){
            questionEntity  = questionBusinessService.getQuestion(questionUuid, authorization);
            questionEntity.setContent(editRequest.getContent());
            editedQuestion = questionBusinessService.editQuestion(questionEntity,authorization);
        }



        String updatedUuid = editedQuestion.getUuid();
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(updatedUuid).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);

    }



//this method gets all the question posted by a userId
    @RequestMapping(method = RequestMethod.GET, path = "question/all/{userId}", produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@RequestHeader("authorization") final String authorization, @PathVariable ("userId") final String userUuid) throws AuthorizationFailedException, UserNotFoundException {
        List<QuestionEntity> listOfUserQuestions = new ArrayList<>();
        try{

            String[] bearerAccessToken = authorization.split("Bearer ");
            listOfUserQuestions = questionBusinessService.getAllQuestionsByUser(bearerAccessToken[1],userUuid);
        }
        catch(Exception e){
            listOfUserQuestions = questionBusinessService.getAllQuestionsByUser(authorization,userUuid);
        }



        List<QuestionDetailsResponse> displayUserQuestionIdAndContent = new ArrayList<>();
        for(QuestionEntity que:  listOfUserQuestions){
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse().id(que.getUuid()).
                    content(que.getContent());

            displayUserQuestionIdAndContent.add(questionDetailsResponse);
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(displayUserQuestionIdAndContent,HttpStatus.OK);

    }
}