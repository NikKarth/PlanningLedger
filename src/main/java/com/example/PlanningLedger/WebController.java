package com.example.PlanningLedger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Controller
public class WebController {

    @Autowired
    private ProtocolService protocolService;

    @Autowired
    private ResourceTypeService resourceTypeService;

    @Autowired
    private PlanService planService;

    @Autowired
    private ActionService actionService;

    @GetMapping("/ledger")
    public String ledger(Model model) {
        List<Account> accounts = resourceTypeService.getAllAccounts();
        Map<Long, Double> balances = new LinkedHashMap<>();
        for (Account account : accounts) {
            double b = 0.0;
            for (Entry e : account.getEntries()) {
                if (e.getAmount() != null) b += e.getAmount();
            }
            balances.put(account.getId(), b);
        }
        model.addAttribute("accounts", accounts);
        model.addAttribute("balances", balances);
        return "ledger";
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("protocols", protocolService.getAllProtocols());
        model.addAttribute("resourceTypes", resourceTypeService.getAllResourceTypes());
        model.addAttribute("plans", planService.getAllPlans());
        return "index";
    }

    @GetMapping("/protocols")
    public String protocols(Model model) {
        model.addAttribute("protocols", protocolService.getAllProtocols());
        return "protocols";
    }

    @PostMapping("/protocols")
    public String createProtocol(@RequestParam String name,
                                @RequestParam String description,
                                @RequestParam(required = false) String[] stepSubProtocols,
                                @RequestParam(required = false) String[] stepDependencies) {
        Protocol protocol = new Protocol();
        protocol.setName(name);
        protocol.setDescription(description);

        // Create the protocol first
        Protocol savedProtocol = protocolService.createProtocol(protocol);

        // Add steps if any were provided
        if (stepSubProtocols != null && stepSubProtocols.length > 0) {
            for (int i = 0; i < stepSubProtocols.length; i++) {
                String subProtocol = stepSubProtocols[i];
                if (subProtocol != null && !subProtocol.trim().isEmpty()) {
                    String dependsOn = (stepDependencies != null && i < stepDependencies.length) ?
                                     stepDependencies[i] : null;
                    protocolService.addStepToProtocol(savedProtocol.getId(), subProtocol, dependsOn);
                }
            }
        }

        return "redirect:/protocols";
    }

    @GetMapping("/resource-types")
    public String resourceTypes(Model model) {
        model.addAttribute("resourceTypes", resourceTypeService.getAllResourceTypes());
        return "resource-types";
    }

    @GetMapping("/plans")
    public String plans(Model model) {
        model.addAttribute("plans", planService.getAllPlans());
        model.addAttribute("protocols", protocolService.getAllProtocols());
        return "plans";
    }

    @GetMapping("/protocols/{id}")
    public String protocolDetail(@PathVariable Long id, Model model) {
        Optional<Protocol> protocol = protocolService.getProtocolById(id);
        if (protocol.isPresent()) {
            model.addAttribute("protocol", protocol.get());
            return "protocol-detail";
        }
        return "redirect:/protocols";
    }

    @PostMapping("/protocols/{id}/steps")
    public String addStepToProtocol(@PathVariable Long id,
                                   @RequestParam String subProtocol,
                                   @RequestParam(required = false) String dependsOn) {
        protocolService.addStepToProtocol(id, subProtocol, dependsOn);
        return "redirect:/protocols/" + id;
    }

    @PostMapping("/protocols/{id}/delete")
    public String deleteProtocol(@PathVariable Long id) {
        protocolService.deleteProtocol(id);
        return "redirect:/protocols";
    }

    @PostMapping("/resource-types")
    public String createResourceType(@RequestParam String name,
                                    @RequestParam String kind,
                                    @RequestParam String unit,
                                    @RequestParam(required = false) String[] poolAccountNames,
                                    @RequestParam(required = false) Double[] poolAccountInitialBalances) {
        ResourceType resourceType = new ResourceType();
        resourceType.setName(name);
        resourceType.setKind(kind);
        resourceType.setUnit(unit);

        ResourceType savedResourceType = resourceTypeService.createResourceType(resourceType);

        if (poolAccountNames != null && poolAccountNames.length > 0) {
            for (int i = 0; i < poolAccountNames.length; i++) {
                String accountName = poolAccountNames[i];
                if (accountName != null && !accountName.trim().isEmpty()) {
                    Double balance = (poolAccountInitialBalances != null && i < poolAccountInitialBalances.length)
                            ? poolAccountInitialBalances[i] : null;
                    resourceTypeService.createPoolAccountForResourceType(savedResourceType.getId(), accountName, balance);
                }
            }
        }

        return "redirect:/resource-types";
    }

