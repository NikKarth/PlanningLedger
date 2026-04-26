package com.example.PlanningLedger;

import java.util.*;

class DepthFirstPlanIterator implements Iterator<PlanNode> {
    private Stack<PlanNode> stack = new Stack<>();

    public DepthFirstPlanIterator(Plan root) {
        stack.push(root);
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public PlanNode next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        PlanNode current = stack.pop();
        if (current instanceof Plan) {
            List<PlanNode> children = ((Plan) current).getChildren();
            for (int i = children.size() - 1; i >= 0; i--) {
                stack.push(children.get(i));
            }
        }
        return current;
    }
}