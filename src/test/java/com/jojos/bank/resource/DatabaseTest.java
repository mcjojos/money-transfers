package com.jojos.bank.resource;

import com.jojos.bank.money.Account;
import com.jojos.bank.money.Currency;
import com.jojos.bank.util.AccountCreator;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author karanikasg@gmail.com
 */
public class DatabaseTest {

    @Test
    public void testNewAccountsStorageAndRetrieval() throws Exception {
        int initialAccountsCount = Database.count();
        List<String> amounts = new ArrayList<>();
        amounts.add("100000");
        amounts.add("200000");
        amounts.add("300000");
        amounts.add("400000");
        amounts.add("500000");
        amounts.add("600000");
        List<Integer> accountIds = AccountCreator.createAndStoreEuroAccountsFor(amounts);

        int i = 0;
        for (int accountId : accountIds) {
            Account account = Database.get(accountId);
            Assert.assertTrue(account.getBalance().compareTo(new BigDecimal(amounts.get(i++))) == 0);
        }

        Assert.assertEquals(initialAccountsCount + 6, Database.count());

    }

    @Test
    public void testAccountsUpdate() throws Exception {
        int initialAccountsCount = Database.count();
        List<String> amounts = new ArrayList<>();
        amounts.add("100");
        amounts.add("300");
        List<Integer> accountIds = AccountCreator.createAndStoreEuroAccountsFor(amounts);

        List<Account> updateAccounts = new ArrayList<>();
        Account updateAccount1 = new Account(new BigDecimal("200"), Currency.EURO);
        Account updateAccount2 = new Account(new BigDecimal("600"), Currency.EURO);
        updateAccounts.add(updateAccount1);
        updateAccounts.add(updateAccount2);

        for (int i = 0; i < accountIds.size(); i++) {
            Database.update(accountIds.get(i), updateAccounts.get(i));
            Assert.assertEquals(Database.get(accountIds.get(i)), updateAccounts.get(i));
        }
        Assert.assertEquals(initialAccountsCount + 2, Database.count());

    }

}
