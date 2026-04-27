package com.example.PlanningLedger;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

@Service
class ProtocolService {
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
class ResourceTypeService {
    @Autowired
    private ResourceTypeRepository resourceTypeRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<ResourceType> getAllResourceTypes() {
        return resourceTypeRepository.findAll();
    }

    public ResourceType createResourceType(ResourceType resourceType) {
        return resourceTypeRepository.save(resourceType);
    }

    public Optional<ResourceType> getResourceTypeById(Long id) {
        return resourceTypeRepository.findById(id);
    }

    public Account createPoolAccountForResourceType(Long resourceTypeId, String accountName) {
        ResourceType resourceType = resourceTypeRepository.findById(resourceTypeId)
            .orElseThrow(() -> new RuntimeException("ResourceType not found"));

        Account account = new Account();
        account.setName(accountName);
        account.setKind("POOL");
        account.setResourceType(resourceType);
        account = accountRepository.save(account);

        resourceType.getPoolAccounts().add(account);
        resourceTypeRepository.save(resourceType);

        return account;
    }

    public void deleteResourceType(Long id) {
        resourceTypeRepository.deleteById(id);
    }
}

@Service
class PlanService {
    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private ProposedActionRepository proposedActionRepository;

    @Autowired
    private ProtocolRepository protocolRepository;

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
}

@Service
class ActionService {
    @Autowired
    private ProposedActionRepository proposedActionRepository;

    public ProposedAction implementAction(Long id) {
        ProposedAction action = proposedActionRepository.findById(id).orElseThrow();
        action.setState("IN_PROGRESS");
        return proposedActionRepository.save(action);
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
        action.setState("PROPOSED");
        return proposedActionRepository.save(action);
    }

    public ProposedAction abandonAction(Long id) {
        ProposedAction action = proposedActionRepository.findById(id).orElseThrow();
        action.setState("ABANDONED");
        return proposedActionRepository.save(action);
    }
}