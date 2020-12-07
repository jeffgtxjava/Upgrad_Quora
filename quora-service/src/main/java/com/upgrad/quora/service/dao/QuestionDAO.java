package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.DatabaseException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class QuestionDAO {

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Save a new question in the DB
   *
   * @param questionEntity
   * @return
   */
  public QuestionEntity persistQuestion(final QuestionEntity questionEntity)
      throws DatabaseException {
    try {

    } catch (Exception e) {
      throw new DatabaseException("ER-001", "It's not you. It's us. Try again later.");
    }
    entityManager.persist(questionEntity);
    return questionEntity;
  }

  /**
   * @return
   */
  public List<QuestionEntity> getAllQuestions() throws DatabaseException {
    try {
      return entityManager.createNamedQuery("listofAllQuestions", QuestionEntity.class)
          .getResultList();
    } catch (NoResultException nre) {
      return null;
    } catch (Exception e) {
      throw new DatabaseException("ER-001", "It's not you. It's us. Try again later.");
    }
  }

  /**
   * @param uuid
   * @return
   */
  public QuestionEntity getQuestionByUuid(final String uuid) throws DatabaseException {
    try {
      return entityManager.createNamedQuery("questionByUuid", QuestionEntity.class)
          .setParameter("uuid", uuid).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    } catch (Exception e) {
      throw new DatabaseException("ER-001", "It's not you. It's us. Try again later.");
    }
  }

  /**
   * @param questionEntity
   */
  public void deleteQuestion(final QuestionEntity questionEntity) {
    entityManager.remove(questionEntity);
  }

  /**
   * @param questionEntity
   * @return
   */
  public QuestionEntity updateQuestion(final QuestionEntity questionEntity) {
    QuestionEntity updatedQ = entityManager.merge(questionEntity);
    return updatedQ;
  }

  /**
   * @param userEntity
   * @return
   */
  public List<QuestionEntity> getAllQuestionsByUser(final UserEntity userEntity)
      throws DatabaseException {
    try {
      return entityManager.createNamedQuery("allQuestionsByUser", QuestionEntity.class)
          .setParameter("uuid", userEntity.getUuid()).getResultList();
    } catch (NoResultException nre) {
      return null;
    } catch (Exception e) {
      throw new DatabaseException("ER-001", "It's not you. It's us. Try again later.");
    }
  }
}