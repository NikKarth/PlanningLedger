package com.example.PlanningLedger;

import java.util.ArrayList;
import java.util.List;

public class ProtocolForm {
    private String name;
    private String description;
    private List<StepForm> steps = new ArrayList<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<StepForm> getSteps() { return steps; }
    public void setSteps(List<StepForm> steps) { this.steps = steps; }

    public static class StepForm {
        private String subProtocol;
        private String dependsOn;

        public String getSubProtocol() { return subProtocol; }
        public void setSubProtocol(String subProtocol) { this.subProtocol = subProtocol; }
        public String getDependsOn() { return dependsOn; }
        public void setDependsOn(String dependsOn) { this.dependsOn = dependsOn; }
    }
}