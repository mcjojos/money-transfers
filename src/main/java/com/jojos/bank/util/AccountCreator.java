package com.jojos.bank.util;

import com.jojos.bank.money.Account;
import com.jojos.bank.money.Currency;
import com.jojos.bank.resource.Database;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper class to create accounts
 *
 * @author karanikasg@gmail.com
 */
public final class AccountCreator {

    public static List<Integer> createAndStoreEuroAccountsFor(List<String> amounts) {
        List<Account> accounts = amounts.stream()
                .map(BigDecimal::new)
                .map(amount -> new Account(amount, Currency.EURO))
                .collect(Collectors.toList());
        List<Integer> accountIds = new ArrayList<>();
        for (Account account : accounts) {
            int accountId = Database.add(account);
            accountIds.add(accountId);
        }
        return accountIds;
    }

    /**
     * Create a specific amount of accounts used for testing.
     * The balance of the accounts should be greater than 100.000 and less than 900.000
     * @param amountOfAccounts the number of account to create
     */
    public static List<Integer> createAndStoreRandomTestAccounts(int amountOfAccounts) {
        List<Integer> accountIds = new ArrayList<>();
        double min = 100_000;
        double max = 900_000;

        for (int i = 0; i < amountOfAccounts; i++) {
            BigDecimal amount = BigDecimal.valueOf(Math.random() * (max - min) + min);
            Account account = new Account(amount, Currency.EURO);
            int id = Database.add(account);
            accountIds.add(id);
        }
        return accountIds;
    }

}
