package com.arakitski.revolut.service;

import java.math.BigDecimal;

public interface MoneyTransferService {

    /** Transfer money from {@code fromAccountId} to {@code toAccountId}. */
    void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount);
}
