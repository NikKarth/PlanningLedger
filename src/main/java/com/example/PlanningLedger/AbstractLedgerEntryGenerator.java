package com.example.PlanningLedger;

import java.util.*;

public abstract class AbstractLedgerEntryGenerator {
    // Template method
    public final Transaction generateEntries(ImplementedAction action) {
        List<ResourceAllocation> allocations = selectAllocations(action);
        validate(allocations);

        Transaction transaction = createTransaction(action);
        for (ResourceAllocation allocation : allocations) {
            Entry withdrawal = buildWithdrawal(transaction, allocation);
            Entry deposit = buildDeposit(transaction, allocation);
            postEntries(transaction, withdrawal, deposit);
        }
        afterPost(transaction);
        return transaction;
    }

    protected abstract List<ResourceAllocation> selectAllocations(ImplementedAction action);

    protected abstract void validate(List<ResourceAllocation> allocations);

    protected Entry buildWithdrawal(Transaction transaction, ResourceAllocation allocation) {
        // Default implementation
        return new Entry();
    }

    protected Entry buildDeposit(Transaction transaction, ResourceAllocation allocation) {
        // Default implementation
        return new Entry();
    }

    protected void afterPost(Transaction transaction) {
        // Hook method
    }

    private Transaction createTransaction(ImplementedAction action) {
        return new Transaction();
    }

    private void postEntries(Transaction transaction, Entry withdrawal, Entry deposit) {
        // Final implementation
    }
}