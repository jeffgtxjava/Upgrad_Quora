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

    @RequestMapping(path = "/userProfile/{userId}",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUserDetailByUuid(@PathVariable("userId") final String userUuid,
                                                                   @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {

        String[] basicToken = authorization.split("Basic ");
        final UserEntity userEntity= userAdminBusinessService.getUser(userUuid,basicToken[1]);

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();

        userDetailsResponse.setFirstName(userEntity.getFirstName());
        userDetailsResponse.setLastName((userEntity.getLastName()));
        userDetailsResponse.setUserName(userEntity.getUserName());
        userDetailsResponse.setEmailAddress(userEntity.getEmail());
        userDetailsResponse.setDob(userEntity.getDob());
        userDetailsResponse.setCountry(userEntity.getCountry());
        userDetailsResponse.setContactNumber(userEntity.getContactNumber());
        userDetailsResponse.setAboutMe(userEntity.getAboutMe());

        return new ResponseEntity<>(userDetailsResponse, HttpStatus.OK);
    }
}
