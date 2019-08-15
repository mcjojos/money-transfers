package com.jojos.bank.money;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jojos.bank.money.serialization.AccountCurrencyDeserializer;
import com.jojos.bank.money.serialization.AccountCurrencySerializer;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * The Account consists of the balance and the currency.
 * Balance is of type {@link BigDecimal} to tackle accuracy problems that are inherent to {@link Double}s.
 *
 * Custom serializer and deserializer had to be registered for the {@link Currency}
 * since it is identified by its ISO code.
 *
 * @implNote {@link Account#Account(BigDecimal, Currency)} constructor is enforcing the scale of the currency
 * to be respected from the balance.
 *
 * @author karanikasg@gmail.com
 */
public class Account {
    private final BigDecimal balance;
    @JsonSerialize(using = AccountCurrencySerializer.class)
    @JsonDeserialize(using = AccountCurrencyDeserializer.class)
    private final Currency currency;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Account(@JsonProperty("balance") BigDecimal balance, @JsonProperty("currency") Currency currency) {
        this.balance = balance.setScale(currency.getDecimalPlaces(), RoundingMode.HALF_UP);
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (currency != account.currency) {
            return false;
        } else {
            if (balance != null) {
                if (balance.compareTo(account.balance) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = balance != null ? balance.hashCode() : 0;
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "balance=" + balance +
                ", currency=" + currency.getIso() +
                '}';
    }
}
