package com.jojos.bank.resource;

import com.jojos.bank.money.Account;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A storage implementation that is using in-memory key-value pairs for simplicity
 *
 * @author karanikasg@gmail.com
 */
public final class Database {
    private static final Database INSTANCE = new Database();
    private static final AtomicInteger ID = new AtomicInteger();

    private final ConcurrentMap<Integer, Account> store = new ConcurrentHashMap<>();

    /**
     * Add an account to our storage implementation
     * @param account to be added
     * @return the ID associated with this account
     */
    public static int add(Account account) {
        int id = ID.getAndIncrement();
        INSTANCE.store.putIfAbsent(id, account);
        return id;
    }

    /**
     * Update operation of a particular account identified by it's ID
     * @param accountId is associated with the accound to be updated
     * @param account the values to be updated
     * @return false in case the update is not successful and true if the account was updated.
     *
     * @implNote
     * in case no account with this ID exists in the DB the operation will not try to create a new one and simply fail.
     */
    public static boolean update(int accountId, Account account) {
        if (INSTANCE.store.replace(accountId, account) == null) {
            return false;
        }
        return true;
    }

    /**
     * Retrieve operation of an account
     * @param accountId associated with the account to be retrieved
     * @return the account that is mapped to the accountId null if it does not exist.
     */
    public static Account get(int accountId) {
        return INSTANCE.store.get(accountId);
    }

    public static boolean accountExists(int account) {
        return INSTANCE.store.containsKey(account);
    }

    /**
     * select Count(*)
     *
     * @return the total count of the accounts stored in out store
     */
    public static int count() {
        return INSTANCE.store.size();
    }
}
