package com.example.PlanningLedger;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

class LazySubtreeIterator implements Iterator<PlanNode> {

    private final Deque<PlanNode> nodeStack  = new ArrayDeque<>();
    private final Deque<Integer>  depthStack = new ArrayDeque<>();
    private final int depthLimit;

    private int     currentDepth     = 0;
    private boolean currentTruncated = false;

    LazySubtreeIterator(PlanNode root, int depthLimit) {
        this.depthLimit = depthLimit;
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
            if (currentDepth < depthLimit) {
                for (int i = children.size() - 1; i >= 0; i--) {
                    nodeStack.push(children.get(i));
                    depthStack.push(currentDepth + 1);
                }
                currentTruncated = false;
            } else {
                // Depth limit reached: yield the sub-plan itself, skip its children.
                currentTruncated = !children.isEmpty();
            }
        } else {
            currentTruncated = false;
        }

        return current;
    }

    /** Depth of the node returned by the most recent call to {@link #next()}. */
    public int getCurrentDepth() {
        return currentDepth;
    }

    /** True when the most recent node is a Plan whose children were suppressed by the depth limit. */
    public boolean isCurrentTruncated() {
        return currentTruncated;
    }
}
