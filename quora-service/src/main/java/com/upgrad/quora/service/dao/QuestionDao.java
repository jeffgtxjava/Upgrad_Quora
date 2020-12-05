package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

//this method saves the question content posted by the user
    public QuestionEntity persistQuestion(final QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

//this method returns the list of all the question
    public List<QuestionEntity> getAllQuestions(){
        try{
            return entityManager.createNamedQuery("ListofAllQuestions", QuestionEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

//this method returns the qustion posted by a user
    public QuestionEntity getQuestionByUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("QuestionByUuid", QuestionEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

//this method deletes the question content in the database
    public void deleteQuestion(final QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
    }

//this method updates  the  question in the database
    public QuestionEntity updateQuestion(final QuestionEntity questionEntity) {
        QuestionEntity updatedQ = entityManager.merge(questionEntity);
        return updatedQ;
    }

//this methods returns the list of all the question posted by the user
    public List<QuestionEntity> getAllQuestionsByUser(final String userUuid){
        try{
            return entityManager.createNamedQuery("AllQuestionsByUser", QuestionEntity.class).setParameter("uuid",userUuid).getResultList();
        }
        catch (NoResultException nre){
            return null;
        }
    }
}