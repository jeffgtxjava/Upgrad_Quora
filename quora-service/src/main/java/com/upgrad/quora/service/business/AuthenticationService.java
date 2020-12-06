package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import java.time.ZonedDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

  @Autowired
  private UserDao userDao;


  @Autowired
  private PasswordCryptographyProvider passwordCryptographyProvider;


  @Transactional(propagation = Propagation.REQUIRED)
  public UserAuthEntity authenticate(final String username, final String password)
      throws AuthenticationFailedException {
    UserEntity userEntity = userDao.getUserByUsername(username);

    //checking whether user dowes exist in the DB else throw not exist error
    if (userEntity == null) {
      throw new AuthenticationFailedException("ATH-001", "This username does not exist");
    }

    // compare the input password from the password in the DB if it doesn't exist throw passwords doesn't match
    final String encryptedPassword = passwordCryptographyProvider
        .encrypt(password, userEntity.getSalt());

    if (!(encryptedPassword.equals(userEntity.getPassword()))) {
      throw new AuthenticationFailedException("ATH-002", "Password failed");
    }

    // if the user exists and also provided password is correct -> store the user login details

    //generating the jwt token
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);

    //retrieve exisitng accessToken
    UserAuthEntity uaeByUuid = userDao.getUserAuthTokenByUuid(userEntity.getUuid());

    UserAuthEntity userAuthEntity = new UserAuthEntity();

    userAuthEntity.setUserEntity(userEntity);
    userAuthEntity.setUuid(userEntity.getUuid());

    final ZonedDateTime now = ZonedDateTime.now();
    final ZonedDateTime expires = now.plusHours(8);

    userAuthEntity
        .setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expires));

    userAuthEntity.setLoginAt(now);
    userAuthEntity.setExpiresAt(expires);

    if (uaeByUuid == null) {
      userDao.createAuthToken(userAuthEntity);
    } else {
      userAuthEntity.setId(uaeByUuid.getId());
      userDao.updateAuthToken(userAuthEntity);
    }

    return userAuthEntity;
  }

}
