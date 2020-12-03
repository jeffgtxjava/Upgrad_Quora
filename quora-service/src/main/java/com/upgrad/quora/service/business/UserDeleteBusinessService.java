package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
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

        if(!(signedInUser.getRole().equalsIgnoreCase("admin"))) {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }
        return userDao.deleteUser(deleteUser);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUserEntityByAuthToken (String accessToken) throws UserNotFoundException {
        UserAuthEntity uae = userDao.getUserAuthToken(accessToken);
        if(uae == null) {
            throw new UserNotFoundException("USR-001","User with entered uuid to be deleted does not exist");
        }
        UserEntity ue =  userDao.getUserByUuid(uae.getUuid());

        return ue;
    }

}
