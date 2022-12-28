package org.example.jdbc_database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public abstract class BaseDatabase<R> {

    private final static String DATABASE_URL = "jdbc:postgresql://localhost:5432/av_num";
    private final static String DATABASE_USERNAME = "postgres";
    private final static String DATABASE_PASSWORD = "admin";

    public abstract List<R> getList();
    public abstract List<R> getPaginationList(int page, int length);
    protected Connection getConnection(){
        try {
            return DriverManager.getConnection(DATABASE_URL,DATABASE_USERNAME,DATABASE_PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    protected void closeConnections(Connection connection, Statement statement){
        if (connection != null && statement != null){
            try {
                connection.close();
                statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    protected String forSql(String s){
        return "'" + s + "'";
    }

}
