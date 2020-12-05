package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDAO;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerService {

  @Autowired
  private AnswerDAO answerDAO;

  /**
   * Adds a new answer to a question
   *
   * @param answerEntity
   * @return added answer entity
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public AnswerEntity createAnswer(AnswerEntity answerEntity) {
    return answerDAO.createAnswer(answerEntity);
  }

  /**
   * Edit an existing answer but only by answer owner.
   *
   * @param answerEntity
   * @return edited answer entity
   * @throws AnswerNotFoundException
   * @throws AuthorizationFailedException
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public AnswerEntity editAnswerContent(AnswerEntity answerEntity)
      throws AnswerNotFoundException, AuthorizationFailedException {
    AnswerEntity existingAnswerEntity = getAnswer(answerEntity.getUuid());
    //TODO Check if answer owner is same as logged in user
    /*if(!existingAnswerEntity.getUserEntity().getUuid().equals(answerEntity.getUserEntity().getUuid())){
      throw new AuthorizationFailedException("ANS-003", "Only the answer owner can edit the answer");
    }*/
    return answerDAO.editAnswerContent(answerEntity);
  }

  /**
   * Get an answer by answer UUID
   *
   * @param answerUuid answer UUID
   * @return answer entity
   * @throws AnswerNotFoundException
   */
  @Transactional
  public AnswerEntity getAnswer(String answerUuid) throws AnswerNotFoundException {
    AnswerEntity answerEntity = answerDAO.getAnswer(answerUuid);
    if (answerEntity == null) {
      throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
    }
    return answerEntity;
  }

  /**
   * Delete an answer by UUID
   *
   * @param answerUuid answer UUID
   * @return deleted answer entity
   * @throws AnswerNotFoundException
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public AnswerEntity deleteAnswer(String answerUuid) throws AnswerNotFoundException {
    AnswerEntity answerEntity = getAnswer(answerUuid);
    //TODO Check if answer owner is same as logged in user
    /*if(!existingAnswerEntity.getUserEntity().getUuid().equals(answerEntity.getUserEntity().getUuid())){
      throw new AuthorizationFailedException("ANS-003", "Only the answer owner can edit the answer");
    }*/
    //TODO Check if logged in user has admin role
    answerDAO.deleteAnswer(answerEntity);
    return answerEntity;
  }

  /**
   * Get all answers for a question
   *
   * @param questionUuid question UUID
   * @return list of all answer entities
   */
  @Transactional
  public List<AnswerEntity> getAllAnswersToQuestion(String questionUuid) {
    return answerDAO.getAllAnswers(questionUuid);
  }

}
