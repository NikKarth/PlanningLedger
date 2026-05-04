package com.example.PlanningLedger;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class ReversalLedgerEntryGenerator extends AbstractLedgerEntryGenerator {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    protected List<ResourceAllocation> selectAllocations(ImplementedAction action) {
        return action.getProposedAction().getAllocations();
    }

    @Override
    protected void validate(List<ResourceAllocation> allocations) {}

    @Override
    protected Entry buildWithdrawal(Transaction transaction, ResourceAllocation allocation) {
        ResourceType resourceType = allocation.getResourceType();
        if (resourceType.getPoolAccounts().isEmpty()) return new Entry();
        Account pool = resourceType.getPoolAccounts().get(0);
        Entry entry = new Entry();
        entry.setAccount(pool);
        entry.setAmount(allocation.getQuantity()); // negates original -quantity pool deduction
        entry.setChargedAt(new Date());
        entry.setBookedAt(new Date());
        entry.setTransaction(transaction);
        transaction.getEntries().add(entry);
        return entry;
    }

    @Override
    protected Entry buildDeposit(Transaction transaction, ResourceAllocation allocation) {
        return new Entry();
    }

    @Override
    protected void afterPost(Transaction transaction) {
        transaction.setDescription("Reversal entries for reopened action");
        transactionRepository.save(transaction);
    }
}
