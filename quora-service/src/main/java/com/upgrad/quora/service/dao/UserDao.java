package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;


    // get the user from the username
    public UserEntity getUserByUsername(final String username) {
        try {
            return entityManager.createNamedQuery("userByUsername",UserEntity.class).setParameter("username",username).getSingleResult();
        }catch (NoResultException nre) {
            return null;
        }
    }


    // get the user from the email
    public UserEntity getUserByEmail(final String email) {
        try {
            return entityManager.createNamedQuery("userByEmail",UserEntity.class).setParameter("email",email).getSingleResult();
        }catch (NoResultException nre) {
            return null;
        }
    }

    // create the user in the database
    public UserEntity createUser(UserEntity userEntity){
        entityManager.persist(userEntity);
        return  userEntity;
    }

}
