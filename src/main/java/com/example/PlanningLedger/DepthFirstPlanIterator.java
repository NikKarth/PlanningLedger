package com.example.PlanningLedger;

import java.util.*;

class DepthFirstPlanIterator implements Iterator<PlanNode> {
    private final Deque<PlanNode> nodeStack  = new ArrayDeque<>();
    private final Deque<Integer>  depthStack = new ArrayDeque<>();
    private int currentDepth = 0;

    public DepthFirstPlanIterator(Plan root) {
        nodeStack.push(root);
        depthStack.push(0);
    }

    @Override
    public boolean hasNext() {
        return !nodeStack.isEmpty();
    }

    @Override
    public PlanNode next() {
        if (!hasNext()) throw new NoSuchElementException();

        PlanNode current = nodeStack.pop();
        currentDepth = depthStack.pop();

        if (current instanceof Plan) {
            List<PlanNode> children = ((Plan) current).getChildren();
            for (int i = children.size() - 1; i >= 0; i--) {
                nodeStack.push(children.get(i));
                depthStack.push(currentDepth + 1);
            }
        }
        return current;
    }

    /** Depth of the node returned by the most recent call to {@link #next()}. */
    public int getCurrentDepth() {
        return currentDepth;
    }
}
