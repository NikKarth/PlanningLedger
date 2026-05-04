package com.example.PlanningLedger;

import org.springframework.stereotype.Component;

// State Interface
interface ActionState {
    void implement(ActionContext ctx);
    void suspend(ActionContext ctx, String reason);
    void resume(ActionContext ctx);
    void complete(ActionContext ctx);
    void abandon(ActionContext ctx);
    String name();

    default void submitForApproval(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot submit for approval from " + name() + " state");
    }

    default void approve(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot approve from " + name() + " state");
    }

    default void reject(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot reject from " + name() + " state");
    }

    default void reopen(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot reopen from " + name() + " state");
    }
}

// Context
class ActionContext {
    private ProposedAction action;

    public ActionContext(ProposedAction action) {
        this.action = action;
    }

    public ProposedAction getAction() {
        return action;
    }
}

// Concrete States
@Component
class ProposedState implements ActionState {
    @Override
    public void implement(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot implement directly from PROPOSED state");
    }

    @Override
    public void suspend(ActionContext ctx, String reason) {
        ctx.getAction().setState("SUSPENDED");
    }

    @Override
    public void resume(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot resume from PROPOSED state");
    }

    @Override
    public void complete(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot complete from PROPOSED state");
    }

    @Override
    public void abandon(ActionContext ctx) {
        ctx.getAction().setState("ABANDONED");
    }

    @Override
    public String name() {
        return "PROPOSED";
    }

    @Override
    public void submitForApproval(ActionContext ctx) {
        ctx.getAction().setState("PENDING_APPROVAL");
    }
}

@Component
class InProgressState implements ActionState {
    @Override
    public void implement(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot implement from IN_PROGRESS state");
    }

    @Override
    public void suspend(ActionContext ctx, String reason) {
        ctx.getAction().setState("SUSPENDED");
    }

    @Override
    public void resume(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot resume from IN_PROGRESS state");
    }

    @Override
    public void complete(ActionContext ctx) {
        ctx.getAction().setState("COMPLETED");
    }

    @Override
    public void abandon(ActionContext ctx) {
        ctx.getAction().setState("ABANDONED");
    }

    @Override
    public String name() {
        return "IN_PROGRESS";
    }
}

@Component
class SuspendedState implements ActionState {
    @Override
    public void implement(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot implement from SUSPENDED state");
    }

    @Override
    public void suspend(ActionContext ctx, String reason) {
        throw new IllegalStateTransitionException("Already suspended");
    }

    @Override
    public void resume(ActionContext ctx) {
        ctx.getAction().setState("IN_PROGRESS");
    }

    @Override
    public void complete(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot complete from SUSPENDED state");
    }

    @Override
    public void abandon(ActionContext ctx) {
        ctx.getAction().setState("ABANDONED");
    }

    @Override
    public String name() {
        return "SUSPENDED";
    }
}

@Component
class CompletedState implements ActionState {
    @Override
    public void implement(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot transition from COMPLETED state");
    }

    @Override
    public void suspend(ActionContext ctx, String reason) {
        throw new IllegalStateTransitionException("Cannot transition from COMPLETED state");
    }

    @Override
    public void resume(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot transition from COMPLETED state");
    }

    @Override
    public void complete(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot transition from COMPLETED state");
    }

    @Override
    public void abandon(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot transition from COMPLETED state");
    }

    @Override
    public String name() {
        return "COMPLETED";
    }
}

@Component
class AbandonedState implements ActionState {
    @Override
    public void implement(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot transition from ABANDONED state");
    }

    @Override
    public void suspend(ActionContext ctx, String reason) {
        throw new IllegalStateTransitionException("Cannot transition from ABANDONED state");
    }

    @Override
    public void resume(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot transition from ABANDONED state");
    }

    @Override
    public void complete(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot transition from ABANDONED state");
    }

    @Override
    public void abandon(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot transition from ABANDONED state");
    }

    @Override
    public String name() {
        return "ABANDONED";
    }
}

@Component
class PendingApprovalState implements ActionState {
    @Override
    public void implement(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot implement from PENDING_APPROVAL state");
    }

    @Override
    public void suspend(ActionContext ctx, String reason) {
        throw new IllegalStateTransitionException("Cannot suspend from PENDING_APPROVAL state");
    }

    @Override
    public void resume(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot resume from PENDING_APPROVAL state");
    }

    @Override
    public void complete(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot complete from PENDING_APPROVAL state");
    }

    @Override
    public void abandon(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot abandon from PENDING_APPROVAL state");
    }

    @Override
    public String name() {
        return "PENDING_APPROVAL";
    }

    @Override
    public void approve(ActionContext ctx) {
        ctx.getAction().setState("IN_PROGRESS");
    }

    @Override
    public void reject(ActionContext ctx) {
        ctx.getAction().setState("PROPOSED");
    }
}

@Component
class ReopenedState implements ActionState {
    @Override
    public void implement(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot implement from REOPENED state");
    }

    @Override
    public void suspend(ActionContext ctx, String reason) {
        throw new IllegalStateTransitionException("Cannot suspend from REOPENED state");
    }

    @Override
    public void resume(ActionContext ctx) {
        throw new IllegalStateTransitionException("Cannot resume from REOPENED state");
    }

    @Override
    public void complete(ActionContext ctx) {
        ctx.getAction().setState("COMPLETED");
    }

    @Override
    public void abandon(ActionContext ctx) {
        ctx.getAction().setState("ABANDONED");
    }

    @Override
    public String name() {
        return "REOPENED";
    }
}

// Exception
class IllegalStateTransitionException extends RuntimeException {
    public IllegalStateTransitionException(String message) {
        super(message);
    }
}