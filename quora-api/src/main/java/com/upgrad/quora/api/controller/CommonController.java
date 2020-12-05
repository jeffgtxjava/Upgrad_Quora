package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserAdminBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommonController {

    @Autowired
    private UserAdminBusinessService userAdminBusinessService;

    @RequestMapping(path = "userprofile/{userId}",method = RequestMethod.GET,produces =
            MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUserDetailByUuid(@PathVariable("userId") final String userUuid,
                                                                   @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {

        final UserEntity requestedUserEntity= userAdminBusinessService.getUser(userUuid,authorization);

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