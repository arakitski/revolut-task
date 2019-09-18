package com.arakitski.revolut;

import com.arakitski.revolut.annotation.ApplicationPort;
import com.arakitski.revolut.service.*;
import com.arakitski.revolut.tables.daos.AccountDao;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.ThreadLocalTransactionProvider;

import java.io.IOException;
import java.util.Properties;

public class AppModule extends AbstractModule {

    private static final int DEFAULT_PORT = 1234;
    private final Integer port;

    AppModule(Integer port) {
        this.port = port;
    }

    @Override
    protected void configure() {
        bind(AccountService.class).to(AccountServiceImpl.class);
        bind(MoneyTransferService.class).to(MoneyTransferServiceImpl.class);
    }

    @Provides
    public Configuration provideConfiguration() throws IOException, ClassNotFoundException {
        final Properties properties = new Properties();
        properties.load(AppModule.class.getResourceAsStream("/config.properties"));

        Class.forName(properties.getProperty("db.driver"));
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(properties.getProperty("db.url"));
        ds.setUsername(properties.getProperty("db.username"));
        ds.setPassword(properties.getProperty("db.password"));

        Configuration configuration = new DefaultConfiguration().set(SQLDialect.H2).set(ds);
        return configuration.set(new ThreadLocalTransactionProvider(configuration.connectionProvider()));
    }

    @Provides
    @Singleton
    public MutexFactory<Long> provideMutexFactory() {
        return new MutexFactoryImpl<>();
    }

    @Provides
    public DSLContext provideContextTransactionalRunnable(Configuration configuration) {
        return DSL.using(configuration);
    }

    @Provides
    public AccountDao provideAccountDao(Configuration configuration) {
        return new AccountDao(configuration);
    }

    @Provides
    @ApplicationPort
    public Integer applicationPortProvider() {
        if (port == null || port < 0) {
            return DEFAULT_PORT;
        }
        return port;
    }
}
