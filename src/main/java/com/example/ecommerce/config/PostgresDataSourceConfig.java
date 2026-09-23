package com.example.ecommerce.config;

import jakarta.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.ecommerce.vector.repository",
        entityManagerFactoryRef = "postgresEntityManagerFactory",
        transactionManagerRef = "postgresTransactionManager"
)
public class PostgresDataSourceConfig {

    @Bean(name = "postgresDataSourceProperties")
    @ConfigurationProperties(prefix = "app.postgres.datasource")
    public DataSourceProperties postgresDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Bean(name = "postgresDataSource")
    public DataSource postgresDataSource(
            @Qualifier("postgresDataSourceProperties")
            DataSourceProperties postgresDataSourceProperties
    ) {

        return postgresDataSourceProperties
                .initializeDataSourceBuilder()
                .build();
    }


    @Bean(name = "postgresEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean postgresEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("postgresDataSource") DataSource postgresDataSource
    ) {

        return builder
                .dataSource(postgresDataSource)
                .packages("com.example.ecommerce.vector")
                .persistenceUnit("postgres")
                .build();
    }


    @Bean(name = "postgresTransactionManager")
    public PlatformTransactionManager postgresTransactionManager(
            @Qualifier("postgresEntityManagerFactory")
            EntityManagerFactory postgresEntityManagerFactory
    ) {

        return new JpaTransactionManager(
                postgresEntityManagerFactory
        );
    }
}