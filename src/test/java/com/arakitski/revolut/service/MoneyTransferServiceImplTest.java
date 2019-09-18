package com.arakitski.revolut.service;

import com.arakitski.revolut.exception.NotEnoughMoneyException;
import com.arakitski.revolut.tables.daos.AccountDao;
import com.arakitski.revolut.tables.pojos.Account;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MoneyTransferServiceImplTest {

    @Mock DSLContext context;
    @Mock AccountDao accountDao;

    @Captor ArgumentCaptor<ContextTransactionalRunnable> transactionCaptor;

    private MoneyTransferServiceImpl service;

    @Before
    public void setUp() {
        service = new MoneyTransferServiceImpl(new MutexFactoryImpl<>(), context, accountDao);
    }

    @Test
    public void testTransfer() throws Throwable {
        when(accountDao.fetchOneById(100L)).thenReturn(new Account(100L, BigDecimal.valueOf(100)));
        when(accountDao.fetchOneById(200L)).thenReturn(new Account(200L, BigDecimal.valueOf(200)));

        service.transfer(100L, 200L, BigDecimal.TEN);
        verify(context).transaction(transactionCaptor.capture());
        transactionCaptor.getValue().run();

        verify(accountDao).update(new Account(100L, BigDecimal.valueOf(90)), new Account(200L, BigDecimal.valueOf(210)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTransfer_negativeAmount() {
        service.transfer(100L, 200L, BigDecimal.valueOf(-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTransfer_zeroAmount() {
        service.transfer(100L, 200L, BigDecimal.ZERO);

    }

    @Test(expected = NotEnoughMoneyException.class)
    public void testTransfer_notEnoughMoney() {
        when(accountDao.fetchOneById(100L)).thenReturn(new Account(100L, BigDecimal.valueOf(5)));

        service.transfer(100L, 200L, BigDecimal.TEN);
    }
}