package org.example.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class AuksionView {

    String number;
    Long auksion_id;
    Double initial_price;
    Double current_price;
    Double suggested_price;
    Date end_date;
    String region_name;


    public AuksionView(ResultSet resultSet) {
        this.get(resultSet);
    }

    private void get(ResultSet resultSet) {
        try {

            this.number = resultSet.getString("number");
            this.auksion_id = resultSet.getLong("auksion_id");
            this.initial_price = resultSet.getDouble("initial_price");
            this.current_price = resultSet.getDouble("current_price");
            this.suggested_price = resultSet.getDouble("suggested_price");
            this.end_date = resultSet.getDate("end_date");
            this.region_name = resultSet.getString("region_name");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "  *Number*           \t\t: " + number +
               "\n *Auksion id*      \t\t: " + auksion_id +
               "\n *Initial price*   \t\t: " + initial_price +
               "\n *Current price*   \t\t: " + current_price +
               "\n *Suggested price* \t\t: " + suggested_price +
               "\n *End date*        \t\t: " + end_date +
               "\n *Region name*     \t\t: " + region_name;
    }
}
