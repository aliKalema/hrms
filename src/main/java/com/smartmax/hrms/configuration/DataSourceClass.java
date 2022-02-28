package com.smartmax.hrms.configuration;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceClass {
    @Bean
    public DataSource getDataSource() {
    DataSource dataSource =null;
    DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
    dataSourceBuilder.url("jdbc:mysql://localhost:3306/hrms");
    dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
    dataSourceBuilder.username("root");
    dataSourceBuilder.password("password");
    dataSource = dataSourceBuilder.build();
    return dataSource;
    }
}
