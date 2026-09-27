package com.fintrack.fintrackapi.transactions.repository;

import com.fintrack.fintrackapi.transactions.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    /** Finds transactions owned by a user, newest first. */
    List<TransactionEntity> findAllByOwnerIdOrderByCreatedAtDesc(String ownerId);

    /** Finds a transaction only when it belongs to the supplied owner. */
    Optional<TransactionEntity> findByIdAndOwnerId(UUID id, String ownerId);

    /** Deletes all transactions for the supplied owner and returns the affected row count. */
    long deleteAllByOwnerId(String ownerId);
}
