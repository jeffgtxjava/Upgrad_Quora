package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerDeleteResponse;
import com.upgrad.quora.api.model.AnswerDetailsResponse;
import com.upgrad.quora.api.model.AnswerEditRequest;
import com.upgrad.quora.api.model.AnswerEditResponse;
import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.AuthorizationService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.DatabaseException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/")
public class AnswerController {

  @Autowired
  public AnswerService answerService;

  @Autowired
  public AuthorizationService authorizationService;

  @Autowired
  public QuestionService questionService;

  /**
   * Adds a new answer to an existing question. A POST request. Throws "InvalidQuestionException" if
   * questionId is invalid. Throws "AuthorizationFailedException" if access token is not present in
   * the database or user has signed out, or JWT token is malformed.
   *
   * @param accessToken   JWT token.
   * @param questionUuid  UUID of question to which the answer has to be added.
   * @param answerRequest Answer details.
   * @return ResponseEntity<AnswerResponse> with answer UUID, message "ANSWER CREATED" and HTTP
   * STATUS 201.
   * @throws AuthorizationFailedException
   * @throws InvalidQuestionException
   */
  @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerResponse> createAnswer(
      @RequestHeader("authorization") final String accessToken,
      @PathVariable("questionId") String questionUuid,
      AnswerRequest answerRequest)
      throws InvalidQuestionException, AuthorizationFailedException, DatabaseException {

    final AnswerEntity answerEntity = new AnswerEntity();
    answerEntity.setUuid(UUID.randomUUID().toString());
    answerEntity.setAnswer(answerRequest.getAnswer());
    answerEntity.setDate(ZonedDateTime.now());

    UserEntity loggedInUserEntity = authorizationService.validateJWTToken(accessToken);
    QuestionEntity questionEntity = questionService.getQuestion(questionUuid);

    answerEntity.setQuestionEntity(questionEntity);
    answerEntity.setUserEntity(loggedInUserEntity);

    final AnswerEntity createdAnswerEntity = answerService.createAnswer(answerEntity);
    AnswerResponse answerResponse = new AnswerResponse().id(createdAnswerEntity.getUuid())
        .status("ANSWER CREATED");
    return new ResponseEntity<>(answerResponse, HttpStatus.CREATED);

  }

  /**
   * Edits an answer to a question. Only owner of the answer can edit the same. A PUT request.
   * Throws "AnswerNotFoundException" if answerId is invalid. Throws "AuthorizationFailedException"
   * if access token is not present in the database or user has signed out, or JWT token is
   * malformed, or answer is not being modified by answer owner.
   *
   * @param accessToken       JWT token.
   * @param answerUuid        UUID of the answer which has to be edited.
   * @param answerEditRequest Details of the new answer content.
   * @return ResponseEntity<AnswerEditResponse> with answer UUID, message "ANSWER EDITED" and HTTP
   * STATUS 200.
   * @throws AnswerNotFoundException
   * @throws AuthorizationFailedException
   */
  @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerEditResponse> editAnswerContent(
      @RequestHeader("authorization") final String accessToken,
      @PathVariable("answerId") String answerUuid,
      AnswerEditRequest answerEditRequest)
      throws AnswerNotFoundException, AuthorizationFailedException {

    UserEntity loggedInUserEntity = authorizationService.validateJWTToken(accessToken);

    final AnswerEntity answerToEdit = new AnswerEntity();
    answerToEdit.setUuid(answerUuid);
    answerToEdit.setAnswer(answerEditRequest.getContent());
    answerToEdit.setUserEntity(loggedInUserEntity);

    final AnswerEntity createdAnswerEntity = answerService.editAnswerContent(answerToEdit);
    AnswerEditResponse answerEditResponse = new AnswerEditResponse()
        .id(createdAnswerEntity.getUuid())
        .status("ANSWER EDITED");
    return new ResponseEntity<>(answerEditResponse, HttpStatus.OK);

  }

  /**
   * Deletes an answer to a question. Only owner of the answer or admin user can delete the same. A
   * DELETE request. Throws "AnswerNotFoundException" if answerId is invalid. Throws
   * "AuthorizationFailedException" if access token is not present in the database or user has
   * signed out, or JWT token is malformed, or answer is not being deleted by answer owner or
   * admin.
   *
   * @param accessToken JWT token.
   * @param answerUuid  UUID of the answer which has to be edited.
   * @return ResponseEntity<AnswerDeleteResponse> with answer UUID, message "ANSWER DELETED" and
   * HTTP STATUS 200.
   * @throws AnswerNotFoundException
   * @throws AuthorizationFailedException
   */
  @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerDeleteResponse> deleteAnswer(
      @RequestHeader("authorization") final String accessToken,
      @PathVariable("answerId") String answerUuid)
      throws AnswerNotFoundException, AuthorizationFailedException {

    UserEntity loggedInUserEntity = authorizationService.validateJWTToken(accessToken);

    final AnswerEntity answerToDelete = new AnswerEntity();
    answerToDelete.setUuid(answerUuid);
    answerToDelete.setUserEntity(loggedInUserEntity);

    final AnswerEntity deletedAnswerEntity = answerService.deleteAnswer(answerToDelete);
    AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse()
        .id(deletedAnswerEntity.getUuid())
        .status("ANSWER DELETED");
    return new ResponseEntity<>(answerDeleteResponse, HttpStatus.OK);

  }

  /**
   * Get all answers to a question. A GET request. Throws "InvalidQuestionException" if questionId
   * is invalid. Throws "AuthorizationFailedException" if access token is not present in the
   * database or user has signed out, or JWT token is malformed.
   *
   * @param accessToken  JWT token.
   * @param questionUuid UUID of the question for which all the answers are required.
   * @return ResponseEntity<AnswerDetailsResponse> with answer UUID, content of questions, content
   * of all answers and HTTP STATUS 200.
   * @throws InvalidQuestionException
   * @throws AuthorizationFailedException
   */
  @RequestMapping(method = RequestMethod.GET, path = "/answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(
      @RequestHeader("authorization") final String accessToken,
      @PathVariable("questionId") String questionUuid
  )
      throws InvalidQuestionException, AuthorizationFailedException, DatabaseException {

    authorizationService.validateJWTToken(accessToken);
    QuestionEntity questionEntity = questionService.getQuestion(questionUuid);

    final List<AnswerEntity> answerEntityList = answerService.getAllAnswersToQuestion(questionUuid);

    List<AnswerDetailsResponse> answerDetailsResponses = new ArrayList<>();
    for (AnswerEntity answerEntity : answerEntityList) {
      AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse();
      answerDetailsResponse.setId(answerEntity.getUuid());
      answerDetailsResponse.setQuestionContent(questionEntity.getContent());
      answerDetailsResponse.setAnswerContent(answerEntity.getAnswer());
      answerDetailsResponses.add(answerDetailsResponse);
    }

    return new ResponseEntity<>(answerDetailsResponses, HttpStatus.OK);

  }
}