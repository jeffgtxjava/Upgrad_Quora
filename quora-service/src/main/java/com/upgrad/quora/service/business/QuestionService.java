package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.QuestionDAO;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.DatabaseException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.QuestionsNotFoundException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionService {

  @Autowired
  UserDao userDao;

  @Autowired
  QuestionDAO questionDao;

  @Autowired
  UserAdminBusinessService userBusinessService;


  /**
   * To post a new question
   *
   * @param questionEntity New question to be posted
   * @return
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public QuestionEntity postQuestion(final QuestionEntity questionEntity) throws DatabaseException {
    return questionDao.persistQuestion(questionEntity);
  }

  /**
   * Retrieve all the questions
   *
   * @return
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public List<QuestionEntity> getAllQuestions()
      throws DatabaseException, QuestionsNotFoundException {
    List<QuestionEntity> questionEntityList = questionDao.getAllQuestions();
    if (questionEntityList == null || questionEntityList.size() == 0) {
      throw new QuestionsNotFoundException("QUES-002", "No questions posted yet.");
    }
    return questionEntityList;
  }

  /**
   * @param questionToEdit
   * @return
   * @throws AuthorizationFailedException
   * @throws DatabaseException
   * @throws InvalidQuestionException
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public QuestionEntity editQuestion(final QuestionEntity questionToEdit)
      throws AuthorizationFailedException, DatabaseException, InvalidQuestionException {

    final QuestionEntity questionInDB = getQuestion(questionToEdit.getUuid());

    if (questionToEdit.getUserEntity().getUserName()
        .equals(questionInDB.getUserEntity().getUserName())) {
      questionInDB.setContent(questionToEdit.getContent());
      QuestionEntity updatedQuestion = questionDao.updateQuestion(questionInDB);
      return updatedQuestion;
    } else {
      throw new AuthorizationFailedException("ATHR-003",
          "Only the question owner can edit the question");
    }

  }

  /**
   * to delete the question from the database
   * @param questionToDelete
   * @return
   * @throws AuthorizationFailedException
   * @throws InvalidQuestionException
   * @throws DatabaseException
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public QuestionEntity deleteQuestion(final QuestionEntity questionToDelete)
      throws AuthorizationFailedException, InvalidQuestionException, DatabaseException {

    QuestionEntity questionEntity = getQuestion(questionToDelete.getUuid());

    String role = questionToDelete.getUserEntity().getRole();
    String questionOwnnerUuid = questionEntity.getUserEntity().getUuid();
    String signedInUserUuid = questionToDelete.getUserEntity().getUuid();

    if (role.equals("admin") || questionOwnnerUuid.equals(signedInUserUuid)) {
      questionDao.deleteQuestion(questionEntity);
    } else {
      throw new AuthorizationFailedException("ATHR-003",
          "Only the question owner or admin can delete the question");
    }

    return questionEntity;
  }

  /**
   * to retrieve the question from the DB
   * @param questionUuid
   * @return
   * @throws InvalidQuestionException
   * @throws DatabaseException
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public QuestionEntity getQuestion(final String questionUuid)
      throws InvalidQuestionException, DatabaseException {

    QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionUuid);
    if (questionEntity == null) {
      throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
    }
    return questionEntity;

  }

  /**
   * to retrieve all the questions posted by given user UUID
   * @param userEntity
   * @return
   * @throws AuthorizationFailedException
   * @throws UserNotFoundException
   * @throws DatabaseException
   * @throws QuestionsNotFoundException
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public List<QuestionEntity> getAllQuestionsByUser(UserEntity userEntity)
      throws AuthorizationFailedException, UserNotFoundException, DatabaseException,
          QuestionsNotFoundException {

    List<QuestionEntity> questionEntityList = questionDao.getAllQuestionsByUser(userEntity);

    if (questionEntityList == null || questionEntityList.size() == 0) {
      throw new QuestionsNotFoundException("QUES-002", "No questions posted yet.");
    }

    return questionEntityList;

  }
}