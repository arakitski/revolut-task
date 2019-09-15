package com.arakitski.revolut.model;

import com.arakitski.revolut.exception.NotEnoughMoneyException;
import com.google.common.base.Objects;

import java.math.BigDecimal;

public class Account {
    private long id;
    private BigDecimal balance;

    public Account(long id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        checkIsNullOrNegative(balance);
        this.balance = balance;
    }

    public void withdraw(BigDecimal amount) {
        checkIsNullOrNegative(amount);
        if(this.balance.compareTo(amount)<0) {
            throw new NotEnoughMoneyException("Not enough money to withdraw from account");
        }
        this.balance = this.balance.subtract(amount);
    }

    private void checkIsNullOrNegative(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("couldn't set balance null or less the zero.");
        }
    }

    public void deposit(BigDecimal amount) {
        checkIsNullOrNegative(amount);
        this.balance = this.balance.add(amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.id &&
                Objects.equal(balance, account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, balance);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", balance=" + balance +
                '}';
    }
}
