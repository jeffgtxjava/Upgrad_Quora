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
   * @param answerToEdit
   * @return edited answer entity
   * @throws AnswerNotFoundException
   * @throws AuthorizationFailedException
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public AnswerEntity editAnswerContent(AnswerEntity answerToEdit)
      throws AnswerNotFoundException, AuthorizationFailedException {
    AnswerEntity existingAnswerEntity = getAnswer(answerToEdit.getUuid());
    if (!existingAnswerEntity.getUserEntity().getUuid()
        .equals(answerToEdit.getUserEntity().getUuid())) {
      throw new AuthorizationFailedException("ATHR-003",
          "Only the answer owner can edit the answer");
    }
    existingAnswerEntity.setAnswer(answerToEdit.getAnswer());
    return answerDAO.editAnswerContent(existingAnswerEntity);
  }

  /**
   * Get an answer by answer UUID
   *
   * @param answerUuid answer UUID
   * @return answer entity
   * @throws AnswerNotFoundException
   */
  @Transactional(propagation = Propagation.REQUIRED)
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
   * @param answerToDelete answer UUID along with logged in user details
   * @return deleted answer entity
   * @throws AnswerNotFoundException
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public AnswerEntity deleteAnswer(AnswerEntity answerToDelete)
      throws AnswerNotFoundException, AuthorizationFailedException {
    AnswerEntity existingAnswerEntity = getAnswer(answerToDelete.getUuid());
    if (!existingAnswerEntity.getUserEntity().getUuid()
        .equals(answerToDelete.getUserEntity().getUuid()) || answerToDelete.getUserEntity()
        .getRole().equals("admin")) {
      throw new AuthorizationFailedException("ATHR-003",
          "Only the answer owner or admin can delete the answer");
    }
    answerDAO.deleteAnswer(existingAnswerEntity);
    return existingAnswerEntity;
  }

  /**
   * Get all answers for a question
   *
   * @param questionUuid question UUID
   * @return list of all answer entities
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public List<AnswerEntity> getAllAnswersToQuestion(String questionUuid) {
    return answerDAO.getAllAnswers(questionUuid);
  }

}
