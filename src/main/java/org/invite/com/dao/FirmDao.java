package org.invite.com.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import netscape.javascript.JSObject;
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
        String query="INSERT INTO firms(category_id, security_firm_id, business_name, business_nature, registration_pin, is_active) VALUES (?,?,?,?,?,?)";
        int firmId=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, firm.categoryId());
            preparedStatement.setInt(2, firm.securityFirmId());
            preparedStatement.setString(3, firm.businessName());
            preparedStatement.setString(4, firm.businessNature());
            preparedStatement.setString(5, firm.registrationPin());
            preparedStatement.setBoolean(6, true);
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
    public JsonObject getFirmsInAStructure(int structureId){
        String query= """
                SELECT f.firm_id, f.business_name, f.business_nature, fc.category, sf.firm_name as security_firm
                FROM firms f
                INNER JOIN facilities fc ON fc.category_id=f.category_id
                INNER JOIN structures s ON fc.structure_id=s.structure_id
                INNER JOIN security_firm sf ON sf.structure_id=s.structure_id
                WHERE s.structure_id=?
                """;
        var firms= Json.createArrayBuilder();
        var firmsJson=Json.createObjectBuilder();
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setInt(1, structureId);
            ResultSet resultSet=preparedStatement.executeQuery();
            while (resultSet.next()){
                var firm=Json.createObjectBuilder()
                        .add("firmId", resultSet.getInt(1))
                        .add("businessName", resultSet.getString(2))
                        .add("businessNature", resultSet.getString(3))
                        .add("category", resultSet.getString(4))
                        .add("securityFirm", resultSet.getString(5));
                firms.add(firm);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return firmsJson.add("firms", firms).build();
    }
    //deactivate firm
    public boolean deactivateFirm(int firmId){
        String query="UPDATE firms SET is_active=? WHERE firm_id=?";
        boolean status=false;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setBoolean(1, false);
            preparedStatement.setInt(2, firmId);
            status=preparedStatement.executeUpdate()>0;

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return status;
    }

}
