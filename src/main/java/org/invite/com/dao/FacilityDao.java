package org.invite.com.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
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

    public JsonObject getFacilityCategories(int structureId){
        String query="SELECT category, number_of_firms FROM facilities WHERE structure_id=?";
        var categories= Json.createArrayBuilder();
        var categoryJson=Json.createObjectBuilder();
        try (Connection connection = ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query)) {
            preparedStatement.setInt(1, structureId);
            ResultSet resultSet=preparedStatement.executeQuery();

            while (resultSet.next()){
               var category=Json.createObjectBuilder()
                       .add("category", resultSet.getString(1))
                       .add("numberOfFirms", resultSet.getInt(2));
               categories.add(category);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return categoryJson.add("categories", categories).build();
    }

}
