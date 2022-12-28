package org.example.jdbc_database;

import org.example.model.AuksionView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuksionViewDatabase extends BaseDatabase<AuksionView> {
    public List<AuksionView> getAuksionList(Integer i_page, Integer i_page_length, String i_pre_number,
                                            String i_post_number1, String i_post_number2, String
                                                    i_post_number3, String i_post_number4, String i_post_number5,
                                            String i_post_number6) {
        Connection connection = null;
        PreparedStatement statement = null;
        List<AuksionView> auksionList = new ArrayList<>();
        try {
            connection = getConnection();
            statement = connection.prepareStatement("select get_auksion_list(?,?,?,?,?,?,?,?,?)");
            statement.setInt(1, i_page);
            statement.setInt(2, i_page_length);
            statement.setString(3, i_pre_number);
            statement.setString(4, i_post_number1);
            statement.setString(5, i_post_number2);
            statement.setString(6, i_post_number3);
            statement.setString(7, i_post_number4);
            statement.setString(8, i_post_number5);
            statement.setString(9, i_post_number6);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                auksionList.add(new AuksionView(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null && statement != null) {
                try {
                    connection.close();
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return auksionList;
    }

    public String getAuksionInfo(String name) {
        Connection connection = null;
        PreparedStatement statement = null;
        connection = getConnection();
        try {
            statement = connection.prepareStatement("select * from get_auksion_info(?)");
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            String s = new AuksionView(resultSet).toString();
            return s;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnections(connection, statement);
        }
        return null;
    }

    @Override
    public List<AuksionView> getList() {
        return null;
    }

    @Override
    public List<AuksionView> getPaginationList(int page, int length) {
        return null;
    }
}
