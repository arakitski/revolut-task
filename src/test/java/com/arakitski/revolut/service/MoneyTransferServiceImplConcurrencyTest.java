package com.arakitski.revolut.service;

import com.arakitski.revolut.tables.daos.AccountDao;
import com.arakitski.revolut.tables.pojos.Account;
import com.google.common.collect.ImmutableList;
import org.jooq.ContextTransactionalRunnable;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultDSLContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class MoneyTransferServiceImplConcurrencyTest {

    private MoneyTransferServiceImpl service;
    private AccountDao fakeAccountDao;

    private static class FakeDSLContext extends DefaultDSLContext {

        FakeDSLContext() {
            super(SQLDialect.H2);
        }

        @Override
        public void transaction(ContextTransactionalRunnable transactional) {
            try {
                transactional.run();
            } catch (Throwable ignored) {
            }
        }
    }

    private static class FakeAccountDao extends AccountDao {
        private ConcurrentHashMap<Long, Account> map = new ConcurrentHashMap<>();

        @Override
        public synchronized void update(Account... objects) {
            for (Account account : objects) {
                map.put(account.getId(), account);
            }
        }

        @Override
        public Account fetchOneById(Long value) {
            return map.get(value);
        }
    }

    @Before
    public void setUp() {
        fakeAccountDao = new FakeAccountDao();
        service = new MoneyTransferServiceImpl(new MutexFactoryImpl<>(), new FakeDSLContext(), fakeAccountDao);
    }

    @Test
    public void testTransfer() throws Throwable {
        fakeAccountDao.update(new Account(200L, BigDecimal.valueOf(2000000)), new Account(100L, BigDecimal.valueOf(1000000)));
        ImmutableList.Builder<Callable<Void>> calls = ImmutableList.builder();
        for (int i = 0; i < 100000; i++) {
            calls.add(toCallable(() -> service.transfer(100L, 200L, BigDecimal.valueOf(1))));
            calls.add(toCallable(() -> service.transfer(200L, 100L, BigDecimal.valueOf(1))));
        }

        Executors.newFixedThreadPool(10).invokeAll(calls.build());

        assertEquals(BigDecimal.valueOf(1000000), fakeAccountDao.fetchOneById(100L).getBalance());
        assertEquals(BigDecimal.valueOf(2000000), fakeAccountDao.fetchOneById(200L).getBalance());
    }

    private Callable<Void> toCallable(final Runnable runnable) {
        return () -> {
            runnable.run();
            return null;
        };
    }
}