package com.jojos.bank.service;

import com.jojos.bank.money.Account;
import com.jojos.bank.money.Transfer;
import com.jojos.bank.resource.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The class performs transfers and updates the accounts in the database
 *
 * The following operations are currently supported
 * - Execute a transfer between two accounts. Execute both changes as one atomic operation
 * - Retrieve an account based on its ID.
 *
 * Both operations are atomic. The retrieval cannot be executed until a transfer between two accounts is finished.
 * Several threads can retrieve accounts from the database as long as there is no ongoing transfer
 *
 * @author karanikasg@gmail.com.
 */
public class TransferHandler {

	private static final Logger log = LoggerFactory.getLogger(TransferHandler.class);

    /**
     * Accessing the class methods are only allowed through one instance
     */
	public static final TransferHandler INSTANCE = new TransferHandler();

	// the read-write lock are only used while updating the parent-child relationships
	// since it may be the case where we have inconsistencies while calculating the sum (read ) AND updating the
	// relationships on inserting a transaction.
	private final Lock readLock;
	private final Lock writeLock;

	private TransferHandler() {
		ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
		writeLock = readWriteLock.writeLock();
		readLock = readWriteLock.readLock();
	}

	/**
	 * Execute a transfer between two accounts
	 *
	 * TODO return a specific error code that maps to an exact error condition upon transfer failure
     * TODO support multi currency
	 *
	 * @param transfer object containing information about the accounts participating in the transaction and the amount.
	 * @return true if the transfer succeeded, false otherwise
     *
     * @implNote negative transfers are not supported
	 */
	public boolean execute(Transfer transfer) {
        try {
            writeLock.lock();
            //sanity check - ensure accounts exist
            if (!Database.accountExists(transfer.getFromAccountId()) ||
                    !Database.accountExists(transfer.getToAccountId())) {
                log.error("At least one of the account does not exist. Aborting transfer.");
                return false;
            }

            BigDecimal transferAmount = new BigDecimal(transfer.getTransferAmount());
            if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
                log.error("No point on transferring an amount less than or equal to zero. Aborting transfer.");
                return false;
            }

            Account accountFrom = Database.get(transfer.getFromAccountId());
            Account accountTo = Database.get(transfer.getToAccountId());

            accountFrom = subtract(accountFrom, transferAmount);
            accountTo = add(accountTo, transferAmount);

            boolean fromUpdated = Database.update(transfer.getFromAccountId(), accountFrom);
            boolean toUpdated = Database.update(transfer.getToAccountId(), accountTo);

            if (!fromUpdated || !toUpdated) {
                log.error("This should have never happened. One of the accounts were not updated");
                return false;
            }

        } finally {
            writeLock.unlock();
        }

		return true;
	}

    /**
     * Create a new account
     * @param account the account to be created
     * @return the account ID associated with this account
     */
	public int createAccount(Account account) {
		try{
			writeLock.lock();
			return Database.add(account);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Get the account associated with the particular id
	 * @param accountId the id of the account in question
	 * @return the specific account if it exists, null otherwise
	 */
	public Account getAccount(int accountId) {
		try {
			readLock.lock();
			return Database.get(accountId);
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Create a new account object that is a copy of the account passed minus the specified amount
	 * @param acc the account to be used as a reference
	 * @param amount the amount to be subtracted from the account
	 * @return a new account
	 */
	private Account subtract(Account acc, BigDecimal amount) {
		return new Account(acc.getBalance().subtract(amount), acc.getCurrency());
	}

	/**
	 * Create a new account object that is a copy of the account passed plus the specified amount
	 * @param acc the account to be used as a reference
	 * @param amount the amount to be added to the account
	 * @return a new account
	 */
	private Account add(Account acc, BigDecimal amount) {
		return new Account(acc.getBalance().add(amount), acc.getCurrency());
	}

}
