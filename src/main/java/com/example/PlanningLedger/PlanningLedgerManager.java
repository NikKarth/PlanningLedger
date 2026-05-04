package com.example.PlanningLedger;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
class ProtocolManager {
    @Autowired
    private ProtocolRepository protocolRepository;

    @Autowired
    private ProtocolStepRepository protocolStepRepository;

    public List<Protocol> getAllProtocols() {
        return protocolRepository.findAll();
    }

    public Protocol createProtocol(Protocol protocol) {
        return protocolRepository.save(protocol);
    }

    public Optional<Protocol> getProtocolById(Long id) {
        return protocolRepository.findById(id);
    }

    public ProtocolStep addStepToProtocol(Long protocolId, String subProtocol, String dependsOn) {
        Protocol protocol = protocolRepository.findById(protocolId)
            .orElseThrow(() -> new RuntimeException("Protocol not found"));

        ProtocolStep step = new ProtocolStep();
        step.setProtocol(protocol);
        step.setSubProtocol(subProtocol);
        step.setDependsOn(dependsOn);

        protocol.getSteps().add(step);
        protocolRepository.save(protocol);

        return step;
    }

    public void deleteProtocol(Long id) {
        protocolRepository.deleteById(id);
    }
}

@Service
class ResourceTypeManager {
    @Autowired
    private ResourceTypeRepository resourceTypeRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntryRepository entryRepository;

    public List<ResourceType> getAllResourceTypes() {
        return resourceTypeRepository.findAll();
    }

    public ResourceType createResourceType(ResourceType resourceType) {
        return resourceTypeRepository.save(resourceType);
    }

    public Optional<ResourceType> getResourceTypeById(Long id) {
        return resourceTypeRepository.findById(id);
    }

    public Account createPoolAccountForResourceType(Long resourceTypeId, String accountName, Double initialBalance) {
        ResourceType resourceType = resourceTypeRepository.findById(resourceTypeId)
            .orElseThrow(() -> new RuntimeException("ResourceType not found"));

        Account account = new Account();
        account.setName(accountName);
        account.setKind("POOL");
        account.setResourceType(resourceType);
        account = accountRepository.save(account);

        if (initialBalance != null && initialBalance != 0) {
            Transaction tx = new Transaction();
            tx.setDescription("Initial balance for pool '" + accountName + "'");
            transactionRepository.save(tx);

            Entry entry = new Entry();
            entry.setAccount(account);
            entry.setAmount(initialBalance);
            entry.setChargedAt(new java.util.Date());
            entry.setBookedAt(new java.util.Date());
            entry.setTransaction(tx);
            entryRepository.save(entry);
        }

        resourceType.getPoolAccounts().add(account);
        resourceTypeRepository.save(resourceType);

        return account;
    }

    public void deleteResourceType(Long id) {
        resourceTypeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        for (Account account : accounts) {
            if (account.getResourceType() != null) {
                account.getResourceType().getName();
                account.getResourceType().getUnit();
            }
            for (Entry entry : account.getEntries()) {
                if (entry.getTransaction() != null) {
                    entry.getTransaction().getDescription();
                }
            }
        }
        return accounts;
    }
}

@Service
class PlanManager {
    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private ProposedActionRepository proposedActionRepository;

    @Autowired
    private ProtocolRepository protocolRepository;

    @Autowired
    private ResourceTypeRepository resourceTypeRepository;

    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }

    public Plan createPlan(Plan plan) {
        return planRepository.save(plan);
    }

    public Optional<Plan> getPlanById(Long id) {
        return planRepository.findById(id);
    }

    public Plan createPlanFromProtocol(Long protocolId, String planName) {
        Protocol protocol = protocolRepository.findById(protocolId)
            .orElseThrow(() -> new RuntimeException("Protocol not found"));

        Plan plan = new Plan();
        plan.setName(planName);
        plan.setSourceProtocol(protocol);

        // Create ProposedAction for each protocol step
        for (ProtocolStep step : protocol.getSteps()) {
            ProposedAction action = new ProposedAction();
            action.setName(step.getSubProtocol());
            action.setParent(plan);
            plan.getChildren().add(action);
        }

        return planRepository.save(plan);
    }

    public ProposedAction addActionToPlan(Long planId, String actionName) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Plan not found"));

        ProposedAction action = new ProposedAction();
        action.setName(actionName);
        action.setParent(plan);
        plan.getChildren().add(action);

        planRepository.save(plan);
        return action;
    }

    public Plan addSubPlanToPlan(Long parentPlanId, String subPlanName) {
        Plan parentPlan = planRepository.findById(parentPlanId)
            .orElseThrow(() -> new RuntimeException("Parent plan not found"));

        Plan subPlan = new Plan();
        subPlan.setName(subPlanName);
        subPlan.setParent(parentPlan);
        parentPlan.getChildren().add(subPlan);

        return planRepository.save(parentPlan);
    }

    public void deletePlan(Long id) {
        planRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PlanReportRow> generateReport(Long planId) {
        Plan plan = planRepository.findById(planId).orElseThrow();
        List<ResourceType> resourceTypes = resourceTypeRepository.findAll();

        initializePlanNode(plan);

        List<PlanReportRow> rows = new ArrayList<>();
        DepthFirstPlanIterator iterator = new DepthFirstPlanIterator(plan);
        while (iterator.hasNext()) {
            PlanNode node = iterator.next();
            int depth = iterator.getCurrentDepth();

            Map<String, Double> totals = new LinkedHashMap<>();
            for (ResourceType rt : resourceTypes) {
                totals.put(rt.getName() + " (" + rt.getUnit() + ")",
                           node.getTotalAllocatedQuantity(rt));
            }

            String nodeType = (node instanceof Plan) ? "Plan" : "Action";
            rows.add(new PlanReportRow(node.getName(), node.getStatus(), nodeType, depth, totals));
        }
        return rows;
    }

    private void initializePlanNode(PlanNode node) {
        if (node instanceof Plan) {
            List<PlanNode> children = ((Plan) node).getChildren();
            children.size();
            for (PlanNode child : children) {
                initializePlanNode(child);
            }
        } else if (node instanceof ProposedAction) {
            for (ResourceAllocation alloc : ((ProposedAction) node).getAllocations()) {
                alloc.getResourceType().getName();
            }
        }
    }
}

