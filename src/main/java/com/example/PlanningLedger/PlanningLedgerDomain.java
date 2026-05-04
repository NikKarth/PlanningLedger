package com.example.PlanningLedger;

import jakarta.persistence.*;
import java.util.*;

// Domain Entities - Knowledge Level

@Entity
class Protocol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @OneToMany(mappedBy = "protocol", cascade = CascadeType.ALL)
    private List<ProtocolStep> steps = new ArrayList<>();

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<ProtocolStep> getSteps() { return steps; }
    public void setSteps(List<ProtocolStep> steps) { this.steps = steps; }
}

@Entity
class ProtocolStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Protocol protocol;

    private String subProtocol;
    private String dependsOn;

    public Long getId() { return id; }
    public Protocol getProtocol() { return protocol; }
    public void setProtocol(Protocol protocol) { this.protocol = protocol; }
    public String getSubProtocol() { return subProtocol; }
    public void setSubProtocol(String subProtocol) { this.subProtocol = subProtocol; }
    public String getDependsOn() { return dependsOn; }
    public void setDependsOn(String dependsOn) { this.dependsOn = dependsOn; }
}

@Entity
class ResourceType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String kind; // ASSET or CONSUMABLE
    private String unit;
    private Double unitCost; // Unit cost for resource pricing

    @OneToMany(mappedBy = "resourceType", cascade = CascadeType.ALL)
    private List<Account> poolAccounts = new ArrayList<>();

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Double getUnitCost() { return unitCost; }
    public void setUnitCost(Double unitCost) { this.unitCost = unitCost; }
    public List<Account> getPoolAccounts() { return poolAccounts; }
    public void setPoolAccounts(List<Account> poolAccounts) { this.poolAccounts = poolAccounts; }
}

@Entity
class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String kind; // POOL, USAGE, ALERT MEMO

    @ManyToOne
    private ResourceType resourceType;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Entry> entries = new ArrayList<>();

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
    public ResourceType getResourceType() { return resourceType; }
    public void setResourceType(ResourceType resourceType) { this.resourceType = resourceType; }
    public List<Entry> getEntries() { return entries; }
    public void setEntries(List<Entry> entries) { this.entries = entries; }
}

@Entity
class Entry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account account;

    private Double amount;
    private Date chargedAt;
    private Date bookedAt;

    @ManyToOne
    private Transaction transaction;

    public Long getId() { return id; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public Date getChargedAt() { return chargedAt; }
    public void setChargedAt(Date chargedAt) { this.chargedAt = chargedAt; }
    public Date getBookedAt() { return bookedAt; }
    public void setBookedAt(Date bookedAt) { this.bookedAt = bookedAt; }
    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}

@Entity
class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private Date createdAt = new Date();

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
    private List<Entry> entries = new ArrayList<>();

    public Long getId() { return id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getCreatedAt() { return createdAt; }
    public List<Entry> getEntries() { return entries; }
    public void setEntries(List<Entry> entries) { this.entries = entries; }
}

@Entity
class ResourceAllocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ProposedAction action;

    @ManyToOne
    private ResourceType resourceType;

    private Double quantity;
    private String kind; // GENERAL or SPECIFIC
    private String assetId;
    private String timePeriod;

    public Long getId() { return id; }
    public ProposedAction getAction() { return action; }
    public void setAction(ProposedAction action) { this.action = action; }
    public ResourceType getResourceType() { return resourceType; }
    public void setResourceType(ResourceType resourceType) { this.resourceType = resourceType; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
    public String getAssetId() { return assetId; }
    public void setAssetId(String assetId) { this.assetId = assetId; }
    public String getTimePeriod() { return timePeriod; }
    public void setTimePeriod(String timePeriod) { this.timePeriod = timePeriod; }
}

// Domain Entities - Operational Level

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
abstract class PlanNode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan parent;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Plan getParent() { return parent; }
    public void setParent(Plan parent) { this.parent = parent; }

    public abstract String getName();
    public abstract String getStatus();
    public abstract Double getTotalAllocatedQuantity(ResourceType resourceType);
    public abstract void accept(PlanNodeVisitor visitor);
}

@Entity
class Plan extends PlanNode {
    private String name;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<PlanNode> children = new ArrayList<>();

    @ManyToOne
    private Protocol sourceProtocol;

    @Override
    public String getStatus() {
        if (children.isEmpty()) return "PROPOSED";
        int completedCount = 0;
        int inProgressCount = 0;
        int suspendedCount = 0;
        int abandonedCount = 0;

        for (PlanNode child : children) {
            String status = child.getStatus();
            if ("COMPLETED".equals(status)) completedCount++;
            else if ("IN_PROGRESS".equals(status)) inProgressCount++;
            else if ("SUSPENDED".equals(status)) suspendedCount++;
            else if ("ABANDONED".equals(status)) abandonedCount++;
        }

        if (completedCount == children.size()) return "COMPLETED";
        if (inProgressCount > 0 || completedCount > 0) return "IN_PROGRESS";
        if (suspendedCount > 0) return "SUSPENDED";
        if (abandonedCount == children.size()) return "ABANDONED";
        return "PROPOSED";
    }

    @Override
    public Double getTotalAllocatedQuantity(ResourceType resourceType) {
        double total = 0;
        for (PlanNode child : children) {
            total += child.getTotalAllocatedQuantity(resourceType);
        }
        return total;
    }

