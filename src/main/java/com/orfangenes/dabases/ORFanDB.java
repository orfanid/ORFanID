package com.orfangenes.dabases;

import com.orfangenes.util.Database;

import java.sql.*;

public class ORFanDB {

    public static Connection connectToDatabase (String database) {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + Database.HOST + ":" + Database.PORT + "/" +
                    database + "?autoReconnect=true&useSSL=false", Database.USERNAME, Database.PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void insertRecordPreparedStatement (Connection connection, String query, Object[] data) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            for (int i = 0; i < data.length; i++) {
                if (data[i] instanceof Integer) {
                    preparedStatement.setInt(i+1, (int)data[i]);
                } else if (data[i] instanceof String) {
                    preparedStatement.setString(i+1, (String)data[i]);
                }
            }
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean recordExists (Connection connection, String tableName, String sequence) {
        String selectQuery = "SELECT sequence FROM " + tableName + " WHERE sequence = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, sequence);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}
