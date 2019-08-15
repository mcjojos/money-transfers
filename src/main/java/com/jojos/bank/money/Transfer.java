package com.jojos.bank.money;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Immutable object that holds information regarding the two accounts involved
 * in a transaction plus the amount to be transferred between the two accounts.
 **
 * @author karanikasg@gmail.com
 */
public class Transfer {

    private final int fromAccountId;
    private final int toAccountId;
    private final String transferAmount;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Transfer(@JsonProperty("fromAccountId") int fromAccountId,
                    @JsonProperty("toAccountId") int toAccountId,
                    @JsonProperty("transferAmount") String transferAmount) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.transferAmount = transferAmount;
    }

    public int getFromAccountId() {
        return fromAccountId;
    }

    public int getToAccountId() {
        return toAccountId;
    }

    public String getTransferAmount() {
        return transferAmount;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "fromAccountId=" + fromAccountId +
                ", toAccountId=" + toAccountId +
                ", transferAmount='" + transferAmount + '\'' +
                '}';
    }
}
