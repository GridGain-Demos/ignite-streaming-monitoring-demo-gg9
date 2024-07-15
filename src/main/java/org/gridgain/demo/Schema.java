package org.gridgain.demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Schema {

    private static final String DROP_TABLE_BUYER = "DROP TABLE IF EXISTS Buyer";
    private static final String DROP_TABLE_TRADE = "DROP TABLE IF EXISTS Trade";
    private static final String CREATE_TABLE_BUYER = "CREATE TABLE Buyer (id int PRIMARY KEY, first_name varchar, last_name varchar, age int, goverment_id varchar)";
    private static final String CREATE_TABLE_TRADE = "CREATE TABLE Trade (id bigint, buyer_id int, symbol varchar, order_quantity int, bid_price double, trade_type varchar, order_date timestamp, PRIMARY KEY(id, buyer_id)) COLOCATE BY (buyer_id)";
    private static final String INSERT_INTO_BUYER =  "INSERT INTO Buyer (id, first_name, last_name, age, goverment_id) VALUES (?,?,?,?,?)";

    private Connection conn_;

    public Schema(Connection conn) {
        conn_ = conn;
    }

    public void dropTables() {
        try {
            Statement stmt = conn_.createStatement();
            stmt.execute(DROP_TABLE_BUYER);
            stmt.execute(DROP_TABLE_TRADE);
        } catch (SQLException e) {
            System.out.println("Error dropping Buyer or Trade table");
            e.printStackTrace();
        }
    }

    public void createTables() {
        try {
            Statement stmt = conn_.createStatement();
            stmt.execute(CREATE_TABLE_BUYER);
            stmt.execute(CREATE_TABLE_TRADE);
        } catch (SQLException e) {
            System.out.println("Error creating Buyer or Trade table");
            e.printStackTrace();
        }
    }

    public void populateBuyerTable() {
        try {
            PreparedStatement pstmt = conn_.prepareStatement(INSERT_INTO_BUYER);
            pstmt.setInt(1, 1);
            pstmt.setString(2, "John");
            pstmt.setString(3, "Smith");
            pstmt.setInt(4, 45);
            pstmt.setString(5, "7bfjd73");
            pstmt.executeUpdate();

            pstmt.setInt(1, 2);
            pstmt.setString(2, "Arnold");
            pstmt.setString(3, "Mazer");
            pstmt.setInt(4, 55);
            pstmt.setString(5, "unb23212");
            pstmt.executeUpdate();

            pstmt.setInt(1, 3);
            pstmt.setString(2, "Lara");
            pstmt.setString(3, "Croft");
            pstmt.setInt(4, 42);
            pstmt.setString(5, "12338fb31");
            pstmt.executeUpdate();

            pstmt.setInt(1, 4);
            pstmt.setString(2, "Patrick");
            pstmt.setString(3, "Green");
            pstmt.setInt(4, 42);
            pstmt.setString(5, "asbn233");
            pstmt.executeUpdate();

            pstmt.setInt(1, 5);
            pstmt.setString(2, "Anna");
            pstmt.setString(3, "Romanoff");
            pstmt.setInt(4, 46);
            pstmt.setString(5, "klnnk3823");
            pstmt.executeUpdate();

            pstmt.setInt(1, 6);
            pstmt.setString(2, "Alfred");
            pstmt.setString(3, "Black");
            pstmt.setInt(4, 55);
            pstmt.setString(5, "32345");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error inserting data into Buyer table");
            e.printStackTrace();
        }
    }

}
