package org.invite.com.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.invite.com.model.Invitation;
import org.invite.com.utility.Constants;
import org.invite.com.utility.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@ApplicationScoped
public class InvitationDao {
    @Inject
    AgroalDataSource ads;
    private static final Logger logger= LoggerFactory.getLogger(InvitationDao.class);

    public boolean isInvite(int employeeId){
        String query="SELECT COUNT(employee_id) FROM employees WHERE employee_id=? AND is_invite=?";
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

    public int createVisitor(int firmId, Invitation invitation){
        String query="INSERT INTO visitors(firm_id, first_name, last_name, msisdn, email) VALUES (?,?,?,?,?)";
        int visitorId=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, firmId);
            preparedStatement.setString(2, invitation.firstname());
            preparedStatement.setString(3, invitation.lastname());
            preparedStatement.setString(4, invitation.msisdn());
            preparedStatement.setString(5, invitation.email());
            preparedStatement.executeUpdate();
            ResultSet resultSet= preparedStatement.getGeneratedKeys();

            while (resultSet.next()){
                visitorId=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return visitorId;
    }

    public int createInvitation(int visitorId, Invitation invitation){
        String invitationCode= Util.generateOtp();
        int invitationId=0;
        String query="INSERT INTO invitation (visitor_id, employee_id, invitation_code, reason_for_visit, expected_time_in, expected_time_out, status) VALUES (?,?,?,?,?,?,?)";

        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, visitorId);
            preparedStatement.setInt(2, invitation.employeeId());
            preparedStatement.setString(3, invitationCode);
            preparedStatement.setString(4, invitation.reasonForVisit());
            preparedStatement.setTimestamp(5,Timestamp.valueOf(invitation.expectedTimeIn()));
            preparedStatement.setTimestamp(6,Timestamp.valueOf(invitation.expectedTimeOut()));
            preparedStatement.setString(7, "waiting");
            preparedStatement.executeUpdate();
            ResultSet resultSet= preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                invitationId=resultSet.getInt(1);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return invitationId;
    }

}
