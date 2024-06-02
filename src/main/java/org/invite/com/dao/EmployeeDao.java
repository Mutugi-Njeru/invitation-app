package org.invite.com.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.invite.com.model.Employee;
import org.invite.com.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@ApplicationScoped
public class EmployeeDao {
    @Inject
    AgroalDataSource ads;
    private static final Logger logger= LoggerFactory.getLogger(EmployeeDao.class);

    //check if employee exists
    // create employee
    public boolean isEmployeeExist(int employeeNumber, int idNumber){
        String query="SELECT COUNT(employee_id) FROM employees WHERE employee_number=? AND id_number=?";
        int count=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setInt(1, employeeNumber);
            preparedStatement.setInt(2, idNumber);
            ResultSet resultSet= preparedStatement.executeQuery();

            while (resultSet.next()){
                count=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return count==1;
    }

    public int createEmployee(Employee employee){
        String query="INSERT INTO employees(firm_id, employee_number, title, first_name, last_name, msisdn, id_number, email, is_invite, is_active) VALUES (?,?,?,?,?,?,?,?,?,?)";
        int employeeId=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, employee.firmId());
            preparedStatement.setInt(2, employee.employeeNumber());
            preparedStatement.setString(3, employee.title());
            preparedStatement.setString(4, employee.firstName());
            preparedStatement.setString(5, employee.lastName());
            preparedStatement.setString(6, employee.msisdn());
            preparedStatement.setInt(7, employee.idNumber());
            preparedStatement.setString(8, employee.email());
            preparedStatement.setBoolean(9, true);
            preparedStatement.setBoolean(9, false);
            preparedStatement.executeUpdate();
            ResultSet resultSet= preparedStatement.getGeneratedKeys();

            while (resultSet.next()){
                employeeId=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return employeeId;
    }

    public boolean grantInviteAccess(int employeeId){
        String query="UPDATE employees SET is_invite=? WHERE employee_id=?";
        boolean isAllowed=false;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setBoolean(1, true);
            preparedStatement.setInt(2, employeeId);
            isAllowed=preparedStatement.executeUpdate()>0;

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return isAllowed;
    }

    public boolean isEmployeeActive(int employeeId){
        String query="SELECT COUNT(employee_id) FROM employees WHERE employee_id=? AND is_active=?";
        int count=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setInt(1, employeeId);
            preparedStatement.setBoolean(2, true);
            ResultSet resultSet= preparedStatement.executeQuery();
            while (resultSet.next()){
                count=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return count==1;
    }
    public boolean deactivateEmployee(int employeeId){
        String query="UPDATE employees SET is_active=? WHERE employee_id=?";
        boolean status=false;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setBoolean(1, false);
            preparedStatement.setInt(2, employeeId);
            status=preparedStatement.executeUpdate()>0;
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return status;
    }
    public JsonObject getFirmEmployees(int firmId){
        String query="SELECT employee_id, employee_number, title, first_name, last_name, msisdn FROM employees WHERE firm_id=?";
        var employees= Json.createArrayBuilder();
        var employeesJson=Json.createObjectBuilder();
        try (Connection connection = ads.getConnection();  PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setInt(1, firmId);
            ResultSet resultSet=preparedStatement.executeQuery();
            while (resultSet.next()){
                var employee=Json.createObjectBuilder()
                        .add("employeeId", resultSet.getInt(1))
                        .add("employeeNumber", resultSet.getInt(2))
                        .add("title", resultSet.getString(3))
                        .add("firstName", resultSet.getString(4))
                        .add("lastName", resultSet.getString(5))
                        .add("msisdn", resultSet.getString(6));
                employees.add(employee);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return employeesJson.add("firms", employees).build();
    }
    public JsonObject getFirmEmployeesWithInviteRight(int firmId){
        String query="SELECT employee_id, employee_number, title, first_name, last_name, msisdn FROM employees WHERE firm_id=? AND is_invite=?";
        var employees= Json.createArrayBuilder();
        var employeesJson=Json.createObjectBuilder();
        try (Connection connection = ads.getConnection();  PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setInt(1, firmId);
            preparedStatement.setBoolean(2, true);
            ResultSet resultSet=preparedStatement.executeQuery();
            while (resultSet.next()){
                var employee=Json.createObjectBuilder()
                        .add("employeeId", resultSet.getInt(1))
                        .add("employeeNumber", resultSet.getInt(2))
                        .add("title", resultSet.getString(3))
                        .add("firstName", resultSet.getString(4))
                        .add("lastName", resultSet.getString(5))
                        .add("msisdn", resultSet.getString(6));
                employees.add(employee);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return employeesJson.add("firms", employees).build();
    }
}
