package org.invite.com.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.invite.com.model.Facility;
import org.invite.com.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@ApplicationScoped
public class FacilityDao {
    @Inject
    AgroalDataSource ads;
    private static final Logger logger= LoggerFactory.getLogger(FacilityDao.class);

    //check if facility exists with that name
    //add facility

    public boolean isFacilityExist(String category){
        String query="SELECT COUNT(category_id) FROM facilities WHERE category=?";
        int count=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setString(1, category);
            ResultSet resultSet=preparedStatement.executeQuery();

            while (resultSet.next()){
                count=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return count==1;
    }

    public int createFacility(Facility facility){
        String query="INSERT INTO facilities (structure_id, category, number_of_firms) VALUES (?,?,?)";
        int facilityId=0;
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement=connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, facility.structureId());
            preparedStatement.setString(2, facility.category());
            preparedStatement.setInt(3, facility.numberOfFirms());
            preparedStatement.executeUpdate();

            ResultSet resultSet=preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                facilityId=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return facilityId;
    }

}
