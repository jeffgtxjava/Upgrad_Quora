package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.SignoutBusinessService;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import java.util.Base64;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
public class UserController {

  @Autowired
  private SignupBusinessService signupBusinessService;

  @Autowired
  private AuthenticationService authenticationService;

  @Autowired
  private SignoutBusinessService signoutBusinessService;

  // endpoint for signup

  @RequestMapping(path = "/signup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest)
      throws SignUpRestrictedException {

    // store the user request details

    final UserEntity userEntity = new UserEntity();
    userEntity.setUuid(UUID.randomUUID().toString());
    userEntity.setFirstName(signupUserRequest.getFirstName());
    userEntity.setLastName(signupUserRequest.getLastName());
    userEntity.setUserName(signupUserRequest.getUserName());
    userEntity.setEmail(signupUserRequest.getEmailAddress());
    userEntity.setPassword(signupUserRequest.getPassword());
    userEntity.setCountry(signupUserRequest.getCountry());
    userEntity.setAboutMe(signupUserRequest.getAboutMe());
    userEntity.setDob(signupUserRequest.getDob());
    userEntity.setRole("nonadmin");
    userEntity.setContactNumber(signupUserRequest.getContactNumber());

    //Create the user in the database
    final UserEntity createdUserEntity = signupBusinessService.signup(userEntity);

    SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid())
        .status("USER SUCCESSFULLY REGISTERED");

    //return the success code after the user account get's created successfully
    return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
  }

  @RequestMapping(path = "/signin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") String authorization)
      throws AuthenticationFailedException {

    //splitting the authorization with 'Basic ' in order to retrieve the username:password
    byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);

    //converting the decoded byte to string
    String decodedText = new String(decode);

    //spliting up the username and password with :
    String[] decodedArray = decodedText.split(":");

    //need to check with the existance of the provided user in the database
    UserAuthEntity userAuthEntity = authenticationService
        .authenticate(decodedArray[0], decodedArray[1]);

    SigninResponse signinResponse = new SigninResponse();

    signinResponse.setId(userAuthEntity.getUserEntity().getUuid());
    signinResponse.setMessage("SIGNED IN SUCCESSFULLY");

    HttpHeaders headers = new HttpHeaders();
    headers.add("authorization", userAuthEntity.getAccessToken());

    return new ResponseEntity<>(signinResponse, headers, HttpStatus.OK);
  }

  //signout method

  @RequestMapping(path = "/signout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SignoutResponse> signout(
      @RequestHeader("authorization") final String accessToken) throws SignOutRestrictedException {

    UserAuthEntity userAuthEntity = signoutBusinessService.signout(accessToken);

    SignoutResponse signoutResponse = new SignoutResponse();
    signoutResponse.setId(userAuthEntity.getUuid());
    signoutResponse.setMessage("SIGNED OUT SUCCESSFULLY");

    return new ResponseEntity<>(signoutResponse, HttpStatus.OK);
  }

}
