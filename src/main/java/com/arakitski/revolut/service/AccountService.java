package com.arakitski.revolut.service;

import com.arakitski.revolut.model.Account;
import com.google.common.collect.ImmutableList;

import java.math.BigDecimal;

public interface AccountService {
    ImmutableList<Account> getAll();
    Account getById(long id);
    /**Create new account with balance */
    Account create(BigDecimal balance);
    /** Remove account by id*/
    void delete(long id);
}
