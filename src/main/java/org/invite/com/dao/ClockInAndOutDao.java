package org.invite.com.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.invite.com.model.ClockInAndOut;
import org.invite.com.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@ApplicationScoped
public class ClockInAndOutDao {
    @Inject
    AgroalDataSource ads;
    private static final Logger logger= LoggerFactory.getLogger(ClockInAndOutDao.class);
    //signIn guard
    //signOut guard

    public int signInGuardForShift(ClockInAndOut clockInAndOut){
        String query="INSERT INTO clock_in_and_out (guard_id, shift_id, sign_in, sign_out) VALUES (?,?,?,?)";
        int signInId=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, clockInAndOut.guardId());
            preparedStatement.setInt(2, clockInAndOut.shiftId());
            preparedStatement.setBoolean(3, true);
            preparedStatement.setBoolean(4, false);
            preparedStatement.executeUpdate();
            ResultSet resultSet= preparedStatement.getGeneratedKeys();

            while (resultSet.next()){
                signInId=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return signInId;
    }

    public boolean signOutGuardForShift(int clockId, String leaveTime){
        String query="UPDATE clock_in_and_out SET sign_out=?, left_at=? WHERE clock_id=?";
        boolean status=false;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setBoolean(1, true);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(leaveTime));
            preparedStatement.setInt(3, clockId);
            status=preparedStatement.executeUpdate()>0;
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return status;
    }
}
