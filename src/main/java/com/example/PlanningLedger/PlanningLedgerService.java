package com.example.PlanningLedger;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

@Service
class ProtocolService {
    @Autowired
    private ProtocolRepository protocolRepository;

    public List<Protocol> getAllProtocols() {
        return protocolRepository.findAll();
    }

    public Protocol createProtocol(Protocol protocol) {
        return protocolRepository.save(protocol);
    }
}

@Service
class ResourceTypeService {
    @Autowired
    private ResourceTypeRepository resourceTypeRepository;

    public List<ResourceType> getAllResourceTypes() {
        return resourceTypeRepository.findAll();
    }

    public ResourceType createResourceType(ResourceType resourceType) {
        return resourceTypeRepository.save(resourceType);
    }
}

@Service
class PlanService {
    @Autowired
    private PlanRepository planRepository;

    public Plan createPlan(Plan plan) {
        return planRepository.save(plan);
    }

    public Optional<Plan> getPlanById(Long id) {
        return planRepository.findById(id);
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