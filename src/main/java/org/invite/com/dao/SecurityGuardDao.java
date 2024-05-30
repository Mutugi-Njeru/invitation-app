package org.invite.com.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.invite.com.model.SecurityGuard;
import org.invite.com.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@ApplicationScoped
public class SecurityGuardDao {
    @Inject
    AgroalDataSource ads;
    private static final Logger logger= LoggerFactory.getLogger(SecurityGuardDao.class);

    public int createSecurityGuard(SecurityGuard securityGuard){
        String query="INSERT INTO security_guard(security_firm_id, name, guard_number, msisdn, email) VALUES (?,?,?,?,?)";
        int guardId=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, securityGuard.securityFirmId());
            preparedStatement.setString(2, securityGuard.name());
            preparedStatement.setInt(3, securityGuard.guardNumber());
            preparedStatement.setString(4, securityGuard.msisdn());
            preparedStatement.setString(5, securityGuard.email());
            preparedStatement.executeUpdate();

            ResultSet resultSet= preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                guardId=resultSet.getInt(1);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return guardId;
    }
}
