package com.arakitski.revolut.service;

import com.arakitski.revolut.tables.daos.AccountDao;
import com.arakitski.revolut.tables.pojos.Account;
import com.google.common.collect.ImmutableList;
import org.jooq.ContextTransactionalRunnable;
import org.jooq.DSLContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceImplTest {

    @Mock AccountDao accountDao;
    @Mock DSLContext dslContext;

    @Captor ArgumentCaptor<ContextTransactionalRunnable> transactionCaptor;

    private AccountServiceImpl accountService;

    @Before
    public void setUp() {
        accountService = new AccountServiceImpl(dslContext, accountDao);
    }

    @Test
    public void getAll() {
        Account account = new Account(1L, BigDecimal.ZERO);
        when(accountDao.findAll()).thenReturn(ImmutableList.of(account));

        ImmutableList<Account> result = accountService.getAll();

        assertEquals(1, result.size());
        assertEquals(account, result.get(0));
    }

    @Test
    public void getById() {
        Account account = new Account(5L, BigDecimal.ONE);
        when(accountDao.fetchOneById(5050L)).thenReturn(account);

        Account result = accountService.getById(5050L);

        assertEquals(account, result);
    }

    @Test
    public void create() throws Throwable {
        accountService.create(BigDecimal.TEN);

        //test call with transaction
        verify(dslContext).transaction(transactionCaptor.capture());
        transactionCaptor.getValue().run();
        //test call insert method
        Account account = new Account();
        account.setBalance(BigDecimal.TEN);
        verify(accountDao).insert(account);
    }

    @Test
    public void delete() throws Throwable {
        accountService.delete(5L);

        //test call with transaction
        verify(dslContext).transaction(transactionCaptor.capture());
        //test call delete method
        transactionCaptor.getValue().run();
        verify(accountDao).deleteById(5L);
    }
}