package org.invite.com.dao;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.invite.com.model.Structure;
import org.invite.com.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

@ApplicationScoped
public class StructureDao {
    @Inject
    AgroalDataSource ads;
    private static final Logger logger= LoggerFactory.getLogger(StructureDao.class);
    //get preparedStatement
    //check if building exists
    // create building
    private PreparedStatement getPreparedStatement(String query){
        PreparedStatement preparedStatement = null;
        try {
            Connection connection= ads.getConnection();
            preparedStatement= connection.prepareStatement(query);
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return preparedStatement;
    }

    public boolean isStructureExist(int premisesNumber){
        String query="SELECT COUNT(structure_id) FROM structures WHERE premises_number=?";
        int count=0;
        try (PreparedStatement preparedStatement=getPreparedStatement(query)){
            preparedStatement.setInt(1, premisesNumber);
            ResultSet resultSet=preparedStatement.executeQuery();

            while (resultSet.next()){
                count=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return count==1;
    }

    public int createStructure(Structure structure){
        String query="INSERT INTO structures (premises_name, premises_number, location, address, email, number_of_firms, primary_msisdn, secondary_msisdn) VALUES (?,?,?,?,?,?,?,?)";
        int structureId=0;
        try (Connection connection= ads.getConnection(); PreparedStatement preparedStatement= connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1, structure.name());
            preparedStatement.setInt(2, structure.premisesNumber());
            preparedStatement.setString(3, structure.location());
            preparedStatement.setString(4, structure.address());
            preparedStatement.setString(5, structure.email());
            preparedStatement.setInt(6, structure.numberOfFirms());
            preparedStatement.setString(7, structure.primaryMsisdn());
            preparedStatement.setString(8, structure.secondaryMsisdn());
            preparedStatement.executeUpdate();
            ResultSet resultSet=preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                structureId=resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            logger.error(Constants.ERROR_LOG_TEMPLATE, Constants.ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return structureId;
    }
}
