package com.example.PlanningLedger;

import org.springframework.data.jpa.repository.JpaRepository;

interface ProtocolRepository extends JpaRepository<Protocol, Long> {}

interface ProtocolStepRepository extends JpaRepository<ProtocolStep, Long> {}

interface ResourceTypeRepository extends JpaRepository<ResourceType, Long> {}

interface PlanRepository extends JpaRepository<Plan, Long> {}

interface ProposedActionRepository extends JpaRepository<ProposedAction, Long> {}

interface AccountRepository extends JpaRepository<Account, Long> {}

interface TransactionRepository extends JpaRepository<Transaction, Long> {}

interface ImplementedActionRepository extends JpaRepository<ImplementedAction, Long> {}

interface ResourceAllocationRepository extends JpaRepository<ResourceAllocation, Long> {}

interface EntryRepository extends JpaRepository<Entry, Long> {}