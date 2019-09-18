package com.arakitski.revolut.service;

import com.arakitski.revolut.exception.NotEnoughMoneyException;
import com.arakitski.revolut.tables.daos.AccountDao;
import org.jooq.DSLContext;

import javax.inject.Inject;
import java.math.BigDecimal;

public class MoneyTransferServiceImpl implements MoneyTransferService {

    private final DSLContext dslContext;
    private final AccountDao accountDao;
    private final MutexFactory<Long> mutexFactory;

    @Inject
    MoneyTransferServiceImpl(MutexFactory<Long> mutexFactory, DSLContext dslContext, AccountDao accountDao) {
        this.accountDao = accountDao;
        this.mutexFactory = mutexFactory;
        this.accountDao.update();
        this.dslContext = dslContext;
    }

    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        checkIsNullOrNegative(amount);
        //lock in order from lower to higher id
        Long firstIdToLock;
        Long secondIdToLock;
        if (fromAccountId > toAccountId) {
            firstIdToLock = toAccountId;
            secondIdToLock = fromAccountId;
        } else {
            firstIdToLock = fromAccountId;
            secondIdToLock = toAccountId;
        }
        synchronized (mutexFactory.getMutex(firstIdToLock)) {
            synchronized (mutexFactory.getMutex(secondIdToLock)) {
                com.arakitski.revolut.tables.pojos.Account fromAccount = accountDao.fetchOneById(fromAccountId);
                checkIfEnoughMoney(fromAccount.getBalance(), amount);
                com.arakitski.revolut.tables.pojos.Account toAccount = accountDao.fetchOneById(toAccountId);

                fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
                toAccount.setBalance(toAccount.getBalance().add(amount));
                dslContext.transaction(() -> accountDao.update(fromAccount, toAccount));
            }
        }
    }

    private static void checkIfEnoughMoney(BigDecimal currentBalance, BigDecimal amount) {
        checkIsNullOrNegative(amount);
        if (currentBalance.compareTo(amount) < 0) {
            throw new NotEnoughMoneyException("Not enough money to withdraw from account");
        }
    }

    private static void checkIsNullOrNegative(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("couldn't set balance null or less the zero.");
        }
    }
}
