package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import java.time.ZonedDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

  @Autowired
  private UserDao userDao;

  public UserEntity validateJWTToken(String authorizationToken) throws AuthorizationFailedException {

    String tokenToCheck = authorizationToken;

    if(authorizationToken.contains("Bearer ")){
      tokenToCheck = authorizationToken.split("Bearer ")[1];
    }

    UserAuthEntity userAuthEntity = userDao.getUserAuthToken(tokenToCheck);



    if (userAuthEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    final ZonedDateTime now = ZonedDateTime.now();

    if (userAuthEntity.getLogoutAt() != null) {
      if (now.compareTo(userAuthEntity.getLogoutAt()) >= 0
          || now.compareTo(userAuthEntity.getExpiresAt()) > 0) {
        throw new AuthorizationFailedException("ATHR-002",
            "User is signed out.Sign in first to get user details");
      }
    } else {
      if (now.compareTo(userAuthEntity.getExpiresAt()) > 0) {
        throw new AuthorizationFailedException("ATHR-002",
            "User is signed out.Sign in first to get user details");
      }
    }

    return userAuthEntity.getUserEntity();
  }

}
