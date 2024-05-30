package org.invite.com.ruleEngine.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.invite.com.dao.UserDao;
import org.invite.com.model.User;
import org.invite.com.record.ServiceResponder;

@ApplicationScoped
public class UserService {
    @Inject
    UserDao userDao;

    public ServiceResponder createUser(User user){
        boolean isExist= userDao.isUserExist(user.username(), user.password());
        if (!isExist){
            int userId= userDao.createUser(user);

            if (userId>0){
                int userDetailsId=userDao.saveUserDetails(userId, user);
                return (userDetailsId>0)
                        ? new ServiceResponder(true, "user created successfully")
                        : new ServiceResponder(false, "failed to save user details");
            }
            else return new ServiceResponder(false, "cannot create user");
        }
        else return new ServiceResponder(false, "user already exists");
    }
}
