package com.arakitski.revolut.service;

import com.arakitski.revolut.tables.pojos.Account;
import com.google.common.collect.ImmutableList;

import java.math.BigDecimal;

public interface AccountService {
    ImmutableList<Account> getAll();
    Account getById(Long id);
    /**Create new account with balance */
    void create(BigDecimal balance);
    /** Remove account by id*/
    void delete(Long id);
}