    @GetMapping("/resource-types/{id}")
    public String resourceTypeDetail(@PathVariable Long id, Model model) {
        Optional<ResourceType> resourceType = resourceTypeService.getResourceTypeById(id);
        if (resourceType.isPresent()) {
            model.addAttribute("resourceType", resourceType.get());
            return "resource-type-detail";
        }
        return "redirect:/resource-types";
    }

    @PostMapping("/resource-types/{id}/pool-account")
    public String createPoolAccount(@PathVariable Long id,
                                    @RequestParam String accountName,
                                    @RequestParam(required = false) Double initialBalance) {
        resourceTypeService.createPoolAccountForResourceType(id, accountName, initialBalance);
        return "redirect:/resource-types/" + id;
    }

    @PostMapping("/resource-types/{id}/delete")
    public String deleteResourceType(@PathVariable Long id) {
        resourceTypeService.deleteResourceType(id);
        return "redirect:/resource-types";
    }

    @PostMapping("/plans")
    public String createPlan(@ModelAttribute Plan plan) {
        planService.createPlan(plan);
        return "redirect:/plans";
    }

    @GetMapping("/plans/{id}")
    public String planDetail(@PathVariable Long id, Model model) {
        Optional<Plan> plan = planService.getPlanById(id);
        if (plan.isPresent()) {
            model.addAttribute("plan", plan.get());
            model.addAttribute("protocols", protocolService.getAllProtocols());
            model.addAttribute("resourceTypes", resourceTypeService.getAllResourceTypes());
            return "plan-detail";
        }
        return "redirect:/plans";
    }

    @PostMapping("/plans/{planId}/actions/{actionId}/allocate")
    public String allocateResource(@PathVariable Long planId,
                                   @PathVariable Long actionId,
                                   @RequestParam Long resourceTypeId,
                                   @RequestParam Double quantity,
                                   @RequestParam String kind,
                                   @RequestParam(required = false) String assetId,
                                   @RequestParam(required = false) String timePeriod) {
        actionService.allocateResource(actionId, resourceTypeId, quantity, kind, assetId, timePeriod);
        return "redirect:/plans/" + planId;
    }

    @PostMapping("/plans/from-protocol")
    public String createPlanFromProtocol(@RequestParam Long protocolId, @RequestParam String planName) {
        Plan plan = planService.createPlanFromProtocol(protocolId, planName);
        return "redirect:/plans/" + plan.getId();
    }

    @PostMapping("/plans/with-steps")
    public String createPlanWithSteps(@RequestParam String planName,
                                     @RequestParam(required = false) String[] stepNames,
                                     @RequestParam(required = false) String[] stepTypes) {
        Plan plan = new Plan();
        plan.setName(planName);

        // Create the plan first
        Plan savedPlan = planService.createPlan(plan);

        // Add steps if any were provided
        if (stepNames != null && stepNames.length > 0) {
            for (int i = 0; i < stepNames.length; i++) {
                String stepName = stepNames[i];
                if (stepName != null && !stepName.trim().isEmpty()) {
                    String stepType = (stepTypes != null && i < stepTypes.length) ? stepTypes[i] : "ACTION";
                    
                    if ("SUB_PLAN".equals(stepType)) {
                        planService.addSubPlanToPlan(savedPlan.getId(), stepName);
                    } else {
                        planService.addActionToPlan(savedPlan.getId(), stepName);
                    }
                }
            }
        }

        return "redirect:/plans/" + savedPlan.getId();
    }

    @PostMapping("/plans/{id}/actions")
    public String addActionToPlan(@PathVariable Long id, @RequestParam String actionName) {
        planService.addActionToPlan(id, actionName);
        return "redirect:/plans/" + id;
    }

    @PostMapping("/plans/{id}/sub-plans")
    public String addSubPlanToPlan(@PathVariable Long id, @RequestParam String subPlanName) {
        planService.addSubPlanToPlan(id, subPlanName);
        return "redirect:/plans/" + id;
    }

    @PostMapping("/plans/{id}/delete")
    public String deletePlan(@PathVariable Long id) {
        planService.deletePlan(id);
        return "redirect:/plans";
    }
}