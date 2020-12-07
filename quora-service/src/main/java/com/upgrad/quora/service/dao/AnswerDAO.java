package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class AnswerDAO {

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Add an answer to a question
   *
   * @param answerEntity
   * @return added answer entity
   */
  public AnswerEntity createAnswer(AnswerEntity answerEntity) {
    entityManager.persist(answerEntity);
    return answerEntity;
  }

  /**
   * Edit an answer
   *
   * @param answerEntity
   * @return edited answer entity
   */
  public AnswerEntity editAnswerContent(AnswerEntity answerEntity) {
    entityManager.merge(answerEntity);
    return answerEntity;
  }

  /**
   * Get an answer
   *
   * @param answerUuid
   * @return answer entity
   */
  public AnswerEntity getAnswer(String answerUuid) {
    try {
      return entityManager.createNamedQuery("getAnswerByUuid", AnswerEntity.class)
          .setParameter("uuid", answerUuid).getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  /**
   * Delete an answer
   *
   * @param answerEntity
   * @return deleted answer entity
   */
  public AnswerEntity deleteAnswer(AnswerEntity answerEntity) {
    entityManager.remove(answerEntity);
    return answerEntity;
  }

  /**
   * List of answers for a question
   *
   * @param questionUuid
   * @return list of answer entities
   */
  public List<AnswerEntity> getAllAnswers(String questionUuid) {
    return entityManager.createNamedQuery("getAllAnswersToQuestion", AnswerEntity.class)
        .setParameter("uuid", questionUuid).getResultList();
  }

}
