package org.invite.com.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.invite.com.model.SecurityFirm;
import org.invite.com.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@ApplicationScoped
public class SecurityFirmDao {
    @Inject
    AgroalDataSource ads;
    private static final Logger logger= LoggerFactory.getLogger(SecurityFirmDao.class);

    public int createSecurityFirm(SecurityFirm securityFirm){
        String query="INSERT INTO security_firm(structure_id, firm_name, location, email, primary_msisdn, secondary_msisdn) VALUES (?,?,?,?,?,?)";
        int securityFirmId=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, securityFirm.structureId());
            preparedStatement.setString(2, securityFirm.firmName());
            preparedStatement.setString(3, securityFirm.location());
            preparedStatement.setString(4, securityFirm.email());
            preparedStatement.setString(5, securityFirm.primaryMsisdn());
            preparedStatement.setString(6, securityFirm.secondaryMsisdn());
            preparedStatement.executeUpdate();

            ResultSet resultSet=preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                securityFirmId=resultSet.getInt(1);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return securityFirmId;
    }
}
