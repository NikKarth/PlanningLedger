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
        ctx.getAction().setState("IN_PROGRESS");
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
        ctx.getAction().setState("PROPOSED");
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

// Exception
class IllegalStateTransitionException extends RuntimeException {
    public IllegalStateTransitionException(String message) {
        super(message);
    }
}