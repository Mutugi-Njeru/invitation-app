package org.invite.com.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.invite.com.cipher.Sha256Hasher;
import org.invite.com.record.Authentication;
import org.invite.com.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@ApplicationScoped
public class AuthenticationDao {
    @Inject
    AgroalDataSource ads;
    @Inject
    Sha256Hasher sha256Hasher;

    private static final Logger logger= LoggerFactory.getLogger(AuthenticationDao.class);

    public Authentication authenticateUser(String username, String password){
        String query= "SELECT COUNT(u.user_id) from users u WHERE u.username = ? AND u.password = ?";
        int count=0;

        String hashPassword= sha256Hasher.createHashtText(password);
        try (Connection connection=ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)){
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashPassword);
            ResultSet resultSet= preparedStatement.executeQuery();

            while (resultSet.next()){
                count=resultSet.getInt(1);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return new Authentication(count==1, username);
    }
}
