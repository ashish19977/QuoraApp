package com.upgrad.quora.api.controller;
import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.dao.UserDao;
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
public class CommonController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> userProfile(@PathVariable("userId") final String userId,
                                                           @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        UserEntity userEntity = commonBusinessService.userProfile(userId, authorization);

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName()).emailAddress(userEntity.getEmail())
                .contactNumber(userEntity.getContactNumber()).dob(userEntity.getDob()).aboutMe(userEntity.getAboutMe())
                .country(userEntity.getCountry()).userName(userEntity.getUsername());

        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
        
    }
}
