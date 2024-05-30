package org.invite.com.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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
        String query="INSERT INTO employees(firm_id, employee_number, title, first_name, last_name, msisdn, id_number, email, is_invite) VALUES (?,?,?,?,?,?,?,?,?)";
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
}
