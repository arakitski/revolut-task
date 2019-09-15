package com.arakitski.revolut.model;

import com.arakitski.revolut.exception.NotEnoughMoneyException;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Test class for {@link Account}.
 */
public class AccountTest {

    private Account account;

    @Before
    public void setUp() {
        account = new Account(1, BigDecimal.valueOf(1));
    }

    @Test
    public void testSetBalance() {
        Account account = new Account(1, BigDecimal.valueOf(1));
        BigDecimal newValue = BigDecimal.valueOf(3);

        account.setBalance(newValue);

        assertEquals(account.getBalance(), newValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetBalance_null() {
        account.setBalance(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetBalance_negativeNumber() {
        account.setBalance(BigDecimal.valueOf(-0.1));
    }

    @Test
    public void testWithdraw() {
        Account account = new Account(1, BigDecimal.valueOf(10));

        account.withdraw(BigDecimal.valueOf(3));

        assertEquals(account.getBalance(), BigDecimal.valueOf(7));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithdraw_null() {
        account.setBalance(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithdraw_negativeNumber() {
        account.setBalance(null);
    }

    @Test(expected = NotEnoughMoneyException.class)
    public void withdraw_notEnoughMoney() {
        Account account = new Account(1, BigDecimal.valueOf(3));

        account.withdraw(BigDecimal.valueOf(4));
    }

    @Test
    public void testDeposit() {
        Account account = new Account(1, BigDecimal.valueOf(10));

        account.deposit(BigDecimal.ONE);

        assertEquals(account.getBalance(), BigDecimal.valueOf(11));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeposit_negativeNumber() {
        account.deposit(BigDecimal.valueOf(-10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeposit_null() {
        account.deposit(null);
    }
}