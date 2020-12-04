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

import java.time.ZonedDateTime;

@Service
public class UserAdminBusinessService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUser(final String userUuid, final String authorizationToken) throws AuthorizationFailedException,
            UserNotFoundException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(authorizationToken);
        if(userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        final ZonedDateTime now = ZonedDateTime.now();

        if(userAuthEntity.getLogoutAt() != null) {
            if(now.compareTo(userAuthEntity.getLogoutAt()) >=0  ||now.compareTo(userAuthEntity.getExpiresAt())>0 ) {
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
            }
        }

        UserAuthEntity userAuthEntity1 = userDao.getUserAuthTokenByUuid(userUuid);

        if(userAuthEntity1 == null) {
            throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
        }
        return  userDao.getUserByUuid(userUuid);

    }

}
