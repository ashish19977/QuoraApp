package com.upgrad.quora.service.business;
import com.upgrad.quora.service.dao.AdminDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminBusinessService {

    @Autowired
    private AdminDao adminDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity userDelete(final String userId, final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = adminDao.getUserAuthToken(authorization);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if(userAuthTokenEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }

        if(!(userAuthTokenEntity.getUser().getRole().equals("admin"))){
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }

        UserEntity userEntity = adminDao.getUser(userId);
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }

        return adminDao.userDelete(userEntity);
    }

}
