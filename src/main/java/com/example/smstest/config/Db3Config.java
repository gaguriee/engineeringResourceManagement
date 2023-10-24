package com.example.smstest.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * DB3 설정 파일 (License DB)
 * License 패키지 LicenseProject, LicenseCompany entity 적용
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.smstest.license",
        entityManagerFactoryRef = "db3EntityManagerFactory",
        transactionManagerRef = "db3TransactionManager"
)
public class Db3Config {
    @Bean(name = "db3DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db3")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "db3EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    entityManagerFactory(EntityManagerFactoryBuilder builder,
                         @Qualifier("db3DataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.example.smstest.license")
                .persistenceUnit("db3")
                .build();
    }

    @Bean(name = "db3TransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("db3EntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
