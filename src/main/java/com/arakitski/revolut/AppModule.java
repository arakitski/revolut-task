package com.arakitski.revolut;

import com.arakitski.revolut.annotation.ApplicationPort;
import com.arakitski.revolut.service.AccountService;
import com.arakitski.revolut.service.AccountServiceImpl;
import com.arakitski.revolut.service.MoneyTransferService;
import com.arakitski.revolut.service.MoneyTransferServiceImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

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
    @ApplicationPort
    public Integer applicationPortProvider() {
        if (port == null || port < 0) {
            return DEFAULT_PORT;
        }
        return port;
    }
}