@Service
class ActionManager {
    @Autowired
    private ProposedActionRepository proposedActionRepository;

    @Autowired
    private ImplementedActionRepository implementedActionRepository;

    @Autowired
    private ResourceTypeRepository resourceTypeRepository;

    @Autowired
    private ResourceAllocationRepository resourceAllocationRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private ReversalLedgerEntryGenerator reversalLedgerEntryGenerator;

    public ProposedAction implementAction(Long id) {
        ProposedAction action = proposedActionRepository.findById(id).orElseThrow();
        action.setState("IN_PROGRESS");
        proposedActionRepository.save(action);

        ImplementedAction impl = new ImplementedAction();
        impl.setProposedAction(action);
        impl.setActualStart(new java.util.Date());
        impl.setActualParty("Default Party");
        impl.setActualLocation("Default Location");
        implementedActionRepository.save(impl);

        return action;
    }

    public ProposedAction completeAction(Long id) {
        ProposedAction action = proposedActionRepository.findById(id).orElseThrow();
        action.setState("COMPLETED");
        return proposedActionRepository.save(action);
    }

    public ProposedAction suspendAction(Long id) {
        ProposedAction action = proposedActionRepository.findById(id).orElseThrow();
        action.setState("SUSPENDED");
        return proposedActionRepository.save(action);
    }

    public ProposedAction resumeAction(Long id) {
        ProposedAction action = proposedActionRepository.findById(id).orElseThrow();
        action.setState("IN_PROGRESS");
        return proposedActionRepository.save(action);
    }

    public ProposedAction abandonAction(Long id) {
        ProposedAction action = proposedActionRepository.findById(id).orElseThrow();
        action.setState("ABANDONED");
        return proposedActionRepository.save(action);
    }

    public ProposedAction submitForApprovalAction(Long id) {
        ProposedAction action = proposedActionRepository.findById(id).orElseThrow();
        action.setState("PENDING_APPROVAL");
        return proposedActionRepository.save(action);
    }

    public ProposedAction approveAction(Long id) {
        ProposedAction action = proposedActionRepository.findById(id).orElseThrow();
        action.setState("IN_PROGRESS");
        proposedActionRepository.save(action);

        ImplementedAction impl = new ImplementedAction();
        impl.setProposedAction(action);
        impl.setActualStart(new java.util.Date());
        impl.setActualParty("Default Party");
        impl.setActualLocation("Default Location");
        implementedActionRepository.save(impl);

        return action;
    }

    public ProposedAction rejectAction(Long id) {
        ProposedAction action = proposedActionRepository.findById(id).orElseThrow();
        action.setState("PROPOSED");
        return proposedActionRepository.save(action);
    }

    public ProposedAction reopenAction(Long id) {
        ProposedAction action = proposedActionRepository.findById(id).orElseThrow();
        action.setState("REOPENED");
        proposedActionRepository.save(action);
        ImplementedAction impl = action.getImplementedAction();
        if (impl != null) {
            reversalLedgerEntryGenerator.generateEntries(impl);
        }
        return action;
    }

    public ResourceAllocation allocateResource(Long actionId, Long resourceTypeId,
                                               Double quantity, String kind,
                                               String assetId, String timePeriod) {
        ProposedAction action = proposedActionRepository.findById(actionId).orElseThrow();
        ResourceType resourceType = resourceTypeRepository.findById(resourceTypeId).orElseThrow();

        ResourceAllocation allocation = new ResourceAllocation();
        allocation.setAction(action);
        allocation.setResourceType(resourceType);
        allocation.setQuantity(quantity);
        allocation.setKind(kind);
        allocation.setAssetId(assetId);
        allocation.setTimePeriod(timePeriod);
        resourceAllocationRepository.save(allocation);

        if (!resourceType.getPoolAccounts().isEmpty()) {
            Account pool = resourceType.getPoolAccounts().get(0);

            Transaction tx = new Transaction();
            tx.setDescription("Allocation: " + quantity + " " + resourceType.getUnit()
                    + " for action '" + action.getName() + "'");
            transactionRepository.save(tx);

            Entry entry = new Entry();
            entry.setAccount(pool);
            entry.setAmount(-quantity);
            entry.setChargedAt(new java.util.Date());
            entry.setBookedAt(new java.util.Date());
            entry.setTransaction(tx);
            entryRepository.save(entry);
        }

        return allocation;
    }
}