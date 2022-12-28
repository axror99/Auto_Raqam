package org.example.jdbc_database;



import org.example.model.AutoNumber;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AvtoNumberDataBase extends BaseDatabase<AutoNumber> {

//    private final static String ADD_USER = "select add_user()";

    public boolean addAutoNumber(
            String post_number,
            int regionNumber,
            int state
    ) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement("select add_avto_number(?,?,?)");
            statement.setLong(1,regionNumber);
            statement.setString(2,post_number);
            statement.setInt(3,state);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getBoolean(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            closeConnections(connection,statement);
        }
    }

    @Override
    public List<AutoNumber> getList() {
        Connection connection = null;
        Statement statement = null;
        List<AutoNumber> avtoNumberList = new ArrayList<>();
        try {
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet resultSet =
                    statement.executeQuery(
                            "select * from get_auto_number()");

            while (resultSet.next()){
                avtoNumberList.add(new AutoNumber(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            if (connection != null && statement != null){
                try {
                    connection.close();
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return avtoNumberList;
    }

    public List<AutoNumber> getPaginationList(int page, int length ) {
        Connection connection = null;
        PreparedStatement statement = null;
        List<AutoNumber> avtoNumberList = new ArrayList<>();
        try {
            connection = getConnection();
            statement = connection.prepareStatement("select * from get_pagination_number_list(?,?)");
            statement.setInt(1,page);
            statement.setInt(2,length);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                avtoNumberList.add(new AutoNumber(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            if (connection != null && statement != null){
                try {
                    connection.close();
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return avtoNumberList;
    }
}
