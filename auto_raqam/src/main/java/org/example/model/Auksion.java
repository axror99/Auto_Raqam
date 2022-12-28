package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Auksion extends Base {
    private long id;
    private Date startDate;
    private Date endDate;
    private int state;
    private double initialPrice;
    private double currentPrice;
    private long autoNumberId;
    private long userId;

    public Auksion(ResultSet resultSet){
        this.get(resultSet);
    }
    @Override
    protected void get(ResultSet resultSet) {
        try {
            this.id = resultSet.getLong("id");
            this.startDate = resultSet.getDate("start_date");
            this.endDate = resultSet.getDate("end_date");
            this.state = resultSet.getInt("state");
            this.initialPrice = resultSet.getDouble("initial_price");
            this.currentPrice = resultSet.getDouble("current_price");
            this.autoNumberId = resultSet.getLong("avto_number_id");
            this.userId = resultSet.getLong("user_id");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
