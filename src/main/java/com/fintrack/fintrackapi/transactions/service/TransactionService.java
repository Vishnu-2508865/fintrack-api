package com.fintrack.fintrackapi.transactions.service;

import com.fintrack.fintrackapi.common.ResourceNotFoundException;
import com.fintrack.fintrackapi.transactions.model.CreateTransactionRequest;
import com.fintrack.fintrackapi.transactions.model.TransactionEntity;
import com.fintrack.fintrackapi.transactions.model.TransactionResponse;
import com.fintrack.fintrackapi.transactions.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /** Creates a transaction owned by the authenticated user. */
    @Transactional
    public TransactionResponse create(String ownerId, CreateTransactionRequest request) {
        TransactionEntity transaction = transactionRepository.save(
                new TransactionEntity(ownerId, request.description().trim(), request.amount()));
        log.info("Created transaction {} for owner {}", transaction.getId(), ownerId);
        return TransactionResponse.from(transaction);
    }

    /** Returns only the authenticated user's transactions, newest first. */
    @Transactional(readOnly = true)
    public List<TransactionResponse> getByUser(String ownerId) {
        return transactionRepository.findAllByOwnerIdOrderByCreatedAtDesc(ownerId)
                .stream().map(TransactionResponse::from).toList();
    }

    /** Deletes one transaction only when it belongs to the authenticated user. */
    @Transactional
    public void delete(String ownerId, UUID transactionId) {
        TransactionEntity transaction = transactionRepository.findByIdAndOwnerId(transactionId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        transactionRepository.delete(transaction);
        log.info("Deleted transaction {} for owner {}", transactionId, ownerId);
    }

    /** Deletes every transaction belonging to the authenticated user, and no other user's data. */
    @Transactional
    public long deleteAll(String ownerId) {
        long deleted = transactionRepository.deleteAllByOwnerId(ownerId);
        log.info("Deleted {} transactions for owner {}", deleted, ownerId);
        return deleted;
    }
}
