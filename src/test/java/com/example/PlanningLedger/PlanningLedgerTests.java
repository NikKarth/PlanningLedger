package com.example.PlanningLedger;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlanningLedgerTests {

    @Mock
    private ProtocolRepository protocolRepository;

    @InjectMocks
    private ProtocolManager protocolManager;

    @Test
    void createProtocol_validInput_protocolCreated() {
        // Arrange
        Protocol protocol = new Protocol();
        protocol.setName("Test Protocol");
        when(protocolRepository.save(protocol)).thenReturn(protocol);

        // Act
        Protocol result = protocolManager.createProtocol(protocol);

        // Assert
        assertEquals("Test Protocol", result.getName());
        verify(protocolRepository, times(1)).save(protocol);
    }

    @Test
    void depthFirstIterator_traversePlan_correctOrder() {
        // Arrange
        Plan root = new Plan();
        Plan child1 = new Plan();
        ProposedAction child2 = new ProposedAction();
        root.getChildren().add(child1);
        root.getChildren().add(child2);

        DepthFirstPlanIterator iterator = new DepthFirstPlanIterator(root);

        // Act & Assert
        assertTrue(iterator.hasNext());
        assertEquals(root, iterator.next());
        assertEquals(child1, iterator.next());
        assertEquals(child2, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void stateTransition_illegalTransition_throwsException() {
        // Arrange
        ProposedAction action = new ProposedAction();
        action.setState("PROPOSED");
        ActionContext context = new ActionContext(action);
        ProposedState state = new ProposedState();

        // Act & Assert
        assertThrows(IllegalStateTransitionException.class, () -> state.resume(context));
    }
}