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
public class UserDeleteBusinessService {

    @Autowired
    private UserDao userDao;


    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteUserByUuid(final UserEntity signedInUser, final UserEntity deleteUser) throws AuthorizationFailedException {

        if (!(signedInUser.getRole().equalsIgnoreCase("admin"))) {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }
        return userDao.deleteUser(deleteUser);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity getUserAuthEntityNoAdminCheck(String accessToken) throws AuthorizationFailedException {
        UserAuthEntity adminUAE = userDao.getUserAuthToken(accessToken);

        if (adminUAE == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        final ZonedDateTime now = ZonedDateTime.now();

        if (adminUAE.getLogoutAt() != null) {
            if (now.compareTo(adminUAE.getLogoutAt()) >= 0 || now.compareTo(adminUAE.getExpiresAt()) > 0) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            }
        }

        return adminUAE;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUserEntityByAdminCheck(String accessToken) throws UserNotFoundException,
            AuthorizationFailedException {

        UserAuthEntity adminUAE = this.getUserAuthEntityNoAdminCheck(accessToken);

        UserEntity adminUE = userDao.getUserByUuid(adminUAE.getUuid());

        if (!(adminUE.getRole().equalsIgnoreCase("admin"))) {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }

        return adminUE;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUserToDelete(String uuid, String adminAccessToken) throws AuthorizationFailedException, UserNotFoundException {

        // check for the admin is logged-in
        UserEntity adminUE = this.getUserEntityByAdminCheck(adminAccessToken);

        // get the UE of uuid whose account has to be deleted
        UserEntity toBeDeletedUserEntity = userDao.getUserByUuid(uuid);

        if (toBeDeletedUserEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }

        return toBeDeletedUserEntity;

    }

}
