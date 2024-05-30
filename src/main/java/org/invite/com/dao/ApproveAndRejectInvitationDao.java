package org.invite.com.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.invite.com.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

@ApplicationScoped
public class ApproveAndRejectInvitationDao {
    @Inject
    AgroalDataSource ads;
    private static final Logger logger= LoggerFactory.getLogger(ApproveAndRejectInvitationDao.class);
    //approve by reception
    //update invitation status

    public boolean approveInvitation(int invitationId, int receptionId){
        String query="INSERT INTO approve_reject_invitations (invitation_id, reception_id, status) VALUES (?,?,?)";
        boolean status=false;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setInt(1, invitationId);
            preparedStatement.setInt(2, receptionId);
            preparedStatement.setString(3, "approved");
            status=preparedStatement.executeUpdate()>0;

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return status;
    }
    public boolean rejectInvitation(int invitationId, int receptionId){
        String query="INSERT INTO approve_reject_invitations (invitation_id, reception_id, status) VALUES (?,?,?)";
        boolean status=false;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setInt(1, invitationId);
            preparedStatement.setInt(2, receptionId);
            preparedStatement.setString(3, "rejected");
            status=preparedStatement.executeUpdate()>0;

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return status;
    }
    //update invitation status
    public boolean updateInvitationStatusToApproved(int invitationId){
        String query="UPDATE invitation SET status=? WHERE invitation_id=?";
        boolean status=false;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setString(1, "approved");
            preparedStatement.setInt(2, invitationId);
            status=preparedStatement.executeUpdate()>0;
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return status;
    }
    public boolean updateInvitationStatusToRejected(int invitationId){
        String query="UPDATE invitation SET status=? WHERE invitation_id=?";
        boolean status=false;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setString(1, "rejected");
            preparedStatement.setInt(2, invitationId);
            status=preparedStatement.executeUpdate()>0;
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return status;
    }
    //update time in at reception
    public boolean updateVisitorTimeInTheFirm(String timeIn, int approvedId){
        boolean status=false;
        String query="UPDATE approve_reject_invitations SET time_in=? WHERE apinv_id=? AND gate_check_in=?";
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(timeIn));
            preparedStatement.setInt(2, approvedId);
            preparedStatement.setBoolean(3, true);
            status=preparedStatement.executeUpdate()>0;

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return status;
    }

    public boolean updateVisitorTimeOutTheFirm(String timeOut, int approvedId){
        boolean status=false;
        String query="UPDATE approve_reject_invitations SET time_out=? WHERE apinv_id=? AND gate_check_in=?";
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(timeOut));
            preparedStatement.setInt(2, approvedId);
            preparedStatement.setBoolean(3, true);
            status=preparedStatement.executeUpdate()>0;

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return status;
    }
}
