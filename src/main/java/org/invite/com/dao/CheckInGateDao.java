package org.invite.com.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.invite.com.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@ApplicationScoped
public class CheckInGateDao {
    @Inject
    AgroalDataSource ads;
    private static final Logger logger= LoggerFactory.getLogger(CheckInGateDao.class);

    //guard must be signed in
    //check if the invitation is approved
    // approve entry
    //update approved_rejected gate check in to checkedIn

    public boolean isGuardSignedIn(int clockId){
        String query="SELECT COUNT(clock_id) FROM clock_in_and_out WHERE sign_in=? AND sign_out=? AND clock_id=?";
        int count=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setBoolean(1, true);
            preparedStatement.setBoolean(2, false);
            preparedStatement.setInt(3, clockId);
            ResultSet resultSet= preparedStatement.executeQuery();

            while (resultSet.next()){
                count=resultSet.getInt(1);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return count==1;
    }

    public int approveGateEntry(int clockId, int approvedID){
        String query="INSERT INTO check_in_and_out (clock_id, apinv_id, status) VALUES (?,?,?)";
        int entryId=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, clockId);
            preparedStatement.setInt(2, approvedID);
            preparedStatement.setString(3, "checkedIn");
            preparedStatement.executeUpdate();
            ResultSet resultSet=preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                entryId=resultSet.getInt(1);
            }

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return entryId;
    }
    public boolean updateGateTimeOut(String timeOut, int outId){
        String query="UPDATE check_in_and_out SET time_out=?, status=? WHERE in_and_out_id=?";
        boolean isUpdated=false;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(timeOut));
            preparedStatement.setString(2, "checkedOut");
            preparedStatement.setInt(3, outId);
            isUpdated=preparedStatement.executeUpdate()>0;

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return isUpdated;
    }
    public boolean updateApproveRejectInvitationToCheckedIn(int approvedId){
        String query="UPDATE approve_reject_invitations SET gate_check_in=? WHERE apinv_id=?";
        boolean status=false;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setBoolean(1, true);
            preparedStatement.setInt(2, approvedId);
            status=preparedStatement.executeUpdate()>0;

        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return status;
    }
    //update time out


}
