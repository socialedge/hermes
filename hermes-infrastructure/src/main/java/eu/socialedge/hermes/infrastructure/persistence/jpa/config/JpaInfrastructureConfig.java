/**
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016 SocialEdge
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package eu.socialedge.hermes.infrastructure.persistence.jpa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import java.util.Properties;

import javax.sql.DataSource;

@Configuration
@ComponentScan("eu.socialedge.hermes.infrastructure.persistence.jpa")
@ConfigurationProperties(prefix = "database", locations = {"classpath:infrastructure-jpa.yml"})
@EnableJpaRepositories("eu.socialedge.hermes.infrastructure.persistence.jpa.repository.entity")
public class JpaInfrastructureConfig {

    private final static String JPA_ENTITY_PACKAGE
            = "eu.socialedge.hermes.infrastructure.persistence.jpa.entity";

    private String driverName;

    private String url;

    private String username;

    private String password;

    private String hibernateDialect;

    private String hibernateShowSql;

    private String hibernateHbm2ddl;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactory =
                new LocalContainerEntityManagerFactoryBean();

        entityManagerFactory.setDataSource(dataSource());

        entityManagerFactory.setPackagesToScan(JPA_ENTITY_PACKAGE);
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManagerFactory.setJpaVendorAdapter(vendorAdapter);

        entityManagerFactory.setJpaProperties(new Properties() {{
            put("hibernate.dialect", hibernateDialect);
            put("hibernate.show_sql", hibernateShowSql);
            put("hibernate.hbm2ddl.auto", hibernateHbm2ddl);
        }});

        return entityManagerFactory;
    }

    @Bean
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    // Are needed by Spring's @ConfigurationProperties

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setHibernateDialect(String hibernateDialect) {
        this.hibernateDialect = hibernateDialect;
    }

    public void setHibernateShowSql(String hibernateShowSql) {
        this.hibernateShowSql = hibernateShowSql;
    }

    public void setHibernateHbm2ddl(String hibernateHbm2ddl) {
        this.hibernateHbm2ddl = hibernateHbm2ddl;
    }
}
