package com.arakitski.revolut.service;

import com.arakitski.revolut.tables.daos.AccountDao;
import com.arakitski.revolut.tables.pojos.Account;
import com.google.common.collect.ImmutableList;
import org.jooq.DSLContext;

import javax.inject.Inject;
import java.math.BigDecimal;

public class AccountServiceImpl implements AccountService {

    private final AccountDao accountDao;
    private final DSLContext dslContext;

    @Inject
    AccountServiceImpl(DSLContext dslContext, AccountDao accountDao) {
        this.accountDao = accountDao;
        this.dslContext = dslContext;
    }

    public ImmutableList<Account> getAll() {
        return ImmutableList.copyOf(accountDao.findAll());
    }

    public Account getById(Long id) {
        return accountDao.fetchOneById(id);
    }

    public void create(BigDecimal balance) {
        Account account = new Account();
        account.setBalance(balance);
        dslContext.transaction(() -> accountDao.insert(account));
    }

    public void delete(Long id) {
        dslContext.transaction(() -> accountDao.deleteById(id));
    }
}
