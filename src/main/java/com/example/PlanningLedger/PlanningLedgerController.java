package com.example.PlanningLedger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/protocols")
class ProtocolController {
    @Autowired
    private ProtocolManager protocolManager;

    @GetMapping
    public List<Protocol> getAllProtocols() {
        return protocolManager.getAllProtocols();
    }

    @PostMapping
    public Protocol createProtocol(@RequestBody Protocol protocol) {
        return protocolManager.createProtocol(protocol);
    }
}

@RestController
@RequestMapping("/api/resource-types")
class ResourceTypeController {
    @Autowired
    private ResourceTypeManager resourceTypeManager;

    @GetMapping
    public List<ResourceType> getAllResourceTypes() {
        return resourceTypeManager.getAllResourceTypes();
    }

    @PostMapping
    public ResourceType createResourceType(@RequestBody ResourceType resourceType) {
        return resourceTypeManager.createResourceType(resourceType);
    }
}

@RestController
@RequestMapping("/api/plans")
class PlanController {
    @Autowired
    private PlanManager planManager;

    @PostMapping
    public Plan createPlan(@RequestBody Plan plan) {
        return planManager.createPlan(plan);
    }

    @GetMapping("/{id}")
    public Optional<Plan> getPlanById(@PathVariable Long id) {
        return planManager.getPlanById(id);
    }
}

@RestController
@RequestMapping("/api/actions")
class ActionController {
    @Autowired
    private ActionManager actionManager;

    @PostMapping("/{id}/implement")
    public void implementAction(@PathVariable Long id) {
        actionManager.implementAction(id);
    }

    @PostMapping("/{id}/complete")
    public void completeAction(@PathVariable Long id) {
        actionManager.completeAction(id);
    }

    @PostMapping("/{id}/suspend")
    public void suspendAction(@PathVariable Long id) {
        actionManager.suspendAction(id);
    }

    @PostMapping("/{id}/resume")
    public void resumeAction(@PathVariable Long id) {
        actionManager.resumeAction(id);
    }

    @PostMapping("/{id}/abandon")
    public void abandonAction(@PathVariable Long id) {
        actionManager.abandonAction(id);
    }
}