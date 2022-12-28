package org.example.jdbc_database;

import org.example.model.Auksion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AuksionDataBase extends BaseDatabase<Auksion> {
    public boolean addAuksion(
            Date startDate,
            Date endDate,
            int state,
            double initialPrice,
            double currentPrice,
            long avtoNumberId
    ) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet resultSet =
                    statement.executeQuery(
                            "select add_auksion(" + startDate + ", " + endDate + ", " + state + ", " + initialPrice + ", " + currentPrice + ", " + avtoNumberId + ")"
                    );

            return resultSet.getBoolean(0);
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
    }

    @Override
    public List<Auksion> getList() {
        Connection connection = null;
        Statement statement = null;
        List<Auksion> auksionList = new ArrayList<>();
        try {
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet resultSet =
                    statement.executeQuery(
                            "select * from auksion");

            while (resultSet.next()){
                auksionList.add(new Auksion(resultSet));
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
        return auksionList;
    }

    @Override
    public List<Auksion> getPaginationList(int page, int length) {
        return null;
    }
}
