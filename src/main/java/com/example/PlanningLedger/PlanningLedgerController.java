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
    public void implementAction(@PathVariable Long id) {
        actionService.implementAction(id);
    }

    @PostMapping("/{id}/complete")
    public void completeAction(@PathVariable Long id) {
        actionService.completeAction(id);
    }

    @PostMapping("/{id}/suspend")
    public void suspendAction(@PathVariable Long id) {
        actionService.suspendAction(id);
    }

    @PostMapping("/{id}/resume")
    public void resumeAction(@PathVariable Long id) {
        actionService.resumeAction(id);
    }

    @PostMapping("/{id}/abandon")
    public void abandonAction(@PathVariable Long id) {
        actionService.abandonAction(id);
    }
}