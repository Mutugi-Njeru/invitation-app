package org.invite.com.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.invite.com.cipher.Sha256Hasher;
import org.invite.com.model.User;
import org.invite.com.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@ApplicationScoped
public class UserDao {
    @Inject
    AgroalDataSource ads;
    @Inject
    Sha256Hasher sha256Hasher;
    private static final Logger logger= LoggerFactory.getLogger(UserDao.class);

    //check if user exists
    //create user
    //save user details
    public boolean isUserExist(String username, String password){
        String query="SELECT COUNT(user_id) FROM users WHERE username=? AND password=?";
        String hashPassword= sha256Hasher.createHashtText(password);
        int count=0;

        try(Connection connection= ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashPassword);
            ResultSet resultSet=preparedStatement.executeQuery();

            while(resultSet.next()){
                count=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return count==1;
    }

    public int createUser(User user){
        String query="INSERT INTO users(structure_id, user_category_id, username, password) VALUES (?,?,?,?)";
        String hashPassword= sha256Hasher.createHashtText(user.password());
        int userId=0;

        try(Connection connection= ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, user.structureId());
            preparedStatement.setInt(2, user.userCategoryId());
            preparedStatement.setString(3, user.username());
            preparedStatement.setString(4, hashPassword);
            preparedStatement.executeUpdate();
            ResultSet resultSet=preparedStatement.getGeneratedKeys();

            while (resultSet.next()){
                userId=resultSet.getInt(1);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return userId;
    }

    public int saveUserDetails(int userId, User user){
        String query="INSERT INTO users_details (user_id, first_name, last_name, msisdn, email) VALUES (?,?,?,?,?)";
        int userDetailsId=0;

        try(Connection connection= ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, user.firstname());
            preparedStatement.setString(3, user.lastname());
            preparedStatement.setString(4, user.msisdn());
            preparedStatement.setString(5, user.email());
            preparedStatement.executeUpdate();
            ResultSet resultSet= preparedStatement.getGeneratedKeys();

            while (resultSet.next()){
                userDetailsId=resultSet.getInt(1);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return userDetailsId;
    }
}
