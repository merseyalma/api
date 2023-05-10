package com.zcunsoft.tracking.report.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.zcunsoft.tracking.report.repository.mysql",
        entityManagerFactoryRef = "mysqlEntityManagerFactory",
        transactionManagerRef = "mysqlTransactionManager")
public class MysqlConfig {
    @Autowired
    @Qualifier("mysqlDataSource")
    private DataSource mysqlDataSource;

    protected Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
        props.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
        return props;
    }

    @Bean(name = "mysqlEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(mysqlDataSource)
                .properties(jpaProperties())
                .packages("com.zcunsoft.tracking.report.entity.mysql")
                .persistenceUnit("mysql")
                .build();
    }

    @Bean(name = "mysqlEntityManager")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return mysqlEntityManagerFactory(builder).getObject().createEntityManager();
    }

    @Bean(name = "mysqlTransactionManager")
    PlatformTransactionManager mysqlTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(mysqlEntityManagerFactory(builder).getObject());
    }

    @Bean(name = "mysqlJdbcTemplate")
    public NamedParameterJdbcTemplate mysqlJdbcTemplate(
            @Qualifier("mysqlDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

}
