package org.invite.com.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.invite.com.model.Reception;
import org.invite.com.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@ApplicationScoped
public class ReceptionDao {
    @Inject
    AgroalDataSource ads;
    private static final Logger logger= LoggerFactory.getLogger(ReceptionDao.class);

    public int addReception(Reception reception){
        String query="INSERT INTO reception (firm_id, first_name, last_name, msisdn, email, is_active) VALUES (?,?,?,?,?,?)";
        int receptionId=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, reception.firmId());
            preparedStatement.setString(2, reception.firstName());
            preparedStatement.setString(3, reception.lastName());
            preparedStatement.setString(4, reception.msisdn());
            preparedStatement.setString(5, reception.email());
            preparedStatement.setBoolean(6, true);
            preparedStatement.executeUpdate();
            ResultSet resultSet= preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                receptionId=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return receptionId;
    }
    //key in invitation id/ code and check if checked in
    // enter time in
    //enter time out when leaving

}
