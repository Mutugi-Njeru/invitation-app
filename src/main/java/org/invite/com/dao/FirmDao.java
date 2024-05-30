package org.invite.com.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.invite.com.model.Firm;
import org.invite.com.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@ApplicationScoped
public class FirmDao {
    @Inject
    AgroalDataSource ads;
    private static final Logger logger= LoggerFactory.getLogger(FirmDao.class);
    public boolean isFirmExist(String businessName, String registration){
        String query="SELECT COUNT(firm_id) FROM firms WHERE business_name=? AND registration_pin=?";
        int count=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setString(1, businessName);
            preparedStatement.setString(2, registration);
            ResultSet resultSet=preparedStatement.executeQuery();

            while (resultSet.next()){
                count=resultSet.getInt(1);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return count==1;
    }

    public int createFirm(Firm firm){
        String query="INSERT INTO firms(category_id, security_firm_id, business_name, business_nature, registration_pin) VALUES (?,?,?,?,?)";
        int firmId=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, firm.categoryId());
            preparedStatement.setInt(2, firm.securityFirmId());
            preparedStatement.setString(3, firm.businessName());
            preparedStatement.setString(4, firm.businessNature());
            preparedStatement.setString(5, firm.registrationPin());
            preparedStatement.executeUpdate();

            ResultSet resultSet=preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                firmId=resultSet.getInt(1);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return firmId;
    }

    public int saveFirmContacts(int firmId, Firm firm){
        String query="INSERT INTO firms_contact (firm_id, email, primary_msisdn, secondary_msisdn, postal_address) VALUES (?,?,?,?,?)";
        int contactId=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, firmId);
            preparedStatement.setString(2, firm.email());
            preparedStatement.setString(3, firm.primaryMsisdn());
            preparedStatement.setString(4, firm.secondaryMsisdn());
            preparedStatement.setString(5, firm.postalAddress());
            preparedStatement.executeUpdate();

            ResultSet resultSet= preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                contactId=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return contactId;
    }
    //get firm id by employeeId
    public int getFirmIdByEmployeeId(int employeeId){
        String query="SELECT firm_id FROM employees WHERE employee_id=?";
        int firmId=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setInt(1, employeeId);
            ResultSet resultSet=preparedStatement.executeQuery();

            while (resultSet.next()){
                firmId=resultSet.getInt(1);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return firmId;
    }
}
