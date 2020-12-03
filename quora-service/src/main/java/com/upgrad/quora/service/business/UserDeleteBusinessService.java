package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDeleteBusinessService {

    @Autowired
    private UserDao userDao;


    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteUserByUuid(final UserEntity signedInUser,final UserEntity deleteUser) throws AuthorizationFailedException {

        if(signedInUser.getRole().equalsIgnoreCase("admin")) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        return userDao.deleteUser(deleteUser);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUserByUuid(final String uuid) {
        return userDao.getUserByUuid(uuid);
    }

}
