package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.AuthorizationService;
import com.upgrad.quora.service.business.UserAdminBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonController {

  @Autowired
  private UserAdminBusinessService userAdminBusinessService;

  @Autowired
  private AuthorizationService authorizationService;

  /**
   * returns the requested userProfile data only if the requesting user id logged in
   *
   * @param userUuid UUID of the user whose details need to be return
   * @param accessToken access-token of the loggedin user
   * @return ResponseEntity<UserDetailsResponse> provides the details of the requested user
   * @throws AuthorizationFailedException
   * @throws UserNotFoundException
   */
  @RequestMapping(path = "/userprofile/{userId}", method = RequestMethod.GET, produces =
      MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<UserDetailsResponse> userProfile(
      @PathVariable("userId") final String userUuid,
      @RequestHeader("authorization") final String accessToken)
      throws AuthorizationFailedException, UserNotFoundException {

    authorizationService.validateJWTToken(accessToken);

    final UserEntity requestedUserEntity = userAdminBusinessService
        .getUser(userUuid);

    UserDetailsResponse userDetailsResponse = new UserDetailsResponse();

    userDetailsResponse.setFirstName(requestedUserEntity.getFirstName());
    userDetailsResponse.setLastName((requestedUserEntity.getLastName()));
    userDetailsResponse.setUserName(requestedUserEntity.getUserName());
    userDetailsResponse.setEmailAddress(requestedUserEntity.getEmail());
    userDetailsResponse.setDob(requestedUserEntity.getDob());
    userDetailsResponse.setCountry(requestedUserEntity.getCountry());
    userDetailsResponse.setContactNumber(requestedUserEntity.getContactNumber());
    userDetailsResponse.setAboutMe(requestedUserEntity.getAboutMe());

    return new ResponseEntity<>(userDetailsResponse, HttpStatus.OK);
  }
}
