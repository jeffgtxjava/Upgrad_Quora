package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private SignupBusinessService signupBusinessService;


    // endpoint for signup

    @RequestMapping(path = "/signup",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup (final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {

        // store the user request details

        final UserEntity userEntity= new UserEntity();
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setSalt("abc1234");
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setRole("nonadmin");
        userEntity.setContactNumber(signupUserRequest.getContactNumber());

        //Create the user in the database
        final UserEntity createdUserEntity = signupBusinessService.signup(userEntity);

        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("REGISTERED");

        //return the success code after the user account get's created successfully
        return new ResponseEntity<SignupUserResponse>(userResponse,HttpStatus.CREATED);
    }

}
