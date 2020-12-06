package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.UserAdminBusinessService;
import com.upgrad.quora.service.business.UserDeleteBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdminController {

    @Autowired
    private UserAdminBusinessService userAdminBusinessService;

    @Autowired
    private UserDeleteBusinessService userDeleteBusinessService;

    @RequestMapping(path = "/admin/user/{userId}", method = RequestMethod.DELETE, produces =
            MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> userDelete(@PathVariable("userId") String uuid, @RequestHeader(
            "authorization") String accessToken) throws AuthorizationFailedException, UserNotFoundException {

        final UserEntity adminLoginUserEntity = userDeleteBusinessService.getUserEntityByAdminCheck(accessToken);
        final UserEntity userToDelete = userDeleteBusinessService.getUserToDelete(uuid, accessToken);

        String deletedUuid = userDeleteBusinessService.deleteUserByUuid(adminLoginUserEntity, userToDelete);

        UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id(deletedUuid).status("USER " +
                "SUCCESSFULLY DELETED");

        return new ResponseEntity<>(userDeleteResponse, HttpStatus.OK);
    }
}
