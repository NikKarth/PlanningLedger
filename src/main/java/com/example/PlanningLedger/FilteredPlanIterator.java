package com.example.PlanningLedger;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

class FilteredPlanIterator implements Iterator<PlanNode> {

    private final DepthFirstPlanIterator delegate;
    private final Predicate<PlanNode> predicate;

    private PlanNode pending = null;
    private int pendingDepth = 0;
    private int currentDepth = 0;

    FilteredPlanIterator(Plan root, Predicate<PlanNode> predicate) {
        this.delegate = new DepthFirstPlanIterator(root);
        this.predicate = predicate;
        advance();
    }

    private void advance() {
        pending = null;
        while (delegate.hasNext()) {
            PlanNode candidate = delegate.next();
            if (predicate.test(candidate)) {
                pending = candidate;
                pendingDepth = delegate.getCurrentDepth();
                return;
            }
        }
    }

    @Override
    public boolean hasNext() {
        return pending != null;
    }

    @Override
    public PlanNode next() {
        if (pending == null) throw new NoSuchElementException();
        PlanNode result = pending;
        currentDepth = pendingDepth;
        advance();
        return result;
    }

    /** Depth of the node returned by the most recent call to {@link #next()}. */
    public int getCurrentDepth() {
        return currentDepth;
    }
}
