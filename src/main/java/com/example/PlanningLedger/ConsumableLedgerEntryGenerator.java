package com.example.PlanningLedger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
class ConsumableLedgerEntryGenerator extends AbstractLedgerEntryGenerator {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    protected List<ResourceAllocation> selectAllocations(ImplementedAction action) {
        List<ResourceAllocation> result = new ArrayList<>();
        for (ResourceAllocation alloc : action.getProposedAction().getAllocations()) {
            if ("GENERAL".equals(alloc.getKind())) {
                result.add(alloc);
            }
        }
        return result;
    }

    @Override
    protected void validate(List<ResourceAllocation> allocations) {}

    @Override
    protected Entry buildWithdrawal(Transaction transaction, ResourceAllocation allocation) {
        // Pool was already deducted at allocation time; no second deduction on completion.
        Entry entry = new Entry();
        entry.setAmount(-allocation.getQuantity());
        entry.setChargedAt(new Date());
        entry.setBookedAt(new Date());
        entry.setTransaction(transaction);
        transaction.getEntries().add(entry);
        return entry;
    }

    @Override
    protected Entry buildDeposit(Transaction transaction, ResourceAllocation allocation) {
        Entry entry = new Entry();
        entry.setAmount(allocation.getQuantity());
        entry.setChargedAt(new Date());
        entry.setBookedAt(new Date());
        entry.setTransaction(transaction);
        transaction.getEntries().add(entry);
        return entry;
    }

    @Override
    protected void afterPost(Transaction transaction) {
        if (!transaction.getEntries().isEmpty()) {
            transaction.setDescription("Consumable resource completion record");
            transactionRepository.save(transaction);
        }
    }
}
