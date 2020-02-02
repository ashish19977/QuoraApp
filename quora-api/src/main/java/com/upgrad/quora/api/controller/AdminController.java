package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/")
public class AdminController {

    @Autowired
    private AdminBusinessService adminBusinessService;

    /* The endpoint "/admin/user/{userId}" is used to delete an user from the Quora Application.
    Only an admin is authorized to access this endpoint. It throws AuthorizationFailedException
    and UserNotFoundException. */
    @RequestMapping(method = RequestMethod.DELETE, path = "/admin/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> userDelete (@PathVariable("userId") final String userId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {

        final UserEntity deletedUserEntity = adminBusinessService.userDelete(userId, authorization);
        UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id(deletedUserEntity.getUuid()).status("USER SUCCESSFULLY DELETED!");
        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse, HttpStatus.OK);

    }

}
