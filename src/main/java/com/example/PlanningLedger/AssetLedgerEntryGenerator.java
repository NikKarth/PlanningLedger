package com.example.PlanningLedger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class AssetLedgerEntryGenerator extends AbstractLedgerEntryGenerator {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AuditLogEntryRepository auditLogEntryRepository;

    @Override
    protected List<ResourceAllocation> selectAllocations(ImplementedAction action) {
        List<ResourceAllocation> result = new ArrayList<>();
        for (ResourceAllocation alloc : action.getProposedAction().getAllocations()) {
            if ("SPECIFIC".equals(alloc.getKind())) {
                result.add(alloc);
            }
        }
        return result;
    }

    @Override
    protected void validate(List<ResourceAllocation> allocations) {
        for (ResourceAllocation allocation : allocations) {
            if (allocation.getTimePeriod() == null || allocation.getTimePeriod().isEmpty()) {
                throw new IllegalArgumentException(
                    "Asset allocation '" + allocation.getId() + "' must have a non-null time period");
            }
            double hours;
            try {
                hours = Double.parseDouble(allocation.getTimePeriod());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                    "Time period must be a numeric duration in hours: " + allocation.getTimePeriod());
            }
            if (hours <= 0) {
                throw new IllegalArgumentException(
                    "Duration in hours must be positive, got: " + hours);
            }
        }
    }

    @Override
    protected Entry buildWithdrawal(Transaction transaction, ResourceAllocation allocation) {
        double hours = Double.parseDouble(allocation.getTimePeriod());
        // No account link: asset pool balance is fixed and must not change.
        // The entry records hours used as a journal line inside the transaction only.
        Entry entry = new Entry();
        entry.setAmount(-hours);
        entry.setChargedAt(new Date());
        entry.setBookedAt(new Date());
        entry.setTransaction(transaction);
        transaction.getEntries().add(entry);
        return entry;
    }

    @Override
    protected Entry buildDeposit(Transaction transaction, ResourceAllocation allocation) {
        double hours = Double.parseDouble(allocation.getTimePeriod());
        Entry entry = new Entry();
        entry.setAmount(hours);
        entry.setChargedAt(new Date());
        entry.setBookedAt(new Date());
        entry.setTransaction(transaction);
        transaction.getEntries().add(entry);
        return entry;
    }

    @Override
    protected void afterPost(Transaction transaction) {
        if (transaction.getEntries().isEmpty()) return;
        transaction.setDescription("Asset utilisation record");
        transactionRepository.save(transaction);
        for (Entry entry : transaction.getEntries()) {
            AuditLogEntry log = new AuditLogEntry();
            log.setEvent("ASSET_UTILISED");
            if (entry.getId() != null) log.setEntryId(entry.getId());
            auditLogEntryRepository.save(log);
        }
    }
}
