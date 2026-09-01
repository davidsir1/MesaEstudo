package com.example.mesaestudo.utils;

import android.os.StrictMode;

import com.example.mesaestudo.BuildConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static final String URL = BuildConfig.URL;
    public static final String USER = BuildConfig.USER;
    public static final String PASSWORD = BuildConfig.PASSWORD;

    // Métodos para gerenciar a conexão podem ser adicionados aqui
    public static Connection conectar() {
        try {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                StrictMode.ThreadPolicy policy = new
                        StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
            return null;
        }
    }
}