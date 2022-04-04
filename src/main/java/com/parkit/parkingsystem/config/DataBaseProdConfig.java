package com.parkit.parkingsystem.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DataBaseProdConfig extends DataBaseConfig {

    @Override
    public Connection getConnection() throws ClassNotFoundException, SQLException, IOException {
        logger.info("Create DB connection");
        Class.forName("com.mysql.cj.jdbc.Driver");
        InputStream inputStream = null;
        String urlProd , username , password;
        try {
            Properties loginProperties = new Properties();
            inputStream = getClass().getResourceAsStream(loginPropertiesFile);
            loginProperties.load(inputStream);
            username = loginProperties.getProperty("username");
            password = loginProperties.getProperty("password");
            urlProd = loginProperties.getProperty("urlprod");
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return DriverManager.getConnection(urlProd,username,password);
    }
}
