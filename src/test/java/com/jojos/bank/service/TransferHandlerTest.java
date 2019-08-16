package com.jojos.bank.service;

import com.jojos.bank.money.Account;
import com.jojos.bank.money.Currency;
import com.jojos.bank.money.Transfer;
import com.jojos.bank.resource.Database;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Test for the {@link TransferHandler} class. Also includes a concurrency
 * @author karanikasg@gmail.com
 */
public class TransferHandlerTest {

    @Test
    public void testTransferBetweenTwoAccounts() throws Exception {
        Account from = new Account(BigDecimal.valueOf(1000), Currency.EURO);
        Account to = new Account(BigDecimal.valueOf(1500), Currency.EURO);

        int fromId = Database.add(from);
        int toId = Database.add(to);

        Transfer transfer = new Transfer(fromId, toId, "350");

        TransferHandler.INSTANCE.execute(transfer);

        BigDecimal expectedFrom = BigDecimal.valueOf(650);
        BigDecimal expectedTo = BigDecimal.valueOf(1850);

        Assert.assertTrue(Database.get(fromId).getBalance().compareTo(expectedFrom) == 0);
        Assert.assertTrue(Database.get(toId).getBalance().compareTo(expectedTo) == 0);

    }

    @Test
    public void testCreateAccounts() throws Exception {
        BigDecimal balance1 = new BigDecimal("1200");
        BigDecimal balance2 = new BigDecimal("5000");
        Account account1 = new Account(balance1, Currency.EURO);
        Account account2 = new Account(balance2, Currency.EURO);

        int accountId1 = TransferHandler.INSTANCE.createAccount(account1);
        int accountId2 = TransferHandler.INSTANCE.createAccount(account2);

        Assert.assertEquals(Database.get(accountId1).getBalance().compareTo(balance1), 0);
        Assert.assertEquals(Database.get(accountId2).getBalance().compareTo(balance2), 0);
    }

    /**
     * This is how we'll test concurrency:
     * Initialize four accounts (A, B, C, D) with 1 million euros each.
     * Then start three threads:
     * The first thread is going to transfer from A to B all 1 million with 1 million
     * transfers of 1 euros per transfer.
     * The second thread is going to transfer from B to C 1 million with 1 million transfers
     * of 1 euro per transfer.
     * The third thread is going to transfer from C to D 1 million with 1 million transfers
     * of 1 euro per transfer.
     * The forth thread is going to transfer from D to A 1 million with 1 million transfers
     * of 1 euro per transfer.
     *
     * In the end accounts A, B, C and D must have 1 million euros each
     *
     */
    @Test
    public void testConcurrency() {
        int amount = 1_000_000;
        BigDecimal initialBalance = BigDecimal.valueOf(amount);

        Account accountA = new Account(initialBalance, Currency.EURO);
        Account accountB = new Account(initialBalance, Currency.EURO);
        Account accountC = new Account(initialBalance, Currency.EURO);
        Account accountD = new Account(initialBalance, Currency.EURO);

        int accountIdA = Database.add(accountA);
        int accountIdB = Database.add(accountB);
        int accountIdC = Database.add(accountC);
        int accountIdD = Database.add(accountD);

        TransferHandler transferHandler = TransferHandler.INSTANCE;
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < amount; i++) {
            executorService.submit(() -> {
                transferHandler.execute(new Transfer(accountIdA, accountIdB, "1"));
            });
            executorService.submit(() -> {
                transferHandler.execute(new Transfer(accountIdB, accountIdC, "1"));
            });
            executorService.submit(() -> {
                transferHandler.execute(new Transfer(accountIdC, accountIdD, "1"));
            });
            executorService.submit(() -> {
                transferHandler.execute(new Transfer(accountIdD, accountIdA, "1"));
            });
        }

        executorService.shutdown();

        System.out.println("all tasks submitted");

        try {
            executorService.awaitTermination(150, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Thread has been interrupted, please retry.");
        }

        System.out.println("All tasks completed");

        Assert.assertTrue(Database.get(accountIdA).getBalance().compareTo(initialBalance) == 0);
        Assert.assertTrue(Database.get(accountIdB).getBalance().compareTo(initialBalance) == 0);
        Assert.assertTrue(Database.get(accountIdC).getBalance().compareTo(initialBalance) == 0);
        Assert.assertTrue(Database.get(accountIdD).getBalance().compareTo(initialBalance) == 0);
    }
}