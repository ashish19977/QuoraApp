package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZonedDateTime;

@Service
public class SignoutBusinessService {
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity signout(String authorization) throws SignOutRestrictedException {
        UserAuthTokenEntity userAuthToken = authenticationService.getUserAuthToken(authorization);
        UserEntity user = userAuthToken.getUser();
        userAuthToken.setLogoutAt(ZonedDateTime.now());
        userDao.updateAuthToken(userAuthToken);
        return userAuthToken;
    }
}
