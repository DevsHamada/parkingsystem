package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.config.DataBaseProdConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParkingSpotDAO {
    private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseProdConfig();

// recupére la prochaine place disponible dans la base
    public int getNextAvailableSlot(ParkingType parkingType) {
        Connection con = null;
        int result = -1;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
            ps.setString(1, parkingType.toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException | RuntimeException | IOException ex) {
            logger.error("Error fetching next available slot", ex);
        } finally {
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
        }
        return result;
    }

    public boolean updateParking(ParkingSpot parkingSpot) {
        //update the availability fo that parking slot
        Connection con = null;
        PreparedStatement ps = null;
        boolean result = false;
        ResultSet rs = null;
        try {
            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
            ps.setBoolean(1, parkingSpot.isAvailable());
            ps.setInt(2, parkingSpot.getId());
            result = (ps.executeUpdate() == 1);
        } catch (SQLException | ClassNotFoundException | RuntimeException | IOException ex) {
            logger.error("Error updating parking info", ex);
        } catch (Exception ex) {
            logger.error("Exception updating parking ", ex);
        } finally {
            if (ps != null)
            {
                dataBaseConfig.closePreparedStatement(ps);
            }
            dataBaseConfig.closeConnection(con);
        }
        return result;
    }
}
