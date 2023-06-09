package com.zcunsoft.tracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SpringBootApplication
public class App 
{
    public static void main( String[] args ) throws SQLException {
       SpringApplication.run(App.class, args);

    }
}
