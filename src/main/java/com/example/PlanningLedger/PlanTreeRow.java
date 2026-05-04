package com.example.PlanningLedger;

class PlanTreeRow {

    private final PlanNode node;
    private final int      depth;
    private final boolean  truncated;

    PlanTreeRow(PlanNode node, int depth, boolean truncated) {
        this.node      = node;
        this.depth     = depth;
        this.truncated = truncated;
    }

    public PlanNode getNode()      { return node; }
    public int      getDepth()     { return depth; }
    public boolean  isTruncated()  { return truncated; }

    /** Returns the node cast to ProposedAction, or null if it is a Plan. */
    public ProposedAction getAction() {
        return (node instanceof ProposedAction) ? (ProposedAction) node : null;
    }

    /** "Plan" or "Action" — avoids exposing raw Hibernate proxy class names to templates. */
    public String getNodeTypeName() {
        return (node instanceof Plan) ? "Plan" : "Action";
    }
}
