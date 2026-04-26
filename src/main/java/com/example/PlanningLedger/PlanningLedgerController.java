package com.example.PlanningLedger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/protocols")
class ProtocolController {
    @Autowired
    private ProtocolService protocolService;

    @GetMapping
    public List<Protocol> getAllProtocols() {
        return protocolService.getAllProtocols();
    }

    @PostMapping
    public Protocol createProtocol(@RequestBody Protocol protocol) {
        return protocolService.createProtocol(protocol);
    }
}

@RestController
@RequestMapping("/api/resource-types")
class ResourceTypeController {
    @Autowired
    private ResourceTypeService resourceTypeService;

    @GetMapping
    public List<ResourceType> getAllResourceTypes() {
        return resourceTypeService.getAllResourceTypes();
    }

    @PostMapping
    public ResourceType createResourceType(@RequestBody ResourceType resourceType) {
        return resourceTypeService.createResourceType(resourceType);
    }
}

@RestController
@RequestMapping("/api/plans")
class PlanController {
    @Autowired
    private PlanService planService;

    @PostMapping
    public Plan createPlan(@RequestBody Plan plan) {
        return planService.createPlan(plan);
    }

    @GetMapping("/{id}")
    public Optional<Plan> getPlanById(@PathVariable Long id) {
        return planService.getPlanById(id);
    }
}

@RestController
@RequestMapping("/api/actions")
class ActionController {
    @Autowired
    private ActionService actionService;

    @PostMapping("/{id}/implement")
    public ProposedAction implementAction(@PathVariable Long id) {
        return actionService.implementAction(id);
    }

    @PostMapping("/{id}/complete")
    public ProposedAction completeAction(@PathVariable Long id) {
        return actionService.completeAction(id);
    }

    @PostMapping("/{id}/suspend")
    public ProposedAction suspendAction(@PathVariable Long id) {
        return actionService.suspendAction(id);
    }

    @PostMapping("/{id}/resume")
    public ProposedAction resumeAction(@PathVariable Long id) {
        return actionService.resumeAction(id);
    }

    @PostMapping("/{id}/abandon")
    public ProposedAction abandonAction(@PathVariable Long id) {
        return actionService.abandonAction(id);
    }
}