package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDeleteResponse;
import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionEditRequest;
import com.upgrad.quora.api.model.QuestionEditResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.AuthorizationService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserAdminBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.DatabaseException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.QuestionsNotFoundException;
import com.upgrad.quora.service.exception.UserNotFoundException;
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
public class QuestionController {

  @Autowired
  private QuestionService questionService;

  @Autowired
  private AuthorizationService authorizationService;

  @Autowired
  private UserAdminBusinessService userAdminBusinessService;

  /**
   * Posts a new question. A POST request. Throws "AuthorizationFailedException" if access token is
   * not present in the database or user has signed out, or JWT token is malformed.
   *
   * @param questionRequest Details of new question to be posted
   * @param accessToken     JWT token
   * @return ResponseEntity<QuestionResponse> with question UUID, message "QUESTION CREATED" and
   * HTTP STATUS 201.
   * @throws AuthorizationFailedException
   */
  @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest,
      @RequestHeader("authorization") final String accessToken)
      throws AuthorizationFailedException, DatabaseException {

    UserEntity userEntity = authorizationService.validateJWTToken(accessToken);

    final QuestionEntity questionEntity = new QuestionEntity();

    questionEntity.setUuid(UUID.randomUUID().toString());
    questionEntity.setContent(questionRequest.getContent());
    questionEntity.setDate(ZonedDateTime.now());
    questionEntity.setUserEntity(userEntity);

    QuestionEntity createdQuestionEntity = questionService.postQuestion(questionEntity);

    QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid())
        .status("QUESTION CREATED");

    return new ResponseEntity<>(questionResponse, HttpStatus.CREATED);
  }


  /**
   * Get all questions posted on portal. A GET request. Throws "AuthorizationFailedException" if
   * access token is not present in the database or user has signed out, or JWT token is malformed.
   * Throws "QuestionsNotFoundException" if no questions have been posted yet. Throws
   * "DatabaseException" when some DB access issues occur.
   *
   * @param accessToken JWT token
   * @return ResponseEntity<QuestionDetailsResponse> with list of all questions and HTTP STATUS 200.
   * @throws AuthorizationFailedException
   * @throws DatabaseException
   * @throws QuestionsNotFoundException
   */
  @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(
      @RequestHeader("authorization") final String accessToken)
      throws AuthorizationFailedException, DatabaseException, QuestionsNotFoundException {

    authorizationService.validateJWTToken(accessToken);

    List<QuestionEntity> questionEntities = questionService.getAllQuestions();

    List<QuestionDetailsResponse> displayQuestionIdAndContent = new ArrayList<>();
    for (QuestionEntity question : questionEntities) {
      QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse()
          .id(question.getUuid()).
              content(question.getContent());

      displayQuestionIdAndContent.add(questionDetailsResponse);
    }

    return new ResponseEntity<>(displayQuestionIdAndContent,
        HttpStatus.OK);
  }

  /**
   * Edit the content of an existing question only by the question owner. A PUT request. Throws
   * "AuthorizationFailedException" if access token is not present in the database or user has
   * signed out, or JWT token is malformed. Throws "InvalidQuestionException" if question does not
   * exist in database. Throws "DatabaseException" when some DB access issues occur.
   *
   * @param questionUuid UUID of the question to be edited.
   * @param accessToken  JWT token.
   * @param editRequest  The new content of the question.
   * @return
   * @throws AuthorizationFailedException
   * @throws InvalidQuestionException
   * @throws DatabaseException
   */
  @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<QuestionEditResponse> editQuestionContent(
      @PathVariable("questionId") final String questionUuid,
      @RequestHeader("authorization") final String accessToken,
      final QuestionEditRequest editRequest)
      throws AuthorizationFailedException, InvalidQuestionException, DatabaseException {

    UserEntity userEntity = authorizationService.validateJWTToken(accessToken);

    final QuestionEntity questionToEdit = new QuestionEntity();
    questionToEdit.setUuid(questionUuid);
    questionToEdit.setUserEntity(userEntity);
    questionToEdit.setContent(editRequest.getContent());

    final QuestionEntity editedQuestion = questionService.editQuestion(questionToEdit);
    QuestionEditResponse questionEditResponse = new QuestionEditResponse()
        .id(editedQuestion.getUuid())
        .status("QUESTION EDITED");
    return new ResponseEntity<>(questionEditResponse, HttpStatus.OK);

  }

  /**
   * @param questionUuid
   * @param accessToken
   * @return
   * @throws AuthorizationFailedException
   * @throws InvalidQuestionException
   * @throws DatabaseException
   */
  @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<QuestionDeleteResponse> deleteQuestion(
      @PathVariable("questionId") final String questionUuid,
      @RequestHeader("authorization") final String accessToken)
      throws AuthorizationFailedException, InvalidQuestionException, DatabaseException {

    UserEntity userEntity = authorizationService.validateJWTToken(accessToken);

    final QuestionEntity questionToDelete = new QuestionEntity();
    questionToDelete.setUuid(questionUuid);
    questionToDelete.setUserEntity(userEntity);

    final QuestionEntity deletedQuestion = questionService.deleteQuestion(questionToDelete);

    QuestionDeleteResponse authorizedDeletedResponse = new QuestionDeleteResponse()
        .id(deletedQuestion.getUuid())
        .status("QUESTION DELETED");

    return new ResponseEntity<>(authorizedDeletedResponse, HttpStatus.OK);
  }


  /**
   * @param accessToken
   * @param userUuid
   * @return
   * @throws AuthorizationFailedException
   * @throws UserNotFoundException
   * @throws DatabaseException
   * @throws QuestionsNotFoundException
   */
  @RequestMapping(method = RequestMethod.GET, path = "question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(
      @RequestHeader("authorization") final String accessToken,
      @PathVariable("userId") final String userUuid)
      throws AuthorizationFailedException, UserNotFoundException, DatabaseException, QuestionsNotFoundException {

    authorizationService.validateJWTToken(accessToken);

    UserEntity userEntity = userAdminBusinessService.getUser(userUuid);

    List<QuestionEntity> listOfUserQuestions = questionService.getAllQuestionsByUser(userEntity);

    List<QuestionDetailsResponse> displayUserQuestionIdAndContent = new ArrayList<>();
    for (QuestionEntity que : listOfUserQuestions) {
      QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse()
          .id(que.getUuid()).
              content(que.getContent());

      displayUserQuestionIdAndContent.add(questionDetailsResponse);
    }
    return new ResponseEntity<>(displayUserQuestionIdAndContent,
        HttpStatus.OK);

  }
}