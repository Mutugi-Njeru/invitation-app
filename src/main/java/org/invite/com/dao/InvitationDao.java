package org.invite.com.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
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
    public JsonObject getAllFirmVisitors(int firmId){
        String query="SELECT visitor_id, first_name, last_name, msisdn FROM visitors WHERE firm_id=?";
        var visitors= Json.createArrayBuilder();
        var visitorsJson=Json.createObjectBuilder();
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setInt(1, firmId);
            ResultSet resultSet= preparedStatement.executeQuery();
            while (resultSet.next()){
                var visitor=Json.createObjectBuilder()
                        .add("visitorId", resultSet.getInt(1))
                        .add("firstName", resultSet.getString(2))
                        .add("lastName", resultSet.getString(3))
                        .add("msisdn", resultSet.getString(4));
                visitors.add(visitor);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return visitorsJson.add("visitors", visitors).build();
    }
    //show all firm invitations
    //show approved invitation
    //show rejected invitations
    public JsonObject getInvitationDetailsByCode(String invitationCode){
        String query= """
                SELECT i.invitation_id, i.visitor_id, v.first_name, v.last_name, i.reason_for_visit, i.status, i.expected_time_in, i.expected_time_out, v.msisdn
                FROM invitation i
                INNER JOIN visitors v ON v.visitor_id=i.visitor_id
                WHERE i.invitation_code=? AND expected_time_out > current_time()
                """;
        var invitation=Json.createObjectBuilder();
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setString(1, invitationCode);
            ResultSet resultSet= preparedStatement.executeQuery();
            while (resultSet.next()){
                invitation.add("invitationId", resultSet.getInt(1))
                        .add("visitorId", resultSet.getInt(2))
                        .add("firstName", resultSet.getString(3))
                        .add("lastName", resultSet.getString(4))
                        .add("reasonForVisit", resultSet.getString(5))
                        .add("status", resultSet.getString(6))
                        .add("expectedTimeIn", String.valueOf(resultSet.getTimestamp(7)))
                        .add("expectedTimeOut", String.valueOf(resultSet.getTimestamp(8)))
                        .add("msisdn", resultSet.getString(9));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return invitation.build();
    }
    public JsonObject getApprovedInvitations(int firmId){
        String query= """
                SELECT i.invitation_id, i.visitor_id, v.first_name, v.last_name, i.reason_for_visit, i.status,\s
                i.expected_time_in, i.expected_time_out, v.msisdn, concat(e.first_name, ' ',  e.last_name) as invited_by, r.first_name as approved_by, f.business_name
                FROM invitation i
                INNER JOIN visitors v ON v.visitor_id=i.visitor_id
                INNER JOIN employees e ON i.employee_id=e.employee_id
                INNER JOIN firms f ON f.firm_id=e.firm_id
                INNER JOIN reception r ON r.firm_id=e.firm_id
                WHERE i.status='approved' AND f.firm_id=?
                """;
        var invitations=Json.createArrayBuilder();
        var invitationJson=Json.createObjectBuilder();
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setInt(1, firmId);
            ResultSet resultSet=preparedStatement.executeQuery();
            while (resultSet.next()){
                var invitation=Json.createObjectBuilder()
                        .add("invitationId", resultSet.getInt(1))
                        .add("visitorId", resultSet.getInt(2))
                        .add("firstName", resultSet.getString(3))
                        .add("lastName", resultSet.getString(4))
                        .add("reasonForVisit", resultSet.getString(5))
                        .add("status", resultSet.getString(6))
                        .add("expectedTimeIn", String.valueOf(resultSet.getTimestamp(7)))
                        .add("expectedTimeOut", String.valueOf(resultSet.getTimestamp(8)))
                        .add("msisdn", resultSet.getString(9))
                        .add("invitedBy", resultSet.getString(10))
                        .add("approvedBy", resultSet.getString(11))
                        .add("businessName", resultSet.getString(12));
                invitations.add(invitation);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return invitationJson.add("invitations", invitations).build();
    }

}
