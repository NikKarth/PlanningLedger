package com.example.PlanningLedger;

import java.util.Map;

class PlanReportRow {
    private final String name;
    private final String status;
    private final String nodeType;   // "Plan" or "Action"
    private final int depth;
    private final Map<String, Double> resourceTotals; // resource type name -> total quantity

    PlanReportRow(String name, String status, String nodeType,
                  int depth, Map<String, Double> resourceTotals) {
        this.name           = name;
        this.status         = status;
        this.nodeType       = nodeType;
        this.depth          = depth;
        this.resourceTotals = resourceTotals;
    }

    public String getName()                         { return name; }
    public String getStatus()                       { return status; }
    public String getNodeType()                     { return nodeType; }
    public int    getDepth()                        { return depth; }
    public Map<String, Double> getResourceTotals()  { return resourceTotals; }
}
