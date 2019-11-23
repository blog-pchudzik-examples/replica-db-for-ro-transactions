package com.pchudzik.blog.example.readfromreplica.infrastructure;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EntityScan
public class DatabaseConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "app.datasource.master")
    public HikariConfig masterConfiguration() {
        return new HikariConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "app.datasource.slave")
    public HikariConfig slaveConfiguration() {
        return new HikariConfig();
    }

    @Bean
    public DataSource routingDataSource() {
        return new MasterReplicaRoutingDataSource(
                loggingProxy("master", new HikariDataSource(masterConfiguration())),
                loggingProxy("replica", new HikariDataSource(slaveConfiguration())));
    }

    private DataSource loggingProxy(String name, DataSource dataSource) {
        SLF4JQueryLoggingListener loggingListener = new SLF4JQueryLoggingListener();
        loggingListener.setLogLevel(SLF4JLogLevel.INFO);
        loggingListener.setLogger(name);
        loggingListener.setWriteConnectionId(false);
        return ProxyDataSourceBuilder
                .create(dataSource)
                .name(name)
                .listener(loggingListener)
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(routingDataSource())
                .packages("com.pchudzik.blog.example.readfromreplica.model")
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("jpaTxManager") PlatformTransactionManager wrapped) {
        return new ReplicaAwareTransactionManager(wrapped);
    }

    @Bean(name = "jpaTxManager")
    public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

}
