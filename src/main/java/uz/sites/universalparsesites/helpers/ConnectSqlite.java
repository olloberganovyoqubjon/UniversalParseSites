package uz.sites.universalparsesites.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectSqlite {

    public void connectToSqlite(String databaseName){
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:C:/Users/" + System.getProperty("user.name") + "/Desktop/" + databaseName + ".db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