    @Override
    public void accept(PlanNodeVisitor visitor) {
        visitor.visitComposite(this);
        for (PlanNode child : children) {
            child.accept(visitor);
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<PlanNode> getChildren() { return children; }
    public void setChildren(List<PlanNode> children) { this.children = children; }
    public Protocol getSourceProtocol() { return sourceProtocol; }
    public void setSourceProtocol(Protocol sourceProtocol) { this.sourceProtocol = sourceProtocol; }
}

@Entity
class ProposedAction extends PlanNode {
    private String name;
    private String state = "PROPOSED"; // PROPOSED, IN_PROGRESS, etc.

    @OneToOne(mappedBy = "proposedAction", cascade = CascadeType.ALL)
    private ImplementedAction implementedAction;

    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL)
    private List<ResourceAllocation> allocations = new ArrayList<>();

    @Override
    public String getStatus() {
        return state;
    }

    @Override
    public Double getTotalAllocatedQuantity(ResourceType resourceType) {
        double total = 0;
        for (ResourceAllocation alloc : allocations) {
            if (alloc.getResourceType().equals(resourceType)) {
                total += alloc.getQuantity();
            }
        }
        return total;
    }

    @Override
    public void accept(PlanNodeVisitor visitor) {
        visitor.visitLeaf(this);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public ImplementedAction getImplementedAction() { return implementedAction; }
    public List<ResourceAllocation> getAllocations() { return allocations; }
    public void setAllocations(List<ResourceAllocation> allocations) { this.allocations = allocations; }
}

@Entity
class ImplementedAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private ProposedAction proposedAction;

    private Date actualStart;
    private String actualParty;
    private String actualLocation;

    public Long getId() { return id; }
    public ProposedAction getProposedAction() { return proposedAction; }
    public void setProposedAction(ProposedAction proposedAction) { this.proposedAction = proposedAction; }
    public Date getActualStart() { return actualStart; }
    public void setActualStart(Date actualStart) { this.actualStart = actualStart; }
    public String getActualParty() { return actualParty; }
    public void setActualParty(String actualParty) { this.actualParty = actualParty; }
    public String getActualLocation() { return actualLocation; }
    public void setActualLocation(String actualLocation) { this.actualLocation = actualLocation; }
}

// Visitor interface for composite pattern
interface PlanNodeVisitor {
    void visitLeaf(ProposedAction leaf);
    void visitComposite(Plan plan);
}

@Entity
class PostingRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account triggerAccount;

    @ManyToOne
    private Account outputAccount;

    private String strategyType;

    public Long getId() { return id; }
    public Account getTriggerAccount() { return triggerAccount; }
    public void setTriggerAccount(Account triggerAccount) { this.triggerAccount = triggerAccount; }
    public Account getOutputAccount() { return outputAccount; }
    public void setOutputAccount(Account outputAccount) { this.outputAccount = outputAccount; }
    public String getStrategyType() { return strategyType; }
    public void setStrategyType(String strategyType) { this.strategyType = strategyType; }
}

@Entity
class AuditLogEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String event;
    private Long accountId;
    private Long entryId;
    private Long actionId;
    private Date timestamp = new Date();

    public Long getId() { return id; }
    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }
    public Long getActionId() { return actionId; }
    public void setActionId(Long actionId) { this.actionId = actionId; }
    public Date getTimestamp() { return timestamp; }
}

// Concrete Visitors for metrics calculation

class CompletionRatioVisitor implements PlanNodeVisitor {
    private int totalLeaves = 0;
    private int completedLeaves = 0;

    @Override
    public void visitLeaf(ProposedAction leaf) {
        totalLeaves++;
        if ("COMPLETED".equals(leaf.getStatus())) {
            completedLeaves++;
        }
    }

    @Override
    public void visitComposite(Plan plan) {
        // No action needed for composite nodes
    }

    public double getRatio() {
        if (totalLeaves == 0) return 0.0;
        return (double) completedLeaves / totalLeaves;
    }
}

class ResourceCostVisitor implements PlanNodeVisitor {
    private double totalCost = 0.0;

    @Override
    public void visitLeaf(ProposedAction leaf) {
        for (ResourceAllocation alloc : leaf.getAllocations()) {
            ResourceType resourceType = alloc.getResourceType();
            Double unitCost = resourceType.getUnitCost();
            if (unitCost != null && alloc.getQuantity() != null) {
                totalCost += alloc.getQuantity() * unitCost;
            }
        }
    }

    @Override
    public void visitComposite(Plan plan) {
        // No action needed for composite nodes
    }

    public double getTotalCost() {
        return totalCost;
    }
}

class RiskScoreVisitor implements PlanNodeVisitor {
    private int riskCount = 0;

    @Override
    public void visitLeaf(ProposedAction leaf) {
        String status = leaf.getStatus();
        if ("SUSPENDED".equals(status) || "ABANDONED".equals(status)) {
            riskCount++;
        }
    }

    @Override
    public void visitComposite(Plan plan) {
        // No action needed for composite nodes
    }

    public int getScore() {
        return riskCount;
    }
}

// DTO for metrics response
class PlanNodeMetrics {
    private double completionRatio;
    private double totalResourceCost;
    private int riskScore;

    public PlanNodeMetrics(double completionRatio, double totalResourceCost, int riskScore) {
        this.completionRatio = completionRatio;
        this.totalResourceCost = totalResourceCost;
        this.riskScore = riskScore;
    }

    public double getCompletionRatio() { return completionRatio; }
    public double getTotalResourceCost() { return totalResourceCost; }
    public int getRiskScore() { return riskScore; }
}