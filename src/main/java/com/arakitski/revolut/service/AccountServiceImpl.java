package com.arakitski.revolut.service;

import com.arakitski.revolut.model.Account;
import com.google.common.collect.ImmutableList;

import java.math.BigDecimal;

public class AccountServiceImpl implements AccountService {

    public ImmutableList<Account> getAll() {
        return ImmutableList.of(
                new Account(1, BigDecimal.ZERO),
                new Account(2, BigDecimal.TEN)
        );
    }

    public Account getById(long id) {
        return new Account(100, BigDecimal.ZERO);
    }

    public Account create(BigDecimal balance) {
        return new Account(100, BigDecimal.valueOf(555));
    }

    public void delete(long id) {

    }
}
