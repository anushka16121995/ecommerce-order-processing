package com.example.ecommerce.config;

import jakarta.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = {
                "com.example.ecommerce.user",
                "com.example.ecommerce.product",
                "com.example.ecommerce.order"
        },
        entityManagerFactoryRef = "mysqlEntityManagerFactory",
        transactionManagerRef = "mysqlTransactionManager"
)
public class MySqlDataSourceConfig {

    @Primary
    @Bean(name = "mysqlDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties mysqlDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Primary
    @Bean(name = "mysqlDataSource")
    public DataSource mysqlDataSource(
            @Qualifier("mysqlDataSourceProperties")
            DataSourceProperties mysqlDataSourceProperties
    ) {
        return mysqlDataSourceProperties
                .initializeDataSourceBuilder()
                .build();
    }


    @Primary
    @Bean(name = "mysqlEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("mysqlDataSource") DataSource mysqlDataSource
    ) {

        return builder
                .dataSource(mysqlDataSource)
                .packages(
                        "com.example.ecommerce.user",
                        "com.example.ecommerce.product",
                        "com.example.ecommerce.order"
                )
                .persistenceUnit("mysql")
                .build();
    }


    @Primary
    @Bean(name = "mysqlTransactionManager")
    public PlatformTransactionManager mysqlTransactionManager(
            @Qualifier("mysqlEntityManagerFactory")
            EntityManagerFactory mysqlEntityManagerFactory
    ) {

        return new JpaTransactionManager(
                mysqlEntityManagerFactory
        );
    }
}